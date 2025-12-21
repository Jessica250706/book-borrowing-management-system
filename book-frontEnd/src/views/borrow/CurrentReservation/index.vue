<template>
  <div class="book-reserve-page">
    <div class="page-header">
      <div class="search-section">
        <BookSearchInput @search="handleSearchInput" style="width: 150px" />
        <div class="filter-group">
          <span class="filter-label">书籍分类:</span>
          <BookCategorySelect @change="handleCategoryChange" style="width: 150px" />
        </div>
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
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column label="序号" width="70" align="center">
          <template #default="scope">
            <!-- 计算全局序号：从当前页第一条的全局索引+1 -->
            <span>{{ getGlobalIndex(scope.$index) }}</span>
          </template>
        </el-table-column>
        <!-- 找到书籍名称列的template -->
        <el-table-column label="书籍名称" min-width="200" align="left">
          <template #default="scope">
            <div class="book-info">
              <!-- 替换原有的img标签 -->
              <template v-if="scope.row.coverUrl">
                <img 
                  :src="scope.row.coverUrl" 
                  :alt="scope.row.bookName" 
                  class="book-cover" 
                />
              </template>
              <template v-else>
                <Cover />
              </template>
              
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
            <!-- 修改：使用el-tag组件，与借阅界面保持一致 -->
            <el-tag
              :type="getStatusClass(scope.row.reservationStatus)"
              effect="light"
            >
              {{ scope.row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="200" align="center">
          <template #default="scope">
            <span class="date-display">{{ formatDate(scope.row.reservationTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="失效时间" width="200" align="center">
          <template #default="scope">
            <span class="date-display">{{ formatDate(scope.row.invalidTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <span class="text-button detail-btn" @click="handleDetail(scope.row)">详情</span>
              <span 
                class="text-button cancel-btn" 
                @click="handleCancelReserve(scope.row)"
                :class="{ 'disabled-btn': !canCancel(scope.row.reservationStatus) }"
                :style="{ cursor: canCancel(scope.row.reservationStatus) ? 'pointer' : 'not-allowed' }"
              >
                取消预约
              </span>
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
// 导入组件
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';
// 在现有导入下方添加
import Cover from '@/components/BookCoverPlaceholder/cover.vue'; // 假设cover.vue路径正确，根据实际路径调整
// 导入接口和类型
import { getCurrentReserveList, cancelReserveBook } from '@/apis/Reserve/index';
import type { 
  GetCurrentReserveListParams, 
  CurrentReservationDTO,
  CancelReserveParams
} from '@/apis/Reserve/type';

const router = useRouter();
const defaultCoverImg = '/src/assets/default.jpg';
const selectedBooks = ref<any[]>([]);

// 状态选项
interface StatusOption {
  label: string;
  value: string;
}
const statusOptions = ref<StatusOption[]>([
  { label: '所有状态', value: '' },
  { label: '等待中', value: '0' },
  { label: '已确认', value: '1' },
  { label: '已取消', value: '2' },
  { label: '已过期', value: '3' }
]);

// 标准化状态字典
const RESERVE_STATUS = {
  PENDING: 0,    // 等待中
  CONFIRMED: 1,  // 已确认
  CANCELLED: 2,  // 已取消
  EXPIRED: 3     // 已过期
};
const statusDict = {
  [RESERVE_STATUS.PENDING]: '等待中',
  [RESERVE_STATUS.CONFIRMED]: '已确认',
  [RESERVE_STATUS.CANCELLED]: '已取消',
  [RESERVE_STATUS.EXPIRED]: '已过期'
};

// 响应式数据
const reservedBooks = ref<CurrentReservationDTO[]>([]);
const loading = ref(false);

// 修复：只有一个 pagination 声明
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});

// 搜索筛选参数
const searchParams = reactive({
  bookName: '',
  categoryCode: '',
  bookStatus: ''
});

// 分类字典
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

// 状态样式类名映射
const getStatusClass = (status?: number) => {
  const typeMap: Record<number, string> = {
    [RESERVE_STATUS.PENDING]: 'primary',
    [RESERVE_STATUS.CONFIRMED]: 'success',
    [RESERVE_STATUS.EXPIRED]: 'danger',
    [RESERVE_STATUS.CANCELLED]: 'warning'
  };
  return typeMap[status || 0] || 'info';
};

// 判断是否可取消
const canCancel = (status?: number) => {
  return [RESERVE_STATUS.PENDING, RESERVE_STATUS.CONFIRMED].includes(status || 0);
};

// 计算过滤后的数据 - 修复：统一状态字段名
const filteredBooks = computed(() => {
  let filtered = reservedBooks.value;
  
  // 按书名/作者搜索
  if (searchParams.bookName) {
    filtered = filtered.filter(book => 
      (book.bookName?.toLowerCase().includes(searchParams.bookName.toLowerCase()) || false) ||
      (book.author?.toLowerCase().includes(searchParams.bookName.toLowerCase()) || false)
    );
  }
  
  // 修复：按预约状态筛选，使用正确的字段名
  if (searchParams.bookStatus) {
    filtered = filtered.filter(book => 
      book.reservationStatus?.toString() === searchParams.bookStatus
    );
  }
  
  // 按分类筛选
  if (searchParams.categoryCode) {
    filtered = filtered.filter(book => 
      book.categoryCode === searchParams.categoryCode
    );
  }
  
  return filtered.map(book => {
    const validStatus = Object.values(RESERVE_STATUS).includes(book.reservationStatus || 0);
    const finalStatus = validStatus ? book.reservationStatus : RESERVE_STATUS.EXPIRED;
    return {
      ...book,
      bookId: book.bookId ? Number(book.bookId) : 0,
      category: categoryDict[book.categoryCode || ''] || book.categoryCode || '未分类',
      statusText: statusDict[finalStatus] || '未知状态',
      reservationStatus: finalStatus,
      reservationTime: book.reservationTime || 0,
      invalidTime: book.invalidTime || 0
    };
  });
});

// 计算当前页数据对应的全局序号
const getGlobalIndex = (index: number) => {
  // 全局序号 = (当前页-1) * 每页条数 + 当前行索引 + 1
  return (pagination.current - 1) * pagination.pageSize + index + 1;
};

// 修复：更新分页总数应该基于 filteredBooks
watch(filteredBooks, () => {
  pagination.total = filteredBooks.value.length;
  // 修复：如果当前页超出范围，重置到第一页
  const maxPage = Math.ceil(pagination.total / pagination.pageSize);
  if (pagination.current > maxPage && maxPage > 0) {
    pagination.current = 1;
  }
});

// 当前页数据
const currentPageData = computed(() => {
  const start = (pagination.current - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return filteredBooks.value.slice(start, end);
});

// 获取预约列表
const fetchReservedBooks = async () => {
  try {
    loading.value = true;
    const params: GetCurrentReserveListParams = {
      // 移除分页参数，让接口返回所有数据
      keyword: searchParams.bookName.trim() || undefined,
      categoryCode: searchParams.categoryCode || undefined
    };
    
    const response = await getCurrentReserveList(params);
    console.log('接口返回数据：', response.data?.records);
    
    if (response.code === 200 && response.data) {
      reservedBooks.value = response.data.records || [];
      // 注意：这里不再设置 pagination.total，由 watch(filteredBooks) 自动更新
    } else {
      ElMessage.error(`获取预约列表失败：${response.message || '接口返回异常'}`);
      reservedBooks.value = [];
    }
  } catch (error: any) {
    console.error('获取预约列表出错:', error);
    ElMessage.error('获取数据失败，请重试');
    reservedBooks.value = [];
  } finally {
    loading.value = false;
  }
};

// 分页事件处理
const handlePageChange = (page: number) => {
  pagination.current = page;
};

const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.current = 1;
};

// 监听筛选条件变化
watch([() => searchParams.bookName, () => searchParams.bookStatus, () => searchParams.categoryCode], () => {
  pagination.current = 1;
  fetchReservedBooks();
});

// 取消预约
const handleCancelReserve = async (book: CurrentReservationDTO) => {
  if (!book.bookId && book.bookId !== 0) {
    ElMessage.warning('缺少有效书籍ID，无法取消预约');
    console.log('书籍ID缺失：', book);
    return;
  }
  
  if (!canCancel(book.reservationStatus)) {
    ElMessage.info(`当前状态【${book.reservationStatus}】，无法取消预约`);
    return;
  }
  
  try {
    await showConfirmDialog({
      title: '取消预约',
      message: `是否取消《${book.bookName}》的预约？`,
      confirmText: '确认',
      cancelText: '取消',
    });
    
    const params: CancelReserveParams = { bookId: Number(book.bookId) };
    const response = await cancelReserveBook(params);
    
    if ([200, 0].includes(response.code || 0)) {
      ElMessage.success('取消预约成功');
      fetchReservedBooks();
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

// 详情跳转
const handleDetail = (book: CurrentReservationDTO) => {
  const bookId = Number(book.bookId);
  if (isNaN(bookId) || bookId <= 0) {
    ElMessage.warning('缺少有效书籍ID，无法查看详情');
    console.log('书籍ID异常：', book.bookId, '原始数据：', book);
    return;
  }
  
  try {
    router.push({
      path: `/borrow/BookBorrow/BookDetail/${bookId}`,
    }).catch(err => {
      console.error('跳转详情失败:', err);
      ElMessage.error('详情页跳转失败，请检查权限或路径');
    });
  } catch (err) {
    console.error('跳转详情异常:', err);
    ElMessage.error('详情页跳转失败，请稍后重试');
  }
};

// 日期格式化
const formatDate = (timestamp: any) => {
  if (!timestamp || isNaN(Number(timestamp))) return '未知日期';
  const date = new Date(Number(timestamp));
  if (isNaN(date.getTime())) return '无效日期';
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}/${month}/${day} ${hours}:${minutes}`;
};

// 搜索、分类、状态筛选事件
const handleSearchInput = (val: string) => {
  searchParams.bookName = val;
};

const handleCategoryChange = (val: string) => {
  searchParams.categoryCode = val;
};

const handleStatusChange = (val: string) => {
  searchParams.bookStatus = val;
};

const handleSelectionChange = (val: any[]) => {
  selectedBooks.value = val;
};

// 初始化加载数据
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
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px; /* 保持最小高度，确保垂直居中 */
  padding-top: 100px; /* 关键：增加顶部间距，实现下移（数值可根据需求调整） */
  margin-top: 10px; /* 可选：额外增加与表格区域的间距 */
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
  width: 50px;
  height: 70px;
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
/* 修复：状态徽章样式（去掉el-tag，直接用span，确保样式生效） */
.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
  font-weight: 500;
  display: inline-block;
  min-width: 60px;
}
:deep(.el-tag) {
  padding: 2px 8px;
  font-size: 12px;
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
/* 禁用状态样式 */
.disabled-btn {
  color: #c0c4cc !important;
  cursor: not-allowed !important;
}
.disabled-btn:hover {
  background-color: transparent !important;
  color: #c0c4cc !important;
}
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 10px 0;
}
/* 响应式适配 */
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