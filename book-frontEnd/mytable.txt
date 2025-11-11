<template>
  <div class="book-table-page">
    <!-- 直接使用Element Plus表格 -->
      <el-table
        :data="tableData.rows"
        border
        stripe
        :row-key="(row) => row.id"  
        table-layout="fixed"  
        style="width: 100%; margin: 0;"  
      >
      <!-- 1. 复选框列 -->
      <el-table-column
        type="selection"
        width="55px"
        fixed="left"
        align="center"
      ></el-table-column>
      <!-- 2. 序号列（自定义连续序号） -->
      <el-table-column
        label="序号"
        width="80px"
        align="center"
        fixed="left"
      >
        <template #default="scope">
          {{ (tableData.pageIndex - 1) * tableData.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <!-- 3. 书籍信息列（使用已封装的BookInfo组件） -->
      <el-table-column
        label="书籍信息"
        width="320px"
        align="left"
      >
        <template #default="scope">
          <BookInfo :book="scope.row" />
        </template>
      </el-table-column>
      <!-- 4. 分类列 -->
      <el-table-column
        label="分类"
        width="120px"
        align="center"
      >
        <template #default="scope">
          {{ scope.row.category }}
        </template>
      </el-table-column>
      <!-- 5. 状态列 -->
      <el-table-column
        label="状态"
        width="120px"
        align="center"
      >
        <template #default="scope">
          {{ scope.row.status }}
        </template>
      </el-table-column>
      <!-- 6. 上新时间列 -->
      <el-table-column
        label="上新时间"
        width="160px"
        align="center"
      >
        <template #default="scope">
          {{ scope.row.shelfTime }}
        </template>
      </el-table-column>
      <!-- 7. 操作列（加宽以解决拥挤问题） -->
      <el-table-column
        label="操作"
        width="240px"  
        align="center"
        fixed="right"
      >
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleDetail(scope.row)">详情</el-button>
          <el-button type="success" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="tableData.pageIndex"
      :page-sizes="[10, 20, 50]"
      :page-size="tableData.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="tableData.total"
      style="margin-top: 16px; text-align: right;"
    ></el-pagination>
  </div>
</template>

<script setup lang="ts">
import BookInfo from '@/components/BookInfo/BookInfo.vue'; // 引入已封装的书籍组件
import { ref } from 'vue';

// 书籍数据结构（与BookInfo组件props匹配）
interface Book {
  id: number;
  bookImg: string;
  bookName: string;
  author: string;
  translator: string;
  category: string;
  status: string;
  shelfTime: string;
}

// 分页数据结构
interface PageData {
  pageIndex: number;
  pageSize: number;
  total: number;
  rows: Book[];
}
// 修正状态数组，确保不会产生undefined
const statusList = ['待发布', '可借阅', '已借光', '待上架'];

// 模拟全量书籍数据
const mockBookData = Array.from({ length: 200 }).map((_, index) => ({
  id: index + 1,
  bookImg: `https://picsum.photos/100/140?random=book${index}`,
  bookName: `书籍${index + 1}`,
  author: index % 2 === 0 ? 'gengeng' : '佚名',
  translator: index % 3 === 0 ? '佚名' : '失名',
  category: '社会人文',
  status: statusList[index % statusList.length],  // 确保取值安全
  shelfTime: '2022/06/12'
}));

// 初始化分页数据
const tableData = ref<PageData>({
  pageIndex: 1,
  pageSize: 10,
  total: mockBookData.length,
  rows: mockBookData.slice(0, 10)
});

// 分页事件：切换页码/每页条数时更新数据
const handleSizeChange = (size: number) => {
  const start = (tableData.value.pageIndex - 1) * size;
  const end = start + size;
  tableData.value.pageSize = size;
  // 明确类型转换
  tableData.value.rows = mockBookData.slice(start, end) as unknown as Book[];
};

const handleCurrentChange = (page: number) => {
  const start = (page - 1) * tableData.value.pageSize;
  const end = start + tableData.value.pageSize;
  tableData.value.pageIndex = page;
  // 明确类型转换
  tableData.value.rows = mockBookData.slice(start, end) as unknown as Book[];
};

// 操作按钮事件
const handleDetail = (row: Book) => {
  console.log('查看详情：', row);
};
const handleEdit = (row: Book) => {
  console.log('编辑书籍：', row);
};
const handleDelete = (row: Book) => {
  console.log('删除书籍：', row);
};
</script>

<style scoped>
.book-table-page {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

/* 修复Element Plus表格分割线对齐和边距问题 */
::v-deep .el-table {
  border-collapse: collapse !important;
}
::v-deep .el-table__fixed-right,
::v-deep .el-table__fixed-left {
  height: 100% !important;
  box-shadow: none !important;
}

/* 统一表格单元格内边距，解决对齐问题 */
::v-deep .el-table__header,
::v-deep .el-table__body {
  width: 100% !important;
}
::v-deep .el-table__cell {
  padding: 8px 0 !important;  /* 统一单元格内边距 */
}
</style>