<template>
  <div class="borrow-book-page">
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
      </div>
    </div>
    <!-- 核心表格组件：保留原有插槽和组件调用 -->
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
      <!-- 自定义剩余时间列：添加状态色 -->
      <template #column-remainDays="{ row }">
        <span :class="getRemainTimeClass(row.remainDays)">
          {{ row.remainDays > 0 ? `${row.remainDays}天` : '已超时' }}
        </span>
      </template>
      <!-- 自定义操作列：替换默认按钮，添加续借禁用逻辑 -->
      <template #actions="{ row }">
        <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
        <el-button type="warning" size="small" @click="handleReturn(row)">归还</el-button>
        <el-button 
          type="success" 
          size="small" 
          @click="handleReBorrow(row)"
          :disabled="!row.canRenew || row.remainDays < 0"
        >
          续借
        </el-button>
      </template>
    </BookTable>
  </div>
</template>
<script setup lang="ts">
import BookTable from '@/components/mytable/Table.vue';
import BookInfo from '@/components/BookInfo/BookInfo.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

// 原有代码保持不变（分页、数据生成、筛选逻辑等）
const currentPage = ref(1);
const pageSize = ref(10);
const searchParams = ref({ keyword: '', category: '' });
const bookCategories = [
  '社会人文', '企业管理', '散文', '计算机', '文学', '历史', '心理学', '儿童文学',
  '自然科学', '医学', '经济', '法律', '哲学', '艺术', '教育', '体育', '军事', '地理'
];
const bookNamePrefixes = [
  '不要让未来的你', '多情却被', 'Python编程', '百年', '人类', '活着', '思考', '小王子',
  '数据结构与算法', '经济学原理', '法律基础', '哲学导论', '艺术鉴赏', '教育心理学', '体育健身',
  '军事理论', '地理探索', '医学常识', '自然奥秘', '职场沟通', '时间管理', '情绪调节', '财富自由'
];
const bookNameSuffixes = [
  '讨厌现在的自己', '无情恼', '从入门到实践', '孤独', '简史', '的意义', '快与慢', '的旅行',
  '进阶指南', '（上册）', '实务', '基础', '入门', '精讲', '指南', '教程', '探索', '解密',
  '技巧', '法则', '艺术', '之路', '实战'
];
const authors = [
  'gengeng', '佚名', '未知', '张三', '埃里克·马瑟斯', '加西亚·马尔克斯', '尤瓦尔·赫拉利',
  '余华', '丹尼尔·卡尼曼', '安托万·德·圣-埃克苏佩里', '李四', '王五', '赵六', '杨绛', '钱钟书',
  '鲁迅', '老舍', '巴金', '茅盾', '托尔斯泰', '海明威', '村上春树', '东野圭吾'
];
const translators = ['佚名', '失名', '无', '袁国忠', '范晔', '林俊宏', '胡晓姣', '林珍妮', '张维君'];
const generateMockBooks = (count: number) => {
  return Array.from({ length: count }, (_, index) => {
    const randomMonth = Math.floor(Math.random() * 5) + 1;
    const randomDay = Math.floor(Math.random() * 28) + 1;
    const borrowDate = `2022/${String(randomMonth).padStart(2, '0')}/${String(randomDay).padStart(2, '0')}`;
    const dueMonth = randomMonth + Math.floor(Math.random() * 3) + 1;
    const dueDay = Math.floor(Math.random() * 28) + 1;
    const dueDate = `2022/${String(dueMonth > 12 ? dueMonth - 12 : dueMonth).padStart(2, '0')}/${String(dueDay).padStart(2, '0')} 12:00:00`;
    const remainDays = Math.floor(Math.random() * 121) - 30;
    const canRenew = remainDays >= 0 && Math.random() > 0.5;
    const renewableDays = canRenew ? Math.floor(Math.random() * 60) + 15 : 0;
    return {
      id: index + 1,
      bookImg: `https://picsum.photos/100/140?random=${index + 1}`,
      bookName: `${bookNamePrefixes[Math.floor(Math.random() * bookNamePrefixes.length)]}${bookNameSuffixes[Math.floor(Math.random() * bookNameSuffixes.length)]}`,
      author: authors[Math.floor(Math.random() * authors.length)],
      translator: translators[Math.floor(Math.random() * translators.length)],
      category: bookCategories[Math.floor(Math.random() * bookCategories.length)],
      status: '借阅中',
      shelfTime: borrowDate,
      dueDate,
      remainDays,
      renewableDays,
      canRenew
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
  { prop: 'remainDays', label: '剩余借阅时间', width: 140, align: 'center' },
  { prop: 'dueDate', label: '最晚归还日期', width: 180, align: 'center' },
  { prop: 'renewableDays', label: '可续借天数', width: 120, align: 'center' },
  { prop: 'status', label: '状态', width: 120, align: 'center' },
  { prop: 'shelfTime', label: '借阅时间', width: 160, align: 'center' },
]);
const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'return', label: '归还', type: 'warning' },
  { name: 'borrow', label: '续借', type: 'success' },
]);
const getRemainTimeClass = (remainDays: number) => {
  if (remainDays < 0) return 'overdue';
  if (remainDays <= 7) return 'warning';
  return 'normal';
};
const handleSearchInput = (val: string) => {
  searchParams.value.keyword = val;
};
const handleCategoryChange = (val: string) => {
  searchParams.value.category = val;
};
const handleDetail = (row: any) => {
  ElMessage.info(`查看《${row.bookName}》的详情`);
};
const handleReturn = (row: any) => {
  ElMessage.warning(`申请归还《${row.bookName}》`);
};
const handleReBorrow = (row: any) => {
  ElMessage.success(`成功续借《${row.bookName}》，可续借${row.renewableDays}天`);
};
</script>
<style scoped>
/* 1. 表格外围背景色改为#F5F5F5（核心修改） */
.borrow-book-page {
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
  margin-bottom: 20px;
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

/* 剩余时间状态色（保持原有，适配新背景） */
.overdue {
  color: #f56c6c; /* 超时红色 */
  font-weight: 500;
}
.warning {
  color: #e6a23c; /* 临近到期橙色 */
  font-weight: 500;
}
.normal {
  color: #67c23a; /* 正常绿色 */
}

/* 2. 操作按钮样式定制（核心修改，与归还页保持一致） */
:deep(.el-button--small) {
  padding: 6px 14px; /* 增大内边距 */
  margin: 0 6px; /* 增加间距 */
  border-radius: 4px; /* 优化圆角 */
  font-size: 13px; /* 放大字体 */
}
/* 详情按钮（主色） */
:deep(.el-button--primary.el-button--small) {
  background-color: #1890FF;
  border-color: #1890FF;
}
:deep(.el-button--primary.el-button--small):hover {
  background-color: #096DD9;
  border-color: #096DD9;
}
/* 归还按钮（警告色） */
:deep(.el-button--warning.el-button--small) {
  background-color: #FAAD14;
  border-color: #FAAD14;
}
:deep(.el-button--warning.el-button--small):hover {
  background-color: #FF9C07;
  border-color: #FF9C07;
}
/* 续借按钮（成功色）：新增，与其他按钮样式统一 */
:deep(.el-button--success.el-button--small) {
  background-color: #52C41A;
  border-color: #52C41A;
}
:deep(.el-button--success.el-button--small):hover {
  background-color: #4CAF50;
  border-color: #4CAF50;
}
/* 禁用状态优化：续借按钮禁用时更清晰 */
:deep(.el-button--success.el-button--small.is-disabled) {
  background-color: #F0F0F0;
  border-color: #DDDDDD;
  color: #AAAAAA;
}

/* 表格样式优化（保持原有，适配新背景） */
:deep(.el-table) {
  --el-table-header-text-color: #303133;
  --el-table-row-hover-bg-color: #F0F0F0; /* 适配#F5F5F5背景 */
  border-radius: 8px;
  overflow: hidden;
  background-color: #FFFFFF; /* 表格内部白色，区分外围背景 */
}
:deep(.el-table th) {
  background-color: #FAFAFA !important;
  font-weight: 600;
  border-bottom: 1px solid #EEEEEE; /* 表头加边框，更清晰 */
}

/* 响应式适配（保持原有） */
@media (max-width: 900px) {
  .search-filter-group {
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>