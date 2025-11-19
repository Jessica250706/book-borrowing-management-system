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
// 引入你指定的两个组件
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

// 分页数据（静态）
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索和筛选参数
const searchParams = ref({
  keyword: '',
  category: ''
});

// 书籍分类列表（批量生成数据时随机选用）
const bookCategories = [
  '社会人文', '企业管理', '散文', '计算机', '文学', '历史', '心理学', '儿童文学',
  '自然科学', '医学', '经济', '法律', '哲学', '艺术', '教育', '体育', '军事', '地理'
];

// 书籍名称列表（批量生成数据时随机组合）
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

// 作者列表（批量生成数据时随机选用）
const authors = [
  'gengeng', '佚名', '未知', '张三', '埃里克·马瑟斯', '加西亚·马尔克斯', '尤瓦尔·赫拉利',
  '余华', '丹尼尔·卡尼曼', '安托万·德·圣-埃克苏佩里', '李四', '王五', '赵六', '杨绛', '钱钟书',
  '鲁迅', '老舍', '巴金', '茅盾', '托尔斯泰', '海明威', '村上春树', '东野圭吾'
];

// 译者列表（批量生成数据时随机选用）
const translators = ['佚名', '失名', '无', '袁国忠', '范晔', '林俊宏', '胡晓姣', '林珍妮', '张维君'];

// 批量生成模拟数据（默认100条，可修改length调整数量）
const generateMockBooks = (count: number) => {
  return Array.from({ length: count }, (_, index) => {
    // 随机生成日期（2022年1月-5月）
    const randomMonth = Math.floor(Math.random() * 5) + 1;
    const randomDay = Math.floor(Math.random() * 28) + 1;
    const borrowDate = `2022/${String(randomMonth).padStart(2, '0')}/${String(randomDay).padStart(2, '0')}`;

    // 随机生成最晚归还日期（借阅日期+30-90天）
    const dueMonth = randomMonth + Math.floor(Math.random() * 3) + 1;
    const dueDay = Math.floor(Math.random() * 28) + 1;
    const dueDate = `2022/${String(dueMonth > 12 ? dueMonth - 12 : dueMonth).padStart(2, '0')}/${String(dueDay).padStart(2, '0')} 12:00:00`;

    // 随机生成剩余天数（-30到90天）
    const remainDays = Math.floor(Math.random() * 121) - 30;

    // 随机生成可续借天数和是否可续借
    const canRenew = remainDays >= 0 && Math.random() > 0.5;
    const renewableDays = canRenew ? Math.floor(Math.random() * 60) + 15 : 0;

    return {
      id: index + 1,
      bookImg: `https://picsum.photos/100/140?random=${index + 1}`, // 不同随机图
      bookName: `${bookNamePrefixes[Math.floor(Math.random() * bookNamePrefixes.length)]}${bookNameSuffixes[Math.floor(Math.random() * bookNameSuffixes.length)]}`,
      author: authors[Math.floor(Math.random() * authors.length)],
      translator: translators[Math.floor(Math.random() * translators.length)],
      category: bookCategories[Math.floor(Math.random() * bookCategories.length)],
      status: '借阅中',
      shelfTime: borrowDate, // 借阅时间
      dueDate, // 最晚归还日期
      remainDays, // 剩余借阅时间
      renewableDays, // 可续借天数
      canRenew // 是否可续借
    };
  });
};

// 生成100条模拟数据（可修改数字生成更多，比如500、1000条）
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

// 列配置：保持原有结构
const columns = ref([
  { prop: 'bookInfo', label: '书籍信息', width: 320, align: 'left' },
  { prop: 'category', label: '分类', width: 120, align: 'center' },
  { prop: 'remainDays', label: '剩余借阅时间', width: 140, align: 'center' },
  { prop: 'dueDate', label: '最晚归还日期', width: 180, align: 'center' },
  { prop: 'renewableDays', label: '可续借天数', width: 120, align: 'center' },
  { prop: 'status', label: '状态', width: 120, align: 'center' },
  { prop: 'shelfTime', label: '借阅时间', width: 160, align: 'center' },
]);

// 操作列配置（防止组件报错）
const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'return', label: '归还', type: 'warning' },
  { name: 'borrow', label: '续借', type: 'success' },
]);

// 剩余时间状态类：区分超时、临近到期、正常
const getRemainTimeClass = (remainDays: number) => {
  if (remainDays < 0) return 'overdue';
  if (remainDays <= 7) return 'warning';
  return 'normal';
};

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
/* 页面整体样式 */
.borrow-book-page {
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

/* 剩余时间状态色 */
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

/* 响应式适配：小屏幕下搜索筛选栏换行 */
@media (max-width: 900px) {
  .search-filter-group {
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>