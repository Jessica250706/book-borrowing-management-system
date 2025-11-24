<template>
  <div class="return-book-page">
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
          <BookCategorySelect @change="handleCategoryChange" style="width: 150px" />
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
    <!-- 核心表格组件（添加全选功能） -->
    <BookTable 
      ref="tableRef"
      :data="filteredBookList"    
      :columns="columns" 
      :total="bookList.length"  
      :actions="customActions"
      :current-page="currentPage"
      :page-size="pageSize"
      @selection-change="handleSelectionChange"
      :show-selection="true" 
      :show-index="true"
    >
      <!-- 自定义书籍信息列：使用BookInfo组件 -->
      <template #column-bookInfo="{ row }">
        <BookInfo :book="row" />
      </template>
      <!-- 自定义用户信息列：复用UserInfo组件 -->
      <template #column-userInfo="{ row }">
        <UserInfo :user="row.user" />
      </template>
      <!-- 自定义操作列：仅保留详情和归还 -->
      <template #actions="{ row }">
        <span class="text-button" @click="handleDetail(row)">详情</span>
        <span class="text-button" @click="handleReturn(row)">归还</span>
      </template>
    </BookTable>
  </div>
</template>
<script setup lang="ts">
// 新增：导入路由相关依赖
import { useRouter } from 'vue-router';
import BookTable from '@/components/mytable/Table.vue';
import BookInfo from '@/components/BookInfo/BookInfo.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import UserInfo from '@/components/UserInfo/UserInfo.vue';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

// 新增：初始化路由实例
const router = useRouter();
// 分页相关
const currentPage = ref(1);
const pageSize = ref(100);
// 搜索筛选参数
const searchParams = ref({ keyword: '', category: '' });
// 选中的书籍（用于批量操作）
const selectedBooks = ref<any[]>([]);

// 书籍分类等模拟数据
const bookCategories = [
  '社会人文', '企业管理', '散文', '计算机', '文学', '历史', '心理学', '儿童文学',
  '自然科学', '医学', '经济', '法律', '哲学', '艺术', '教育', '体育', '军事', '地理'
];
const bookNamePrefixes = [
  '不要让未来的你', '多情却被', 'Python编程', '百年', '人类', '活着', '思考', '小王子',
  '数据结构与算法', '经济学原理', '法律基础', '哲学导论', '艺术鉴赏', '教育心理学',
  '参加义工活动', '参加视频会议', '组织员工学习', '职场沟通', '时间管理', '情绪调节'
];
const bookNameSuffixes = [
  '讨厌现在的自己', '无情恼', '从入门到实践', '孤独', '简史', '的意义', '快与慢', '的旅行',
  '进阶指南', '（上册）', '实务', '基础', '入门', '精讲', '指南', '教程', '探索', ''
];
const authors = [
  'gengeng', '佚名', '未知', '张三', '埃里克·马瑟斯', '加西亚·马尔克斯', '余华', '鲁迅', '老舍'
];
const userList = [
  { username: 'xuwei', realName: '徐伟', avatarUrl: 'https://picsum.photos/40/40?random=101' },
  { username: 'wanue', realName: '万悦', avatarUrl: 'https://picsum.photos/40/40?random=102' },
  { username: 'futu', realName: '傅途', avatarUrl: 'https://picsum.photos/40/40?random=103' },
  { username: 'lisi', realName: '李四', avatarUrl: 'https://picsum.photos/40/40?random=104' },
  { username: 'wangwu', realName: '王五', avatarUrl: 'https://picsum.photos/40/40?random=105' },
  { username: 'zhaoliu', realName: '赵六', avatarUrl: 'https://picsum.photos/40/40?random=106' }
];

