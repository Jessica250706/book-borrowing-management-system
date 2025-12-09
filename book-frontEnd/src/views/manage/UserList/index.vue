<template>
  <div class="admin-user-manage">
    <!-- 页面标题和操作区域 -->
    <div class="page-header">
      <div class="search-section">
        <!-- 搜索框 -->
        <div class="search-input-container">
          <el-input
            v-model="filterForm.keyword"
            placeholder="请输入用户名称/uid"
            clearable
            :maxlength="12"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
            style="width: 200px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- 权限角色筛选 -->
        <div class="filter-group">
          <span class="filter-label">权限角色:</span>
          <el-select
            v-model="filterForm.roleFilter"
            placeholder="所有角色"
            clearable
            @change="handleSearch"
            style="width: 150px"
          >
            <el-option label="所有角色" :value="ROLES.FILTER.ALL" />
            <el-option
              :label="ROLES.NAME.READER_SOCIAL"
              :value="ROLES.FILTER.READER_SOCIAL"
            />
            <el-option
              :label="ROLES.NAME.READER_STUDENT"
              :value="ROLES.FILTER.READER_STUDENT"
            />
            <el-option
              :label="ROLES.NAME.READER_TEACHER"
              :value="ROLES.FILTER.READER_TEACHER"
            />
            <el-option
              :label="ROLES.NAME.ADMIN"
              :value="ROLES.FILTER.ADMIN"
            />
            <el-option
              :label="ROLES.NAME.SUPER_ADMIN"
              :value="ROLES.FILTER.SUPER_ADMIN"
            />
          </el-select>
        </div>
      </div>
    </div>

    <!-- 表格组件 -->
    <Table
      :data="tableData"
      :columns="columns"
      :loading="loading"
      :total="total"
      :actions="actions"
      :show-selection="true"
      :show-index="true"
      :show-actions="true"
      :pagination="false"
      @selection-change="handleSelectionChange"
      @action-click="handleActionClick"
    >
      <!-- 用户信息列 -->
      <template #column-userInfo="{ row }">
        <div class="user-info-cell">
          <div class="user-avatar">
            <el-avatar :size="40" :src="row.avatar" />
          </div>
          <div class="user-details">
            <div class="user-name" :title="row.username">
              {{ truncateUsername(row.username) }}
            </div>
            <div class="user-uid">UID: {{ row.uid }}</div>
          </div>
        </div>
      </template>

      <!-- 权限角色列 -->
      <template #column-role="{ row }">
        <div class="role-cell">
          <el-tag
            :type="getRoleType(row.roleName)"
            effect="light"
            size="small"
          >
            {{ row.roleName }}
          </el-tag>
        </div>
      </template>

      <!-- 借阅信用列 -->
      <template #column-credit="{ row }">
        <div class="credit-cell" v-if="row.creditScore !== undefined && row.creditScore !== null">
          <span class="credit-score">{{ row.creditScore }}分</span>
          <span class="credit-rating" :class="getCreditRatingClass(row.creditScore)">
            {{ row.creditLevel }}
          </span>
        </div>
        <div v-else class="no-credit">-</div>
      </template>

      <!-- 账号状态列 -->
      <template #column-status="{ row }">
        <div class="status-cell">
          <el-tag
            :type="getStatusType(row.status)"
            effect="light"
            size="small"
          >
            {{ row.statusName }}
          </el-tag>
        </div>
      </template>

      <!-- 注册时间列 -->
      <template #column-registerTime="{ row }">
        <div class="register-time-cell">
          {{ formatDateTime(row.registerTime) }}
        </div>
      </template>

      <!-- 操作列 -->
      <template #actions="{ row }">
        <div class="action-buttons">
          <span 
            v-if="row.canUpgradeRole"
            class="action-text upgrade"
            @click="handleUpgradeRole(row)"
          >
            升级权限
          </span>
          <span 
            v-if="!row.canUpgradeRole && isReaderRole(row.roleId)"
            class="action-text upgrade disabled"
          >
            已升级
          </span>
        </div>
      </template>
    </Table>

    <!-- 无数据提示 -->
    <div v-if="!loading && tableData.length === 0" class="no-data">
      <el-empty description="当前无相关用户" />
    </div>

    <!-- 分页控件 -->
    <div v-if="tableData.length > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="paginationConfig.pageSizes"
        :layout="paginationConfig.layout"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 升级权限确认对话框 -->
    <el-dialog
      v-model="upgradeDialog.visible"
      :title="upgradeDialog.title"
      width="400px"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="upgrade-dialog-content">
        <div class="upgrade-message">
          <el-icon v-if="upgradeDialog.hasBorrowedBooks" class="warning-icon">
            <Warning />
          </el-icon>
          {{ upgradeDialog.message }}
        </div>
        
        <div v-if="upgradeDialog.hasBorrowedBooks" class="warning-info">
          <el-alert
            type="warning"
            :closable="false"
            show-icon
          >
            确认后会自动归还所有书籍
          </el-alert>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="upgradeDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="confirmUpgradeRole" :loading="upgradeDialog.loading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Warning } from '@element-plus/icons-vue'
