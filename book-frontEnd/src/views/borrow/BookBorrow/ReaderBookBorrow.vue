<template>
  <div class="book-borrow-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="search-section">
        <BookSearchInput @search="handleSearchInput" style="width: 150px" />
        <div class="filter-group">
          <span class="filter-label">书籍状态:</span>
          <BookStatusSelect 
            :options="statusOptions"
            :model-value="searchParams.bookStatus"
            placeholder="所有状态"
            @update:model-value="handleStatusUpdate"
            style="width: 150px" 
          />
        </div>
        <div class="filter-group">
          <span class="filter-label">书籍分类:</span>
          <BookCategorySelect 
            :options="categoryOptions"
            :model-value="searchParams.categoryId"
            placeholder="所有分类"
            @update:model-value="handleCategoryUpdate"
            style="width: 150px"
          />
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" color="#409EFF" :size="32">
        <Loading />
      </el-icon>
      <div class="loading-text">加载中...</div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="books.length === 0" class="empty-container">
      <el-empty description="暂无可借阅的图书" />
    </div>

    <!-- 书籍网格布局 -->
    <div v-else class="books-grid">
      <div 
        v-for="book in books" 
        :key="book.id" 
        class="book-item"
      >
        <!-- 书籍卡片 -->
        <div class="book-card">
          <!-- 左侧书籍图片 -->
          <div class="book-image-section">
            <!-- 有封面时显示图片 -->
            <img 
              v-if="book.bookImg && book.bookImg.trim() !== ''" 
              class="book-img" 
              :src="book.bookImg" 
              :alt="book.bookName"
              @error="handleImgError"
            />
            <!-- 无封面时显示占位符 -->
            <div v-else class="cover-placeholder">
              <el-icon class="cover-icon"><Picture /></el-icon>
              <div class="cover-text">暂无封面</div>
            </div>
          </div>
          
          <!-- 右侧书籍信息 -->
          <div class="book-content-section">
            <!-- 标题和操作按钮 -->
            <div class="book-header">
              <div class="book-name">{{ book.bookName }}</div>
              <div class="book-actions">
                <button class="more-btn" @click="toggleMenu(book.id)">
                  ⋯
                </button>
                <div v-if="showMenuId === book.id" class="dropdown-menu">
                  <div class="menu-item" @click="handleDetail(book)">详情</div>
                  <div 
                    v-if="book.status === '可借阅' && !book.isBorrowedByCurrentUser" 
                    class="menu-item" 
                    @click="handleBorrow(book)"
                  >
                    借阅
                  </div>
                  <div 
                    v-if="['待上架', '已借光'].includes(book.status) && !book.isReservedByCurrentUser" 
                    class="menu-item" 
                    @click="handleReserve(book)"
                  >
                    预约
                  </div>
                  <div 
                    v-if="book.isReservedByCurrentUser" 
                    class="menu-item" 
                    @click="handleCancelReserve(book)"
                  >
                    取消预约
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 状态和上架时间 -->
            <div class="book-status-row">
              <span class="status-badge" :class="getStatusClass(book.status)">{{ book.status }}</span>
              <span class="shelf-time">上架时间: {{ formatDate(book.shelfTime) }}</span>
            </div>
            
            <!-- 作者 -->
            <div class="book-author">
              <span class="label">作者:</span>
              <span class="value">{{ book.author || '佚名' }}</span>
            </div>
            
            <!-- 分类 -->
            <div class="book-category">
              <span class="label">分类:</span>
              <span class="value">{{ book.category || '未分类' }}</span>
            </div>
            
            <!-- 简介 -->
            <div class="book-description">
              <span class="label">简介:</span>
              <span class="value">{{ book.description || '暂无简介' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-container" style="min-height: 40px;">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[12, 24, 36]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Loading, Picture } from '@element-plus/icons-vue';
import { getBooks, borrowBook, reserveBook, cancelReserve } from '@/apis/book';
import type { 
  BookListDTO, 
  Request as GetBooksParams,
  ActionResponse
} from '@/apis/book/type';

// 导入搜索组件
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';

// 导入自定义对话框组件
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';

import { useUserStore } from '@/store/modules/user';
const userStore = useUserStore();
// 使用路由
const router = useRouter();

// 前端书籍信息类型
interface Book {
  id: number;
  bookName: string;
  author: string;
  bookImg: string;
  translator: string;
  status: string;
  shelfTime: string;
  category: string;
  description: string;
  availableCount: number;
  totalCount: number;
  isReservedByCurrentUser: boolean;
  isBorrowedByCurrentUser: boolean;
}

interface StatusOption {
  label: string;
  value: string;
}

interface CategoryOption {
  label: string;
  value: string;
}

// 状态选项配置
const statusOptions = ref<StatusOption[]>([
  { label: '所有状态', value: '' },
  { label: '待上架', value: '2' },      // 2-待上架
  { label: '可借阅', value: '3' },      // 3-可借阅
  { label: '已借光', value: '4' }       // 4-已借光
])

// 分类选项配置
const categoryOptions = ref<CategoryOption[]>([
  { label: '所有分类', value: '' },
  { label: 'A、马克思主义、列宁主义、毛泽东思想、邓小平理论', value: 'A' },
  { label: 'B、哲学、宗教', value: 'B' },
  { label: 'C、社会科学总论', value: 'C' },
  { label: 'D、政治、法律', value: 'D' },
  { label: 'E、军事', value: 'E' },
  { label: 'F、经济', value: 'F' },
  { label: 'G、文化、科学、教育、体育', value: 'G' },
  { label: 'H、语言、文字', value: 'H' },
  { label: 'I、文学', value: 'I' },
  { label: 'J、艺术', value: 'J' },
  { label: 'K、历史、地理', value: 'K' },
  { label: 'N、自然科学总论', value: 'N' },
  { label: 'O、数理科学和化学', value: 'O' },
  { label: 'P、天文学、地球科学', value: 'P' },
  { label: 'Q、生物科学', value: 'Q' },
  { label: 'R、医药、卫生', value: 'R' },
  { label: 'S、农业科学', value: 'S' },
  { label: 'T、工业技术', value: 'T' },
  { label: 'U、交通运输', value: 'U' },
  { label: 'V、航空、航天', value: 'V' },
  { label: 'X、环境科学、安全科学', value: 'X' },
  { label: 'Z、综合性图书', value: 'Z' }
])

// 书籍状态映射 - 读者端
const BookStatusMap: { [key: number]: string } = {
  0: '未发布',   // 草稿状态
  1: '未发布',   // 未发布状态
  2: '待上架',   // 待上架
  3: '可借阅',   // 可借阅
  4: '已借光'    // 已借光
};

// 清理分类名称的函数
const cleanCategoryName = (categoryName: string): string => {
  if (!categoryName) return '';
  // 去掉"A、"这样的前缀
  return categoryName.replace(/^[A-Z]、/, '');
};

// 添加获取显示分类的函数
const getDisplayCategory = (categoryName: string | undefined): string => {
  if (!categoryName) return '未分类';
  
  // 在分类选项中查找匹配的项
  const matchedOption = categoryOptions.value.find(option => {
    const cleaned = cleanCategoryName(option.label);
    return cleaned === categoryName;
  });
  
  // 如果找到匹配的，返回完整的带字母的分类名
  if (matchedOption) {
    return matchedOption.label;
  }
  
  // 如果没有找到，返回原始名称
  return categoryName;
};

// 根据分类ID获取清理后的分类名称
const getCategoryNameById = (categoryId: string): string => {
  const category = categoryOptions.value.find(item => item.value === categoryId);
  if (!category) return '';
  
  return cleanCategoryName(category.label);
};

// 根据分类名称查找对应的categoryId
const findCategoryIdByName = (categoryName: string): string => {
  if (!categoryName) return '';
  
  const cleanedName = cleanCategoryName(categoryName);
  const foundCategory = categoryOptions.value.find(option => {
    const optionCleanedName = cleanCategoryName(option.label);
    return optionCleanedName === cleanedName || 
           option.label.includes(categoryName);
  });
  
  return foundCategory ? foundCategory.value : '';
};

// 根据用户身份获取借阅天数
const getUserBorrowDays = (): number => {
  const readerType = userStore.readerType;
  console.log('用户类型 (readerType):', readerType);

  // 根据用户类型返回借阅天数
  const borrowDaysMap: Record<string, number> = {
    'social': 15,        // 社会人员
    'student': 30,       // 学生
    'teacher': 60,       // 老师
  };

  return borrowDaysMap[readerType] ?? 0; 
};

// 响应式数据
const books = ref<Book[]>([]);
const loading = ref(false);
const showMenuId = ref<number | null>(null);

// 搜索参数
const searchParams = reactive({
  bookName: '',
  categoryId: '',
  bookStatus: ''
});

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0
});

