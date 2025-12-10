<template>
  <div class="borrow-book-page">
    <div class="page-header">
      <h2>当前借阅</h2>
      <div class="operation-buttons">
        <el-button 
          type="primary" 
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

    <!-- 搜索和筛选区域 -->
    <div class="search-bar">
      <el-input
        v-model="searchParams.keyword"
        placeholder="搜索书名..."
        clearable
        @change="handleSearchInput"
        style="width: 300px"
      />
      <el-select
        v-model="searchParams.category"
        placeholder="选择分类"
        clearable
        @change="handleCategoryChange"
        style="width: 200px; margin-left: 10px"
      >
        <el-option
          v-for="category in bookCategories"
          :key="category"
          :label="category"
          :value="category"
        />
      </el-select>
    </div>

    <!-- 借阅列表表格 -->
    <el-table
      :data="filteredBookList"
      border
      stripe
      :loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="bookName" label="书名" width="200">
        <template #default="scope">
          <div class="book-info">
            <img 
              :src="scope.row.bookCover || '默认封面路径'" 
              alt="封面" 
              class="book-cover"
              @error="handleImgError"
            >
            <span>{{ scope.row.bookName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="bookAuthor" label="作者" width="120" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="remainingDays" label="剩余天数" width="120">
        <template #default="scope">
          <span :class="getRemainTimeClass(scope.row.remainingDays)">
            {{ scope.row.remainingDays >= 0 ? `${scope.row.remainingDays}天` : `逾期${-scope.row.remainingDays}天` }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="latestReturnTime" label="最晚归还日期" width="160" />
      <el-table-column prop="renewableDays" label="可续借天数" width="120" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button 
            size="small" 
            text 
            @click="handleDetail(scope.row)"
          >
            详情
          </el-button>
          <el-button 
            size="small" 
            text 
            type="primary"
            @click="handleReturn(scope.row)"
            :disabled="!scope.row.operations?.includes('return')"
          >
            归还
          </el-button>
          <el-button 
            size="small" 
            text 
            type="success"
            @click="handleReBorrow(scope.row)"
            :disabled="!scope.row.operations?.includes('renew')"
          >
            续借
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      @size-change="handlePageSizeChange"
      @current-change="handlePageChange"
      :current-page="pagination.currentPage"
      :page-sizes="[10, 20, 50]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      style="margin-top: 20px; text-align: right"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  getCurrentBorrowList, 
  getRemainingRenewDays,
  returnBooks, 
  renewBooks  // 导入续借接口
} from '@/apis/Borrowv/index';
import type { 
  CurrentBorrowDTO, 
  CurrentBorrowResponse,
  ReturnBooksParams 
} from '@/apis/Borrowv/type';
import type { BaseResponse } from '@/apis/Commonw/type';

// 路由实例
const router = useRouter();

// 分类列表
const bookCategories = [
  '经济', '医学', '历史', '自然科学', '军事', '散文', '文学', '地理'
];

// 搜索参数
const searchParams = reactive({
  keyword: '',
  category: ''
});
// 借阅列表数据
const bookList = ref<CurrentBorrowDTO[]>([]);
const selectedBooks = ref<CurrentBorrowDTO[]>([]);
const loading = ref(false);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 筛选后的列表
const filteredBookList = computed(() => {
  return bookList.value.filter(book => {
    const matchKeyword = book.bookName?.includes(searchParams.keyword.trim()) || false;
    const matchCategory = !searchParams.category || book.category === searchParams.category;
    return matchKeyword && matchCategory;
  });
});

// 获取当前借阅列表数据
const fetchCurrentBorrowList = async () => {
  try {
    loading.value = true;
    const response = await getCurrentBorrowList({
      current: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchParams.keyword
    });
    
    if (response.data.code === 0 && response.data.data) {
      bookList.value = response.data.data.records || [];
      pagination.total = response.data.data.pageInfo?.total || 0;
    } else {
      ElMessage.error('获取借阅列表失败');
    }
  } catch (error) {
    console.error('获取借阅列表出错:', error);
    ElMessage.error('获取数据失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 页面加载时获取数据
onMounted(() => {
  fetchCurrentBorrowList();
});


// 表格选择事件
const handleSelectionChange = (val: CurrentBorrowDTO[]) => {
  selectedBooks.value = val;
};

// 搜索和筛选事件
const handleSearchInput = () => {
  pagination.currentPage = 1;
  fetchCurrentBorrowList();
};

const handleCategoryChange = () => {
  pagination.currentPage = 1;
  fetchCurrentBorrowList();
};

// 分页事件
const handlePageChange = (page: number) => {
  pagination.currentPage = page;
  fetchCurrentBorrowList();
};

const handlePageSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.currentPage = 1;
  fetchCurrentBorrowList();
};

// 图片加载失败处理
const handleImgError = (e: Event) => {
  const img = e.target as HTMLImageElement;
  img.src = 'https://picsum.photos/100/140?random=default'; // 默认封面
};

// 查看详情
const handleDetail = (row: CurrentBorrowDTO) => {
  router.push({
    path: '/currentBorrow/BookDetail',  // 修改为当前文件夹下的路径
    query: { id: row.id?.toString() }
  });
};

// 单本归还
const handleReturn = async (row: CurrentBorrowDTO) => {
  if (!row.id) return;

  const confirm = await ElMessageBox.confirm(
    `是否归还书籍《${row.bookName}》？`,
    '归还确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).catch(() => false);

      if (confirm) {
      try {
        const axiosResponse = await returnBooks({ ids: [row.id] });
        const response = axiosResponse.data as BaseResponse;
        if (response.code === 200) {
          ElMessage.success(`成功归还《${row.bookName}》`);
          fetchCurrentBorrowList(); // 刷新列表
        } else {
          ElMessage.error('归还失败：' + (response.message || '操作失败'));
        }
      } catch (error) {
        console.error('归还出错：', error);
        ElMessage.error('网络错误，归还失败');
      }
    }
};

// 单本续借
const handleReBorrow = async (row: CurrentBorrowDTO) => {
  if (!row.id) return;
  if (!row.operations?.includes('renew')) {
    ElMessage.error(`《${row.bookName}》不可续借`);
    return;
  }

  const confirm = await ElMessageBox.confirm(
    `是否续借《${row.bookName}》？可续借${row.renewableDays}天`,
    '续借确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).catch(() => false);

  if (confirm) {
    try {
      const axiosResponse = await renewBooks({ ids: [row.id] });
      const response = axiosResponse.data as BaseResponse; // 提取 data 属性并转换为 BaseResponse 类型
      if (response.code === 200) {
        ElMessage.success(`成功续借《${row.bookName}》`);
        fetchCurrentBorrowList(); // 刷新列表
      } else {
        ElMessage.error('续借失败：' + (response.message || '操作失败'));
      }
    } catch (error) {
      console.error('续借出错：', error);
      ElMessage.error('网络错误，续借失败');
    }
  }
};

// 批量归还
const handleBatchReturn = async () => {
  if (selectedBooks.value.length === 0) return;

  const bookNames = selectedBooks.value.map(book => `《${book.bookName}》`).join('、');
  const confirm = await ElMessageBox.confirm(
    `是否归还以下书籍：${bookNames}`,
    '批量归还确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true
    }
  ).catch(() => false);

  if (confirm) {
    try {
      const ids = selectedBooks.value.map(book => book.id).filter(Boolean) as number[];
      const response = await returnBooks({ ids });
      const baseResponse = response.data as BaseResponse<any>;
      if (baseResponse.code === 200) {
        ElMessage.success(`成功归还${selectedBooks.value.length}本书籍`);
        selectedBooks.value = [];
        fetchCurrentBorrowList(); // 刷新列表
      } else {
        ElMessage.error('批量归还失败：' + (baseResponse.message || '操作失败'));
      }
    } catch (error) {
      console.error('批量归还出错：', error);
      ElMessage.error('网络错误，批量归还失败');
    }
  }
};

// 批量续借
const handleBatchReBorrow = async () => {
  if (selectedBooks.value.length === 0) return;

  // 筛选可续借和不可续借的书籍
  const canRenewBooks = selectedBooks.value.filter(book => 
    book.id && book.operations?.includes('renew')
  );
  const cannotRenewBooks = selectedBooks.value.filter(book => 
    !book.id || !book.operations?.includes('renew')
  );

  // 构建提示信息
  const bookNames = selectedBooks.value.map(book => `《${book.bookName}》`).join('、');
  let message = `选中书籍：${bookNames}`;
  
  if (cannotRenewBooks.length > 0) {
    const cannotRenewNames = cannotRenewBooks.map(book => `《${book.bookName}》`).join('、');
    message += `<br>不可续借书籍：${cannotRenewNames}`;
  }

  const confirm = await ElMessageBox.confirm(
    message,
    '批量续借确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info',
      dangerouslyUseHTMLString: true
    }
  ).catch(() => false);

  if (confirm && canRenewBooks.length > 0) {
    try {
      const ids = canRenewBooks.map(book => book.id) as number[];
      const response = await renewBooks({ ids });
      const baseResponse = response.data as BaseResponse;

      
      if (baseResponse.code === 200) {
        ElMessage.success(`成功续借${canRenewBooks.length}本书籍`);
        if (cannotRenewBooks.length > 0) {
          ElMessage.warning(`有${cannotRenewBooks.length}本书籍不可续借`);
        }
        selectedBooks.value = [];
        fetchCurrentBorrowList(); // 刷新列表
      } else {
        ElMessage.error('批量续借失败：' + (baseResponse.message || '操作失败'));
      }
    } catch (error) {
      console.error('批量续借出错：', error);
      ElMessage.error('网络错误，批量续借失败');
    }
  }
};

// 剩余时间样式
const getRemainTimeClass = (remainingDays?: number) => {
  if (remainingDays === undefined) return '';
  if (remainingDays < 0) return 'text-red-500'; // 逾期
  if (remainingDays <= 7) return 'text-orange-500'; // 即将到期
  return 'text-green-500'; // 正常
};
</script>

<style scoped>
.borrow-book-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 80vh;
}

.page-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.book-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.book-cover {
  width: 50px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
}

.operation-buttons {
  display: flex;
  gap: 10px;
}
<<<<<<< HEAD
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
/* 剩余时间状态色 */
.overdue {
  color: #f56c6c;
  font-weight: 500;
}
.warning {
  color: #e6a23c;
  font-weight: 500;
}
.normal {
  color: #67c23a;
}
/* 操作按钮样式 */
:deep(.el-button--small) {
  padding: 6px 14px;
  margin: 0 6px;
  border-radius: 4px;
  font-size: 13px;
}
:deep(.el-button--primary.el-button--small) {
  background-color: #1890FF;
  border-color: #1890FF;
}
:deep(.el-button--primary.el-button--small):hover {
  background-color: #096DD9;
  border-color: #096DD9;
}
:deep(.el-button--warning.el-button--small) {
  background-color: #FAAD14;
  border-color: #FAAD14;
}
:deep(.el-button--warning.el-button--small):hover {
  background-color: #FF9C07;
  border-color: #FF9C07;
}
:deep(.el-button--success.el-button--small) {
  background-color: #52C41A;
  border-color: #52C41A;
}
:deep(.el-button--success.el-button--small):hover {
  background-color: #4CAF50;
  border-color: #4CAF50;
}
:deep(.el-button--success.el-button--small.is-disabled) {
  background-color: #F0F0F0;
  border-color: #DDDDDD;
  color: #AAAAAA;
}
/* 表格样式 */
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
/* 响应式适配 */
@media (max-width: 900px) {
  .search-filter-group {
    gap: 10px;
  }
}
/* 蓝色文字按钮样式 */
.text-button {
  color: #1890ff; /* 标准蓝色 */
  cursor: pointer;
  font-size: 14px;
  margin-right: 16px; /* 按钮间距 */
  padding: 2px 4px; /* 增大点击区域 */
}
.text-button:hover {
  text-decoration: underline; /*  hover下划线效果 */
  background-color: #f0f7ff; /* 轻微背景色变化 */
  border-radius: 2px;
}
</style>

<!-- <script setup lang="ts">
import { useRouter } from 'vue-router';
import Table from '@/components/mytable/Table.vue';
import BookInfo from '@/components/BookInfo/BookInfo.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios'; // 导入axios

const router = useRouter();

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const totalBooks = ref(0); // 新增：总条数
// 搜索筛选参数
const searchParams = ref({ 
  keyword: '', 
  category: '',
  author: '', // 新增：作者筛选
  bookStatus: 3 // 新增：状态筛选（3表示上架可借阅已借）
});
const selectedBooks = ref<any[]>([]);
const bookList = ref<any[]>([]); // 改为从接口获取数据

// 新增：API请求函数
const fetchBookList = async () => {
  try {
    const response = await axios.get('/api/book/list', {
      params: {
        currentPage: currentPage.value,
        pageSize: pageSize.value,
        bookName: searchParams.value.keyword,
        categoryName: searchParams.value.category,
        author: searchParams.value.author,
        bookStatus: searchParams.value.bookStatus
      },
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}` // 从本地存储获取token
      }
    });

    const { code, data, message } = response.data;
    if (code === 0) {
      // 转换接口返回数据格式以适配前端需求
      bookList.value = data.records.map((book: any) => ({
        id: book.bookId,
        bookImg: book.coverUrl,
        bookName: book.bookName,
        author: book.author,
        translator: book.translator,
        category: book.category,
        status: mapBookStatus(book.bookStatus), // 状态映射
        shelfTime: book.shelfTime,
        dueDate: book.dueDate || '', // 假设接口返回此字段
        remainDays: book.remainDays || 0, // 假设接口返回此字段
        renewableDays: book.renewableDays || 0, // 假设接口返回此字段
        canRenew: book.canRenew || false, // 假设接口返回此字段
        cannotRenewReason: book.cannotRenewReason || '' // 假设接口返回此字段
      }));
      totalBooks.value = data.total; // 更新总条数
    } else {
      ElMessage.error(`获取书籍列表失败: ${message}`);
    }
  } catch (error) {
    console.error('请求书籍列表出错:', error);
    ElMessage.error('网络错误，无法获取书籍列表');
  }
};

// 新增：书籍状态映射函数
const mapBookStatus = (status: number) => {
  const statusMap = {
    0: '草稿未发布',
    1: '保存未发布',
    2: '发布未上架',
    3: '上架可借阅已借',
    4: '已经借光'
  };
  return statusMap[status] || '未知状态';
};

// 初始化时加载数据
onMounted(() => {
  fetchBookList();
});

// 筛选后的书籍列表（保持不变）
const filteredBookList = computed(() => {
  return bookList.value.filter(book => {
    const matchKeyword = book.bookName.includes(searchParams.value.keyword.trim());
    const matchCategory = !searchParams.value.category || book.category === searchParams.value.category;
    return matchKeyword && matchCategory;
  });
});

// 表格列配置（保持不变）
const columns = ref([
  { prop: 'bookInfo', label: '书籍信息', width: 320, align: 'left' },
  { prop: 'category', label: '分类', width: 120, align: 'center' },
  { prop: 'remainDays', label: '剩余借阅时间', width: 140, align: 'center' },
  { prop: 'dueDate', label: '最晚归还日期', width: 180, align: 'center' },
  { prop: 'renewableDays', label: '可续借天数', width: 120, align: 'center' },
  { prop: 'status', label: '状态', width: 120, align: 'center' },
  { prop: 'shelfTime', label: '借阅时间', width: 160, align: 'center' },
]);

// 搜索输入事件（添加重新加载逻辑）
const handleSearchInput = (val: string) => {
  searchParams.value.keyword = val;
  currentPage.value = 1; // 重置页码
  fetchBookList(); // 重新请求数据
};

// 分类切换事件（添加重新加载逻辑）
const handleCategoryChange = (val: string) => {
  searchParams.value.category = val;
  currentPage.value = 1; // 重置页码
  fetchBookList(); // 重新请求数据
};

// 分页变化处理（新增）
const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchBookList();
};

// 其他方法（handleDetail、handleReturn等）保持不变，但需要注意：
// 1. 实际项目中，归还和续借操作也需要调用对应的后端接口
// 2. 这里仅展示列表获取部分的联调，其他操作需根据实际接口进行补充

// 单条归还：实际项目中需要调用后端接口
const handleReturn = async (row: any) => {
  const isConfirm = await showConfirmDialog({
    title: '归还书籍',
    message: `是否归还书籍《${row.bookName}》？`,
    confirmText: '确定',
    cancelText: '取消',
    onConfirm: async () => {
      try {
        // 调用归还接口
        await axios.post('/api/book/return', { bookId: row.id }, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        // 刷新列表
        fetchBookList();
        ElMessage.success(`成功归还《${row.bookName}》`);
      } catch (error) {
        console.error('归还书籍失败:', error);
        ElMessage.error('归还书籍失败，请重试');
      }
    }
  });
  if (!isConfirm) return;
};

// 单条续借：实际项目中需要调用后端接口
const handleReBorrow = async (row: any) => {
  // 原有验证逻辑保持不变
  if (!row.canRenew) {
    ElMessage.error(`《${row.bookName}》不可续借：${row.cannotRenewReason}`);
    return;
  }

  const isConfirm = await showConfirmDialog({
    title: '续借书籍',
    message: `是否续借书籍《${row.bookName}》？剩余可续借天数为${row.renewableDays}天。`,
    confirmText: '确定',
    cancelText: '取消',
    dangerouslyUseHTMLString: true,
    onConfirm: async () => {
      try {
        // 调用续借接口
        await axios.post('/api/book/renew', { bookId: row.id }, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        // 刷新列表
        fetchBookList();
        ElMessage.success(`成功续借《${row.bookName}》`);
      } catch (error) {
        console.error('续借书籍失败:', error);
        ElMessage.error('续借书籍失败，请重试');
      }
    }
  });
  if (!isConfirm) return;
};

// 批量操作方法类似，都需要调用后端接口并刷新列表
</script> -->
=======
</style>
>>>>>>> release/v1.0