import Table from '@/components/mytable/Table.vue'

// 导入API和类型
import { 
  getUsers, 
  updateUserRole, 
  checkUserRoleChange,
  updateUserStatus,
  isReaderRole,
  ROLES,
  USER_STATUS,
  type UserListResponseDTO,
  type CheckRoleChangeRequest,
  type UpdateUserRoleRequest,
  type UpdateUserStatusRequest,
  type RoleInfoDTO,
  type CreditInfoDTO,
  type AccountStatusDTO
} from '@/apis/user/index'

// 类型定义
interface User {
  userId: number
  serialNumber: number
  uid: string
  username: string
  avatar: string
  roleId: number
  roleCode: string
  roleName: string
  creditScore?: number
  creditLevel?: string
  status: number
  statusName: string
  statusDesc: string
  registerTime: string
  canUpgradeRole: boolean
  hasBorrowingBooks: boolean
  borrowedCount?: number
  unreturnedCount?: number
  email?: string
  phone?: string
}

interface FilterForm {
  keyword: string
  roleFilter: string
}

interface UpgradeDialog {
  visible: boolean
  title: string
  message: string
  userId: number
  newRoleId: number
  loading: boolean
  hasBorrowedBooks: boolean
}

// 响应式数据
const loading = ref(false)
const allTableData = ref<User[]>([])
const filteredData = ref<User[]>([])
const total = ref(0)
const selectedRows = ref<User[]>([])
const currentPage = ref(1)
const pageSize = ref(10)

// 筛选表单
const filterForm = reactive<FilterForm>({
  keyword: '',
  roleFilter: ROLES.FILTER.ALL
})

// 升级权限对话框
const upgradeDialog = reactive<UpgradeDialog>({
  visible: false,
  title: '升级权限',
  message: '',
  userId: 0,
  newRoleId: 0,
  loading: false,
  hasBorrowedBooks: false
})

// 分页配置
const paginationConfig = reactive({
  pageSizes: [10, 20, 30, 50],
  layout: "total, sizes, prev, pager, next, jumper"
})

// 状态名称映射
const statusNameMap: Record<number, string> = {
  [USER_STATUS.NORMAL]: '正常',
  [USER_STATUS.FROZEN]: '冻结',
  [USER_STATUS.DISABLED]: '停用',
  [USER_STATUS.DELETED]: '注销'
}

// 计算当前页要显示的数据
const tableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

// 表格列配置
const columns = [
  {
    prop: 'userInfo',
    label: '用户',
    width: '280px',
    align: 'left' as const
  },
  {
    prop: 'role',
    label: '权限角色',
    width: '120px',
    align: 'center' as const
  },
  {
    prop: 'credit',
    label: '借阅信用',
    width: '120px',
    align: 'center' as const
  },
  {
    prop: 'status',
    label: '账号状态',
    width: '100px',
    align: 'center' as const
  },
  {
    prop: 'registerTime',
    label: '注册时间',
    width: '180px',
    align: 'center' as const
  }
]

