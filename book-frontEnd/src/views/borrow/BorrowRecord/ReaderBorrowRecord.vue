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
          <div class="book-text">
            <div class="book-name">{{ row.bookName }}</div>
            <div class="book-author">作者: {{ row.author }}</div>
            <div class="book-translator">译者: {{ row.translator }}</div>
          </div>
        </div>
      </template>

      <!-- 操作类型状态（与第二个图片样式对齐） -->
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

// 模拟借阅记录数据（保持不变）
const recordList = ref([
  {
    id: 1,
    bookName: '思考无烦恼',
    bookImg: 'https://picsum.photos/60/80?random=201',
    author: 'gengeng',
    translator: '袁国忠',
    category: '经济',
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
    operationType: '取消预约',
    operationTime: '2022/01/08 12:00:00'
  }
]);

// 筛选后的数据（保持不变）
const filteredRecordList = computed(() => {
  return recordList.value.filter(record => {
    const matchKeyword = record.bookName.includes(searchParams.value.keyword);
    const matchCategory = !searchParams.value.category || record.category === searchParams.value.category;
    const matchOperation = !searchParams.value.operationType || record.operationType === searchParams.value.operationType;
    return matchKeyword && matchCategory && matchOperation;
  });
});

// 表格列配置（保持不变）
const columns = ref([
  { prop: 'bookInfo', label: '书籍名称', width: 250 },
  { prop: 'category', label: '书籍分类', width: 120 },
  { prop: 'operationType', label: '操作类别', width: 120 },
  { prop: 'operationTime', label: '操作时间', width: 180 },
]);

// 操作类型样式映射（关键修改：与第二个图片状态色对齐）
const getOperationTypeClass = (type: string) => {
  // 对应第二个图片的颜色体系：绿色=可借阅/归还，蓝色=续借/待上架，橙色=预约/未发布，红色=取消预约/已借光
  const styles = {
    '借阅': 'status-borrow',       // 绿色（对应可借阅）
    '归还': 'status-return',        // 绿色（对应可借阅）
    '续借': 'status-renew',         // 蓝色（对应待上架）
    '预约': 'status-reserve',       // 橙色（对应未发布）
    '取消预约': 'status-cancel'     // 红色（对应已借光）
  };
  return styles[type] || 'status-default';
};

// 删除记录（保持不变）
const handleDeleteRecord = (row: any) => {
  recordList.value = recordList.value.filter(item => item.id !== row.id);
  ElMessage.success(`已删除《${row.bookName}》的记录`);
};

// 批量操作（保持不变）
const handleBatchOperation = () => {
  ElMessage.info('批量操作功能待实现');
};
</script>

<style scoped>
/* 基础样式保持不变 */
.book-borrow-page {
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
  align-items: flex-start;
  gap: 12px;
  padding: 8px 0;
  width: 100%;
}

.book-cover {
  width: 64px;
  height: 88px;
  object-fit: cover;
}

.book-text {
  text-align: left;
  line-height: 1.6;
}

.book-name {
  font-weight: 500;
  margin-bottom: 6px;
}

.book-author, .book-translator {
  font-size: 12px;
  color: #666;
  margin-bottom: 6px;
}

/* 关键修改：状态徽章样式与第二个图片完全一致 */
.status-badge {
  /* 浅色背景+同色文字（与第二个图片一致） */
  padding: 2px 8px;
  border-radius: 12px; /* 更圆润的边角 */
  font-size: 12px;
  font-weight: 500;
  display: inline-block;
  border: 1px solid transparent; /* 预留边框位置 */
}

/* 颜色体系完全对齐第二个图片 */
.status-badge.status-borrow { 
  background-color: #f0f9eb; /* 浅绿色背景 */
  color: #52c41a; /* 绿色文字（对应“可借阅”） */
  border-color: #b7eb8f;
}
.status-badge.status-return { 
  background-color: #f0f9eb; 
  color: #52c41a; 
  border-color: #b7eb8f;
}
.status-badge.status-renew { 
  background-color: #e6f7ff; /* 浅蓝色背景 */
  color: #1890ff; /* 蓝色文字（对应“待上架”） */
  border-color: #91d5ff;
}
.status-badge.status-reserve { 
  background-color: #fff7e6; /* 浅橙色背景 */
  color: #faad14; /* 橙色文字（对应“未发布”） */
  border-color: #ffd699;
}
.status-badge.status-cancel { 
  background-color: #fff1f0; /* 浅红色背景 */
  color: #f5222d; /* 红色文字（对应“已借光”） */
  border-color: #ffccc7;
}
.status-badge.status-default { 
  background-color: #f5f5f5; 
  color: #8c8c8c; 
}

/* 操作按钮样式保持不变 */
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

/* 表格样式调整 */
:deep(.el-table-column[data-property="bookInfo"]) {
  text-align: left !important;
}

:deep(.el-table__cell) {
  padding-left: 12px !important;
  padding-right: 12px !important;
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