// 转换后端数据为前端格式
const convertBookData = (bookData: BookListDTO): Book => {
  // 根据用户是否预约/借阅决定显示状态
  let status = BookStatusMap[bookData.bookStatus || 0] || '未知状态';
  
  if (bookData.isReservedByCurrentUser) {
    status = '已预约';
  } else if (bookData.isBorrowedByCurrentUser && status === '可借阅') {
    status = '已借阅';
  }
  
  // 处理封面图片 - 只检查是否为空，不检查具体路径
  const bookImg = bookData.coverUrl && bookData.coverUrl.trim() !== '' 
    ? bookData.coverUrl 
    : '';
  
  // 优先使用 category 字段，如果没有则使用 categoryName
  const rawCategory = bookData.category || bookData.categoryName;
  
  const category = getDisplayCategory(rawCategory);
  
  return {
    id: bookData.bookId || 0,
    bookName: bookData.bookName || '',
    author: bookData.author || '',
    bookImg: bookImg, 
    translator: bookData.translator || '',
    status: status,
    shelfTime: bookData.shelfTime || '',
    category: category, // 使用格式化后的分类名称
    description: bookData.intro || '',
    availableCount: bookData.availableCount || 0,
    totalCount: bookData.totalCount || 0,
    isReservedByCurrentUser: bookData.isReservedByCurrentUser || false,
    isBorrowedByCurrentUser: bookData.isBorrowedByCurrentUser || false
  };
};

