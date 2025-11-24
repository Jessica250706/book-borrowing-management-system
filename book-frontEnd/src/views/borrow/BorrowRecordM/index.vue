<template>
  <div class="borrow-record-page">
    <!-- 搜索筛选栏 -->
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

      <!-- 操作类型状态（徽章样式） -->
      <template #column-operationType="{ row }">
        <span class="status-badge" :class="getOperationTypeClass(row.operationType)">
          {{ row.operationType }}
        </span>
      </template>

      <!-- 操作列 -->
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

// 搜索参数
const searchParams = ref({ 
  keyword: '', 
  category: '',
  operationType: ''
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

// 筛选后的数据
const filteredRecordList = computed(() => {
  return recordList.value.filter(record => {
    const matchKeyword = record.bookName.includes(searchParams.value.keyword);
    const matchCategory = !searchParams.value.category || record.category === searchParams.value.category;
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

// 操作类型样式映射（与图书借阅管理系统对齐）
const getOperationTypeClass = (type: string) => {
  const styles = {
    '借阅': 'status-borrow',       // 绿色（对应可借阅）
    '归还': 'status-return',        // 绿色（对应可借阅）
    '续借': 'status-renew',         // 蓝色（对应待上架）
    '预约': 'status-reserve',       // 橙色（对应未发布）
    '取消预约': 'status-cancel'     // 红色（对应已借光）
  };
  return styles[type] || 'status-default';
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
  padding-bottom: 20px;
    max-width: 1400px;
    margin: 0 auto;
    min-height: 80vh;
}

.search-filter-group {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
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

/* 关键修改：状态徽章样式（与图书借阅管理系统一致） */
.status-badge {
  padding: 2px 8px;
  border-radius: 12px; /* 圆润边角 */
  font-size: 12px;
  font-weight: 500;
  display: inline-block;
  border: 1px solid transparent;
}

/* 颜色体系与第二个图片完全匹配 */
.status-badge.status-borrow { 
  background-color: #f0f9eb; 
  color: #52c41a; 
  border-color: #b7eb8f;
}
.status-badge.status-return { 
  background-color: #f0f9eb; 
  color: #52c41a; 
  border-color: #b7eb8f;
}
.status-badge.status-renew { 
  background-color: #e6f7ff; 
  color: #1890ff; 
  border-color: #91d5ff;
}
.status-badge.status-reserve { 
  background-color: #fff7e6; 
  color: #faad14; 
  border-color: #ffd699;
}
.status-badge.status-cancel { 
  background-color: #fff1f0; 
  color: #f5222d; 
  border-color: #ffccc7;
}
.status-badge.status-default { 
  background-color: #f5f5f5; 
  color: #8c8c8c; 
}

/* 操作按钮样式 */
.text-button {
  color: #1890ff;
  cursor: pointer;
  font-size: 14px;
  padding: 2px 4px;
}

.text-button:hover {
  text-decoration: underline;
  background-color: #f0f7ff;
  border-radius: 2px;
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