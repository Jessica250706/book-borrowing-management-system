<template>
  <div class="borrow-book-page">
    <!-- 页面标题 + 搜索筛选栏 + 批量操作按钮 -->
    <div class="page-header">
      <!-- 搜索和分类筛选组件 -->
      <div class="search-filter-group">
        <BookSearchInput 
          @search="handleSearchInput" 
          placeholder="请输入书籍名称" 
          style="width: 200px" 
        />
        <div class="filter-group">
          <span class="filter-label">书籍分类:</span>
          <BookCategorySelect @change="handleCategoryChange" style="width: 180px" />
        </div>
      </div>
      <!-- 批量操作按钮 -->
      <div class="batch-actions">
        <el-button 
          type="warning" 
          @click="handleBatchReturn" 
          :disabled="selectedBooks.length === 0"
        >
          批量归还
        </el-button>
        <el-button 
          type="success" 
          @click="handleBatchReBorrow" 
          :disabled="selectedBooks.length === 0"
        >
          批量续借
        </el-button>
      </div>
    </div>
    <!-- 核心表格组件 -->
    <SimpleTable 
      :data="bookList"    
      :columns="columns" 
      :total="pagination.total"
      :current-page="pagination.currentPage"
      :page-size="pagination.pageSize"
      :actions="customActions"
      pagination-mode="backend"
      @selection-change="handleSelectionChange"
      @size-change="handlePageSizeChange"
      @current-change="handleCurrentPageChange"
      :show-selection="true" 
      :show-index="true"
      :loading="loading"
      :show-pagination="true"
    >
      <!-- 自定义书籍信息列：使用 BookInfo 组件 -->
      <template #column-bookInfo="{ row }">
        <BookInfo :book="row" />
      </template>
      <!-- 自定义剩余时间列：添加状态色 -->
      <template #column-remainDays="{ row }">
        <span :class="getRemainTimeClass(row.remainDays)">
          {{ row.remainDays > 0 ? `${row.remainDays}天` : `已超时${-row.remainDays}天` }}
        </span>
      </template>
      <!-- 自定义状态列：使用标签样式 -->
      <template #column-status="{ row }">
        <el-tag
          :type="getStatusType(row.borrowStatus)"
          effect="light"
        >
          {{ row.status }}
        </el-tag>
      </template>
      <!-- 自定义分类列：靠左显示并留空隙 -->
      <template #column-category="{ row }">
        <div class="category-cell">
          {{ row.category }}
        </div>
      </template>
      <!-- 自定义操作列：靠左显示 -->
       <template #actions="{ row }">
        <div class="action-buttons">
          <span class="text-button" @click="handleDetail(row)">详情</span>
          <span class="text-button" @click="handleReturn(row)">归还</span>
          <span class="text-button" @click="handleReBorrow(row)"
          :disabled="!row.canRenew || row.remainDays < 0"
          :title="!row.canRenew ? row.cannotRenewReason : ''">续借</span>
        </div>
      </template>
    </SimpleTable>
  </div>
</template>

<script setup lang="ts">
// 导入路由相关依赖
import { useRouter } from 'vue-router';
import SimpleTable from '@/components/mytable/SimpleTable.vue'; 
import BookInfo from '@/components/BookInfo/BookInfo.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';
import { ref, computed, onMounted, reactive } from 'vue';
import { ElMessage, ElTag } from 'element-plus';

// 导入接口和类型定义
import { 
  getCurrentBorrowList, 
  returnBooks, 
  renewBooks  
} from '@/apis/Borrowv/index';
import type { 
  CurrentBorrowDTO, 
  ReturnBooksParams,
  GetCurrentBorrowListParams 
} from '@/apis/Borrowv/type';
import type { BaseResponse } from '@/apis/Borrowv/type';

// 初始化路由实例
const router = useRouter();

// 分页相关
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 搜索筛选参数
const searchParams = ref({ 
  keyword: '', 
  category: ''
});