// fetchBooks 函数
const fetchBooks = async () => {
  try {
    loading.value = true;
    
    // 构建查询参数
    const params: any = {
      currentPage: pagination.current,  // 使用当前页码
      pageSize: pagination.pageSize
    };
    
    if (searchParams.bookName && searchParams.bookName.trim()) {
      params.keyword = searchParams.bookName.trim();
    }
    
    if (searchParams.bookStatus) {
      params.bookStatus = parseInt(searchParams.bookStatus);
    }
    
    if (searchParams.categoryId && searchParams.categoryId !== '') {
      const categoryName = getCategoryNameById(searchParams.categoryId);
      if (categoryName && categoryName.trim()) {
        params.categoryName = categoryName.trim();
      }
    }
    
    console.log('请求参数:', params);
    
    const response = await getBooks(params) as any;
    
    if (response.code === 200) {
      const data = response.data;
      
      if (data && data.records && data.records.length > 0) {
        books.value = data.records.map(convertBookData);
        pagination.total = Number(data.total) || 0;
      } else {
        books.value = [];
        pagination.total = 0;
      }
    } else {
      ElMessage.error(response.message || '获取书籍列表失败');
      books.value = [];
      pagination.total = 0;
    }
  } catch (error: any) {
    console.error('获取书籍列表失败:', error);
    ElMessage.error(error.message || '网络错误，请重试');
    books.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
};

// 搜索处理
const handleSearch = async () => {
  pagination.current = 1;
  await fetchBooks();
};

// 分页事件处理
const handlePageChange = (page: number) => {
  pagination.current = page;
  fetchBooks();
};

const handleSizeChange = (size: number) => {
  pagination.current = 1;
  pagination.pageSize = size;
  fetchBooks();
};

// 搜索组件事件处理
const handleSearchInput = (val: string) => {
  searchParams.bookName = val;
  handleSearch();
};

const handleCategoryUpdate = (val: string) => {
  searchParams.categoryId = val;
  handleSearch();
};

const handleStatusUpdate = (val: string) => {
  searchParams.bookStatus = val;
  handleSearch();
};

// 图片加载失败处理
const handleImgError = (e: Event) => {
  const img = e.target as HTMLImageElement;
  // 隐藏图片显示占位符
  img.style.display = 'none';
  
  // 找到父元素，显示占位符
  const parent = img.parentElement;
  if (parent) {
    // 创建占位符
    const placeholder = document.createElement('div');
    placeholder.className = 'cover-placeholder';
    placeholder.innerHTML = `
      <svg class="cover-icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
        <path d="M959.877 128l0.123 0.123v767.775l-0.123 0.122H64.102l-0.122-0.122V128.123l0.122-0.123h895.775zM960 64H64C28.795 64 0 92.795 0 128v768c0 35.205 28.795 64 64 64h896c35.205 0 64-28.795 64-64V128c0-35.205-28.795-64-64-64zM832 288.01c0 53.023-42.988 96.01-96.01 96.01S639.98 341.033 639.98 288.01 682.968 192 735.99 192 832 234.988 832 288.01zM896 832H128V704l224-384 256 320h64l224-192z"/>
      </svg>
      <div class="cover-text">暂无封面</div>
    `;
    
    // 替换图片
    parent.replaceChild(placeholder, img);
  }
};

// 切换菜单显示
const toggleMenu = (bookId: number) => {
  showMenuId.value = showMenuId.value === bookId ? null : bookId;
};

// 处理详情点击
const handleDetail = (book: Book) => {
  console.log('查看详情:', book);
  showMenuId.value = null;
  
  if (!book.id) {
    ElMessage.error('书籍ID不存在');
    return;
  }
  
  router.push({
    name: 'bookDetail',
    params: {
      id: book.id.toString()
    }
  }).catch(err => {
    console.error('路由跳转失败:', err);
    // 添加错误回退
    router.push(`/borrow/BookBorrow/BookDetail/${book.id}`);
  });
};

// 处理借阅点击
const handleBorrow = async (book: Book) => {
  try {
    showMenuId.value = null;
    
    // 获取用户借阅天数
    const borrowDays = getUserBorrowDays();
    
    let message = '是否借阅书籍？';
    if (borrowDays > 0) {
      message = `是否借阅书籍？书籍可借阅天数为${borrowDays}天。`;
    } else {
      message = '是否借阅书籍？系统将根据您的身份自动设置借阅天数。';
    }
    
    const result = await showConfirmDialog({
      title: '借阅',
      message: message,
      confirmText: '确认',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          const response = await borrowBook(book.id) as any;
          
          console.log('借阅API响应:', response);
          
          if (response.code === 200) {
            ElMessage.success(response.message || '借阅成功');
            await fetchBooks(); // 重新加载数据
          } else {
            ElMessage.error(response.message || '借阅失败');
          }
        } catch (error: any) {
          console.error('借阅失败:', error);
          let errorMsg = '借阅失败，请重试';
          if (error.response?.data?.message) {
            errorMsg = error.response.data.message;
          } else if (error.message) {
            errorMsg = error.message;
          }
          ElMessage.error(errorMsg);
        }
      }
    });
    
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '借阅失败，请重试');
      console.error('借阅失败:', error);
    }
  }
};

