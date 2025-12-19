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
          :disabled="!hasRenewableBooks"
        >
          批量续借
        </el-button>
      </div>
    </div>
    
    <!-- 核心表格组件：禁用内置分页，与管理员页面一致 -->
    <Table 
      :data="bookList"    
      :columns="columns"
      :loading="loading"
      :total="total"
      :actions="customActions"
      :show-selection="true" 
      :show-index="true"
      :show-actions="true"
      :pagination="false"  
      row-key="id"  
      :server-pagination="true"  
      :parent-current-page="currentPage"  
      :parent-page-size="pageSize" 
      @selection-change="handleSelectionChange"
      @action-click="handleActionClick"
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
      <!-- 自定义操作列：根据条件显示续借按钮 -->
      <template #actions="{ row }">
        <div class="action-buttons">
          <span class="text-button" @click="handleDetail(row)">详情</span>
          <span class="text-button" @click="handleReturn(row)">归还</span>
          <span 
            class="text-button" 
            @click="handleReBorrow(row)"
            v-if="row.canRenew && row.renewableDays > 0"
          >
            续借
          </span>
        </div>
      </template>
    </Table>
    
    <!-- 独立分页控件：强制显示并确保样式正确 -->
    <div class="pagination-container" v-show="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="paginationConfig.pageSizes"
        :layout="paginationConfig.layout"
        :total="Number(total)"  
        :hide-on-single-page="false"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 如果总条数为0，显示无数据提示 -->
    <div v-if="!loading && bookList.length === 0" class="empty-state">
      <el-empty description="暂无借阅记录" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Table from '@/components/mytable/Table.vue';
import BookInfo from '@/components/BookInfo/BookInfo.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import { ElMessage, ElEmpty } from 'element-plus';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';

// 导入API
import { getCurrentBorrowList } from '@/apis/Borrowv/index';
import { returnBooks, renewBooks } from '@/apis/Borrowv/index';

// 类型导入
import type { CurrentBorrowDTO } from '@/apis/Borrowv/type';
import type { GetCurrentBorrowListParams } from '@/apis/Borrowv/type';

const router = useRouter();

// 状态字典映射
const statusDict = {
  0: '借阅中',
  1: '已归还',
  2: '已超时',
  3: '归还待确认'
};

// 分类字典映射（完整匹配分类选项）
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

// 响应式数据
const loading = ref(false);
const bookList = ref<CurrentBorrowDTO[]>([]);  // 存储当前页数据
const selectedBooks = ref<CurrentBorrowDTO[]>([]);
const currentPage = ref(1);  // 当前页码
const pageSize = ref(10);    // 每页条数
const total = ref(0);        // 总条数

// 筛选参数
const filterForm = reactive<{
  keyword: string;
  categoryCode: string;
}>({
  keyword: '',
  categoryCode: ''
});

// 表格列配置
const columns = ref([
  { prop: 'bookInfo', label: '书籍信息', width: 250, align: 'left' as const },
  { prop: 'category', label: '分类', width: 200, align: 'left' as const },
  { prop: 'remainDays', label: '剩余借阅时间', width: 140, align: 'center' as const },
  { prop: 'dueDate', label: '最晚归还日期', width: 160, align: 'center' as const },
  { prop: 'renewableDays', label: '可续借天数', width: 100, align: 'center' as const },
  { prop: 'status', label: '状态', width: 100, align: 'center' as const },
]);

// 自定义操作按钮（Table组件需要）
const customActions = ref([]);

// 分页配置
const paginationConfig = reactive({
  pageSizes: [10, 20, 30, 50],
  layout: "total, sizes, prev, pager, next, jumper" as const
});

// 计算属性：是否有可续借的书籍
const hasRenewableBooks = computed(() => {
  return selectedBooks.value.some(book => book.canRenew && book.renewableDays && book.renewableDays > 0);
});

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

// 分页事件处理：切换分页时需要重新获取数据
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize;
  currentPage.value = 1; // 切换页大小时回到第一页
  fetchCurrentBorrowList();
};

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage;
  fetchCurrentBorrowList(); // 切换页码时需要重新获取数据
};

// 选择事件处理
const handleSelectionChange = (selection: CurrentBorrowDTO[]) => {
  selectedBooks.value = selection;
};

// 搜索和筛选处理
const handleSearchInput = (val: string) => {
  filterForm.keyword = val.trim();
  currentPage.value = 1;
  fetchCurrentBorrowList();
};

const handleCategoryChange = (val: string) => {
  filterForm.categoryCode = val;
  currentPage.value = 1;
  fetchCurrentBorrowList();
};

// 表格操作按钮点击事件（Table组件必需）
const handleActionClick = (action: string, row: CurrentBorrowDTO) => {
  switch (action) {
    case 'detail':
      handleDetail(row);
      break;
    case 'return':
      handleReturn(row);
      break;
    case 'borrow':
      handleReBorrow(row);
      break;
  }
};

