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
    <!-- 核心表格组件 (使用SimpleTable) -->
    <SimpleTable
      ref="tableRef"
      :data="bookList"
      :columns="columns"
      :total="pagination.total"
      :current-page="pagination.currentPage"
      :page-size="pagination.pageSize"
      :loading="loading"
      :show-selection="true"
      :show-index="true"
      :show-pagination="true"
      :empty-text="loading ? '加载中...' : '暂无待归还书籍'"
      row-key="borrowId"
      @selection-change="handleSelectionChange"
      @current-change="handlePageChange"
      @size-change="handlePageSizeChange"
      class="book-table"
    >
      <!-- 书籍信息列 - 使用自定义插槽 -->
      <template #column-bookInfo="{ row }">
        <BookInfo :book="row.bookInfo || {}" :showDraftIcon="false" />
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
    </SimpleTable>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from "vue-router";
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
// 组件导入
import SimpleTable from "@/components/mytable/SimpleTable.vue";
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
// 状态管理
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});
const searchParams = ref({ 
  keyword: "", 
  categoryCode: "" 
});
const selectedBooks = ref<CurrentReturnDTO[]>([]);
const loading = ref(false);
const bookList = ref<CurrentReturnDTO[]>([]);
const tableRef = ref<InstanceType<typeof SimpleTable> | null>(null);
// 表格列配置 - 操作列宽度适配间距
const columns = ref([
  { 
    prop: "bookInfo", 
    label: "书籍信息", 
    width: 200, 
    align: "left",
    slot: true
  },
  { 
    prop: "category", 
    label: "分类", 
    width: 200, 
    align: "center",
    slot: true
  },
  { 
    prop: "userInfo", 
    label: "借阅人", 
    width: 220,
    align: "left",
    slot: true
  },
  { 
    prop: "returnStatus", 
    label: "归还状态", 
    width: 120, 
    align: "center",
    slot: true
  },
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
// 核心：获取待归还列表
const fetchReturnBookList = async () => {
  try {
    loading.value = true;
    
    const params: GetReturnBookListParams = {
      currentPage: pagination.currentPage,
      pageSize: pagination.pageSize,
      keyword: searchParams.value.keyword.trim() || undefined,
      categoryCode: searchParams.value.categoryCode || undefined
    };
    console.log('请求参数:', params);
    
    const response: CurrentReturnListResponse = await getReturnBookList(params);
    console.log('接口返回数据:', response);
    
    if (response.code === 200 || response.code === 0) {
      bookList.value = response.data?.records || [];
      pagination.total = response.data?.pageInfo?.total || 0;
    } else {
      ElMessage.error(`获取数据失败：${response.message || "接口返回错误"}`);
      bookList.value = [];
    }
  } catch (error: any) {
    console.error('获取列表失败:', error);
    ElMessage.error(`获取数据失败：${error.message || "网络异常"}`);
    bookList.value = [];
  } finally {
    loading.value = false;
  }
};
// 初始化加载
onMounted(() => {
  fetchReturnBookList();
});
// 分页事件
const handlePageChange = (page: number) => {
  pagination.currentPage = page;
  fetchReturnBookList();
};
const handlePageSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.currentPage = 1;
  fetchReturnBookList();
};
// 搜索和筛选事件
const handleSearchInput = (val: string) => {
  searchParams.value.keyword = val;
  pagination.currentPage = 1;
  fetchReturnBookList();
};
const handleCategoryChange = (val: string) => {
  searchParams.value.categoryCode = val;
  pagination.currentPage = 1;
  fetchReturnBookList();
};
// 选择事件
const handleSelectionChange = (selection: CurrentReturnDTO[]) => {
  selectedBooks.value = selection;
  console.log('已选择:', selection.length, '条记录');
};
// 详情跳转
const handleDetail = (row: CurrentReturnDTO) => {
  if (row.bookInfo?.bookId) {
    router.push({
      path: `/borrow/BookBorrow/BookDetail/${row.bookInfo.bookId}`,
    }).catch(err => {  // 建议加上错误捕获
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
/* 表格样式 */
:deep(.el-table) {
  --el-table-header-text-color: #333;
  --el-table-row-hover-bg-color: #f8f9fa;
}
:deep(.el-table__cell) {
  padding: 12px 0;
}
</style>