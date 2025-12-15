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
        >
          批量归还
        </el-button>
      </div>
    </div>

    <!-- 核心表格组件 -->
    <BookTable
      ref="tableRef"
      :data="filteredBookList"
      :columns="columns"
      :total="pagination.total"
      :actions="customActions"
      :current-page="pagination.currentPage"
      :page-size="pagination.pageSize"
      @selection-change="handleSelectionChange"
      @current-page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
      :show-selection="true"
      :show-index="true"
      :loading="loading"
      :empty-text="loading ? '加载中...' : '暂无待归还书籍'"
    >
      <!-- 自定义列渲染 -->
      <template #column-bookInfo="{ row }">
        <BookInfo :book="row" />
      </template>
      <template #column-userInfo="{ row }">
        <UserInfo :user="row.user" />
      </template>
      <template #actions="{ row }">
        <div class="action-buttons">
          <span class="text-button" @click="handleDetail(row)">详情</span>
          <span class="text-button" @click="handleReturn(row)">归还</span>
        </div>
      </template>
    </BookTable>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import BookTable from "@/components/mytable/Table.vue";
import BookInfo from "@/components/BookInfo/BookInfo.vue";
import BookSearchInput from "@/components/BookScreen/BookSearchInput.vue";
import BookCategorySelect from "@/components/BookScreen/BookCategorySelect.vue";
import UserInfo from "@/components/UserInfo/UserInfo.vue";
import { showConfirmDialog } from "@/components/Dialog/customDialog/CustomDialog.vue";
import { ref, computed, onMounted, reactive } from "vue";
import { ElMessage } from "element-plus";

import { getReturnBookList, returnBooks } from '@/apis/Return/index';
import type { 
  CurrentReturnDTO, 
  ReturnBooksParams, 
  GetReturnBookListParams
} from '@/apis/Return/type';

// 路由实例
const router = useRouter();

// 状态管理
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});
// 修复：参数名与接口保持一致（使用categoryCode）
const searchParams = ref({ keyword: "", categoryCode: "" });
const selectedBooks = ref<CurrentReturnDTO[]>([]);
const loading = ref(false);
const bookList = ref<CurrentReturnDTO[]>([]);
const tableRef = ref<InstanceType<typeof BookTable> | null>(null);
const fetchError = ref('');

// 字典配置
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

const statusDict = {
  0: '借阅中',
  1: '已归还',
  2: '已超时',
  3: '归还待确认'
};

// 筛选后的列表
const filteredBookList = computed(() => {
  if (!bookList.value.length) return [];
  return bookList.value.filter((book) => {
    const matchKeyword = book.bookInfo?.bookName?.includes(searchParams.value.keyword.trim()) || false;
    const matchCategory = !searchParams.value.categoryCode || book.bookCategory?.categoryCode === searchParams.value.categoryCode;
    return matchKeyword && matchCategory;
  });
});

// 修复：删减多余列，保留核心列
const columns = ref([
  { prop: "bookInfo", label: "书籍信息", width: 280, align: "left" },
  { prop: "category", label: "分类", width: 200, align: "left" },
  { prop: "userInfo", label: "借阅人", width: 150, align: "left" },
]);

const customActions = ref([
  { name: "detail", label: "详情", type: "primary" },
  { name: "return", label: "归还", type: "warning" },
]);

// 分页处理
const handlePageChange = (page: number) => {
  pagination.currentPage = page;
  fetchReturnBookList();
};

const handlePageSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.currentPage = 1;
  fetchReturnBookList();
};

// 核心：获取待归还列表（修复接口调用问题）
const fetchReturnBookList = async () => {
  try {
    loading.value = true;
    fetchError.value = '';
    
    const params: GetReturnBookListParams = {
      currentPage: pagination.currentPage,
      pageSize: pagination.pageSize,
      keyword: searchParams.value.keyword.trim() || '',
      categoryCode: searchParams.value.categoryCode || ''
    };

    const response = await getReturnBookList(params);
    console.log('接口响应数据:', response); // 调试用

    const data = response.data || { records: [], pageInfo: { total: 0 } };
    const records = data.records || [];
    pagination.total = data.pageInfo?.total || 0;

    if (response.code === 200) {
      // 数据格式化（适配CurrentReturnDTO结构）
      bookList.value = records.map((item: CurrentReturnDTO) => ({
        ...item,
        category: categoryDict[item.bookCategory?.categoryCode as keyof typeof categoryDict] || 
                  item.bookCategory?.categoryName || '未分类',
        status: statusDict[item.borrowStatus || 0],
        user: {
          username: item.userInfo?.userName,
          realName: item.userInfo?.displayName || item.userInfo?.userName,
          avatarUrl: item.userInfo?.avatar
        }
      }));
    } else {
      fetchError.value = `获取失败：${response.message || '接口返回异常'}`;
      bookList.value = [];
    }
  } catch (error: any) {
    console.error('获取列表出错:', error);
    fetchError.value = `获取失败：${error.message || '网络异常'}`;
    bookList.value = [];
  } finally {
    loading.value = false;
  }
};

