<template>
  <div class="return-book-page">
    <!-- 页面标题 + 搜索筛选栏 + 批量操作按钮 -->
    <div class="page-header">
      <!-- 搜索和分类筛选栏 -->
      <div class="search-filter-group">
        <BookSearchInput
          @search="handleSearchInput"
          placeholder="请输入书籍名称"
          style="width: 200px"
        />
        <div class="filter-group">
          <span class="filter-label">书籍分类:</span>
          <BookCategorySelect
            @change="handleCategoryChange"
            style="width: 180px"
          />
        </div>
      </div>
      <!-- 批量操作按钮 -->
      <div class="batch-actions">
        <el-button
          type="warning"
          @click="handleBatchReturn"
          :disabled="selectedBooks.length === 0"
          :class="{ 'btn-disabled': selectedBooks.length === 0 }"
        >
          批量归还
        </el-button>
      </div>
    </div>
    
    <!-- 核心表格组件 (使用Table) -->
    <Table
      v-if="loading || bookList.length > 0"  
      :data="bookList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :actions="customActions"
      :show-selection="true"
      :show-index="true"
      :show-actions="true"
      :pagination="false"
      row-key="borrowId"
      :server-pagination="true"
      :parent-current-page="currentPage"
      :parent-page-size="pageSize"
      @selection-change="handleSelectionChange"
      @action-click="handleActionClick"
      class="book-table"
    >
      <!-- 书籍信息列 - 使用自定义插槽 -->
      <template #column-bookInfo="{ row }">
        <BookInfo 
          :book="{
            bookImg: row.bookInfo?.coverUrl || '',  // 关键修改
            bookName: row.bookInfo?.bookName || '未知书籍',
            author: row.bookInfo?.author || '佚名',
            translator: row.bookInfo?.translator || '',
            coverUrl: row.bookInfo?.coverUrl || ''  // 也可以直接传递整个对象
          }" 
          :showDraftIcon="false" 
        />
      </template>
      
      <!-- 分类列 - 使用自定义插槽 -->
      <template #column-category="{ row }">
        <span class="category-cell">
          {{ getCategoryName(row.bookCategory?.categoryCode) }}
        </span>
      </template>
      
      <!-- 借阅人列 - 头像放大 -->
      <template #column-userInfo="{ row }">
        <div class="user-info-cell">
          <div class="user-avatar">
            <el-avatar 
              v-if="row.userInfo?.avatar"
              :src="row.userInfo.avatar"
              size="medium"
            />
            <el-avatar 
              v-else
              size="medium"
              class="avatar-text"
            >
              {{ getAvatarText(row.userInfo?.userName || row.userInfo?.displayName || '?') }}
            </el-avatar>
          </div>
          <div class="user-info-text">
            <div class="user-name">{{ row.userInfo?.userName || row.userInfo?.displayName || row.userInfo?.uid || '未知用户' }}</div>
            <div class="user-uid" v-if="row.userInfo?.uid">UID: {{ row.userInfo.uid }}</div>
          </div>
        </div>
      </template>
      
      <!-- 归还状态列 -->
      <template #column-returnStatus="{ row }">
        <el-tag :type="getStatusTagType(row.returnConfirmStatus)">
          {{ getReturnStatusText(row.returnConfirmStatus) }}
        </el-tag>
      </template>
      
      <!-- 操作列 - 靠左排列+增大间距 -->
      <template #actions="{ row }">
        <div class="action-buttons">
          <span 
            class="action-text detail" 
            @click="handleDetail(row)"
            :title="row.bookInfo?.bookName || '详情'"
          >
            详情
          </span>
          <span 
            class="action-text return" 
            @click="handleReturn(row)"
            :class="{ 'action-disabled': row.returnConfirmStatus !== 0 }"
            :title="row.returnConfirmStatus !== 0 ? '当前状态不可操作' : '确认归还'"
          >
            确认归还
          </span>
        </div>
      </template>
    </Table>
    
    <!-- 独立分页控件 -->
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
      <el-empty description="暂无待归还书籍" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElEmpty } from "element-plus";
// 组件导入
import Table from "@/components/mytable/Table.vue";
import BookInfo from "@/components/BookInfo/BookInfo.vue";
import BookSearchInput from "@/components/BookScreen/BookSearchInput.vue";
import BookCategorySelect from "@/components/BookScreen/BookCategorySelect.vue";
import { showConfirmDialog } from "@/components/Dialog/customDialog/CustomDialog.vue";
// API导入
import { 
  getReturnBookList,
  confirmReturnBooks
} from '@/apis/Return/index';
// 类型导入
import type { 
  CurrentReturnDTO,
  GetReturnBookListParams,
  ReturnBooksParams,
  CurrentReturnListResponse
} from '@/apis/Return/type';
// 路由实例
const router = useRouter();