// 选中的书籍（用于批量操作）
const selectedBooks = ref<CurrentBorrowDTO[]>([]);
const loading = ref(false);

// 书籍列表数据
const bookList = ref<CurrentBorrowDTO[]>([]);

// 表格列配置
const columns = ref([
  { prop: 'bookInfo', label: '书籍信息', width: 250, align: 'left' },
  { prop: 'category', label: '分类', width: 180, align: 'left' },
  { prop: 'remainDays', label: '剩余借阅时间', width: 140, align: 'center' },
  { prop: 'dueDate', label: '最晚归还日期', width: 160, align: 'center' },
  { prop: 'renewableDays', label: '可续借天数', width: 100, align: 'center' },
  { prop: 'status', label: '状态', width: 100, align: 'center' },
]);

// 自定义操作按钮配置
const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'return', label: '归还', type: 'warning' },
  { name: 'borrow', label: '续借', type: 'success' },
]);

// 剩余时间状态类
const getRemainTimeClass = (remainDays?: number) => {
  if (remainDays === undefined) return '';
  if (remainDays < 0) return 'overdue';
  if (remainDays > 0) return 'normal';
  return '';
};

// 状态标签类型映射
const getStatusType = (status?: number) => {
  const typeMap: Record<number, string> = {
    0: 'primary',
    1: 'success',
    2: 'danger',
    3: 'warning'
  };
  return typeMap[status || 0] || 'info';
};

// 分页事件处理
const handlePageSizeChange = (size: number) => {
  console.log('index.vue: handlePageSizeChange', size);
  pagination.pageSize = size;
  pagination.currentPage = 1;
  fetchCurrentBorrowList();
};

const handleCurrentPageChange = (page: number) => {
  console.log('index.vue: handleCurrentPageChange', page);
  pagination.currentPage = page;
  fetchCurrentBorrowList();
};

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

// 状态字典
const statusDict = {
  0: '借阅中',
  1: '已归还',
  2: '已超时',
  3: '归还待确认'
};

