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

    <!-- 核心表格组件 -->
    <BookTable 
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
// 引入指定组件 + 新增UserInfo组件
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import UserInfo from '@/components/UserInfo/UserInfo.vue'; // 复用用户信息组件
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

const multipleSelection = ref([]); // 存储选中的行

// 分页数据（静态，匹配UI图100条/页）
const currentPage = ref(1);
const pageSize = ref(100);

// 搜索和筛选参数
const searchParams = ref({
  keyword: '',
  category: ''
});

// 书籍分类列表（与借阅页面一致）
const bookCategories = [
  '社会人文', '企业管理', '散文', '计算机', '文学', '历史', '心理学', '儿童文学',
  '自然科学', '医学', '经济', '法律', '哲学', '艺术', '教育', '体育', '军事', '地理'
];

// 书籍名称列表（批量生成数据）
const bookNamePrefixes = [
  '不要让未来的你', '多情却被', 'Python编程', '百年', '人类', '活着', '思考', '小王子',
  '数据结构与算法', '经济学原理', '法律基础', '哲学导论', '艺术鉴赏', '教育心理学',
  '参加义工活动', '参加视频会议', '组织员工学习', '职场沟通', '时间管理', '情绪调节'
];
const bookNameSuffixes = [
  '讨厌现在的自己', '无情恼', '从入门到实践', '孤独', '简史', '的意义', '快与慢', '的旅行',
  '进阶指南', '（上册）', '实务', '基础', '入门', '精讲', '指南', '教程', '探索', ''
];

// 作者列表
const authors = [
  'gengeng', '佚名', '未知', '张三', '埃里克·马瑟斯', '加西亚·马尔克斯', '余华', '鲁迅', '老舍'
];

// 用户信息列表（批量生成借阅用户）
const userList = [
  { username: 'xuwei', realName: '徐伟', avatarUrl: 'https://picsum.photos/40/40?random=101' },
  { username: 'wanue', realName: '万悦', avatarUrl: 'https://picsum.photos/40/40?random=102' },
  { username: 'futu', realName: '傅途', avatarUrl: 'https://picsum.photos/40/40?random=103' },
  { username: 'lisi', realName: '李四', avatarUrl: 'https://picsum.photos/40/40?random=104' },
  { username: 'wangwu', realName: '王五', avatarUrl: 'https://picsum.photos/40/40?random=105' },
  { username: 'zhaoliu', realName: '赵六', avatarUrl: 'https://picsum.photos/40/40?random=106' }
];

// 批量生成模拟数据（100条，匹配UI图总条数）
const generateMockBooks = (count: number) => {
  return Array.from({ length: count }, (_, index) => {
    const bookNo = Math.floor(Math.random() * 900000) + 100000;
    // 确保随机用户不会超出数组范围（添加边界检查）
    const userIndex = Math.floor(Math.random() * userList.length);
    const randomUser = userList[userIndex] || userList[0]; // 兜底逻辑

    return {
      id: index + 1,
      bookNo,
      bookImg: `https://picsum.photos/100/140?random=${index + 100}`,
      bookName: `${bookNamePrefixes[Math.floor(Math.random() * bookNamePrefixes.length)]}${bookNameSuffixes[Math.floor(Math.random() * bookNameSuffixes.length)]}`,
      author: authors[Math.floor(Math.random() * authors.length)],
      category: bookCategories[Math.floor(Math.random() * bookCategories.length)],
      status: '已归还',
      user: randomUser, // 确保此处一定有值
      translator: ['佚名', '无', '袁国忠'][Math.floor(Math.random() * 3)]
    };
  });
};

// 生成100条模拟数据（匹配UI图总条数）
const bookList = ref(generateMockBooks(100));

// 筛选后的数据（搜索+分类）
const filteredBookList = computed(() => {
  return bookList.value.filter(book => {
    // 搜索筛选（书籍名称包含关键词）
    const matchKeyword = book.bookName.includes(searchParams.value.keyword.trim());
    // 分类筛选（为空则匹配所有）
    const matchCategory = !searchParams.value.category || book.category === searchParams.value.category;
    return matchKeyword && matchCategory;
  });
});

// 列配置：匹配UI图字段（序号、书籍信息、分类、用户、操作）
const columns = ref([
  { prop: 'bookInfo', label: '书籍信息', width: 320, align: 'left' },
  { prop: 'category', label: '分类', width: 120, align: 'center' },
  { prop: 'userInfo', label: '用户', width: 150, align: 'center' }, // 新增用户列
]);

// 操作列配置（仅详情和归还）
const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'return', label: '归还', type: 'warning' }
]);

// 搜索输入事件
const handleSearchInput = (val: string) => {
  searchParams.value.keyword = val;
};

// 分类变更事件
const handleCategoryChange = (val: string) => {
  searchParams.value.category = val;
};

// 按钮点击事件（静态提示）
const handleDetail = (row: any) => {
  ElMessage.info(`查看《${row.bookName}》的详情（ID:${row.bookNo}）`);
};

const handleReturn = (row: any) => {
  ElMessage.success(`《${row.bookName}》已确认归还（借阅人：${row.user.username}）`);
};
</script>

<style scoped>
/* 页面整体样式 */
.return-book-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
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

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 批量操作按钮 */
.batch-operation {
  margin-bottom: 15px;
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

/* 书籍ID样式 */
.book-id {
  margin-left: 8px;
  font-size: 12px;
  color: #909399;
}

/* 适配BookTable组件的样式 */
:deep(.el-table) {
  --el-table-header-text-color: #303133;
  --el-table-row-hover-bg-color: #f8f9fa;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #fafafa !important;
  font-weight: 600;
}

:deep(.el-button--small) {
  padding: 5px 10px;
  margin: 0 2px;
}

/* 响应式适配 */
@media (max-width: 900px) {
  .search-filter-group {
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>