// 响应式数据
const loading = ref(false);
const bookList = ref<CurrentReturnDTO[]>([]);
const selectedBooks = ref<CurrentReturnDTO[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 筛选参数
const searchParams = ref({ 
  keyword: "", 
  categoryCode: "" 
});

// 分页配置
const paginationConfig = reactive({
  pageSizes: [10, 20, 30, 50],
  layout: "total, sizes, prev, pager, next, jumper" as const
});

// 表格列配置
const columns = ref([
  { 
    prop: "bookInfo", 
    label: "书籍信息", 
    width: 250, 
    align: "left" as const
  },
  { 
    prop: "category", 
    label: "分类", 
    width: 200, 
    align: "left" as const
  },
  { 
    prop: "userInfo", 
    label: "借阅人", 
    width: 220,
    align: "left" as const
  },
  { 
    prop: "returnStatus", 
    label: "归还状态", 
    width: 120, 
    align: "center" as const
  },
]);

// 自定义操作按钮（Table组件需要）
const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'return', label: '确认归还', type: 'success' }
]);

// 工具函数
const getCategoryName = (code: string | undefined): string => {
  if (!code) return '未知分类';
  
  const categoryDict: Record<string, string> = {
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
  
  return categoryDict[code] || code;
};

const formatDate = (dateStr: string | undefined): string => {
  if (!dateStr) return '未知时间';
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString("zh-CN", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit"
    }).replace(/\//g, "-");
  } catch (error) {
    return dateStr;
  }
};

const isOverdue = (expectedReturnTime: string | undefined): boolean => {
  if (!expectedReturnTime) return false;
  try {
    const dueDate = new Date(expectedReturnTime);
    const today = new Date();
    return dueDate < today;
  } catch (error) {
    return false;
  }
};

const getReturnStatusText = (status: number | undefined): string => {
  const statusMap: Record<number, string> = {
    0: "待确认",
    1: "已确认"
  };
  return statusMap[status as number] || "未知状态";
};

const getStatusTagType = (status: number | undefined): string => {
  const typeMap: Record<number, string> = {
    0: "warning",
    1: "success"
  };
  return typeMap[status as number] || "default";
};

// 获取头像文字（首字母）
const getAvatarText = (username: string): string => {
  if (!username) return '?';
  return username.charAt(0).toUpperCase();
};

// 分页事件处理
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchReturnBookList();
};

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage;
  fetchReturnBookList();
};

