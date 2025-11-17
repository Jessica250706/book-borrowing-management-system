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
            :model-value="filterForm.status"
            placeholder="所有状态"
            @change="handleStatusChange"
            @update:model-value="handleStatusUpdate"
            style="width: 150px" 
          />
        </div>
        <div class="filter-group">
          <span class="filter-label">书籍分类:</span>
          <BookCategorySelect @change="handleCategoryChange" style="width: 150px" />
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
            <img 
              class="book-img" 
              :src="book.bookImg" 
              :alt="book.bookName"
              @error="handleImgError"
            />
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
                    v-if="book.status === '可借阅'" 
                    class="menu-item" 
                    @click="handleBorrow(book)"
                  >
                    借阅
                  </div>
                  <div 
                    v-if="['待上架', '已借光'].includes(book.status)" 
                    class="menu-item" 
                    @click="handleReserve(book)"
                  >
                    预约
                  </div>
                  <div 
                    v-if="book.status === '已预约'" 
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
    <div v-if="books.length > 0" class="pagination-container">
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
  import { ElMessage } from 'element-plus';
  import { Loading } from '@element-plus/icons-vue';
  import { getBooks, borrowBook, reserveBook, cancelReserve, searchBooks } from '@/apis/book';
  import type { 
    Response, 
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

  // 状态选项配置
  const statusOptions = ref<StatusOption[]>([
    { label: '所有状态', value: '' },
    { label: '待上架', value: '待上架' },
    { label: '已预约', value: '已预约' },
    { label: '可借阅', value: '可借阅' },
    { label: '已借光', value: '已借光' },
    { label: '已借阅', value: '已借阅' }
  ])

  // 书籍状态映射
  const BookStatusMap: { [key: number]: string } = {
    0: '待上架',
    1: '已预约', 
    2: '可借阅',
    3: '已借光',
    4: '已借阅'
  };

  const handleStatusUpdate = (val: string) => {
    filterForm.status = val
  }

  // 响应式数据
  const books = ref<Book[]>([]);
  const loading = ref(false);
  const showMenuId = ref<number | null>(null);

  // 筛选表单
  const filterForm = reactive({
    bookName: '',
    status: '',
    categoryId: ''
  })

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
    return {
      id: bookData.bookId || 0,
      bookName: bookData.bookName || '',
      author: bookData.author || '',
      bookImg: bookData.coverUrl || '',
      translator: bookData.translator || '',
      status: BookStatusMap[bookData.bookStatus || 0] || '未知状态',
      shelfTime: bookData.shelfTime || '',
      category: bookData.categoryName || '',
      description: bookData.intro || '',
      availableCount: bookData.availableCount || 0,
      totalCount: bookData.totalCount || 0,
      isReservedByCurrentUser: bookData.isReservedByCurrentUser || false,
      isBorrowedByCurrentUser: bookData.isBorrowedByCurrentUser || false
    };
  };

  // 获取书籍列表
  const fetchBooks = async () => {
    try {
      loading.value = true;
      
      const params: GetBooksParams = {
        currentPage: pagination.current,
        pageSize: pagination.pageSize,
        bookName: searchParams.bookName || undefined,
        categoryId: searchParams.categoryId ? Number(searchParams.categoryId) : undefined,
        bookStatus: searchParams.bookStatus ? Number(searchParams.bookStatus) : undefined
      };

      const response: Response = await getBooks(params);
      
      if ([200, 0].includes(response.code || -1)) {
        if (response.data?.records) {
          books.value = response.data.records.map(convertBookData);
          pagination.total = response.data.total || response.data.pageInfo?.total || 0;
        } else {
          // 模拟数据处理
          const allMockBooks = getMockBooks();
          const start = (pagination.current - 1) * pagination.pageSize;
          const end = start + pagination.pageSize;
          books.value = allMockBooks.slice(start, end);
          pagination.total = allMockBooks.length;
        }
      } else {
        ElMessage.error(response.message || '获取书籍列表失败');
        // 模拟数据处理
        const allMockBooks = getMockBooks();
        const start = (pagination.current - 1) * pagination.pageSize;
        const end = start + pagination.pageSize;
        books.value = allMockBooks.slice(start, end);
        pagination.total = allMockBooks.length;
      }
    } catch (error) {
      console.error('获取书籍列表失败:', error);
      ElMessage.error('网络错误，使用模拟数据');
      // 模拟数据处理
      const allMockBooks = getMockBooks();
      const start = (pagination.current - 1) * pagination.pageSize;
      const end = start + pagination.pageSize;
      books.value = allMockBooks.slice(start, end);
      pagination.total = allMockBooks.length;
    } finally {
      loading.value = false;
    }
  };

  // 搜索处理
  const handleSearch = async () => {
    pagination.current = 1;
    
    if (searchParams.bookName.trim()) {
      await searchBooksByKeyword(searchParams.bookName.trim());
    } else {
      fetchBooks();
    }
  };

  // 搜索书籍方法
  const searchBooksByKeyword = async (keyword: string) => {
    try {
      loading.value = true;
      
      const response: Response = await searchBooks({
        keyword,
        currentPage: pagination.current,
        pageSize: pagination.pageSize
      });
      
      if ([200, 0].includes(response.code || -1)) {
        if (response.data?.records) {
          books.value = response.data.records.map(convertBookData);
          pagination.total = response.data.total || response.data.pageInfo?.total || 0;
        } else {
          // 模拟数据处理
          const allMockBooks = getMockBooks();
          const filteredBooks = allMockBooks.filter(book => 
            book.bookName.includes(keyword) || 
            book.author.includes(keyword)
          );
          const start = (pagination.current - 1) * pagination.pageSize;
          const end = start + pagination.pageSize;
          books.value = filteredBooks.slice(start, end);
          pagination.total = filteredBooks.length;
        }
      } else {
        ElMessage.error(response.message || '搜索失败');
        // 模拟数据处理
        const allMockBooks = getMockBooks();
        const filteredBooks = allMockBooks.filter(book => 
          book.bookName.includes(keyword) || 
          book.author.includes(keyword)
        );
        const start = (pagination.current - 1) * pagination.pageSize;
        const end = start + pagination.pageSize;
        books.value = filteredBooks.slice(start, end);
        pagination.total = filteredBooks.length;
      }
    } catch (error) {
      console.error('搜索失败:', error);
      ElMessage.error('搜索失败，使用模拟数据');
      // 模拟数据处理
      const allMockBooks = getMockBooks();
      const filteredBooks = allMockBooks.filter(book => 
        book.bookName.includes(keyword) || 
        book.author.includes(keyword)
      );
      const start = (pagination.current - 1) * pagination.pageSize;
      const end = start + pagination.pageSize;
      books.value = filteredBooks.slice(start, end);
      pagination.total = filteredBooks.length;
    } finally {
      loading.value = false;
    }
  };

  // 分页事件处理
  const handlePageChange = (page: number) => {
    pagination.current = page;
    if (searchParams.bookName.trim()) {
      searchBooksByKeyword(searchParams.bookName.trim());
    } else {
      fetchBooks();
    }
  };

  const handleSizeChange = (size: number) => {
    pagination.current = 1;
    pagination.pageSize = size;
    if (searchParams.bookName.trim()) {
      searchBooksByKeyword(searchParams.bookName.trim());
    } else {
      fetchBooks();
    }
  };

  // 分类列表
  const categoryList = [
    'A、马克思主义、列宁主义、毛泽东思想、邓小平理论',
    'B、哲学、宗教', 
    'C、社会科学总论',
    'D、政治、法律',
    'E、军事',
    'F、经济',
    'G、文化、科学、教育、体育',
    'H、语言、文字',
    'I、文学',
    'J、艺术',
    'K、历史、地理',
    'N、自然科学总论',
    'O、数理科学和化学',
    'P、天文学、地球科学',
    'Q、生物科学',
    'R、医药、卫生',
    'S、农业科学',
    'T、工业技术',
    'U、交通运输',
    'V、航空、航天',
    'X、环境科学、安全科学',
    'Z、综合性图书'
  ];

  // 状态数组
  // 状态数组（用于模拟数据生成）
