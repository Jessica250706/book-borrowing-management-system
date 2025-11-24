<template>
  <div class="borrow-book-page">
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
        <el-button 
          type="success" 
          @click="handleBatchReBorrow" 
          :disabled="selectedBooks.length === 0"
        >
          批量续借
        </el-button>
      </div>
    </div>
    <!-- 核心表格组件：使用通用 Table.vue -->
    <Table 
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
      <!-- 自定义书籍信息列：使用 BookInfo 组件 -->
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
        <span class="text-button" @click="handleDetail(row)">详情</span>
        <span class="text-button" @click="handleReturn(row)">归还</span>
        <span class="text-button" @click="handleReBorrow(row)"
        :disabled="!row.canRenew || row.remainDays < 0"
        :title="!row.canRenew ? row.cannotRenewReason : ''">续借</span>
      </template>
    </Table>
  </div>
</template>

<script setup lang="ts">
// 新增：导入路由相关依赖
import { useRouter } from 'vue-router';
import Table from '@/components/mytable/Table.vue'; // 导入通用 Table 组件
import BookInfo from '@/components/BookInfo/BookInfo.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

// 新增：初始化路由实例
const router = useRouter();

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
// 搜索筛选参数
const searchParams = ref({ keyword: '', category: '' });
// 选中的书籍（用于批量操作）
const selectedBooks = ref<any[]>([]);

// 书籍分类/名称/作者等模拟数据
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

// 生成模拟借阅数据（补充不可续借原因）
const generateMockBooks = (count: number) => {
  return Array.from({ length: count }, (_, index) => {
    const randomMonth = Math.floor(Math.random() * 5) + 1;
    const randomDay = Math.floor(Math.random() * 28) + 1;
    const borrowDate = `2022/${String(randomMonth).padStart(2, '0')}/${String(randomDay).padStart(2, '0')}`;
    const dueMonth = randomMonth + Math.floor(Math.random() * 3) + 1;
    const dueDay = Math.floor(Math.random() * 28) + 1;
    const dueDate = `2022/${String(dueMonth > 12 ? dueMonth - 12 : dueMonth).padStart(2, '0')}/${String(dueDay).padStart(2, '0')} 12:00:00`;
    const remainDays = Math.floor(Math.random() * 121) - 30;
    
    // 补充不可续借规则（参考搜索摘要2、5）
    const hasOverdue = remainDays < 0; // 逾期不可续借
    const hasRenewed = Math.random() > 0.7; // 已续借1次不可续借
    const hasPenalty = Math.random() > 0.8; // 有滞纳金不可续借
    const canRenew = !hasOverdue && !hasRenewed && !hasPenalty && Math.random() > 0.5;
    const cannotRenewReason = hasOverdue ? '书籍已逾期' : 
                             hasRenewed ? '已续借过1次' : 
                             hasPenalty ? '账号有滞纳金' : '系统限制';
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
      canRenew,
      cannotRenewReason // 新增：不可续借原因
    };
  });
};

// 原始借阅数据
const bookList = ref(generateMockBooks(100));
// 筛选后的书籍列表（自动响应bookList变化）
const filteredBookList = computed(() => {
  return bookList.value.filter(book => {
    const matchKeyword = book.bookName.includes(searchParams.value.keyword.trim());
    const matchCategory = !searchParams.value.category || book.category === searchParams.value.category;
    return matchKeyword && matchCategory;
  });
});

// 表格列配置
const columns = ref([
  { prop: 'bookInfo', label: '书籍信息', width: 320, align: 'left' },
  { prop: 'category', label: '分类', width: 120, align: 'center' },
  { prop: 'remainDays', label: '剩余借阅时间', width: 140, align: 'center' },
  { prop: 'dueDate', label: '最晚归还日期', width: 180, align: 'center' },
  { prop: 'renewableDays', label: '可续借天数', width: 120, align: 'center' },
  { prop: 'status', label: '状态', width: 120, align: 'center' },
  { prop: 'shelfTime', label: '借阅时间', width: 160, align: 'center' },
]);

