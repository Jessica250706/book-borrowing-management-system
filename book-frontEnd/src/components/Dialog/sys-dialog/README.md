# SysDialog 系统对话框组件使用说明

SysDialog 是一个通用的系统对话框组件，基于 Element Plus 的 Dialog 组件封装，提供了统一的对话框样式和便捷的使用方式。

## 组件位置

```
src\components\Dialog\sys-dialog\SysDialog.vue
```

## 功能特性

- 🎯 **统一样式**：提供一致的系统对话框样式
- ⚙️ **灵活配置**：支持多种对话框配置选项
- 📱 **响应式**：自适应不同屏幕尺寸
- 🎨 **自定义内容**：支持插槽自定义对话框内容
- 🔧 **类型安全**：完整的 TypeScript 类型支持

## 基本用法

```vue
<template>
  <div>
    <!-- 触发按钮 -->
    <el-button @click="dialogVisible = true">
      打开对话框
    </el-button>

    <!-- 系统对话框 -->
    <SysDialog
      v-model="dialogVisible"
      title="用户信息"
      :show-footer="true"
      @confirm="handleConfirm"
      @cancel="handleCancel"
    >
      <!-- 自定义对话框内容 -->
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="form.age" />
        </el-form-item>
      </el-form>
    </SysDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import SysDialog from '@/components/Dialog/sys-dialog/SysDialog.vue'

const dialogVisible = ref(false)

const form = reactive({
  name: '',
  age: ''
})

const handleConfirm = () => {
  console.log('确认操作', form)
  // 处理确认逻辑
  dialogVisible.value = false
}

const handleCancel = () => {
  console.log('取消操作')
  dialogVisible.value = false
}
</script>
```

## Props 配置

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `modelValue` | `boolean` | `false` | 对话框显示状态，使用 `v-model` 绑定 |
| `title` | `string` | `''` | 对话框标题 |
| `width` | `string` | `'600px'` | 对话框宽度 |
| `show-footer` | `boolean` | `true` | 是否显示底部操作按钮 |
| `confirm-text` | `string` | `'确定'` | 确认按钮文本 |
| `cancel-text` | `string` | `'取消'` | 取消按钮文本 |
| `confirm-loading` | `boolean` | `false` | 确认按钮加载状态 |
| `close-on-click-modal` | `boolean` | `false` | 是否可以通过点击 modal 关闭 Dialog |
| `close-on-press-escape` | `boolean` | `true` | 是否可以通过按下 ESC 关闭 Dialog |
| `show-close` | `boolean` | `true` | 是否显示关闭按钮 |
| `append-to-body` | `boolean` | `true` | Dialog 自身是否插入至 body 元素上 |

## 事件说明

| 事件名 | 参数 | 说明 |
|--------|------|------|
| `update:modelValue` | `(value: boolean)` | 对话框显示状态更新事件 |
| `confirm` | `()` | 确认按钮点击事件 |
| `cancel` | `()` | 取消按钮点击事件 |
| `open` | `()` | 对话框打开事件 |
| `close` | `()` | 对话框关闭事件 |

## 插槽说明

| 插槽名 | 说明 |
|--------|------|
| `default` | 对话框主要内容区域 |
| `title` | 自定义标题内容 |
| `footer` | 自定义底部内容 |

## 完整使用示例