// 操作按钮配置
const actions = [
  { name: 'detail', label: '详情', type: 'primary' as const },
  { name: 'edit', label: '编辑', type: 'success' as const },
  { name: 'freeze', label: '冻结', type: 'warning' as const }
]

// 用户名截断
const truncateUsername = (username: string) => {
  if (!username) return ''
  if (username.length > 10) {
    return username.substring(0, 10) + '...'
  }
  return username
}

// 格式化日期时间
const formatDateTime = (dateTime: string): string => {
  if (!dateTime) return '-'
  try {
    const date = new Date(dateTime)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch {
    return dateTime
  }
}

// 获取信用评级
const getCreditRating = (score: number): string => {
  if (score >= 80) return '优'
  if (score >= 60) return '良'
  return '差'
}

// 获取信用评级样式类
const getCreditRatingClass = (score: number): string => {
  if (score >= 80) return 'credit-excellent'
  if (score >= 60) return 'credit-good'
  return 'credit-poor'
}

// 获取角色标签类型
const getRoleType = (roleName: string) => {
  const typeMap: Record<string, string> = {
    [ROLES.NAME.READER_SOCIAL]: 'info',
    [ROLES.NAME.READER_STUDENT]: 'primary',
    [ROLES.NAME.READER_TEACHER]: 'success',
    [ROLES.NAME.ADMIN]: 'warning',
    [ROLES.NAME.SUPER_ADMIN]: 'danger'
  }
  return typeMap[roleName] || 'info'
}

// 获取状态标签类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    [USER_STATUS.NORMAL]: 'success',
    [USER_STATUS.FROZEN]: 'warning',
    [USER_STATUS.DISABLED]: 'info',
    [USER_STATUS.DELETED]: 'danger'
  }
  return typeMap[status] || 'info'
}

// 事件处理函数
const handleSelectionChange = (selection: User[]) => {
  selectedRows.value = selection
}

const handleActionClick = (action: string, row: User) => {
  switch (action) {
    case 'detail':
      handleDetail(row)
      break
    case 'edit':
      handleEdit(row)
      break
    case 'freeze':
      handleFreeze(row)
      break
  }
}

const handleDetail = (row: User) => {
  ElMessage.success(`查看用户详情: ${row.username}`)
  // TODO: 跳转到用户详情页面
  console.log('查看用户详情:', row.userId)
}

const handleEdit = (row: User) => {
  ElMessage.success(`编辑用户: ${row.username}`)
  // TODO: 跳转到用户编辑页面
  console.log('编辑用户:', row.userId)
}

