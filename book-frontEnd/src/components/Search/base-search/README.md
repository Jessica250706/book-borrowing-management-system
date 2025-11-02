# Search 组件使用说明

Search 组件是一个通用的搜索表单组件，支持多种类型的搜索条件，可以快速构建数据查询界面。

## 功能特性

- **多种搜索类型**：支持输入框、下拉选择、日期范围选择
- **快捷搜索**：支持回车键快速搜索
- **数据同步**：自动同步外部搜索参数变化
- **响应式设计**：自适应布局，样式美观
- **用户体验优化**：输入框带搜索图标，选择器自动触发搜索

## 组件位置

```
src\components\Search\base-search\base-search.vue
```

## 基本用法

```vue
<template>
  <div>
    <!-- 搜索组件 -->
    <BaseSearch
      :search-param="searchParams"
      :search-items="searchConfig"
      @queryTableData="handleQuery"
    />
    
    <!-- 数据表格 -->
    <el-table :data="tableData">
      <!-- 表格列定义 -->
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import BaseSearch from '@/components/Search/base-search/base-search.vue'

// 搜索参数
const searchParams = ref({
  name: '',
  status: '',
  createTime: []
})

// 搜索配置
const searchConfig = ref([
  {
    field: 'name',
    label: '用户名称',
    type: 'input',
    placeholder: '请输入用户名称'
  },
  {
    field: 'status',
    label: '用户状态',
    type: 'select',
    placeholder: '请选择状态',
    options: [
      { label: '启用', value: '1' },
      { label: '禁用', value: '0' }
    ]
  },
  {
    field: 'createTime',
    label: '创建时间',
    type: 'datepicker'
  }
])

// 查询处理
const handleQuery = (params: Record<string, any>) => {
  console.log('查询参数:', params)
  // 调用API获取数据
  fetchTableData(params)
}
</script>
```

## 导入方式

```typescript
// 方式一：直接导入
import BaseSearch from '@/components/Search/base-search/base-search.vue'

// 方式二：如果配置了自动导入
// 直接在模板中使用 <BaseSearch /> 即可
```

## Props 配置

### searchParam
- **类型**: `Record<string, any>`
- **必需**: 是
- **说明**: 搜索参数对象，用于双向数据绑定

### searchItems
- **类型**: `SearchItem[]`
- **必需**: 是
- **说明**: 搜索项配置数组

## SearchItem 数据结构

```typescript
interface SearchItem {
  /** 字段名，对应 searchParam 的键 */
  field: string
  /** 表单项标签 */
  label: string
  /** 表单项类型 */
  type: 'input' | 'select' | 'datepicker'
  /** 占位符文本 */
  placeholder?: string
  /** 下拉选项（仅 type 为 select 时有效） */
  options?: Options[]
}

interface Options {
  label: string
  value: string
}
```

## 事件说明

### queryTableData
- **参数**: `formData: Record<string, any>`
- **说明**: 当用户触发搜索时发出，携带当前表单数据

## 搜索类型详解

### 1. 输入框 (input)
**特点**:
- 支持回车键快速搜索
- 带搜索图标，点击也可触发搜索
- 默认最大长度12个字符

**配置示例**:
```typescript
{
  field: 'keyword',
  label: '关键词',
  type: 'input',
  placeholder: '请输入关键词搜索'
}
```

### 2. 下拉选择 (select)
**特点**:
- 选择后自动触发搜索
- 支持自定义选项
- 自动处理空值

**配置示例**:
```typescript
{
  field: 'category',
  label: '分类',
  type: 'select',
  placeholder: '请选择分类',
  options: [
    { label: '技术', value: 'tech' },
    { label: '产品', value: 'product' },
    { label: '设计', value: 'design' }
  ]
}
```

### 3. 日期范围选择 (datepicker)
**特点**:
- 日期范围选择器
- 固定宽度 270px
- 返回格式: YYYY-MM-DD

