<template>
  <div>
    <div>currentBorrow</div>
    <BookTable 
      :data="bookList" 
      :columns="columns" 
      :total="600" 
      :actions="customActions"
    >
      
      <!-- 自定义书籍信息列：使用BookInfo组件 -->
      <template #column-bookInfo="{ row }">
        <BookInfo :book="row" />
      </template>
      
      <!-- 自定义操作列：替换默认按钮 -->
      <template #actions="{ row }">
        <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
        <el-button type="warning" size="small" @click="handleReturn(row)">归还</el-button>
        <el-button type="success" size="small" @click="handleReBorrow(row)">续借</el-button>
      </template>
    </BookTable>
  </div>
</template>

<script setup lang="ts">
import BookTable from '@/components/mytable/Table.vue';
import BookInfo from '@/components/BookInfo/BookInfo.vue';
import { ref } from 'vue';

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);

// 模拟书籍数据
const bookList = ref([
  {
    id: 1,
    bookImg: 'https://picsum.photos/100/140?random=1',
    bookName: '不要让未来的你讨厌现在的自己',
    author: 'gengeng',
    translator: '佚名',
    category: '社会人文',
    status: '待发布',
    shelfTime: '2022/06/12'
  },
  {
    id: 2,
    bookImg: 'https://picsum.photos/100/140?random=2',
    bookName: '多情却被无情恼',
    author: '佚名',
    translator: '失名',
    category: '社会人文',
    status: '可借阅',
    shelfTime: '2022/06/12'
  }
]);

// 列配置
const columns = ref([
  { prop: 'bookInfo', label: '书籍信息', width: 320, align: 'left' },
  { prop: 'category', label: '分类', width: 120, align: 'center' },
  { prop: 'status', label: '状态', width: 120, align: 'center' },
  { prop: 'shelfTime', label: '上新时间', width: 160, align: 'center' },
]);

// 操作列配置（必须传入，否则组件内操作按钮会因undefined报错）
const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'edit', label: '编辑', type: 'success' },
  { name: 'delete', label: '删除', type: 'danger' },
  { name: 'publish', label: '发布', type: 'warning' },
]);

// 操作按钮事件
const handleDetail = (row: any) => console.log('查看详情：', row);
const handleEdit = (row: any) => console.log('编辑书籍：', row);
const handleDelete = (row: any) => console.log('删除书籍：', row);
</script>