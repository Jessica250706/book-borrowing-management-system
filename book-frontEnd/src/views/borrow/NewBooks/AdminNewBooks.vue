<template>
  <div class="page-container">
    <h1>表格组件展示</h1>
    
    <!-- 使用 Table 组件 -->
    <Table
      :data="tableData"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="true"
      :show-selection="true"
      :show-index="true"
      :show-actions="true"
      :actions="actions"
      @selection-change="handleSelectionChange"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      @action-click="handleActionClick"
    >
      <!-- 自定义列插槽示例 -->
      <template #column-status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </template>

      <!-- 自定义操作列插槽 -->
      <template #actions="{ row }">
        <el-button type="primary" size="small" @click="handleView(row)">
          查看
        </el-button>
        <el-button type="success" size="small" @click="handleEdit(row)">
          编辑
        </el-button>
        <el-button type="danger" size="small" @click="handleDelete(row)">
          删除
        </el-button>
      </template>
    </Table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Table from '@/components/mytable/Table.vue'
import { ElMessageBox, ElMessage } from 'element-plus'

// 表格数据
const loading = ref(false)
const total = ref(0)
const tableData = ref<Array<{
  id: number
  name: string
  age: number
  email: string
  status: number
  createTime: string
}>>([])

// 定义列配置类型
interface TableColumn {
  prop: string
  label: string
  width?: string
  align?: 'left' | 'center' | 'right'
}

// 列配置 - 添加类型注解
const columns = ref<TableColumn[]>([
  {
    prop: 'name',
    label: '姓名',
    width: '120',
    align: 'center'
  },
  {
    prop: 'age',
    label: '年龄',
    width: '100',
    align: 'center'
  },
  {
    prop: 'email',
    label: '邮箱',
    width: '200'
  },
  {
    prop: 'status',
    label: '状态',
    width: '100',
    align: 'center'
  },
  {
    prop: 'createTime',
    label: '创建时间',
    width: '180',
    align: 'center'
  }
])

interface ActionConfig {
  name: string
  label: string
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
}

// 操作按钮配置
const actions = ref<ActionConfig[]>([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'edit', label: '编辑', type: 'success' },
  { name: 'delete', label: '删除', type: 'danger' }
])

// 模拟数据
const mockData = [
  {
    id: 1,
    name: '张三',
    age: 25,
    email: 'zhangsan@example.com',
    status: 1,
    createTime: '2023-01-01 10:00:00'
  },
  {
    id: 2,
    name: '李四',
    age: 30,
    email: 'lisi@example.com',
    status: 0,
    createTime: '2023-01-02 14:30:00'
  },
  {
    id: 3,
    name: '王五',
    age: 28,
    email: 'wangwu@example.com',
    status: 1,
    createTime: '2023-01-03 09:15:00'
  },
  {
    id: 4,
    name: '赵六',
    age: 35,
    email: 'zhaoliu@example.com',
    status: 1,
    createTime: '2023-01-04 16:45:00'
  },
  {
    id: 5,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 6,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 7,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 8,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 9,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 10,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 11,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 12,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 13,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 14,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 15,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  },
  {
    id: 16,
    name: '钱七',
    age: 22,
    email: 'qianqi@example.com',
    status: 0,
    createTime: '2023-01-05 11:20:00'
  }
]

// 事件处理
const handleSelectionChange = (selection: any[]) => {
  console.log('选中行:', selection)
}

const handleSizeChange = (size: number) => {
  console.log('每页大小改变:', size)
  loadData()
}

const handleCurrentChange = (page: number) => {
  console.log('当前页改变:', page)
  loadData()
}

const handleActionClick = (action: string, row: any) => {
  console.log('操作点击:', action, row)
  switch (action) {
    case 'detail':
      handleView(row)
      break
    case 'edit':
      handleEdit(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

const handleView = (row: any) => {
  console.log('查看:', row)
  // 这里可以打开详情弹窗等
}

const handleEdit = (row: any) => {
  console.log('编辑:', row)
  // 这里可以打开编辑弹窗等
}

const handleDelete = (row: any) => {
  console.log('删除:', row)
  // 这里可以显示确认删除对话框
  ElMessageBox.confirm('确定删除这条数据吗？', '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  })
}

// 加载数据
const loadData = () => {
  loading.value = true
  // 模拟异步请求
  setTimeout(() => {
    tableData.value = mockData
    total.value = mockData.length
    loading.value = false
  }, 500)
}

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

h1 {
  margin-bottom: 20px;
  color: #303133;
}
</style>