### 表单对话框示例
```vue
<template>
  <div>
    <el-button type="primary" @click="openUserDialog">
      添加用户
    </el-button>

    <SysDialog
      v-model="userDialogVisible"
      :title="dialogTitle"
      width="700px"
      :confirm-loading="loading"
      @confirm="submitUserForm"
      @cancel="resetForm"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入用户姓名" />
        </el-form-item>
        
        <el-form-item label="用户角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="admin" />
            <el-option label="编辑" value="editor" />
            <el-option label="查看者" value="viewer" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
    </SysDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import SysDialog from '@/components/Dialog/sys-dialog/SysDialog.vue'

const userDialogVisible = ref(false)
const loading = ref(false)
const userFormRef = ref<FormInstance>()

const userForm = reactive({
  name: '',
  role: '',
  email: '',
  phone: ''
})

const userRules: FormRules = {
  name: [{ required: true, message: '请输入用户姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => {
  return userForm.id ? '编辑用户' : '添加用户'
})

const openUserDialog = () => {
  userDialogVisible.value = true
}

const submitUserForm = async () => {
  if (!userFormRef.value) return
  
  try {
    const valid = await userFormRef.value.validate()
    if (!valid) return
    
    loading.value = true
    // 调用API提交表单
    await userApi.save(userForm)
    ElMessage.success('保存成功')
    userDialogVisible.value = false
    resetForm()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  userFormRef.value?.resetFields()
  Object.assign(userForm, {
    name: '',
    role: '',
    email: '',
    phone: ''
  })
}
</script>
```

### 确认对话框示例
```vue
<template>
  <div>
    <el-button type="danger" @click="openDeleteDialog">
      删除用户
    </el-button>

    <SysDialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="500px"
      @confirm="handleDelete"
    >
      <div class="delete-content">
        <el-icon color="#e6a23c" size="20">
          <Warning />
        </el-icon>
        <span>确定要删除选中的用户吗？此操作不可恢复。</span>
      </div>
    </SysDialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'
import SysDialog from '@/components/Dialog/sys-dialog/SysDialog.vue'

const deleteDialogVisible = ref(false)

const openDeleteDialog = () => {
  deleteDialogVisible.value = true
}

const handleDelete = async () => {
  try {
    // 调用删除API
    await userApi.delete(selectedUserId.value)
    ElMessage.success('删除成功')
    deleteDialogVisible.value = false
  } catch (error) {
    ElMessage.error('删除失败')
  }
}
</script>

<style scoped>
.delete-content {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 0;
}
</style>
```

### 自定义底部插槽示例
```vue
<template>
  <SysDialog
    v-model="customDialogVisible"
    title="自定义底部"
    :show-footer="false"
  >
    <div>对话框内容</div>
    
    <template #footer>
      <div class="custom-footer">
        <el-button>辅助操作</el-button>
        <div>
          <el-button @click="customDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCustomConfirm">确定</el-button>
        </div>
      </div>
    </template>
  </SysDialog>
</template>

<style scoped>
.custom-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}
</style>
```

## 组件目录结构

```
src/components/Dialog/
├── sys-dialog/
│   ├── SysDialog.vue          # 对话框组件主文件
│   └── index.ts               # 组件导出文件（可选）
└── index.ts                   # 对话框组件统一导出
```

## 导入方式

```typescript
// 方式一：直接导入
import SysDialog from '@/components/Dialog/sys-dialog/SysDialog.vue'

// 方式二：如果配置了统一导出
import { SysDialog } from '@/components/Dialog'
```

## 样式定制

组件支持通过 CSS 变量或样式覆盖进行自定义：

```css
/* 自定义对话框样式 */
.sys-dialog {
  --dialog-header-bg: #409eff;
  --dialog-header-color: #fff;
}

/* 覆盖默认样式 */
.sys-dialog .el-dialog__header {
  background: var(--dialog-header-bg);
  color: var(--dialog-header-color);
}
```

## 最佳实践

1. **表单验证**：在对话框中使用表单时，务必添加表单验证
2. **加载状态**：异步操作时使用 `confirm-loading` 提供反馈
3. **数据重置**：对话框关闭时重置表单数据
4. **错误处理**：对异步操作进行错误捕获和用户提示
5. **可访问性**：确保对话框可以通过键盘操作

## 注意事项

1. **组件路径**：组件位于 `src/components/Dialog/sys-dialog/` 目录下
2. **双向绑定**：使用 `v-model` 控制对话框显示隐藏
3. **表单引用**：在 Composition API 中正确使用模板引用
4. **内存管理**：及时清理事件监听器和定时器

## 兼容性

- Vue 3.x
- Element Plus 2.x
- TypeScript 4.x+

SysDialog 组件提供了统一的对话框解决方案，可以快速在各种业务场景中创建一致的用户体验。