// 处理预约点击
const handleReserve = async (book: Book) => {
  try {
    showMenuId.value = null;
    
    let messageContent = '';
    if (book.status === '待上架') {
      messageContent = '是否预约书籍？若预约成功，则书籍上架时会发送消息提醒。';
    } else if (book.status === '已借光') {
      messageContent = '是否预约书籍？若预约成功，则书籍有库存时会发送消息提醒。';
    }

    const result = await showConfirmDialog({
      title: '预约',
      message: messageContent,
      confirmText: '确认',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          // 使用类型断言 as any
          const response = await reserveBook(book.id) as any;
          
          console.log('预约API响应:', response);
          
          if (response.code === 200 || response.code === 201) {
            ElMessage.success('预约成功');
            await fetchBooks(); // 重新加载数据
          } else {
            ElMessage.error(response.message || '预约失败');
          }
        } catch (error: any) {
          console.error('预约失败:', error);
          let errorMsg = '预约失败，请重试';
          if (error.response?.data?.message) {
            errorMsg = error.response.data.message;
          } else if (error.message) {
            errorMsg = error.message;
          }
          ElMessage.error(errorMsg);
        }
      }
    });
    
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '预约失败，请重试');
      console.error('预约失败:', error);
    }
  }
};

// 处理取消预约点击
const handleCancelReserve = async (book: Book) => {
  try {
    showMenuId.value = null;
    
    const result = await showConfirmDialog({
      title: '取消预约',
      message: '是否取消预约书籍？',
      confirmText: '确认',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          // 使用类型断言 as any
          const response = await cancelReserve(book.id) as any;
          
          console.log('取消预约API响应:', response);
          
          if (response.code === 200) {
            ElMessage.success(response.message || '取消预约成功');
            await fetchBooks(); // 重新加载数据
          } else {
            ElMessage.error(response.message || '取消预约失败');
          }
        } catch (error: any) {
          console.error('取消预约失败:', error);
          let errorMsg = '取消预约失败，请重试';
          if (error.response?.data?.message) {
            errorMsg = error.response.data.message;
          } else if (error.message) {
            errorMsg = error.message;
          }
          ElMessage.error(errorMsg);
        }
      }
    });
    
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消预约失败，请重试');
      console.error('取消预约失败:', error);
    }
  }
};