// 自定义操作按钮配置（备用）
const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'return', label: '归还', type: 'warning' },
  { name: 'borrow', label: '续借', type: 'success' },
]);

// 剩余时间状态类
const getRemainTimeClass = (remainDays: number) => {
  if (remainDays < 0) return 'overdue';
  if (remainDays <= 7) return 'warning';
  return 'normal';
};

// 搜索输入事件
const handleSearchInput = (val: string) => {
  searchParams.value.keyword = val;
};

// 分类切换事件
const handleCategoryChange = (val: string) => {
  searchParams.value.category = val;
};

// 表格多选事件：更新选中的书籍
const handleSelectionChange = (val: any[]) => {
  selectedBooks.value = val;
};

// 单条详情
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
};

// 单条归还：删除书籍（优化后）
const handleReturn = async (row: any) => {
  const isConfirm = await showConfirmDialog({
    title: '归还书籍',
    message: `是否归还书籍《${row.bookName}》？`,
    confirmText: '确定',
    cancelText: '取消',
    onConfirm: async () => {
      // 从列表中删除当前书籍
      bookList.value = bookList.value.filter(book => book.id !== row.id);
      ElMessage.success(`成功归还《${row.bookName}》`);
    }
  });
  if (!isConfirm) return;
};

// 单条续借：显示不可续借原因（优化后）
const handleReBorrow = async (row: any) => {
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
      row.remainDays += row.renewableDays;
      ElMessage.success(`成功续借《${row.bookName}》，可续借${row.renewableDays}天`);
    }
  });
  if (!isConfirm) return;
};

// 批量归还：显示选中列表+删除书籍（优化后）
const handleBatchReturn = async () => {
  const count = selectedBooks.value.length;
  const selectedBookNames = selectedBooks.value.map(book => `《${book.bookName}》`).join('、');
  const message = `
    选中书籍：${selectedBookNames}
    是否归还这${count}本书籍？
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

// 批量续借：显示选中列表+不可续借提示（优化后）
const handleBatchReBorrow = async () => {
  const count = selectedBooks.value.length;
  const canRenewBooks = selectedBooks.value.filter(book => book.canRenew);
  const cannotRenewBooks = selectedBooks.value.filter(book => !book.canRenew);
  
  const selectedBookNames = selectedBooks.value.map(book => `《${book.bookName}》`).join('、');
  const cannotRenewTip = cannotRenewBooks.length > 0 
    ? `当前选中的不可续借书籍有：${cannotRenewBooks.map(book => `《${book.bookName}》`).join('、')}` 
    : '';
  const message = `
    选中书籍：${selectedBookNames}
    是否续借这${count}本书籍？${cannotRenewTip}
  `;

  const isConfirm = await showConfirmDialog({
    title: '批量续借',
    message,
    confirmText: '确定',
    cancelText: '取消',
    dangerouslyUseHTMLString: true,
    onConfirm: async () => {
      canRenewBooks.forEach(book => {
        book.remainDays += book.renewableDays;
      });
      ElMessage.success(`成功续借${canRenewBooks.length}本书籍`);
      if (cannotRenewBooks.length > 0) {
        ElMessage.warning(`不可续借书籍：${cannotRenewBooks.map(book => `《${book.bookName}》`).join('、')}`);
      }
      selectedBooks.value = [];
    }
  });
  if (!isConfirm) return;
};
</script>

<style scoped>
/* 原有样式不变，新增对话框内容样式（可选） */
:deep(.custom-dialog .el-message-box__message) {
  line-height: 1.8 !important; /* 增加对话框文本行高，更易读 */
}
.borrow-book-page {
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
  flex-wrap: wrap;
  gap: 15px;
}
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}
.batch-actions {
  display: flex;
  gap: 10px;
}
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