// 核心：获取待归还列表
const fetchReturnBookList = async () => {
  try {
    loading.value = true;
    
    const params: GetReturnBookListParams = {
      currentPage: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchParams.value.keyword.trim() || undefined,
      categoryCode: searchParams.value.categoryCode || undefined
    };
    console.log('请求参数:', params);
    
    const response: CurrentReturnListResponse = await getReturnBookList(params);
    console.log('接口返回数据:', response);
    
    if (response.code === 200 || response.code === 0) {
      bookList.value = response.data?.records || [];
      
      // 同步总条数到分页控件
      const totalFromApi = response.data?.pageInfo?.total;
      if (typeof totalFromApi === 'string') {
        total.value = parseInt(totalFromApi, 10) || 0;
      } else {
        total.value = totalFromApi || 0;
      }
    } else {
      ElMessage.error(`获取数据失败：${response.message || "接口返回错误"}`);
      bookList.value = [];
      total.value = 0;
    }
  } catch (error: any) {
    console.error('获取列表失败:', error);
    ElMessage.error(`获取数据失败：${error.message || "网络异常"}`);
    bookList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 初始化加载
onMounted(() => {
  fetchReturnBookList();
});

// 搜索和筛选事件
const handleSearchInput = (val: string) => {
  searchParams.value.keyword = val;
  currentPage.value = 1;
  fetchReturnBookList();
};

const handleCategoryChange = (val: string) => {
  searchParams.value.categoryCode = val;
  currentPage.value = 1;
  fetchReturnBookList();
};

// 选择事件
const handleSelectionChange = (selection: CurrentReturnDTO[]) => {
  selectedBooks.value = selection;
  console.log('已选择:', selection.length, '条记录');
};

// 表格操作按钮点击事件
const handleActionClick = (action: string, row: CurrentReturnDTO) => {
  switch (action) {
    case 'detail':
      handleDetail(row);
      break;
    case 'return':
      handleReturn(row);
      break;
  }
};

// 详情跳转
const handleDetail = (row: CurrentReturnDTO) => {
  if (row.bookInfo?.bookId) {
    router.push({
      path: `/borrow/BookBorrow/BookDetail/${row.bookInfo.bookId}`,
    }).catch(err => {
      console.error('跳转失败:', err);
      ElMessage.error('详情页跳转失败，请检查权限或路径');
    });
  } else {
    ElMessage.warning('缺少书籍ID，无法查看详情');
  }
};

// 单条确认归还
const handleReturn = async (row: CurrentReturnDTO) => {
  if (!row.borrowId) {
    ElMessage.warning('缺少借阅记录ID，无法归还');
    return;
  }
  
  if (row.returnConfirmStatus !== 0) {
    ElMessage.warning(`当前状态为${getReturnStatusText(row.returnConfirmStatus)}，无需重复操作`);
    return;
  }
  const confirm = await showConfirmDialog({
    title: "确认归还",
    message: `是否确认归还《${row.bookInfo?.bookName || '未知书籍'}》？`,
    confirmText: "确定",
    cancelText: "取消"
  });
  if (!confirm) return;
  try {
    const originalStatus = row.returnConfirmStatus;
    row.returnConfirmStatus = 1;
    
    const params: ReturnBooksParams = { ids: [row.borrowId] };
    const response = await confirmReturnBooks(params);
    
    if (response.code === 200 || response.code === 0) {
      ElMessage.success('确认归还成功');
      await fetchReturnBookList();
    } else {
      row.returnConfirmStatus = originalStatus;
      ElMessage.error(`归还失败：${response.message || '操作失败'}`);
    }
  } catch (error: any) {
    row.returnConfirmStatus = originalStatus;
    console.error('归还失败：', error);
    ElMessage.error(`归还失败：${error.message || '网络异常'}`);
  }
};

// 批量确认归还
const handleBatchReturn = async () => {
  if (selectedBooks.value.length === 0) {
    ElMessage.warning('请先选择需要归还的书籍');
    return;
  }
  const confirm = await showConfirmDialog({
    title: "批量确认归还",
    message: `是否确认归还选中的${selectedBooks.value.length}本书记录？`,
    confirmText: "确定",
    cancelText: "取消"
  });
  if (!confirm) return;
  try {
    const ids = selectedBooks.value
      .map(book => book.borrowId)
      .filter((id): id is number => id !== undefined);
    
    const params: ReturnBooksParams = { ids };
    const response = await confirmReturnBooks(params);
    
    if (response.code === 200 || response.code === 0) {
      ElMessage.success(`批量确认归还成功`);
      selectedBooks.value = [];
      await fetchReturnBookList();
    } else {
      ElMessage.error(`批量归还失败：${response.message || '操作失败'}`);
    }
  } catch (error: any) {
    console.error('批量归还失败：', error);
    ElMessage.error(`批量归还失败：${error.message || '网络异常'}`);
  }
};


</script>

<style scoped>
:deep(.custom-dialog .el-message-box__message) {
  line-height: 1.8 !important;
}

.return-book-page {
  padding-bottom: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 80vh;
  background: none;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.search-filter-group {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #666;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

.book-table {
  width: 100%;
}

.category-cell {
  padding: 8px 0;
}

/* 借阅人列样式 */
.user-info-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  flex-shrink: 0;
}

.avatar-text {
  background-color: #409eff;
  color: #fff;
}

.user-info-text {
  min-width: 0;
}

.user-name {
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-uid {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

/* 操作列样式 - 靠左+增大间距 */
.action-buttons {
  display: flex;
  gap: 25px; /* 间距从8px放大到25px，可按需调整 */
  justify-content: flex-start; /* 靠左排列 */
  padding-left: 10px; /* 左内边距，避免贴边 */
}

.action-text {
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s;
}

.action-text.detail {
  color: #409eff;
}

.action-text.return {
  color: #67c23a;
}

.action-text.detail:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.action-text.return:hover {
  color: #85ce61;
  text-decoration: underline;
}

.action-disabled {
  color: #c0c4cc !important;
  cursor: not-allowed !important;
  text-decoration: none !important;
}

.overdue {
  color: #f56c6c;
  font-weight: 500;
}

.btn-disabled {
  opacity: 0.7;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px; /* 保持最小高度，确保垂直居中 */
  padding-top: 100px; /* 关键：增加顶部间距，实现下移（数值可根据需求调整） */
  margin-top: 10px; /* 可选：额外增加与表格区域的间距 */
}

/* 分页容器样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 10px 0;
}

.pagination-container :deep(.el-pagination) {
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

/* 表格样式 */
:deep(.el-table) {
  --el-table-header-text-color: #333;
  --el-table-row-hover-bg-color: #f8f9fa;
  border-radius: 4px;
  overflow: hidden;
}

:deep(.el-table__cell) {
  padding: 12px 0;
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