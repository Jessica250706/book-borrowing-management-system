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
</style>