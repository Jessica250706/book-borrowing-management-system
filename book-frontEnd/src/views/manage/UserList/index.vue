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
              :label="ROLES.NAME.SYS_ADMIN"
              :value="ROLES.FILTER.SYS_ADMIN"
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
      :show-operations="isSysAdmin" 
      :pagination="false"
      row-key="userId" 
      @selection-change="handleSelectionChange"
      @action-click="handleActionClick"
    >
      <!-- 用户信息列 -->
      <template #column-userInfo="{ row }">
        <div class="user-info-cell">
          <div class="user-avatar">
            <el-avatar :size="40" :src="row.avatar">
              <span v-if="!row.avatar" class="avatar-text">
                {{ getAvatarText(row.username) }}
              </span>
            </el-avatar>
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
            {{ getCreditRatingText(row.creditScore) }}
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
              v-if="!row.canUpgradeRole && row.roleId <= 3"  
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import Table from '@/components/mytable/Table.vue'
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';

// 导入用户store判断身份
import { useUserStore } from '@/store/modules/user'

// 导入API和类型
import { 
  getUsers, 
  updateUserRole, 
  updateUserStatus,
  ROLES,
  USER_STATUS,
  type UserListResponseDTO,
  type RoleInfoDTO,
  type AccountStatusDTO,
  type UpdateUserRoleRequest
} from '@/apis/user/index'

const userStore = useUserStore()

// 计算属性：判断是否是系统管理员
const isSysAdmin = computed(() => {
  return userStore.roleCode === 'SYS_ADMIN'
})

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

// 获取头像文字（首字母）
const getAvatarText = (username: string): string => {
  if (!username) return '?'
  // 获取第一个字符的大写
  return username.charAt(0).toUpperCase()
}

// 分页配置
const paginationConfig = reactive({
  pageSizes: [10, 20, 30, 50],
  layout: "total, sizes, prev, pager, next, jumper"
})

