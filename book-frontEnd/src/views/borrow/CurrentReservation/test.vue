<template>
  <!-- 原有布局完全保留，仅修改数据渲染逻辑 -->
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
              <!-- 适配接口返回的coverUrl字段 -->
              <img :src="scope.row.coverUrl || defaultCoverImg" :alt="scope.row.bookName" class="book-cover" />
              <div class="book-text">
                <span class="book-name">{{ scope.row.bookName }}</span>
                <span class="book-author">作者: {{ scope.row.author || '未知作者' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="200" align="center" prop="category" />
        <el-table-column label="状态" width="150" align="center">
          <template #default="scope">
            <span class="status-badge" :class="getStatusClass(scope.row.reservationStatus)">
              {{ scope.row.statusText }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="200" align="center">
          <template #default="scope">
            <span class="date-display">
              {{ formatDate(scope.row.reservationTime) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="失效时间" width="200" align="center">
          <template #default="scope">
            <span class="date-display">
              {{ formatDate(scope.row.invalidTime) }}
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
// 导入组件（原有不变）
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';
// 导入正确的接口和类型
import { getCurrentReserveList, cancelReserveBook } from '@/apis/Reserve/index';
import type { 
  GetCurrentReserveListParams, 
  CurrentReservationDTO 
} from '@/apis/Reserve/type';

const router = useRouter();
const defaultCoverImg = '/src/assets/default.jpg'; // 默认封面图

// 复选框选中数据（原有不变）
const selectedBooks = ref<any[]>([]);
interface StatusOption {
  label: string;
  value: string;
}
// 状态选项：对齐接口的reservationStatus（0-等待中，1-已确认，2-已取消，3-已过期）
const statusOptions = ref<StatusOption[]>([
  { label: '所有状态', value: '' },
  { label: '等待中', value: '0' },
  { label: '已确认', value: '1' },
  { label: '已取消', value: '2' },
  { label: '已过期', value: '3' }
]);

// 响应式数据（类型改为接口返回的CurrentReservationDTO）
const reservedBooks = ref<CurrentReservationDTO[]>([]);
const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});
// 搜索筛选参数（对齐接口的Query参数）
const searchParams = reactive({
  bookName: '',       // 对应keyword（书名/作者搜索）
  categoryCode: '',   // 分类编码（接口的categoryCode）
  bookStatus: ''      // 预约状态（接口的reservationStatus）
});

// 分类字典：带字母前缀（和图书借阅保持一致）
const categoryDict = {
  'A': 'A、马克思主义、列宁主义、毛泽东思想、邓小平理论',
  'B': 'B、哲学、宗教',
  'C': 'C、社会科学总论',
  'D': 'D、政治、法律',
  'E': 'E、军事',
  'F': 'F、经济',
  'G': 'G、文化、科学、教育、体育',
  'H': 'H、语言、文字',
  'I': 'I、文学',
  'J': 'J、艺术',
  'K': 'K、历史、地理',
  'N': 'N、自然科学总论',
  'O': 'O、数理科学和化学',
  'P': 'P、天文学、地球科学',
  'Q': 'Q、生物科学',
  'R': 'R、医药、卫生',
  'S': 'S、农业科学',
  'T': 'T、工业技术',
  'U': 'U、交通运输',
  'V': 'V、航空、航天',
  'X': 'X、环境科学、安全科学',
  'Z': 'Z、综合性图书'
};

// 预约状态字典（对齐接口的reservationStatus）
const statusDict = {
  0: '等待中',
  1: '已确认',
  2: '已取消',
  3: '已过期'
};

// 状态样式映射（和图书借阅保持一致）
const getStatusClass = (status?: number) => {
  const statusClassMap: { [key: number]: string } = {
    0: 'pending',    // 等待中 - 蓝色
    1: 'available',  // 已确认 - 绿色
    2: 'default',    // 已取消 - 灰色
    3: 'out-of-stock'// 已过期 - 红色
  };
  return statusClassMap[status || 0] || 'default';
};

// 计算过滤后的数据（适配接口返回字段）
const filteredBooks = computed(() => {
  let filtered = reservedBooks.value;
  
  // 按书名/作者搜索（对接接口的keyword参数）
  if (searchParams.bookName) {
    filtered = filtered.filter(book => 
      (book.bookName?.toLowerCase().includes(searchParams.bookName.toLowerCase()) || false) ||
      (book.author?.toLowerCase().includes(searchParams.bookName.toLowerCase()) || false)
    );
  }
  
  // 按预约状态筛选（对接接口的reservationStatus）
  if (searchParams.bookStatus) {
    filtered = filtered.filter(book => 
      book.reservationStatus?.toString() === searchParams.bookStatus
    );
  }
  
  // 按分类筛选（对接接口的categoryCode）
  if (searchParams.categoryCode) {
    filtered = filtered.filter(book => 
      book.categoryCode === searchParams.categoryCode
    );
  }
  
  // 处理分类显示和状态文本（适配页面渲染）
  return filtered.map(book => ({
    ...book,
    // 分类：带字母前缀
    category: categoryDict[book.categoryCode || ''] || book.categoryCode || '未分类',
    // 状态文本：数字转文字
    statusText: statusDict[book.reservationStatus || 0] || '未知状态'
  }));
});

// 计算当前页数据（原有逻辑不变）
const currentPageData = computed(() => {
  const start = (pagination.current - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return filteredBooks.value.slice(start, end);
});

// 从正确接口获取预约列表（核心修改）
const fetchReservedBooks = async () => {
  try {
    loading.value = true;
    // 构建接口要求的Query参数
    const params: GetCurrentReserveListParams = {
      currentPage: pagination.current,
      pageSize: pagination.pageSize,
      keyword: searchParams.bookName.trim() || undefined,
      categoryCode: searchParams.categoryCode || undefined
    };
    
    // 调用正确的列表接口
    const response = await getCurrentReserveList(params);
    if (response.code === 200 && response.data) {
      reservedBooks.value = response.data.records || [];
      // 从接口获取总条数（适配分页）
      pagination.total = response.data.pageInfo?.total || 0;
    } else {
      ElMessage.error(`获取预约列表失败：${response.message || '接口返回异常'}`);
      reservedBooks.value = [];
      pagination.total = 0;
    }
  } catch (error: any) {
    console.error('获取预约列表出错:', error);
    ElMessage.error('获取数据失败，请重试');
    reservedBooks.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
};

// 监听筛选条件变化（重置页码并重新请求）
watch([() => searchParams.bookName, () => searchParams.bookStatus, () => searchParams.categoryCode], () => {
  pagination.current = 1;
  fetchReservedBooks();
});

// 取消预约（对接正确接口）
const handleCancelReserve = async (book: CurrentReservationDTO) => {
  if (!book.bookId) {
    ElMessage.warning('缺少书籍ID，无法取消预约');
    return;
  }
  
  try {
    await showConfirmDialog({
      title: '取消预约',
      message: `是否取消《${book.bookName}》的预约？`,
      confirmText: '确认',
      cancelText: '取消',
    });
    
    // 调用取消预约接口（Path传bookId）
    const response = await cancelReserveBook({ bookId: book.bookId });
    if (response.code === 200 || response.code === 0) {
      ElMessage.success('取消预约成功');
      fetchReservedBooks(); // 重新获取列表
    } else {
      ElMessage.error(`取消预约失败：${response.message || '操作失败'}`);
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消预约出错:', error);
      ElMessage.error('网络错误，取消预约失败');
    }
  }
};

// 分页事件（原有不变）
const handlePageChange = (page: number) => {
  pagination.current = page;
  fetchReservedBooks();
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.current = 1;
  fetchReservedBooks();
};

// 搜索、分类、状态筛选事件（适配新参数名）
const handleSearchInput = (val: string) => {
  searchParams.bookName = val;
};

const handleCategoryChange = (val: string) => {
  searchParams.categoryCode = val;
};

const handleStatusChange = (val: string) => {
  searchParams.bookStatus = val;
};

// 详情跳转（原有不变）
const handleDetail = (book: any) => {
  if (book.bookId) {
    router.push({
      path: '/borrow/BookBorrow/BookDetail',
      query: { id: book.bookId.toString() }
    });
  } else {
    ElMessage.warning('缺少书籍ID，无法查看详情');
  }
};

// 复选框选中事件（原有不变）
const handleSelectionChange = (val: any[]) => {
  selectedBooks.value = val;
};

// 日期格式化（修复类型错误，适配接口返回的时间格式）
const formatDate = (dateString: any) => {
  if (!dateString || typeof dateString !== 'string') return '未知日期';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return '无效日期';
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}/${month}/${day} ${hours}:${minutes}`;
};

// 初始化加载数据
onMounted(() => {
  fetchReservedBooks();
});
</script>
<style scoped>
/* 原有样式完全保留，仅补充状态徽章样式 */
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

/* 状态徽章样式（和图书借阅保持一致） */
.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
  font-weight: 500;
  display: inline-block;
  min-width: 60px;
}
.status-badge.pending {
  background-color: #409eff; /* 等待中 - 蓝色 */
}
.status-badge.available {
  background-color: #67c23a; /* 已确认 - 绿色 */
}
.status-badge.out-of-stock {
  background-color: #f56c6c; /* 已过期 - 红色 */
}
.status-badge.default {
  background-color: #909399; /* 已取消 - 灰色 */
}
</style>