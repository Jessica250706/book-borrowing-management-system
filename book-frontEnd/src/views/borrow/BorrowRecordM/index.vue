<template>
  <div class="borrow-record-page">
    <!-- 搜索筛选栏：新增操作类型下拉筛选 -->
    <div class="search-filter-group">
      <el-input
        placeholder="请输入书籍名称"
        v-model="searchParams.keyword"
        style="width: 200px"
      />
      <el-select
        v-model="searchParams.category"
        placeholder="所有分类"
        style="width: 150px; margin-left: 10px"
      >
        <el-option
          v-for="item in bookCategories"
          :key="item"
          :label="item"
          :value="item"
        />
      </el-select>
      <!-- 新增：操作类型筛选下拉 -->
      <el-select
        v-model="searchParams.operationType"
        placeholder="所有操作"
        style="width: 150px; margin-left: 10px"
      >
        <el-option label="借阅" value="借阅" />
        <el-option label="归还" value="归还" />
        <el-option label="续借" value="续借" />
        <el-option label="预约" value="预约" />
        <el-option label="取消预约" value="取消预约" />
        <el-option label="所有" value="" />
      </el-select>
      <el-button type="primary" @click="handleBatchOperation">批量操作</el-button>
    </div>

    <!-- 核心表格组件 -->
    <BookTable 
      :data="filteredRecordList"
      :columns="columns"
      :total="recordList.length"
      :current-page="currentPage"
      :page-size="pageSize"
    >
      <!-- 书籍信息 -->
      <template #column-bookInfo="{ row }">
        <div class="book-info">
          <img :src="row.bookImg" class="book-cover" />
          <div>
            <div>{{ row.bookName }}</div>
            <div class="book-author">作者: {{ row.author }}</div>
            <div class="book-translator">译者: {{ row.translator }}</div>
          </div>
        </div>
      </template>

      <!-- 用户信息 -->
      <template #column-userInfo="{ row }">
        <div class="user-info">
          <img :src="row.user.avatarUrl" class="user-avatar" />
          <div>
            <div>{{ row.user.realName }}</div>
            <div class="user-id">ID: {{ row.user.username }}</div>
          </div>
        </div>
      </template>

      <!-- 操作类型状态 -->
      <template #column-operationType="{ row }">
        <span :class="getOperationTypeClass(row.operationType)">
          {{ row.operationType }}
        </span>
      </template>

      <!-- 操作列：只保留删除记录 -->
      <template #actions="{ row }">
        <span class="text-button" @click="handleDeleteRecord(row)">删除记录</span>
      </template>
    </BookTable>
  </div>
</template>

<script setup lang="ts">
import BookTable from '@/components/mytable/Table.vue';
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

// 分页
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索参数：新增operationType字段
const searchParams = ref({ 
  keyword: '', 
  category: '',
  operationType: ''  // 新增：操作类型筛选值
});

// 书籍分类
const bookCategories = [
  '经济', '医学', '历史', '自然科学', '军事', '散文', '文学', '地理'
];

// 模拟用户数据
const userList = [
  { username: 'futu', realName: '傅途', avatarUrl: 'https://picsum.photos/40/40?random=103' },
  { username: 'xuwei', realName: '徐伟', avatarUrl: 'https://picsum.photos/40/40?random=101' }
];