**配置示例**:
```typescript
{
  field: 'timeRange',
  label: '时间范围',
  type: 'datepicker'
}
```

## 完整使用示例

### 用户管理搜索
```vue
<template>
  <div class="user-management">
    <BaseSearch
      :search-param="searchForm"
      :search-items="searchItems"
      @queryTableData="getUserList"
    />
    
    <el-table :data="userList" style="width: 100%">
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="createTime" label="创建时间" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import BaseSearch from '@/components/Search/base-search/base-search.vue'

// 搜索表单数据
const searchForm = ref({
  name: '',
  role: '',
  status: '',
  createTime: []
})

// 搜索配置
const searchItems = ref([
  {
    field: 'name',
    label: '用户姓名',
    type: 'input',
    placeholder: '输入用户姓名'
  },
  {
    field: 'role',
    label: '用户角色',
    type: 'select',
    options: [
      { label: '管理员', value: 'admin' },
      { label: '编辑', value: 'editor' },
      { label: '查看者', value: 'viewer' }
    ]
  },
  {
    field: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '启用', value: 'active' },
      { label: '禁用', value: 'inactive' }
    ]
  },
  {
    field: 'createTime',
    label: '注册时间',
    type: 'datepicker'
  }
])

// 获取用户列表
const getUserList = async (params: any) => {
  // 调用API接口
  const response = await userApi.getList(params)
  userList.value = response.data
}

onMounted(() => {
  getUserList(searchForm.value)
})
</script>
```

### 订单管理搜索
```typescript
// 订单搜索配置
const orderSearchItems = ref([
  {
    field: 'orderNo',
    label: '订单号',
    type: 'input',
    placeholder: '输入订单号搜索'
  },
  {
    field: 'orderStatus',
    label: '订单状态',
    type: 'select',
    options: [
      { label: '待支付', value: 'pending' },
      { label: '已支付', value: 'paid' },
      { label: '已发货', value: 'shipped' },
      { label: '已完成', value: 'completed' },
      { label: '已取消', value: 'cancelled' }
    ]
  },
  {
    field: 'orderTime',
    label: '下单时间',
    type: 'datepicker'
  }
])
```

## 组件源码结构

```
src/components/Search/base-search
├── base-search.vue          # 搜索组件主文件
└── types.ts                 # 类型定义文件（可选）
```

## 样式定制

组件使用 SCSS 编写样式，支持自定义：

```scss
// 覆盖默认样式
.above-table {
  .form {
    .form-item {
      width: 300px; // 修改表单项宽度
      margin-right: 20px; // 修改间距
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .above-table {
    .form {
      flex-direction: column;
      
      .form-item {
        width: 100%;
        margin-right: 0;
        margin-bottom: 16px;
      }
    }
  }
}
```

## 注意事项

1. **组件名称**: 组件文件名为 `base-search.vue`，使用时组件名为 `BaseSearch`
2. **字段名一致性**: `field` 属性必须与 `searchParam` 对象的键名保持一致
3. **数据格式**: 日期范围选择器返回的是数组格式 `[startDate, endDate]`
4. **空值处理**: 下拉选择器会自动处理 `null` 和 `undefined` 值
5. **性能优化**: 使用 `watch` 深度监听确保数据同步，避免不必要的渲染

## 最佳实践

1. **合理配置搜索项**: 根据业务需求选择合适的搜索类型
2. **提供默认值**: 为搜索参数提供合理的初始值
3. **处理加载状态**: 在搜索时显示加载状态，提升用户体验
4. **错误处理**: 对搜索请求进行错误捕获和提示

## 兼容性

- Vue 3.x
- Element Plus 2.x
- TypeScript 4.x+

这个 BaseSearch 组件位于 `src/components/Search/base-search` 目录下，提供了灵活的配置方式和良好的用户体验，可以快速在各种管理系统中实现搜索功能。