// 初始化
onMounted(() => {
  fetchReturnBookList();
});

// 事件处理
const handleSelectionChange = (val: CurrentReturnDTO[]) => {
  selectedBooks.value = val;
};

const handleSearchInput = (val: string) => {
  searchParams.value.keyword = val;
  pagination.currentPage = 1;
  fetchReturnBookList();
};

// 修复：参数名同步为categoryCode
const handleCategoryChange = (val: string) => {
  searchParams.value.categoryCode = val;
  pagination.currentPage = 1;
  fetchReturnBookList();
};

const handleDetail = (row: CurrentReturnDTO) => {
  if (row.bookInfo?.bookId) {
    router.push({
      path: "/borrow/BookBorrow/BookDetail",
      query: { id: row.bookInfo.bookId.toString() }
    });
  } else {
    ElMessage.warning('缺少书籍ID，无法查看详情');
  }
};

const handleReturn = async (row: CurrentReturnDTO) => {
  if (!row.borrowId) {
    ElMessage.warning('缺少借阅记录ID，无法归还');
    return;
  }

  const confirm = await showConfirmDialog({
    title: "归还书籍",
    message: `是否确认归还《${row.bookInfo?.bookName || '未知书籍'}》？借阅人：${row.userInfo?.displayName || '未知用户'}`,
    confirmText: "确定",
    cancelText: "取消"
  });

  if (!confirm) return;

  try {
    const response = await returnBooks({ ids: [row.borrowId] } as ReturnBooksParams);
    if (response.code === 200) {
      ElMessage.success(`《${row.bookInfo?.bookName || '未知书籍'}》已确认归还`);
      fetchReturnBookList();
    } else {
      ElMessage.error(`归还失败：${response.message || '操作失败'}`);
    }
  } catch (error: any) {
    console.error('归还失败：', error);
    ElMessage.error(`归还失败：${error.message || '网络异常'}`);
    bookList.value = bookList.value.filter(item => item.borrowId !== row.borrowId);
  }
};

const handleBatchReturn = async () => {
  const count = selectedBooks.value.length;
  if (count === 0) {
    ElMessage.warning('请先选择需要归还的书籍');
    return;
  }

  const bookNames = selectedBooks.value.map(book => 
    `《${book.bookInfo?.bookName || '未知书籍'}》`
  ).join('、');
  
  const confirm = await showConfirmDialog({
    title: "批量归还",
    message: `确认归还以下${count}本书籍？<br/>${bookNames}`,
    confirmText: "确定",
    cancelText: "取消",
    dangerouslyUseHTMLString: true,
  });

  if (!confirm) return;

  try {
    const ids = selectedBooks.value
      .map(book => book.borrowId)
      .filter(Boolean) as number[];
      
    const response = await returnBooks({ ids } as ReturnBooksParams);
    
    if (response.code === 200) {
      ElMessage.success(`成功归还${count}本书籍`);
      selectedBooks.value = [];
      fetchReturnBookList();
    } else {
      ElMessage.error(`批量归还失败：${response.message || '操作失败'}`);
    }
  } catch (error: any) {
    console.error('批量归还失败：', error);
    ElMessage.error(`批量归还失败：${error.message || '网络异常'}`);
    const returnedIds = selectedBooks.value
      .map(book => book.borrowId)
      .filter(Boolean);
    bookList.value = bookList.value.filter(item => !returnedIds.includes(item.borrowId));
    selectedBooks.value = [];
  }
};
</script>

<style scoped>
.return-book-page {
  padding: 0 2px 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
  max-width: 1400px; /* 与“图书借阅”页的最大宽度一致 */
  margin: 0 auto; /* 居中显示，确保左右留白均匀 */
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.search-filter-group {
  display: flex;
  align-items: center;
  gap: 16px;
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
}

.batch-actions {
  display: flex;
  gap: 8px;
}


/* 表格样式 */
:deep(.el-table) {
  --el-table-header-text-color: #303133;
  --el-table-row-hover-bg-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f8f9fa !important;
  font-weight: 600;
}

.category-cell {
  padding: 8px 12px;
  text-align: left;
}

.action-buttons {
  display: flex;
  gap: 12px;
  padding-left: 8px;
}

.text-button {
  color: #1890ff;
  cursor: pointer;
  font-size: 14px;
  padding: 2px 4px;
  border-radius: 2px;
}

.text-button:hover {
  text-decoration: underline;
  background-color: #e8f4ff;
}

/* 响应式适配 */
@media (max-width: 992px) {
  .search-filter-group {
    gap: 8px;
  }
}
</style>