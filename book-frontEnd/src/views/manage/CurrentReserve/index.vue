<template>
  <div class="book-reserve-page">
    <div class="page-header">
      <div class="search-section">
        <BookSearchInput @search="handleSearchInput" style="width: 150px" />
        <div class="filter-group">
          <span class="filter-label">书籍状态:</span>
          <BookStatusSelect 
            :options="statusOptions"
            v-model="searchParams.bookStatus"
            placeholder="所有状态"
            style="width: 150px" 
            @change="handleStatusChange"
          />
        </div>
        <div class="filter-group">
          <span class="filter-label">书籍分类:</span>
          <BookCategorySelect @change="handleCategoryChange" style="width: 150px" />
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" color="#409EFF" :size="32">
        <Loading />
      </el-icon>
      <div class="loading-text">加载中...</div>
    </div>

    <div v-else-if="filteredBooks.length === 0" class="empty-container">
      <el-empty description="暂无预约的图书" />
    </div>

    <div v-else class="reserved-books-table">
      <el-table
        :data="currentPageData"
        border
        stripe
        :header-cell-style="{ 
          background: '#FAFAFA', 
          fontWeight: 600,
          color: '#333',
          textAlign: 'center'
        }"
        :cell-style="{ textAlign: 'center' }"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" type="index" width="70" align="center" />
        <el-table-column label="书籍名称" min-width="200" align="left">
          <template #default="scope">
            <div class="book-info">
              <img :src="scope.row.bookImg" :alt="scope.row.bookName" class="book-cover" />
              <div class="book-text">
                <span class="book-name">{{ scope.row.bookName }}</span>
                <span class="book-author">作者: {{ scope.row.author }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="200" align="center" prop="category" />
        <el-table-column label="状态" width="150" align="center">
          <template #default="scope">
            <span class="status-badge" :class="getStatusClass(scope.row.status)">
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="上架时间" width="200" align="center">
          <template #default="scope">
            <span class="date-display">
              {{ formatDate(scope.row.shelfTime) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <span class="text-button detail-btn" @click="handleDetail(scope.row)">详情</span>
              <span class="text-button cancel-btn" @click="handleCancelReserve(scope.row)">取消预约</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-if="filteredBooks.length > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Loading } from '@element-plus/icons-vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';

const router = useRouter();

// 复选框选中数据
const selectedBooks = ref<any[]>([]);

interface StatusOption {
  label: string;
  value: string;
}

const statusOptions = ref<StatusOption[]>([
  { label: '所有状态', value: '' },
  { label: '待上架', value: '0' },
  { label: '已预约', value: '1' },
  { label: '可借阅', value: '2' },
  { label: '已借光', value: '3' },
  { label: '待发布', value: '4' }
]);

const reservedBooks = ref<any[]>([]);
const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});

const searchParams = reactive({
  bookName: '',
  categoryId: '',
  bookStatus: ''
});

// 根据图片中的数据生成模拟数据
const generateMockReservedBooks = (): any[] => {
  return [
    {
      id: 1,
      bookName: '参加视频会议(3)',
      author: '佚名',
      bookImg: 'https://picsum.photos/100/140?random=book1',
      translator: '佚名',
      status: '已预约',
      shelfTime: '2022-07-22',
      category: '社会人文',
      reserveTime: '2022-07-20',
    },
    {
      id: 2,
      bookName: '组织员工学习(4)',
      author: '佚名',
      bookImg: 'https://picsum.photos/100/140?random=book2',
      translator: '佚名',
      status: '可借阅',
      shelfTime: '2022-02-28',
      category: '散文',
      reserveTime: '2022-02-25',
    },
    {
      id: 3,
      bookName: '不要让未来的你讨厌现在的自己',
      author: '佚名',
      bookImg: 'https://picsum.photos/100/140?random=book3',
      translator: '佚名',
      status: '待发布',
      shelfTime: '2022-09-02',
      category: '社会人文',
      reserveTime: '2022-08-30',
    },
    {
      id: 4,
      bookName: '参加义工活动(2)(5)',
      author: '佚名',
      bookImg: 'https://picsum.photos/100/140?random=book4',
      translator: '佚名',
      status: '已借阅',
      shelfTime: '2022-12-25',
      category: '社会人文',
      reserveTime: '2022-12-20',
    }
  ];
};

// 计算过滤后的数据
const filteredBooks = computed(() => {
  let filtered = reservedBooks.value;
  
  // 按书名搜索
  if (searchParams.bookName) {
    filtered = filtered.filter(book => 
      book.bookName.toLowerCase().includes(searchParams.bookName.toLowerCase())
    );
  }
  
  // 按状态筛选
  if (searchParams.bookStatus) {
    const statusMap: { [key: string]: string } = {
      '0': '待上架',
      '1': '已预约',
      '2': '可借阅',
      '3': '已借光',
      '4': '待发布'
    };
    filtered = filtered.filter(book => 
      book.status === statusMap[searchParams.bookStatus]
    );
  }
  
  // 按分类筛选
  if (searchParams.categoryId) {
    filtered = filtered.filter(book => 
      book.category === searchParams.categoryId
    );
  }
  
  return filtered;
});

// 计算当前页数据
const currentPageData = computed(() => {
  const start = (pagination.current - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return filteredBooks.value.slice(start, end);
});

// 更新分页总数
const updatePaginationTotal = () => {
  pagination.total = filteredBooks.value.length;
  // 如果当前页超出范围，重置到第一页
  if (pagination.current > Math.ceil(pagination.total / pagination.pageSize) && pagination.total > 0) {
    pagination.current = 1;
  }
};

const fetchReservedBooks = async () => {
  try {
    loading.value = true;
    reservedBooks.value = generateMockReservedBooks();
    updatePaginationTotal();
  } catch (error) {
    ElMessage.error('获取预约列表失败');
  } finally {
    loading.value = false;
  }
};

// 监听筛选条件变化
watch([() => searchParams.bookName, () => searchParams.bookStatus, () => searchParams.categoryId], () => {
  updatePaginationTotal();
});

// 复选框选中事件
const handleSelectionChange = (val: any[]) => {
  selectedBooks.value = val;
};

const handleSearchInput = (val: string) => {
  searchParams.bookName = val;
};

const handleCategoryChange = (val: string) => {
  searchParams.categoryId = val;
};

const handleStatusChange = (val: string) => {
  searchParams.bookStatus = val;
};

// 日期格式化
const formatDate = (dateString: string) => {
  if (!dateString) return '未知日期';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return '无效日期';
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}/${month}/${day}`;
};

const handleDetail = (book: any) => {
  router.push({
    path: '/borrow/BookBorrow/BookDetail',
    query: { id: book.id.toString() }
  });
};

const handleCancelReserve = async (book: any) => {
  try {
    await showConfirmDialog({
      title: '取消预约',
      message: `是否取消《${book.bookName}》的预约？`,
      confirmText: '确认',
      cancelText: '取消',
    });
    ElMessage.success('取消预约成功');
    // 模拟删除该条预约记录
    reservedBooks.value = reservedBooks.value.filter(item => item.id !== book.id);
    updatePaginationTotal();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消预约失败');
    }
  }
};

const getStatusClass = (status: string) => {
  const statusClassMap: { [key: string]: string } = {
    '可借阅': 'available',
    '待上架': 'pending',
    '已借光': 'out-of-stock',
    '已预约': 'reserved',
    '待发布': 'pending',
    '已借阅': 'borrowed'
  };
  return statusClassMap[status] || 'default';
};

const handlePageChange = (page: number) => {
  pagination.current = page;
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.current = 1;
};

onMounted(() => {
  fetchReservedBooks();
});
</script>

<style scoped>
.book-reserve-page {
  padding-bottom: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 80vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 25px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.filter-label {
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  flex-shrink: 0;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.loading-text {
  margin-top: 16px;
  color: #666;
  font-size: 14px;
}

.empty-container {
  padding: 60px 0;
}

.reserved-books-table {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 100%;
}

.book-info {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  width: 100%;
  padding: 8px 0;
}

.book-cover {
  width: 40px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.book-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  text-align: left;
}

.book-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  line-height: 1.4;
}

.book-author {
  font-size: 12px;
  color: #666;
}

.date-display {
  font-size: 13px;
  color: #333;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
  font-weight: 500;
  display: inline-block;
  min-width: 60px;
}

.status-badge.available {
  background-color: #67c23a;
}

.status-badge.pending {
  background-color: #409eff;
}

.status-badge.out-of-stock {
  background-color: #f56c6c;
}

.status-badge.reserved {
  background-color: #e6a23c;
}

.status-badge.borrowed {
  background-color: #909399;
}

.status-badge.default {
  background-color: #b9cbf3;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.text-button {
  color: #1890ff;
  cursor: pointer;
  font-size: 13px;
  padding: 2px 6px;
  border-radius: 3px;
  transition: all 0.2s;
}

.text-button:hover {
  background-color: #f0f7ff;
}

.text-button.cancel-btn:hover {
  color: #f56c6c;
  background-color: #fef0f0;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 10px 0;
}

/* 表格响应式适配 */
@media (max-width: 900px) {
  .search-section {
    gap: 15px;
    flex-wrap: wrap;
  }
  
  .reserved-books-table {
    overflow-x: auto;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 600px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .filter-group {
    flex: 1;
    min-width: 150px;
  }
}
</style>