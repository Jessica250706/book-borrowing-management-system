<template>
  <div class="return-book-page">
    <!-- 页面标题 + 搜索筛选栏 -->
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
        <el-button type="primary" size="medium" @click="">批量操作</el-button>
      </div>
    </div>
    <!-- 核心表格组件（新增ref和全选事件，完善全选逻辑） -->
    <BookTable 
      ref="tableRef"
      :data="filteredBookList"    
      :columns="columns" 
      :total="bookList.length"  
      :actions="customActions"
      :current-page="currentPage"
      :page-size="pageSize"
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
        <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
        <el-button type="warning" size="small" @click="handleReturn(row)">归还</el-button>
      </template>
    </BookTable>
  </div>
</template>
<script setup lang="ts">
import BookTable from '@/components/mytable/Table.vue';
import BookInfo from '@/components/BookInfo/BookInfo.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import UserInfo from '@/components/UserInfo/UserInfo.vue';
import { ref, computed } from 'vue';
import { ElMessage, ElTable } from 'element-plus';


// 原有代码保持不变（分页、数据生成、筛选逻辑等）
const currentPage = ref(1);
const pageSize = ref(100);
const searchParams = ref({ keyword: '', category: '' });
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
      status: '已归还',
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
  { prop: 'userInfo', label: '用户', width: 150, align: 'left' }, // 调整为靠左对齐
]);
const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'return', label: '归还', type: 'warning' }
]);
const handleSearchInput = (val: string) => {
  searchParams.value.keyword = val;
};
const handleCategoryChange = (val: string) => {
  searchParams.value.category = val;
};
const handleDetail = (row: any) => {
  ElMessage.info(`查看《${row.bookName}》的详情（ID:${row.bookNo}）`);
};
const handleReturn = (row: any) => {
  ElMessage.success(`《${row.bookName}》已确认归还（借阅人：${row.user.username}）`);
};
</script>
<style scoped>
/* 1. 表格外围背景色改为#F5F5F5（核心修改） */
.return-book-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  background-color: #F5F5F5; /* 原#f5f7fa，改为需求的#F5F5F5 */
  min-height: calc(100vh - 60px);
}

/* 页面标题 + 搜索筛选栏（保持原有布局） */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  flex-wrap: wrap;
  gap: 15px;
}
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 搜索筛选组样式（保持原有） */
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

/* 2. 操作按钮样式定制（核心修改，通过:deep()覆盖Element默认样式） */
:deep(.el-button--small) {
  padding: 6px 14px; /* 增大内边距，按钮更宽 */
  margin: 0 6px; /* 增加按钮间距，避免拥挤 */
  border-radius: 4px; /* 圆角优化，更柔和 */
  font-size: 13px; /* 字体放大，更清晰 */
}
/* 详情按钮（主色）：加深蓝色，hover效果优化 */
:deep(.el-button--primary.el-button--small) {
  background-color: #1890FF;
  border-color: #1890FF;
}
:deep(.el-button--primary.el-button--small):hover {
  background-color: #096DD9;
  border-color: #096DD9;
}
/* 归还按钮（警告色）：调整橙色，hover效果优化 */
:deep(.el-button--warning.el-button--small) {
  background-color: #FAAD14;
  border-color: #FAAD14;
}
:deep(.el-button--warning.el-button--small):hover {
  background-color: #FF9C07;
  border-color: #FF9C07;
}

/* 表格样式优化（保持原有，适配背景色） */
:deep(.el-table) {
  --el-table-header-text-color: #303133;
  --el-table-row-hover-bg-color: #F0F0F0; /*  hover色适配#F5F5F5背景 */
  border-radius: 8px;
  overflow: hidden;
  background-color: #FFFFFF; /* 表格内部白色，与外围背景区分 */
}
:deep(.el-table th) {
  background-color: #FAFAFA !important;
  font-weight: 600;
  border-bottom: 1px solid #EEEEEE; /* 表头加下边框，更清晰 */
}

/* 响应式适配（保持原有） */
@media (max-width: 900px) {
  .search-filter-group {
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>