// 获取借阅列表数据
const fetchCurrentBorrowList = async () => {
  try {
    loading.value = true;
    const params: GetCurrentBorrowListParams = {
      currentPage: pagination.currentPage,
      pageSize: pagination.pageSize,
      keyword: searchParams.value.keyword.trim() || undefined,
      categoryCode: searchParams.value.category || undefined
    };
    
    console.log('fetchCurrentBorrowList: 请求参数', params); 
    const response = await getCurrentBorrowList(params);
    
    console.log('fetchCurrentBorrowList: API响应', response);

    if (response && response.code === 200 && response.data) {
      // 处理数据
      const records = response.data.records || [];
      bookList.value = records.map((book: any) => ({
        ...book,
        id: book.id,
        bookId: book.bookId,
        category: categoryDict[book.categoryCode] || book.categoryCode || '未分类',
        dueDate: book.latestReturnTime ? book.latestReturnTime.split('T').join(' ') : '暂无',
        remainDays: book.remainingDays || 0,
        status: statusDict[book.borrowStatus] || `未知状态(${book.borrowStatus})`,
        borrowStatus: book.borrowStatus,
        bookImg: book.bookCover || '',
        bookName: book.bookName || '未知书籍',
        author: book.author || '未知作者',
        canRenew: book.operations?.includes('renew') || false,
        cannotRenewReason: book.operations?.includes('renew') ? '' : '已超过续借次数或书籍已逾期',
        renewableDays: book.renewableDays || 0
      }));
      
      // 设置总条数
      pagination.total = response.data.pageInfo?.total || 0;
      
      console.log('fetchCurrentBorrowList: 处理后数据', {
        当前页数据条数: bookList.value.length,
        总条数: pagination.total,
        当前页码: pagination.currentPage,
        每页条数: pagination.pageSize,
        总页数: Math.ceil(pagination.total / pagination.pageSize)
      });
    } else {
      ElMessage.error(`获取借阅列表失败：${response?.message || '接口返回异常'}`);
      bookList.value = [];
      pagination.total = 0;
    }
  } catch (error: any) {
    console.error('获取借阅列表出错:', error);
    ElMessage.error('获取数据失败，请重试');
    bookList.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
};

// 初始化加载数据
onMounted(() => {
  console.log('index.vue: onMounted');
  fetchCurrentBorrowList();
});

// 搜索输入事件
const handleSearchInput = (val: string) => {
  console.log('index.vue: handleSearchInput', val);
  searchParams.value.keyword = val;
  pagination.currentPage = 1;
  fetchCurrentBorrowList();
};

// 分类切换事件
const handleCategoryChange = (val: string) => {
  console.log('index.vue: handleCategoryChange', val);
  searchParams.value.category = val;
  pagination.currentPage = 1;
  fetchCurrentBorrowList();
};

// 表格多选事件
const handleSelectionChange = (val: CurrentBorrowDTO[]) => {
  console.log('index.vue: handleSelectionChange', val.length);
  selectedBooks.value = val;
};

// 单条详情
const handleDetail = (row: CurrentBorrowDTO) => {
  if (row.bookId) {
    router.push({
      path: `/borrow/BookBorrow/BookDetail/${row.bookId}`,
    }).catch(err => {
      console.error('跳转失败:', err);
      ElMessage.error('详情页跳转失败，请检查权限或路径');
    });
  } else {
    ElMessage.warning('缺少书籍ID，无法查看详情');
  }
};

// 单条归还
const handleReturn = async (row: CurrentBorrowDTO) => {
  if (!row.id) return;
  const isConfirm = await showConfirmDialog({
    title: '归还书籍',
    message: `是否归还书籍《${row.bookName}》？`,
    confirmText: '确定',
    cancelText: '取消'
  });
  if (!isConfirm) return;
  try {
    const response = await returnBooks({ ids: [row.id] });
    if (response.code === 200) {
      ElMessage.success(`成功归还《${row.bookName}》`);
      fetchCurrentBorrowList(); // 重新获取列表
    } else {
      ElMessage.error(`归还失败：${response.message || '操作失败'}`);
    }
  } catch (error) {
    console.error('归还出错：', error);
    ElMessage.error('网络错误，归还失败');
  }
};

// 单条续借
const handleReBorrow = async (row: CurrentBorrowDTO) => {
  if (!row.id) return;
  
  if (!row.canRenew) {
    ElMessage.error(`《${row.bookName}》不可续借：${row.cannotRenewReason}`);
    return;
  }
  const isConfirm = await showConfirmDialog({
    title: '续借书籍',
    message: `是否续借书籍《${row.bookName}》？剩余可续借天数为${row.renewableDays}天。`,
    confirmText: '确定',
    cancelText: '取消',
    dangerouslyUseHTMLString: true
  });
  if (!isConfirm) return;
  try {
    const response = await renewBooks({ ids: [row.id] });
    if (response.code === 200) {
      ElMessage.success(`成功续借《${row.bookName}》，可续借${row.renewableDays}天`);
      fetchCurrentBorrowList();
    } else {
      ElMessage.error(`续借失败：${response.message || '操作失败'}`);
    }
  } catch (error) {
    console.error('续借出错：', error);
    ElMessage.error('网络错误，续借失败');
  }
};

// 批量归还
const handleBatchReturn = async () => {
  if (selectedBooks.value.length === 0) return;
  const count = selectedBooks.value.length;
  const selectedBookNames = selectedBooks.value.map(book => `《${book.bookName}》`).join('、');
  const message = `
    选中书籍：${selectedBookNames}
    是否归还这${count}本书籍？
  `;
  const isConfirm = await showConfirmDialog({
    title: '批量归还',
    message,
    confirmText: '确定',
    cancelText: '取消',
    dangerouslyUseHTMLString: true
  });
  if (!isConfirm) return;
  try {
    const ids = selectedBooks.value.map(book => book.id).filter(Boolean) as number[];
    const response = await returnBooks({ ids });
    
    if (response.code === 200) {
      ElMessage.success(`成功归还${count}本书籍`);
      selectedBooks.value = [];
      fetchCurrentBorrowList();
    } else {
      ElMessage.error(`批量归还失败：${response.message || '操作失败'}`);
    }
  } catch (error) {
    console.error('批量归还出错：', error);
    ElMessage.error('网络错误，批量归还失败');
  }
};

// 批量续借
const handleBatchReBorrow = async () => {
  if (selectedBooks.value.length === 0) return;
  const canRenewBooks = selectedBooks.value.filter(book => 
    book.id && book.canRenew
  );
  const cannotRenewBooks = selectedBooks.value.filter(book => 
    !book.id || !book.canRenew
  );
  
  if (canRenewBooks.length === 0) {
    ElMessage.warning('选中的书籍均不可续借');
    return;
  }
  
  const selectedBookNames = selectedBooks.value.map(book => `《${book.bookName}》`).join('、');
  const cannotRenewTip = cannotRenewBooks.length > 0 
    ? `<br>不可续借书籍：${cannotRenewBooks.map(book => `《${book.bookName}》`).join('、')}` 
    : '';
  const message = `
    选中书籍：${selectedBookNames}
    是否续借这${canRenewBooks.length}本书籍？${cannotRenewTip}
  `;
  const isConfirm = await showConfirmDialog({
    title: '批量续借',
    message,
    confirmText: '确定',
    cancelText: '取消',
    dangerouslyUseHTMLString: true
  });
  if (!isConfirm) return;
  try {
    const ids = canRenewBooks.map(book => book.id) as number[];
    const response = await renewBooks({ ids });
    
    if (response.code === 200) {
      ElMessage.success(`成功续借${canRenewBooks.length}本书籍`);
      if (cannotRenewBooks.length > 0) {
        ElMessage.warning(`有${cannotRenewBooks.length}本书籍不可续借`);
      }
      selectedBooks.value = [];
      fetchCurrentBorrowList();
    } else {
      ElMessage.error(`批量续借失败：${response.message || '操作失败'}`);
    }
  } catch (error) {
    console.error('批量续借出错：', error);
    ElMessage.error('网络错误，批量续借失败');
  }
};
</script>

<style scoped>
/* 原有样式完全保留 */
:deep(.custom-dialog .el-message-box__message) {
  line-height: 1.8 !important;
}
.borrow-book-page {
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
  flex-wrap: wrap;
  gap: 15px;
}
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}
.batch-actions {
  display: flex;
  gap: 10px;
}
.search-filter-group {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}
.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}
.filter-label {
  font-size: 14px;
  color: #303133;
  white-space: nowrap;
}
/* 剩余时间状态色 - 调整规则 */
.overdue {
  color: #f56c6c;
  font-weight: 500;
}
.normal {
  color: #67c23a;
}
/* 分类列样式：左对齐并留空隙 */
.category-cell {
  padding: 8px 12px;
  text-align: left;
}
/* 操作按钮样式：靠左显示 */
.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-start;
  padding-left: 10px;
}
.text-button {
  color: #1890ff;
  cursor: pointer;
  font-size: 14px;
  margin-right: 8px;
  padding: 2px 4px;
}
.text-button:hover {
  text-decoration: underline;
  background-color: #f0f7ff;
  border-radius: 2px;
}
/* 表格样式调整 */
:deep(.el-table) {
  --el-table-header-text-color: #303133;
  --el-table-row-hover-bg-color: #F0F0F0;
  border-radius: 8px;
  overflow: hidden;
  background-color: #FFFFFF;
}
:deep(.el-table th) {
  background-color: #FAFAFA !important;
  font-weight: 600;
  border-bottom: 1px solid #EEEEEE;
}
/* 状态标签样式 */
:deep(.el-tag) {
  padding: 2px 8px;
  font-size: 12px;
}
/* 响应式适配 */
@media (max-width: 900px) {
  .search-filter-group {
    gap: 10px;
  }
}
</style>