// 模拟借阅记录数据
const recordList = ref([
  {
    id: 1,
    bookName: '思考无烦恼',
    bookImg: 'https://picsum.photos/60/80?random=201',
    author: 'gengeng',
    translator: '袁国忠',
    category: '经济',
    user: userList[0],
    operationType: '借阅',
    operationTime: '2022/09/01 12:00:00'
  },
  {
    id: 2,
    bookName: 'Python编程无烦恼',
    bookImg: 'https://picsum.photos/60/80?random=202',
    author: '张三',
    translator: '佚名',
    category: '医学',
    user: userList[0],
    operationType: '预约',
    operationTime: '2022/11/05 12:00:00'
  },
  {
    id: 3,
    bookName: '多情却无情恼',
    bookImg: 'https://picsum.photos/60/80?random=203',
    author: '加西亚·马尔克斯',
    translator: '佚名',
    category: '历史',
    user: userList[0],
    operationType: '续借',
    operationTime: '2022/06/24 12:00:00'
  },
  {
    id: 4,
    bookName: '不要让未来的你的意义',
    bookImg: 'https://picsum.photos/60/80?random=204',
    author: '鲁迅',
    translator: '袁国忠',
    category: '自然科学',
    user: userList[1],
    operationType: '借阅',
    operationTime: '2022/12/21 12:00:00'
  },
  {
    id: 5,
    bookName: '百年孤独',
    bookImg: 'https://picsum.photos/60/80?random=205',
    author: 'gengeng',
    translator: '无',
    category: '军事',
    user: userList[0],
    operationType: '预约',
    operationTime: '2022/10/19 12:00:00'
  },
  {
    id: 6,
    bookName: '思考孤独',
    bookImg: 'https://picsum.photos/60/80?random=206',
    author: 'gengeng',
    translator: '袁国忠',
    category: '自然科学',
    user: userList[1],
    operationType: '取消预约',
    operationTime: '2022/01/08 12:00:00'
  }
]);

// 筛选后的数据：新增操作类型筛选逻辑
const filteredRecordList = computed(() => {
  return recordList.value.filter(record => {
    const matchKeyword = record.bookName.includes(searchParams.value.keyword);
    const matchCategory = !searchParams.value.category || record.category === searchParams.value.category;
    // 新增：操作类型筛选条件
    const matchOperation = !searchParams.value.operationType || record.operationType === searchParams.value.operationType;
    return matchKeyword && matchCategory && matchOperation;
  });
});

// 表格列配置
const columns = ref([
  { prop: 'bookInfo', label: '书籍名称', width: 250 },
  { prop: 'category', label: '书籍分类', width: 120 },
  { prop: 'userInfo', label: '用户', width: 150 },
  { prop: 'operationType', label: '操作类别', width: 120 },
  { prop: 'operationTime', label: '操作时间', width: 180 },
]);

// 操作类型样式
const getOperationTypeClass = (type: string) => {
  const styles = {
    '借阅': 'status-borrow',
    '归还': 'status-return',
    '续借': 'status-renew',
    '预约': 'status-reserve',
    '取消预约': 'status-cancel'
  };
  return styles[type] || '';
};

// 删除记录
const handleDeleteRecord = (row: any) => {
  recordList.value = recordList.value.filter(item => item.id !== row.id);
  ElMessage.success(`已删除《${row.bookName}》的记录`);
};

// 批量操作
const handleBatchOperation = () => {
  ElMessage.info('批量操作功能待实现');
};
</script>

<style scoped>
.borrow-record-page {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.search-filter-group {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap; /* 适配小屏幕 */
}

.book-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.book-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
}

.book-author, .book-translator {
  font-size: 12px;
  color: #666;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-id {
  font-size: 12px;
  color: #666;
}

/* 操作类型颜色 */
.status-borrow { color: #1890ff; }
.status-return { color: #67c23a; }
.status-renew { color: #faad14; }
.status-reserve { color: #9254de; }
.status-cancel { color: #f56c6c; }

/* 删除记录样式（蓝色文字） */
.text-button {
  color: #1890ff;
  cursor: pointer;
  font-size: 14px;
}

.text-button:hover {
  text-decoration: underline;
}

/* 隐藏多余操作列 */
:deep(.el-table__column) {
  &:last-child:not([data-property="actions"]) {
    display: none !important;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .search-filter-group {
    flex-direction: column;
    align-items: flex-start;
  }
  .search-filter-group > * {
    width: 100% !important;
    margin-left: 0 !important;
    margin-bottom: 10px;
  }
}
</style>