<template>
  <div class="book-borrow-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="search-section">
        <BookSearchInput @search="handleSearchInput" style="width: 150px" />
        <div class="filter-group">
          <span class="filter-label">书籍状态:</span>
          <BookStatusSelect @change="handleStatusChange" style="width: 150px" />
        </div>
        <div class="filter-group">
          <span class="filter-label">书籍分类:</span>
          <BookCategorySelect @change="handleCategoryChange" style="width: 150px" />
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <a-spin size="large" />
      <div class="loading-text">加载中...</div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="books.length === 0" class="empty-container">
      <a-empty description="暂无可借阅的图书" />
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
                  <!-- 根据书籍状态显示不同的菜单项 -->
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
      <a-pagination
        v-model:current="pagination.current"
        v-model:pageSize="pagination.pageSize"
        :total="pagination.total"
        show-size-changer
        show-quick-jumper
        :page-size-options="['12', '24', '36', '48']"
        @change="handlePageChange"
        @showSizeChange="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, reactive } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { ElMessageBox } from 'element-plus';
  import { getBooks, borrowBook, reserveBook, cancelReserve } from '@/apis/book';
  import type { Book, GetBooksParams } from '@/apis/book/type';

  // 导入三个搜索组件
  import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
  import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
  import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';

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

  // 获取书籍列表
  const fetchBooks = async () => {
    try {
      loading.value = true;
      
      const params: GetBooksParams = {
        currentPage: pagination.current,
        pageSize: pagination.pageSize,
        bookName: searchParams.bookName || undefined,
        categoryId: searchParams.categoryId || undefined,
        bookStatus: searchParams.bookStatus || undefined
      };

      const response = await getBooks(params);
      
      if (response.code === 200 || response.code === 0 || response.code === null) {
        // 如果后端返回了数据，使用后端数据；否则使用模拟数据
        if (response.data && response.data.records) {
          books.value = response.data.records;
          pagination.total = response.data.total || 0;
        } else {
          // 使用模拟数据
          books.value = getMockBooks();
          pagination.total = books.value.length;
        }
      } else {
        message.error(response.message || '获取书籍列表失败');
        // 失败时使用模拟数据
        books.value = getMockBooks();
        pagination.total = books.value.length;
      }
    } catch (error) {
      console.error('获取书籍列表失败:', error);
      message.error('网络错误，使用模拟数据');
      // 出错时使用模拟数据
      books.value = getMockBooks();
      pagination.total = books.value.length;
    } finally {
      loading.value = false;
    }
  };

  // 模拟数据（备用）- 添加不同状态的书籍
  const getMockBooks = (): Book[] => {
    return [
      {
        id: 1,
        bookName: 'Vue.js 设计与实现',
        author: '霍春阳',
        bookImg: new URL('@/assets/logo.jpg', import.meta.url).href,
        status: '可借阅',
        shelfTime: '2024-01-15',
        category: '前端开发',
        description: '深入讲解Vue.js框架的设计原理和实现机制',
        availableCount: 3
      },
      {
        id: 2,
        bookName: 'TypeScript 入门教程',
        author: '张三',
        bookImg: new URL('@/assets/logo.jpg', import.meta.url).href,
        translator: '李四',
        status: '待上架',
        shelfTime: '2024-01-10',
        category: '编程语言',
        description: 'TypeScript从入门到实战的完整教程',
        availableCount: 0
      },
      {
        id: 3,
        bookName: 'JavaScript 高级程序设计',
        author: 'Nicholas C. Zakas',
        bookImg: new URL('@/assets/logo.jpg', import.meta.url).href,
        translator: '李松峰',
        status: '已借光',
        shelfTime: '2024-01-08',
        category: '前端开发',
        description: 'JavaScript经典教程，涵盖ES6+新特性',
        availableCount: 0
      },
      {
        id: 4,
        bookName: '深入浅出 Vue.js',
        author: '刘博文',
        bookImg: new URL('@/assets/logo.jpg', import.meta.url).href,
        status: '已借阅',
        shelfTime: '2024-01-05',
        category: '前端开发',
        description: '解析Vue.js源码，理解框架内部原理',
        availableCount: 0
      },
      {
        id: 5,
        bookName: 'CSS 世界',
        author: '张鑫旭',
        bookImg: new URL('@/assets/logo.jpg', import.meta.url).href,
        status: '已预约',
        shelfTime: '2024-01-03',
        category: '前端开发',
        description: '深度讲解CSS技术的专业书籍',
        availableCount: 4
      },
      {
        id: 6,
        bookName: 'React 状态管理与同构实战',
        author: '侯策',
        bookImg: new URL('@/assets/logo.jpg', import.meta.url).href,
        translator: '颜海镜',
        status: '可借阅',
        shelfTime: '2024-01-01',
        category: '前端开发',
        description: 'React高级应用与同构渲染实战',
        availableCount: 2
      }
    ];
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

  // 处理详情点击 - 跳转到书籍详情页面
  const handleDetail = (book: Book) => {
    console.log('查看详情:', book);
    showMenuId.value = null;
    // 这里可以跳转到书籍详情页面
    // router.push(`/book/detail/${book.id}`);
    message.info(`跳转到《${book.bookName}》的详情页面`);
  };

  // 处理借阅点击
  const handleBorrow = async (book: Book) => {
    try {
      showMenuId.value = null;
      
      await ElMessageBox({
        title: '借阅',
        message: `是否借阅书籍？书籍可借阅天数为30天。`,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        customClass: 'custom-message-box',
        showCancelButton: true,
        type: '' 
      });

      const response = await borrowBook({
        bookId: book.id,
        borrowDays: 30
      });
      
      if (response.code === 200 || response.code === 0 || response.code === null) {
        message.success(response.message || '借阅成功');
        // 刷新列表
        fetchBooks();
      } else {
        message.error(response.message || '借阅失败');
      }
    } catch (error: any) {
      if (error !== 'cancel') {
        message.error(error.message || '借阅失败，请重试');
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

      await ElMessageBox({
        title: '预约',
        message: messageContent,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        customClass: 'custom-message-box',
        showCancelButton: true,
        type: '' 
      });

      const response = await reserveBook(book.id);
      
      if (response.code === 200 || response.code === 0 || response.code === null) {
        message.success(response.message || '预约成功');
        // 刷新列表
        fetchBooks();
      } else {
        message.error(response.message || '预约失败');
      }
    } catch (error: any) {
      if (error !== 'cancel') {
        message.error(error.message || '预约失败，请重试');
        console.error('预约失败:', error);
      }
    }
  };

  // 处理取消预约点击
  const handleCancelReserve = async (book: Book) => {
    try {
      showMenuId.value = null;
      
      await ElMessageBox({
        title: '取消预约',
        message: '是否取消预约书籍？',
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        customClass: 'custom-message-box',
        showCancelButton: true,
        type: '' 
      });

      const response = await cancelReserve(book.id);
      
      if (response.code === 200 || response.code === 0 || response.code === null) {
        message.success(response.message || '取消预约成功');
        // 刷新列表
        fetchBooks();
      } else {
        message.error(response.message || '取消预约失败');
      }
    } catch (error: any) {
      if (error !== 'cancel') {
        message.error(error.message || '取消预约失败，请重试');
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

  // 搜索处理
  const handleSearch = () => {
    pagination.current = 1;
    fetchBooks();
  };

  // 分页变化
  const handlePageChange = (page: number) => {
    pagination.current = page;
    fetchBooks();
  };

  // 分页大小变化
  const handleSizeChange = (current: number, size: number) => {
    pagination.current = 1;
    pagination.pageSize = size;
    fetchBooks();
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
    padding: 20px;
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
  }

  .empty-container {
    padding: 60px 0;
  }

  /* 固定三列网格布局 */
  .books-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    justify-items: stretch;
    align-items: start;
    margin-bottom: 30px;
  }

  /* 书籍卡片样式 */
  .book-item {
    display: flex;
  }

  .book-card {
    display: flex;
    width: 100%;
    height: 200px;
    background: white;
    border: 1px solid #e0e0e0;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 20px;
    gap: 20px; 
    position: relative;
    box-sizing: border-box;
    transition: all 0.3s ease;
  }

  .book-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }

  /* 图片区域 */
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
    gap: 4px; 
    min-width: 0;
  }

  /* 书名 */
  .book-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
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
  }

  .status-badge {
    padding: 2px 8px;
    border-radius: 4px; 
    font-size: 12px;
    color: white;
    font-weight: 500;
  }

  /* 不同状态的背景色 */
  .status-badge.available {
    background-color: #66d07a; /* 可借阅 */
  }

  .status-badge.pending {
    background-color: #619cff; /* 待上架 */
  }

  .status-badge.out-of-stock {
    background-color: #ed4044; /* 已借光 */
  }

  .status-badge.borrowed {
    background-color: #f3d05c; /* 已借阅 */
  }

  .status-badge.reserved {
    background-color: #757575; /* 已预约 */
  }

  .status-badge.default {
    background-color: #ed10f1; /* 默认 */
  }

  .shelf-time {
    font-size: 12px;
    color: #999;
  }

  /* 作者和分类 */
  .book-author,
  .book-category,
  .book-description {
    font-size: 12px;
    color: #666;
    line-height: 1.4;
    display: flex;
    text-align: left; 
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
  }

  .book-description .value {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: normal;
    text-align: left;
  }

  /* 分页容器 */
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 30px;
  }

  /* 响应式设计 */
  @media (max-width: 1200px) {
    .books-grid {
      grid-template-columns: repeat(3, 1fr);
    }
  }

  @media (max-width: 900px) {
    .books-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
    }
    
    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
    }
    
    .search-section {
      justify-content: center;
    }
  }

  @media (max-width: 600px) {
    .books-grid {
      grid-template-columns: 1fr;
      gap: 12px;
    }
    
    .book-borrow-page {
      padding: 12px;
    }
    
    .book-card {
      height: auto;
      min-height: 200px;
    }
  }
</style>

<style>
  /* 全局样式，不加 scoped */
  .el-message-box.custom-message-box {
    width: 600px !important;
    height: 180px !important;
  }

  .el-message-box.custom-message-box .el-message-box__header {
    padding: 0 0 8px 10px !important;
    border-bottom: 1px solid #e8e8e8 !important;
  }

  .el-message-box.custom-message-box .el-message-box__title {
    font-size: 18px !important;
    font-weight: 600 !important;
    color: #333 !important;
  }

  .el-message-box.custom-message-box .el-message-box__content {
    padding: 30px 0 0 10px !important;
    min-height: 120px !important;
  }

  .el-message-box.custom-message-box .el-message-box__message {
    padding-left: 20px !important;
    font-size: 16px !important;
    line-height: 1.6 !important;
    color: #666 !important;
    text-align: left !important;
  }

  .el-message-box.custom-message-box .el-message-box__btns {
    padding: 15px 20px 20px !important;
    margin-top: 10px !important;
    text-align: center !important;
  }
</style>