// 获取当前借阅列表数据
const fetchCurrentBorrowList = async () => {
  try {
    loading.value = true;
    const params: GetCurrentBorrowListParams = {
      currentPage: currentPage.value,  // 传递当前页码给后端
      pageSize: pageSize.value,        // 传递每页条数给后端
      keyword: filterForm.keyword || undefined,
      categoryCode: filterForm.categoryCode || undefined
    };
    
    const response = await getCurrentBorrowList(params);
    if (response && response.code === 200 && response.data) {
      const records = response.data.records || [];
      
      // 数据处理：确保字段匹配
      const processedRecords = records.map((book: any) => ({
        ...book,
        id: book.id,  // 与row-key="id"对应
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
      
      // 存储当前页的数据（后端已经做了分页）
      bookList.value = processedRecords;
      
      // 同步总条数到分页控件
      const totalFromApi = response.data.pageInfo?.total;
      if (typeof totalFromApi === 'string') {
        total.value = parseInt(totalFromApi, 10) || 0;
      } else {
        total.value = totalFromApi || 0;
      }
      
    } else {
      ElMessage.error('获取借阅列表失败');
      bookList.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取借阅列表出错:', error);
    ElMessage.error('网络错误，获取借阅列表失败');
    bookList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 操作处理函数
const handleDetail = (row: CurrentBorrowDTO) => {
  if (!row.bookId) {
    ElMessage.error('书籍ID不存在');
    return;
  }
  
  router.push({
    name: 'bookDetail',
    params: { id: row.bookId.toString() }
  }).catch(err => {
    console.error('路由跳转失败:', err);
    router.push(`/borrow/BookBorrow/BookDetail/${row.bookId}`);
  });
};

// 单个归还
const handleReturn = async (row: CurrentBorrowDTO) => {
  if (!row.id) {
    ElMessage.error('借阅记录ID不存在');
    return;
  }
  
  const isConfirm = await showConfirmDialog({
    title: '归还确认',
    message: `确定要归还《${row.bookName}》吗？`,
    confirmText: '确定',
    cancelText: '取消'
  });
  
  if (!isConfirm) return;
  
  try {
    const response = await returnBooks({ ids: [row.id] });
    if (response.code === 200) {
      ElMessage.success(`《${row.bookName}》归还申请已提交`);
      fetchCurrentBorrowList();
    } else {
      ElMessage.error(`归还失败：${response.message || '操作失败'}`);
    }
  } catch (error) {
    console.error('归还出错：', error);
    ElMessage.error('网络错误，归还失败');
  }
};

// 单个续借
const handleReBorrow = async (row: CurrentBorrowDTO) => {
  if (!row.id) {
    ElMessage.error('借阅记录ID不存在');
    return;
  }
  
  if (!row.canRenew || (row.renewableDays || 0) <= 0) {
    ElMessage.warning('该书不可续借');
    return;
  }
  
  const message = `
    确定要续借《${row.bookName}》吗？<br>
    可续借天数：${row.renewableDays}天
  `;
  
  const isConfirm = await showConfirmDialog({
    title: '续借确认',
    message,
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
    选中书籍：${selectedBookNames}<br>
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
    book.id && book.canRenew && book.renewableDays && book.renewableDays > 0
  );
  
  const cannotRenewBooks = selectedBooks.value.filter(book => 
    !book.id || !book.canRenew || !book.renewableDays || book.renewableDays <= 0
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
    选中书籍：${selectedBookNames}<br>
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

// 初始化加载数据
onMounted(() => {
  fetchCurrentBorrowList();
});
</script>

<style scoped>
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

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

/* 剩余时间状态色 */
.overdue {
  color: #f56c6c;
  font-weight: 500;
}

.normal {
  color: #67c23a;
}

.category-cell {
  padding-left: 10px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.text-button {
  color: #409EFF;
  cursor: pointer;
}

.text-button:hover {
  text-decoration: underline;
}

/* 分页容器样式：完全复用管理员页面样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 10px 0;
}

/* 关键：覆盖可能影响分页组件的样式 */
.pagination-container :deep(.el-pagination) {
  /* 确保分页组件正常显示 */
  font-size: 14px;
}

.pagination-container :deep(.el-pagination__total),
.pagination-container :deep(.el-pagination__jump) {
  margin-right: 10px;
}

.pagination-container :deep(.el-pagination .btn-prev),
.pagination-container :deep(.el-pagination .btn-next) {
  border: 1px solid #dcdfe6;
  border-radius: 3px;
}

.pagination-container :deep(.el-pager li) {
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  margin: 0 4px;
}

.pagination-container :deep(.el-pager li.active) {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

/* 表格样式：对齐管理员页面 */
:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
}

:deep(.el-table .cell) {
  padding: 0 12px;
}

:deep(.el-table__header .cell) {
  text-align: center;
}

:deep(.el-table th:last-child .cell) {
  text-align: left;
  padding-left: 22px;
}

/* 分类列左对齐 */
:deep(.el-table .el-table__cell:has(.category-cell)) {
  text-align: left;
}
</style>