const statusList = ['待上架', '已预约', '可借阅', '已借光', '已借阅']

  // 模拟数据
  const getMockBooks = (): Book[] => {
    const allMockBooks = Array.from({ length: 50 }).map((_, index) => ({
      id: index + 1,
      bookName: `书籍${index + 1}`,
      author: index % 2 === 0 ? '作者A' : '作者B',
      bookImg: `https://picsum.photos/100/140?random=book${index}`,
      translator: index % 3 === 0 ? '译者A' : '译者B',
      status: statusList[index % statusList.length] || '可借阅',
      shelfTime: `2024-${String((index % 12) + 1).padStart(2, '0')}-${String((index % 28) + 1).padStart(2, '0')}`,
      category: categoryList[index % categoryList.length] || '未分类',
      description: `这是书籍${index + 1}的简介，这是一本关于${categoryList[index % categoryList.length] || '技术'}的优秀书籍。简介简介简介简介简介简介`,
      availableCount: index % 5,
      totalCount: 5,
      isReservedByCurrentUser: false,
      isBorrowedByCurrentUser: false 
    }));
    
    return allMockBooks;
  };

  // 图片加载失败处理
  const handleImgError = (e: Event) => {
    const img = e.target as HTMLImageElement;
    img.src = 'https://img1.baidu.com/it/u=3363823393,2631112139&fm=253&fmt=auto&app=120&f=JPEG?w=680&h=1024';
  };

  // 切换菜单显示
  const toggleMenu = (bookId: number) => {
    showMenuId.value = showMenuId.value === bookId ? null : bookId;
  };

  // 处理详情点击
  const handleDetail = (book: Book) => {
    console.log('查看详情:', book);
    showMenuId.value = null;
    ElMessage.info(`跳转到《${book.bookName}》的详情页面`);
  };

  // 处理借阅点击
  const handleBorrow = async (book: Book) => {
    try {
      showMenuId.value = null;
      
      const result = await showConfirmDialog({
        title: '借阅',
        message: `是否借阅书籍？书籍可借阅天数为30天。`,
        confirmText: '确认',
        cancelText: '取消',
        onConfirm: async () => {
          const axiosResponse = await borrowBook({
            bookId: book.id,
            borrowDays: 30
          });
          const response: ActionResponse = axiosResponse.data;

          if ([200, 0].includes(response.code)) {
            ElMessage.success(response.message || '借阅成功');
            fetchBooks();
          } else {
            ElMessage.error(response.message || '借阅失败');
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
          const response: ActionResponse = (await cancelReserve(book.id)).data;
          
          if ([200, 0].includes(response.code)) {
            ElMessage.success(response.message || '预约成功');
            fetchBooks();
          } else {
            ElMessage.error(response.message || '预约失败');
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
          const axiosResponse = await cancelReserve(book.id);
          const response: ActionResponse = axiosResponse.data;
          
          if ([200, 0].includes(response.code)) {
            ElMessage.success(response.message || '取消预约成功');
            fetchBooks();
          } else {
            ElMessage.error(response.message || '取消预约失败');
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
      '已预约': 'reserved'
    };
    return statusClassMap[status] || 'default';
  };

  // 搜索组件事件处理
  const handleSearchInput = (val: string) => {
    searchParams.bookName = val;
    handleSearch();
  };

  const handleCategoryChange = (val: string) => {
    searchParams.categoryId = val;
    handleSearch();
  };

  const handleStatusChange = (val: string) => {
    searchParams.bookStatus = val;
    handleSearch();
  };

  // 格式化日期
  const formatDate = (dateString: string) => {
    if (!dateString) return '未知';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return `${year}.${month}.${day}`;
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
    margin-bottom: 30px;
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
    height: 220px;
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
  }

  .book-img {
    width: 110px; 
    height: 150px; 
    object-fit: cover;
    border-radius: 8px;
  }

  .book-content-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 6px; 
    min-width: 0; 
    overflow: hidden;
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
    background-color: #f3d05c;
  }

  .status-badge.reserved {
    background-color: #757575;
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