// 冻结/解冻用户
const handleFreeze = async (row: User) => {
  try {
    const isFrozen = row.status === USER_STATUS.FROZEN
    const action = isFrozen ? '解冻' : '冻结'
    
    await ElMessageBox.confirm(
      `是否${action}用户 ${row.username}？`,
      `${action}用户`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const newStatus = isFrozen ? USER_STATUS.NORMAL : USER_STATUS.FROZEN
    
    try {
      loading.value = true
      
      // 通过 response.data 访问数据
      const response = await updateUserStatus({
        userId: row.userId,
        status: newStatus,
        remark: `系统管理员操作：${action}用户`
      })
      
      // 通过 response.data 访问
      if (response.data?.code === 200) {
        // 重新加载数据
        await loadData()
        ElMessage.success(`${action}成功`)
      } else {
        ElMessage.error(response.data?.message || `${action}失败`)
      }
    } catch (error: any) {
      console.error(`${action}用户失败:`, error)
      ElMessage.error(error.response?.data?.message || `${action}失败，请重试`)
    } finally {
      loading.value = false
    }
  } catch {
    ElMessage.info('取消操作')
  }
}

// 删除用户
const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 ${row.username} 吗？删除后用户将无法登录系统。`,
      '删除用户',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    try {
      loading.value = true
      
      // 调用真实API
      const response = await updateUserStatus({
        userId: row.userId,
        status: USER_STATUS.DELETED,
        remark: '系统管理员操作：删除用户'
      })
      
      // 通过 response.data 访问数据
      if (response.data?.code === 200) {
        // 重新加载数据
        await loadData()
        ElMessage.success('删除成功')
      } else {
        ElMessage.error(response.data?.message || '删除失败')
      }
    } catch (error: any) {
      console.error('删除用户失败:', error)
      ElMessage.error(error.response?.data?.message || error.message || '删除失败，请重试')
    } finally {
      loading.value = false
    }
  } catch {
    ElMessage.info('取消删除')
  }
}

// 升级权限处理
const handleUpgradeRole = async (row: User) => {
  try {
    loading.value = true
    
    try {
      // 检查用户是否可以修改角色
      const response = await checkUserRoleChange({
        userId: row.userId,
        newRoleId: ROLES.ID.ADMIN
      })
      
      // 通过 response.data 访问数据
      const checkData = response.data
      
      upgradeDialog.userId = row.userId
      upgradeDialog.newRoleId = ROLES.ID.ADMIN
      upgradeDialog.hasBorrowedBooks = row.hasBorrowingBooks || (checkData?.data?.hasUnreturnedBooks || false)
      
      if (row.hasBorrowingBooks) {
        upgradeDialog.title = '升级权限'
        upgradeDialog.message = `当前用户尚未归还所有书籍，是否提升该用户为管理员？`
      } else {
        upgradeDialog.title = '升级权限'
        upgradeDialog.message = `是否提升该用户 ${row.username} 为管理员？`
      }
      
      upgradeDialog.visible = true
    } catch (error: any) {
      console.error('检查用户角色变更失败:', error)
      ElMessage.error('检查用户状态失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

// 确认升级权限
const confirmUpgradeRole = async () => {
  try {
    upgradeDialog.loading = true
    
    // 调用真实API
    const response = await updateUserRole({
      userId: upgradeDialog.userId,
      roleId: upgradeDialog.newRoleId,
      remark: '系统管理员操作：升级用户权限'
    })
    
    // 通过 response.data 访问数据
    if (response.data?.code === 200) {
      // 重新加载数据
      await loadData()
      upgradeDialog.visible = false
      ElMessage.success('权限升级成功')
    } else {
      ElMessage.error(response.data?.message || '权限升级失败')
    }
  } catch (error: any) {
    console.error('升级权限失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '升级权限失败，请重试')
  } finally {
    upgradeDialog.loading = false
  }
}


// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

// 分页事件处理
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  loadData()
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
  loadData()
}

// 转换API数据到本地User类型
const convertToUser = (apiData: UserListResponseDTO): User => {
  const roleInfo = apiData.roleInfo || {} as RoleInfoDTO
  const creditInfo = apiData.creditInfo || {} as CreditInfoDTO
  const accountStatus = apiData.accountStatus || {} as AccountStatusDTO
  
  return {
    userId: apiData.userId || 0,
    serialNumber: apiData.serialNumber || 0,
    uid: apiData.uid || '',
    username: apiData.username || '',
    avatar: apiData.avatar || '',
    roleId: roleInfo.roleId || 0,
    roleCode: roleInfo.roleCode || '',
    roleName: roleInfo.roleName || '',
    creditScore: creditInfo.show ? creditInfo.score : undefined,
    creditLevel: creditInfo.show ? creditInfo.level : undefined,
    status: accountStatus.code || USER_STATUS.NORMAL,
    statusName: getStatusName(accountStatus.code || USER_STATUS.NORMAL),
    statusDesc: accountStatus.desc || '',
    registerTime: apiData.registerTime || '',
    canUpgradeRole: apiData.canUpgradeRole || false,
    hasBorrowingBooks: apiData.hasBorrowingBooks || false
  }
}

// 获取状态名称
const getStatusName = (status: number): string => {
  return statusNameMap[status] || '未知状态'
}

const loadData = async () => {
  loading.value = true
  
  try {
    const response = await getUsers({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: filterForm.keyword || undefined,
      roleFilter: filterForm.roleFilter !== ROLES.FILTER.ALL ? filterForm.roleFilter : undefined
    })
    
    // 通过 response.data 访问数据
    const responseData = response.data
    
    if (responseData?.code === 200) {
      const data = responseData.data
      
      if (data) {
        const records = data.records || []
        const pageInfo = data.pageInfo || {}
        
        // 转换数据
        const users: User[] = records.map(record => convertToUser(record))
        
        allTableData.value = users
        filteredData.value = users
        total.value = pageInfo.total || users.length
        
        // 如果当前页没有数据且不是第一页，则跳转到前一页
        if (users.length === 0 && currentPage.value > 1) {
          currentPage.value = Math.max(1, currentPage.value - 1)
          await loadData() // 重新加载数据
        }
      }
    } else {
      ElMessage.error(responseData?.message || '获取用户数据失败')
    }
  } catch (error) {
    console.error('加载用户数据失败:', error)
    ElMessage.error('加载用户数据失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 初始化数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-user-manage {
  padding: 0 0 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 25px;
}

.search-input-container {
  flex-shrink: 0;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.filter-label {
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  flex-shrink: 0;
}

/* 用户信息单元格 */
.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.user-avatar {
  flex-shrink: 0;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0; /* 允许文本溢出 */
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}

.user-uid {
  font-size: 12px;
  color: #909399;
}

/* 借阅信用单元格 */
.credit-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}

.credit-score {
  font-size: 14px;
  color: #333;
}

.credit-rating {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.credit-excellent {
  background-color: #67c23a; /* 绿色 */
}

.credit-good {
  background-color: #e6a23c; /* 黄色 */
}

.credit-poor {
  background-color: #f56c6c; /* 红色 */
}

.no-credit {
  color: #c0c4cc;
  text-align: center;
}

/* 状态和角色单元格 */
.role-cell,
.status-cell {
  display: flex;
  justify-content: center;
  padding: 8px 0;
}

.register-time-cell {
  text-align: center;
  padding: 8px 0;
  font-size: 12px;
  color: #606266;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-start;
  padding-left: 10px;
}

.action-text {
  color: #409EFF;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.2s;
}

.action-text:hover {
  color: #67C23A;
  text-decoration: underline;
}

.action-text.upgrade {
  color: #e6a23c;
}

.action-text.upgrade:hover {
  color: #409EFF;
}

.action-text.upgrade.disabled {
  color: #c0c4cc;
  cursor: not-allowed;
}

.action-text.upgrade.disabled:hover {
  color: #c0c4cc;
  text-decoration: none;
}

.action-text.freeze {
  color: #e6a23c;
}

.action-text.unfreeze {
  color: #67c23a;
}

.action-text.delete {
  color: #F56C6C;
}

.action-text.delete:hover {
  color: #e6a23c;
}

/* 无数据提示 */
.no-data {
  margin-top: 40px;
}

/* 分页容器 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 升级权限对话框 */
.upgrade-dialog-content {
  padding: 10px 0;
}

.upgrade-message {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
}

.warning-icon {
  color: #e6a23c;
  font-size: 18px;
  flex-shrink: 0;
}

.warning-info {
  margin-top: 16px;
}

/* 表格样式覆盖 */
:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
}

:deep(.el-table .cell) {
  padding: 0 12px;
}

:deep(.el-table__header .cell) {
  text-align: center;
}

:deep(.el-table th:last-child .cell) {
  text-align: left;
  padding-left: 22px;
}

/* 各列对齐方式 */
:deep(.el-table .el-table__cell:has(.user-info-cell)) {
  text-align: left;
}
</style>