// 生成模拟书籍数据
const generateMockBooks = (count: number) => {
  return Array.from({ length: count }, (_, index) => {
    const bookNo = Math.floor(Math.random() * 900000) + 100000;
    const userIndex = Math.floor(Math.random() * userList.length);
    const randomUser = userList[userIndex] || userList[0];
    return {
      id: index + 1,
      bookNo,
      bookImg: `https://picsum.photos/100/140?random=${index + 100}`,
      bookName: `${bookNamePrefixes[Math.floor(Math.random() * bookNamePrefixes.length)]}${bookNameSuffixes[Math.floor(Math.random() * bookNameSuffixes.length)]}`,
      author: authors[Math.floor(Math.random() * authors.length)],
      category: bookCategories[Math.floor(Math.random() * bookCategories.length)],
      status: '已借出', // 修改状态为已借出更合理
      user: randomUser,
      translator: ['佚名', '无', '袁国忠'][Math.floor(Math.random() * 3)]
    };
  });
};

const bookList = ref(generateMockBooks(100));
const filteredBookList = computed(() => {
  return bookList.value.filter(book => {
    const matchKeyword = book.bookName.includes(searchParams.value.keyword.trim());
    const matchCategory = !searchParams.value.category || book.category === searchParams.value.category;
    return matchKeyword && matchCategory;
  });
});

const columns = ref([
  { prop: 'bookInfo', label: '书籍信息', width: 320, align: 'left' },
  { prop: 'category', label: '分类', width: 120, align: 'center' },
  { prop: 'userInfo', label: '借阅人', width: 150, align: 'left' }, // 调整列名更准确
]);

const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'return', label: '归还', type: 'warning' }
]);

// 表格多选事件：更新选中的书籍
const handleSelectionChange = (val: any[]) => {
  selectedBooks.value = val;
};

const handleSearchInput = (val: string) => {
  searchParams.value.keyword = val;
};

const handleCategoryChange = (val: string) => {
  searchParams.value.category = val;
};

// 修改：单条详情方法，添加路由跳转
const handleDetail = (row: any) => {
  // 保留原有提示（可选）
  ElMessage.info(`查看《${row.bookName}》的详情`);
  // 新增：跳转到书籍详情页，传递书籍ID参数
  router.push({
    path: '/borrow/BookBorrow/BookDetail', // 使用与ReaderBookBorrow.vue一致的详情页路径
    query: {
      id: row.id.toString() // 传递书籍ID
    }
  });
};  ElMessage.info(`查看《${row.bookName}》的详情（ID:${row.bookNo}）`);

// 单条归还
const handleReturn = async (row: any) => {
  const isConfirm = await showConfirmDialog({
    title: '归还书籍',
    message: `是否确认归还《${row.bookName}》？借阅人：${row.user.realName}`,
    confirmText: '确定',
    cancelText: '取消',
    onConfirm: async () => {
      // 从列表中删除当前书籍
      bookList.value = bookList.value.filter(book => book.id !== row.id);
      ElMessage.success(`《${row.bookName}》已确认归还`);
    }
  });
  if (!isConfirm) return;
};

// 批量归还
const handleBatchReturn = async () => {
  const count = selectedBooks.value.length;
  if (count === 0) return;

  const selectedBookNames = selectedBooks.value.map(book => `《${book.bookName}》`).join('、');
  const message = `
    选中书籍：${selectedBookNames}
    是否确认归还这${count}本书籍？
  `;

  const isConfirm = await showConfirmDialog({
    title: '批量归还',
    message,
    confirmText: '确定',
    cancelText: '取消',
    dangerouslyUseHTMLString: true,
    onConfirm: async () => {
      // 批量删除选中书籍
      const returnedIds = selectedBooks.value.map(book => book.id);
      bookList.value = bookList.value.filter(book => !returnedIds.includes(book.id));
      
      ElMessage.success(`成功归还${count}本书籍`);
      selectedBooks.value = [];
    }
  });
  if (!isConfirm) return;
};
</script>
<style scoped>
/* 表格外围背景色 */
.return-book-page {
  padding-bottom: 20px;
    max-width: 1400px;
    margin: 0 auto;
    min-height: 80vh;
}

/* 页面标题 + 搜索筛选栏 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  flex-wrap: wrap;
  gap: 15px;
}

/* 批量操作按钮容器 */
.batch-actions {
  display: flex;
  gap: 10px;
}

/* 搜索筛选组样式 */
.search-filter-group {
  display: flex;
  align-items: center;
  gap: 20px;
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

/* 操作按钮样式定制 */
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

/* 表格样式优化 */
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
    flex-wrap: wrap;
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