// 获取状态对应的 CSS 类名
const getStatusClass = (status: string) => {
  const statusClassMap: { [key: string]: string } = {
    '可借阅': 'available',
    '待上架': 'pending',
    '已借光': 'out-of-stock',
    '已借阅': 'borrowed',
    '已预约': 'reserved',
    '未发布': 'unpublished'
  };
  return statusClassMap[status] || 'default';
};

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '--';
  
  try {
    let date: Date;
    if (dateString.includes('T')) {
      date = new Date(dateString);
    } else if (dateString.includes(' ')) {
      date = new Date(dateString.replace(' ', 'T'));
    } else {
      date = new Date(dateString);
    }
    
    if (isNaN(date.getTime())) return dateString;
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    
    return `${year}.${month}.${day}`;
  } catch {
    return dateString;
  }
};

// 点击页面其他区域关闭菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (!target.closest('.book-actions')) {
    showMenuId.value = null;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchBooks();
  document.addEventListener('click', handleClickOutside);
});
</script>

<style scoped>
  /* 样式保持不变 */
  .book-borrow-page {
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

  .search-section > :first-child {
    width: 200px !important;
    flex-shrink: 0;
  }

  .filter-group {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-shrink: 0;
  }

  .filter-group :deep(.el-select) {
    width: 150px;
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

  /* 网格布局 */
  .books-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr); /* 改为固定3列 */
    gap: 20px;
    justify-items: stretch;
    align-items: start;
    margin-bottom: 30px;
  }

  .book-item {
    display: flex;
    min-width: 0; 
  }

  .book-card {
    display: flex;
    width: 100%;
    min-width: 0;
    height: 220px; /* 保持固定高度 */
    background: white;
    border: 1px solid #e0e0e0;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 20px;
    gap: 20px; 
    position: relative;
    box-sizing: border-box;
    transition: all 0.3s ease;
    overflow: hidden;
  }

  .book-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }

  .book-image-section {
    flex-shrink: 0;
    display: flex;
    align-items: center; 
    justify-content: center; 
    width: 110px;
    height: 90%; 
    align-self: center;
  }

  /* 书籍图片 */
  .book-img {
    width: 100%;
    height: 85%;
    min-height: 100%;
    object-fit: cover;
    border-radius: 8px;
  }

  .book-content-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 6px; 
    height: 100%;
    min-width: 0; 
    overflow: hidden;
  }

  /* 封面占位符样式 */
  .cover-placeholder {
    width: 110px;
    height: 95%;
    border: 1px dashed #d9d9d9;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #8c939d;
    background-color: #fafafa;
  }

  .cover-icon {
    font-size: 32px;
    margin-bottom: 8px;
    color: #c0c4cc;
  }

  .cover-text {
    font-size: 12px;
  }

  .book-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    min-height: 32px;
  }

  .book-name {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-right: 8px;
    line-height: 1.4;
    text-align: left;
    min-width: 0; 
  }

  .book-actions {
    position: relative;
    flex-shrink: 0;
  }

  .more-btn {
    background: none;
    border: none;
    font-size: 18px;
    color: #666;
    cursor: pointer;
    padding: 0 8px;
    border-radius: 4px;
    transition: background-color 0.2s;
  }

  .more-btn:hover {
    background-color: #f5f5f5;
  }

  .dropdown-menu {
    position: absolute;
    top: 100%;
    right: 0;
    background: white;
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    z-index: 10;
    min-width: 80px;
  }

  .menu-item {
    padding: 8px 12px;
    cursor: pointer;
    font-size: 14px;
    color: #333;
    transition: background-color 0.2s;
  }

  .menu-item:hover {
    background-color: #f5f5f5;
  }

  .book-status-row {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 4px;
    flex-shrink: 0;
  }

  .status-badge {
    padding: 2px 8px;
    border-radius: 4px; 
    font-size: 12px;
    color: white;
    font-weight: 500;
    flex-shrink: 0;
  }

  .status-badge.available {
    background-color: #66d07a;
  }

  .status-badge.pending {
    background-color: #619cff;
  }

  .status-badge.out-of-stock {
    background-color: #ed4044;
  }

  .status-badge.borrowed {
    background-color: #e286f3;
  }

  .status-badge.reserved {
    background-color: #757575;
  }

  .status-badge.unpublished {
    background-color: #f3d05c;
  }

  .status-badge.default {
    background-color: #b9cbf3;
  }

  .shelf-time {
    font-size: 12px;
    color: #999;
    flex-shrink: 0;
  }

  .book-author,
  .book-category,
  .book-description {
    font-size: 12px;
    color: #666;
    line-height: 1.4;
    display: flex;
    text-align: left;
    min-width: 0; 
  }

  .label {
    flex-shrink: 0;
    margin-right: 4px;
    color: #000;
  }

  .value {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    text-align: left;
    color: #4b4949;
    min-width: 0; 
  }

  /* 简介 */
  .book-description {
    flex: 1;
    overflow: hidden;
    min-height: 0;
  }

  .book-description .value {
    display: -webkit-box;
    -webkit-line-clamp: 3; /* 严格限制最多3行 */
    -webkit-box-orient: vertical;
    overflow: hidden; /* 超出部分隐藏 */
    text-overflow: ellipsis; 
    white-space: normal; 
    text-align: left;
    line-height: 1.4; 
    max-height: calc(1.4em * 3);
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 30px;
  }

  @media (max-width: 1200px) {
    .books-grid {
      grid-template-columns: repeat(3, 1fr);
    }
    
    .book-card {
      min-width: 0;
    }
  }

  @media (max-width: 900px) {
    .books-grid {
      grid-template-columns: repeat(2, 1fr); 
      gap: 16px;
    }
  }

  @media (max-width: 600px) {
    .books-grid {
      grid-template-columns: 1fr; 
      gap: 12px;
    }
  }
</style>