// 状态名称映射
const statusNameMap: Record<number, string> = {
    0: '冻结',     // 0-冻结
    1: '正常',     // 1-正常
    2: '停用',     // 2-停用
    3: '注销'      // 3-注销
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

// 获取信用评级样式类
const getCreditRatingClass = (score: number): string => {
  if (score >= 80) return 'credit-excellent'  // 80-100: 优
  if (score >= 60) return 'credit-good'       // 60-79: 良
  return 'credit-poor'                        // 0-59: 差
}

// 获取信用评级文字
const getCreditRatingText = (score: number): string => {
  if (score >= 80) return '优'  // 80-100: 优
  if (score >= 60) return '良'  // 60-79: 良
  return '差'                    // 0-59: 差
}

// 获取角色标签类型
const getRoleType = (roleName: string) => {
  const typeMap: Record<string, string> = {
    [ROLES.NAME.READER_SOCIAL]: '',
    [ROLES.NAME.READER_STUDENT]: 'success',
    [ROLES.NAME.READER_TEACHER]: 'warning',
    [ROLES.NAME.ADMIN]: 'danger',
    [ROLES.NAME.SYS_ADMIN]: 'primary'
  }
  return typeMap[roleName] || 'info'
}

// 获取状态标签类型
const getStatusType = (status: number) => {
    const typeMap: Record<number, string> = {
        0: 'warning',  // 冻结 - 黄色
        1: 'success',  // 正常 - 绿色
        2: 'info',     // 停用 - 灰色
        3: 'danger'    // 注销 - 红色
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
        const isFrozen = row.status === 0 // 注意：0是冻结状态
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
        
        const newStatus = isFrozen ? 1 : 0 // 1=正常, 0=冻结
        
        try {
            loading.value = true
            
            const response = await updateUserStatus({
                userId: row.userId,
                status: newStatus,
                remark: `系统管理员操作：${action}用户`
            })
            
            console.log('更新状态响应:', response)
            
            if (response.code === 200 || response.code === 0) {
                // 重新加载数据
                await loadData()
                ElMessage.success(`${action}成功`)
            } else {
                ElMessage.error(response.message || `${action}失败`)
            }
        } catch (error: any) {
            console.error(`${action}用户失败:`, error)
            ElMessage.error(error.message || `${action}失败，请重试`)
        } finally {
            loading.value = false
        }
    } catch {
        ElMessage.info('取消操作')
    }
}

// 升级权限处理
const handleUpgradeRole = async (row: User) => {
    try {
        const hasBorrowedBooks = row.hasBorrowingBooks || false;
        let message = '';
        let title = '升级权限';

        if (hasBorrowedBooks) {
            message = `当前用户尚未归还所有书籍，是否提升该用户为管理员？确认后会自动归还所有书籍。`;
        } else {
            message = `是否提升用户为管理员？`;
        }

        await showConfirmDialog({
            title,
            message,
            confirmText: '确定',
            cancelText: '取消',
            onConfirm: async () => {
                try {
                    loading.value = true;

                    const updateParams: UpdateUserRoleRequest = {
                        userId: row.userId,
                        roleId: ROLES.ID.ADMIN,
                        remark: '系统管理员操作：升级用户权限'
                    };

                    console.log('发送升级请求:', updateParams);
                    const updateResponse = await updateUserRole(updateParams);
                    
                    console.log('升级权限响应:', updateResponse);

                    if (updateResponse && updateResponse.userId) {
                        // 重新加载数据，更新用户列表
                        await loadData();
                        ElMessage.success('权限升级成功');
                    } else {
                        await loadData();
                        ElMessage.success('权限已更新');
                    }
                } catch (error: any) {
                    console.error('升级权限失败异常:', error);
                    
                    // 错误处理
                    let errorMessage = '升级权限失败，请重试';
                    
                    if (error.response) {
                        // 服务器响应了错误
                        const { data } = error.response;
                        console.error('错误响应数据:', data);
                        
                        // 检查是否有错误消息
                        if (data?.message) {
                            errorMessage = data.message;
                        } else if (data?.error) {
                            errorMessage = data.error;
                        } else if (data && typeof data === 'string') {
                            errorMessage = data;
                        } else if (data && data.code !== undefined && data.message) {
                            // 如果错误响应是完整的结构
                            errorMessage = data.message;
                        }
                    } else if (error.message) {
                        errorMessage = error.message;
                    }
                    
                    ElMessage.error(errorMessage);
                } finally {
                    loading.value = false;
                }
            }
        });
    } catch (dialogError) {
        // 用户点击了取消
        console.log('用户取消升级权限');
        ElMessage.info('取消升级权限');
    }
};

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
const convertToUser = (apiData: UserListResponseDTO, index: number): User => {
    console.log('转换数据:', apiData)
    
    const roleInfo = apiData.roleInfo || {} as RoleInfoDTO
    const creditInfo = apiData.creditInfo
    const accountStatus = apiData.accountStatus || {} as AccountStatusDTO
    
    // 转换数字字符串为数字 
    const userId = typeof apiData.userId === 'string' ? parseInt(apiData.userId) : (apiData.userId || 0)
    const roleId = typeof roleInfo.roleId === 'string' ? parseInt(roleInfo.roleId) : (roleInfo.roleId || 0)
    
    let status: number
    if (accountStatus.status !== undefined && accountStatus.status !== null) {
        if (typeof accountStatus.status === 'string') {
            status = parseInt(accountStatus.status)
        } else {
            status = Number(accountStatus.status) 
        }
        
        // 检查转换结果
        if (isNaN(status)) {
            console.warn(`状态转换失败: ${accountStatus.status}，使用默认值 1`)
            status = 1
        }
    } else {
        // 如果状态不存在，默认设为正常
        status = 1
    }
    
    // 处理 creditInfo 为 null 的情况
    let creditScore: number | undefined
    let creditLevel: string = ''
    
    if (creditInfo && creditInfo !== null) {
        // 尝试从不同字段获取信用分数
        if (typeof creditInfo.creditScore === 'string') {
            creditScore = parseInt(creditInfo.creditScore)
        } else {
            creditScore = creditInfo.creditScore || creditInfo.score || 0
        }
        
        if (creditScore === 0) creditScore = undefined
        creditLevel = creditInfo.creditLevel || creditInfo.level || ''
    }
    
    const user: User = {
        userId: userId || index + 1,
        serialNumber: index + 1, 
        uid: apiData.uid || '',
        username: apiData.username || '',
        avatar: apiData.avatar || '',
        roleId: roleId,
        roleCode: roleInfo.roleCode || '',
        roleName: roleInfo.roleName || '',
        creditScore: creditScore,
        creditLevel: creditLevel,
        status: status,
        statusName: accountStatus.statusName || getStatusName(status),
        statusDesc: accountStatus.desc || '',
        registerTime: apiData.registerTime || '',
        canUpgradeRole: Boolean(apiData.canUpgradeRole),
        hasBorrowingBooks: Boolean(apiData.hasBorrowingBooks)
    }
    
    return user
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

        if (response) {
            const records = response.records || []
            const pageInfo = response.pageInfo || {}
            
            // 转换数据
            const users: User[] = records.map((record: UserListResponseDTO, index: number) => convertToUser(record, index));
            
            allTableData.value = users
            filteredData.value = users
            
            // 处理字符串转数字
            total.value = Number(pageInfo.total) || 0
        } else {
            allTableData.value = []
            filteredData.value = []
            total.value = 0
        }
    } catch (error) {
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

/* 用户头像样式 */
.user-avatar :deep(.el-avatar) {
  background-color: #409EFF; /* 蓝色背景 */
}

.user-avatar .avatar-text {
  color: white;
  font-size: 16px;
  font-weight: bold;
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
  background-color: #f3d05c; /* 黄色 */
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
  text-align: left;
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
  color: #409EFF;
}

.action-text.upgrade:hover {
  color: #67C23A;
  text-decoration: underline;
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