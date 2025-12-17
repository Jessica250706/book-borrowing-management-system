<template>
  <div class="admin-new-books">
    <!-- 页面标题和操作区域 -->
    <div class="page-header">
      <div class="search-section">
        <BookSearchInput @search="handleSearchInput" style="width: 150px" />
        <div class="filter-group">
          <span class="filter-label">书籍状态:</span>
          <BookStatusSelect 
            :options="statusOptions"
            :model-value="filterForm.bookStatus"
            placeholder="所有状态"
            @update:model-value="handleStatusUpdate"
            style="width: 150px" 
          />
        </div>
        <div class="filter-group">
          <span class="filter-label">书籍分类:</span>
          <BookCategorySelect 
            :options="categoryOptions"
            :model-value="filterForm.categoryId"
            placeholder="所有分类"
            @update:model-value="handleCategoryUpdate"
            style="width: 150px" 
          />
        </div>
        
        <!-- 添加批量操作按钮组 -->
        <div class="action-buttons-group">
          <el-dropdown 
            @command="handleBatchCommand"
            trigger="click"
          >
            <el-button 
              type="primary" 
              style="margin-right: 12px;"
              :class="{ 'disabled-btn': selectedRows.length === 0 }"
            >
              批量操作
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu class="batch-dropdown-menu">
                <el-dropdown-item 
                  command="shelve" 
                  :disabled="!canBatchShelve"
                  class="batch-dropdown-item"
                >
                  <span class="dropdown-text">上架</span>
                </el-dropdown-item>
                <el-dropdown-item 
                  command="delete"
                  :disabled="selectedRows.length === 0"
                  class="batch-dropdown-item"
                >
                  <span class="dropdown-text">删除</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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
      :show-index="false"
      :show-actions="true"
      :pagination="false" 
      row-key="bookId" 
      @selection-change="handleSelectionChange"
      @action-click="handleActionClick"
    >
      <!-- 添加序号列插槽 -->
      <template #column-index="{ index }">
        {{ (currentPage - 1) * pageSize + index + 1 }}
      </template>
      
      <template #column-bookInfo="{ row }">
            <div class="book-info-cell">
              <BookInfo 
                :book="{
                  bookImg: row.coverUrl, 
                  bookName: row.bookName,
                  author: row.author,
                  translator: row.translator
                }" 
                :show-draft-icon="row.bookStatus === 0"
              />
            </div>
      </template>

      <template #column-category="{ row }">
        <div class="category-cell">
          {{ getDisplayCategory(row.categoryName || row.category) }}
        </div>
      </template>

      <template #column-status="{ row }">
        <el-tag
          :type="getStatusType(row.bookStatus)"
          effect="light"
        >
          {{ getStatusText(row.bookStatus) }}
        </el-tag>
      </template>

      <template #column-shelfTime="{ row }">
        <div class="shelf-time-cell">
          {{ formatShelfTime(row.shelfTime) }}
        </div>
      </template>

      <template #actions="{ row }">
          <div class="action-buttons">
          <span class="action-text" @click="handleDetail(row)">详情</span>
          <span class="action-text" @click="handleEdit(row)">编辑</span>
          <span 
            v-if="row.bookStatus === 1" 
            class="action-text publish" 
            @click="handlePublish(row)"
          >
            发布
          </span>
          <span 
            v-if="row.bookStatus === 2" 
            class="action-text shelve" 
            @click="handleShelve(row)"
          >
            上架
          </span>
          <span class="action-text delete" @click="handleDelete(row)">删除</span>
        </div>
      </template>

    </Table>

    <!-- 分页控件 -->
    <div class="pagination-container">
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
import { useRouter } from 'vue-router'
import Table from '@/components/mytable/Table.vue'
import BookInfo from '@/components/BookInfo/BookInfo.vue'
import { ElMessage } from 'element-plus'

// 导入图标
import { ArrowDown } from '@element-plus/icons-vue'

// 导入搜索组件
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';

// 导入对话框组件
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';

// 导入API
import { 
  getNewBooks,
  batchPublishBooks,
  batchShelveBooks,
  batchDeleteCheck,
  batchDeleteBooks
} from '@/apis/book';

const router = useRouter()

// 类型定义
interface Book {
  bookId?: number
  bookName?: string
  coverUrl?: string
  author?: string
  translator?: string
  categoryId?: number
  category?: string
  categoryName?: string
  bookStatus?: number
  shelfTime?: string
  intro?: string
  availableCount?: number
  totalCount?: number
  borrowCount?: number
  createTime?: string
  updateTime?: string
  publisher?: string
  isbn?: string
  copyrightHolder?: string
  publishCount?: number
  publishUnit?: string
  publishWebsite?: string
  publishBatch?: string
  publishDate?: string
  price?: number
}

interface FilterForm {
  bookName?: string
  bookStatus?: string
  categoryId?: string
}

interface StatusOption {
  label: string;
  value: string;
}

interface CategoryOption {
  label: string;
  value: string;
}

interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

// 状态选项配置
const statusOptions = ref<StatusOption[]>([
  { label: '所有状态', value: '' },
  { label: '待上架', value: '2' },      // 2-待上架  
  { label: '可借阅', value: '3' },      // 3-可借阅
  { label: '已借光', value: '4' }       // 4-已借光
])

// 分类选项配置
const categoryOptions = ref<CategoryOption[]>([
  { label: '所有分类', value: '' },
  { label: 'A、马克思主义、列宁主义、毛泽东思想、邓小平理论', value: 'A' },
  { label: 'B、哲学、宗教', value: 'B' },
  { label: 'C、社会科学总论', value: 'C' },
  { label: 'D、政治、法律', value: 'D' },
  { label: 'E、军事', value: 'E' },
  { label: 'F、经济', value: 'F' },
  { label: 'G、文化、科学、教育、体育', value: 'G' },
  { label: 'H、语言、文字', value: 'H' },
  { label: 'I、文学', value: 'I' },
  { label: 'J、艺术', value: 'J' },
  { label: 'K、历史、地理', value: 'K' },
  { label: 'N、自然科学总论', value: 'N' },
  { label: 'O、数理科学和化学', value: 'O' },
  { label: 'P、天文学、地球科学', value: 'P' },
  { label: 'Q、生物科学', value: 'Q' },
  { label: 'R、医药、卫生', value: 'R' },
  { label: 'S、农业科学', value: 'S' },
  { label: 'T、工业技术', value: 'T' },
  { label: 'U、交通运输', value: 'U' },
  { label: 'V、航空、航天', value: 'V' },
  { label: 'X、环境科学、安全科学', value: 'X' },
  { label: 'Z、综合性图书', value: 'Z' }
])

// 响应式数据
const loading = ref(false)
const tableData = ref<Book[]>([])
const total = ref(0)
const selectedRows = ref<Book[]>([])
const currentPage = ref(1)
const pageSize = ref(10)

// 筛选表单
const filterForm = reactive<FilterForm>({
  bookName: '',
  bookStatus: '',
  categoryId: ''
})

// 分页配置
const paginationConfig = reactive({
  pageSizes: [10, 20, 30, 50],
  layout: "total, sizes, prev, pager, next, jumper"
})

// 状态文本映射
const statusTextMap: Record<number, string> = {
  0: '未发布', //草稿状态，但是显示为"未发布"
  1: '未发布',
  2: '待上架',
  3: '可借阅',
  4: '已借光'
}

// 表格列配置
const columns = [
  {
    prop: 'index',
    label: '序号',
    width: '80',
    align: 'center' as const
  },
  {
    prop: 'bookInfo',
    label: '书籍',
    width: '320px',
    align: 'left' as const
  },
  {
    prop: 'category',
    label: '分类',
    width: '200px',
    align: 'left' as const
  },
  {
    prop: 'status',
    label: '状态',
    width: '120px',
    align: 'center' as const
  },
  {
    prop: 'shelfTime',
    label: '上架时间',
    width: '180px',
    align: 'center' as const
  }
]

// 操作按钮配置
const actions = [
  { name: 'detail', label: '详情', type: 'primary' as const },
  { name: 'edit', label: '编辑', type: 'success' as const },
  { name: 'delete', label: '删除', type: 'danger' as const }
]

// 清理分类名称的函数 
const cleanCategoryName = (categoryName: string): string => {
  if (!categoryName) return ''
  // 去掉"A、"这样的前缀
  return categoryName.replace(/^[A-Z]、/, '')
}

// 根据分类ID获取清理后的分类名称
const getCategoryNameById = (categoryId: string): string => {
  const category = categoryOptions.value.find(item => item.value === categoryId)
  if (!category) return ''
  
  return cleanCategoryName(category.label)
}

// 根据分类名称获取显示的分类名
const getDisplayCategory = (categoryName: string | undefined): string => {
  if (!categoryName) return '未分类'
  
  // 在分类选项中查找匹配的项
  const matchedOption = categoryOptions.value.find(option => {
    const cleaned = cleanCategoryName(option.label)
    return cleaned === categoryName
  })
  
  // 如果找到匹配的，返回完整的带字母的分类名
  if (matchedOption) {
    return matchedOption.label
  }
  
  // 如果没有找到，检查是否已经有字母前缀
  if (/^[A-Z]、/.test(categoryName)) {
    return categoryName
  }
  
  // 给分类名称添加字母前缀
  for (const option of categoryOptions.value) {
    const cleaned = cleanCategoryName(option.label)
    if (categoryName === cleaned || categoryName.includes(cleaned)) {
      return option.label
    }
  }
  
  return categoryName
}

// 获取状态文本
const getStatusText = (status?: number): string => {
  if (status === undefined || status === null) return '未知'
  return statusTextMap[status] || '未知'
}

// 获取状态对应的标签类型
const getStatusType = (status?: number) => {
  const typeMap: Record<number, string> = {
    0: 'warning',      // 草稿 - 黄色
    1: 'warning',      // 未发布 - 黄色
    2: 'primary',      // 待上架 - 蓝色  
    3: 'success',      // 可借阅 - 绿色
    4: 'danger'        // 已借光 - 红色
  }
  return typeMap[status || 0] || 'info'
}

// 格式化上架时间
const formatShelfTime = (shelfTime?: string): string => {
  if (!shelfTime) return '-'
  
  try {
    // 处理不同的时间格式
    let date: Date
    if (shelfTime.includes('T')) {
      // ISO格式: 2024-12-09T02:23:00
      date = new Date(shelfTime)
    } else if (shelfTime.includes(' ')) {
      // 字符串格式: 2024-12-09 02:23:00
      date = new Date(shelfTime.replace(' ', 'T'))
    } else {
      return shelfTime
    }
    
    if (isNaN(date.getTime())) return shelfTime
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    
    return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`
  } catch {
    return shelfTime
  }
}

// 计算属性：判断是否可以批量发布
const canBatchPublish = computed(() => {
  if (selectedRows.value.length === 0) return false
  // 所有选中的书籍状态都是未发布（状态1）
  return selectedRows.value.every(row => row.bookStatus === 1)
})

// 计算属性：判断是否可以批量上架
const canBatchShelve = computed(() => {
  if (selectedRows.value.length === 0) return false
  // 所有选中的书籍状态都是待上架（状态2）
  return selectedRows.value.every(row => row.bookStatus === 2)
})

// 处理批量操作
const handleBatchCommand = async (command: string) => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要操作的书籍')
    return
  }

  const selectedIds = selectedRows.value
    .filter(row => row.bookId !== undefined)
    .map(row => row.bookId!) as number[]

  if (selectedIds.length === 0) {
    ElMessage.warning('未选择有效书籍')
    return
  }

  switch (command) {
    case 'publish':
      await handleBatchPublish(selectedIds)
      break
    case 'shelve':
      await handleBatchShelve(selectedIds)
      break
    case 'delete':
      await handleBatchDelete(selectedIds)
      break
  }
}

// 批量发布
const handleBatchPublish = async (ids: number[]) => {
  try {
    await showConfirmDialog({
      title: '批量发布',
      message: `确定要发布这${ids.length}本书籍吗？`,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await batchPublishBooks({ ids })
          
          if (response.code === 200) {
            await loadData()
            ElMessage.success(`成功发布${ids.length}本书籍`)
            selectedRows.value = [] // 清空选择
          } else {
            ElMessage.error(response.message || '批量发布失败')
          }
        } catch (error: any) {
          console.error('批量发布失败:', error)
          ElMessage.error(error.message || '批量发布失败，请重试')
        } finally {
          loading.value = false
        }
      }
    })
  } catch {
    ElMessage.info('取消批量发布')
  }
}

// 批量上架
const handleBatchShelve = async (ids: number[]) => {
  try {
    await showConfirmDialog({
      title: '批量上架',
      message: `确定要上架这${ids.length}本书籍吗？`,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await batchShelveBooks({ ids })
          
          if (response.code === 200) {
            await loadData()
            ElMessage.success(`成功上架${ids.length}本书籍`)
            selectedRows.value = [] // 清空选择
          } else {
            ElMessage.error(response.message || '批量上架失败')
          }
        } catch (error: any) {
          console.error('批量上架失败:', error)
          ElMessage.error(error.message || '批量上架失败，请重试')
        } finally {
          loading.value = false
        }
      }
    })
  } catch {
    ElMessage.info('取消批量上架')
  }
}

// 批量删除
const handleBatchDelete = async (ids: number[]) => {
  try {
    // 先检查是否可以删除
    let hasBorrowRecords = false
    let errorMessage = ''
    
    try {
      loading.value = true
      const checkResponse = await batchDeleteCheck({ ids })
      
      if (checkResponse.code === 200 && checkResponse.data) {
        const booksWithRecords = checkResponse.data.filter((book: any) => book.hasBorrowRecord)
        if (booksWithRecords.length > 0) {
          hasBorrowRecords = true
          const bookNames = booksWithRecords.map((book: any) => `《${book.bookName}》`).join('、') 
          const borrowCount = booksWithRecords.reduce((sum: number, book: any) => sum + (book.borrowCount || 0), 0)
          errorMessage = `有${booksWithRecords.length}本书籍（${bookNames}）存在未归还的借阅记录（共${borrowCount}人），无法删除`
        }
      }
    } catch (error) {
      console.error('删除检查失败:', error)
    } finally {
      loading.value = false
    }

    // 如果有借阅记录，直接提示无法删除
    if (hasBorrowRecords) {
      ElMessage.error(errorMessage)
      return
    }

    // 如果没有借阅记录，正常进行删除确认
    const message = `确定要删除这${ids.length}本书籍吗？`
    
    await showConfirmDialog({
      title: '批量删除',
      message,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await batchDeleteBooks({ ids })
          
          if (response.code === 200) {
            await loadData()
            ElMessage.success(`成功删除${ids.length}本书籍`)
            selectedRows.value = [] // 清空选择
          } else {
            ElMessage.error(response.message || '批量删除失败')
          }
        } catch (error: any) {
          console.error('批量删除失败:', error)
          ElMessage.error(error.message || '批量删除失败，请重试')
        } finally {
          loading.value = false
        }
      }
    })
  } catch {
    ElMessage.info('取消批量删除')
  }
}

// 事件处理函数
const handleStatusUpdate = (val: string) => {
  filterForm.bookStatus = val
  currentPage.value = 1
  loadData()
}

const handleCategoryUpdate = (val: string) => {
  filterForm.categoryId = val
  currentPage.value = 1
  loadData()
}

const handleSelectionChange = (selection: Book[]) => {
  selectedRows.value = selection
}

const handleActionClick = (action: string, row: Book) => {
  console.log('操作按钮点击:', action, row)
  switch (action) {
    case 'detail':
      handleDetail(row)
      break
    case 'edit':
      handleEdit(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

const handleDetail = (row: Book) => {
  if (!row.bookId) {
    ElMessage.error('书籍ID不存在')
    return
  }
  
  router.push({
    name: 'bookDetail',
    params: {
      id: row.bookId.toString()
    }
  }).catch(err => {
    console.error('路由跳转失败:', err)
    router.push(`/borrow/BookBorrow/BookDetail/${row.bookId}`)
  })
}

const handleEdit = (row: Book) => {
  if (!row.bookId) {
    ElMessage.error('书籍ID不存在')
    return
  }
  
  router.push({
    name: 'bookEdit',
    params: {
      id: row.bookId.toString()
    },
    query: {
      edit: 'true'
    }
  }).catch(err => {
    console.error('路由跳转失败:', err)
    router.push(`/borrow/BookBorrow/BookEdit/${row.bookId}?edit=true`)
  })
}

// 发布书籍 - 使用批量接口
const handlePublish = async (row: Book) => {
  if (!row.bookId) {
    ElMessage.error('书籍ID不存在')
    return
  }
  
  try {
    await showConfirmDialog({
      title: '发布',
      message: `是否发布书籍《${row.bookName || ''}》？`,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await batchPublishBooks({ ids: [row.bookId!] })
          
          console.log('发布API响应:', response)
          
          if (response.code === 200) {
            // 重新加载数据
            await loadData()
            ElMessage.success(response.message || '发布成功')
          } else {
            ElMessage.error(response.message || '发布失败')
          }
        } catch (error: any) {
          console.error('发布失败:', error)
          console.error('错误详情:', {
            message: error.message,
            response: error.response,
            data: error.response?.data
          })
          
          let errorMsg = '发布失败，请重试'
          if (error.response?.data?.message) {
            errorMsg = error.response.data.message
          } else if (error.message) {
            errorMsg = error.message
          }
          ElMessage.error(errorMsg)
        } finally {
          loading.value = false
        }
      }
    })
  } catch {
    ElMessage.info('取消发布')
  }
}

// 上架书籍 - 使用批量接口
const handleShelve = async (row: Book) => {
  if (!row.bookId) {
    ElMessage.error('书籍ID不存在')
    return
  }
  
  try {
    await showConfirmDialog({
      title: '上架',
      message: `是否上架书籍《${row.bookName || ''}》？`,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await batchShelveBooks({ ids: [row.bookId!] })
          
          if (response.code === 200) {
            await loadData()
            ElMessage.success(response.message || '上架成功')
          } else {
            ElMessage.error(response.message || '上架失败')
          }
        } catch (error: any) {
          console.error('上架失败:', error)
          ElMessage.error(error.message || '上架失败，请重试')
        } finally {
          loading.value = false
        }
      }
    })
  } catch {
    ElMessage.info('取消上架')
  }
}

// 删除书籍 - 使用批量接口
const handleDelete = async (row: Book) => {
  if (!row.bookId) {
    ElMessage.error('书籍ID不存在')
    return
  }
  
  try {
    // 先检查是否可以删除
    let hasBorrowRecords = false
    let errorMessage = ''
    
    try {
      loading.value = true
      const checkResponse = await batchDeleteCheck({ ids: [row.bookId] })

      if (checkResponse.code === 200 && checkResponse.data && checkResponse.data.length > 0) {
        const bookInfo = checkResponse.data[0]
        if (bookInfo && bookInfo.hasBorrowRecord) {
          hasBorrowRecords = true
          errorMessage = `书籍《${row.bookName}》存在未归还的借阅记录（${bookInfo.borrowCount || 0}人），无法删除` 
        }
      }
    } catch (error) {
      console.error('删除检查失败:', error)
    } finally {
      loading.value = false
    }

    // 如果有借阅记录，直接提示无法删除
    if (hasBorrowRecords) {
      ElMessage.error(errorMessage)
      return
    }

    // 如果没有借阅记录，正常进行删除确认
    const message = `是否要删除书籍《${row.bookName}》？` 
    
    await showConfirmDialog({
      title: '删除',
      message,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await batchDeleteBooks({ ids: [row.bookId!] })
          
          console.log('删除API响应:', response)
          
          if (response.code === 200) {
            // 重新加载数据
            await loadData()
            ElMessage.success(response.message || '删除成功')
          } else {
            ElMessage.error(response.message || '删除失败')
          }
        } catch (error: any) {
          console.error('删除书籍失败:', error)
          console.error('错误详情:', {
            message: error.message,
            response: error.response,
            data: error.response?.data
          })
          
          let errorMsg = '删除失败，请重试'
          if (error.response?.data?.message) {
            errorMsg = error.response.data.message
          } else if (error.message) {
            errorMsg = error.message
          }
          ElMessage.error(errorMsg)
        } finally {
          loading.value = false
        }
      }
    })
  } catch {
    ElMessage.info('取消删除')
  }
}

// 分页事件处理
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1
  loadData()
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
  loadData()
}

// 搜索组件事件处理
const handleSearchInput = (val: string) => {
  filterForm.bookName = val
  currentPage.value = 1
  loadData()
}

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    
    // 构建查询参数 - 传递给 getNewBooks 接口
    const params: any = {
      currentPage: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 将筛选条件添加到请求参数中
    if (filterForm.bookName && filterForm.bookName.trim()) {
      params.keyword = filterForm.bookName.trim()
    }
    
    if (filterForm.bookStatus) {
      params.bookStatus = parseInt(filterForm.bookStatus)
    }
    
    if (filterForm.categoryId && filterForm.categoryId !== '') {
      // 获取清理后的分类名称
      const categoryName = getCategoryNameById(filterForm.categoryId)
      if (categoryName && categoryName.trim()) {
        params.categoryName = categoryName.trim()
      }
    }
    
    // 使用 getNewBooks 接口，并传递筛选参数
    const response = await getNewBooks(params) as any

    if (response.code === 200) {
      const data = response.data
      
      if (data && data.records) {
        // 直接转换后端返回的数据
        tableData.value = data.records.map((book: any) => ({
          bookId: book.bookId,
          bookName: book.bookName,
          coverUrl: book.coverUrl,
          author: book.author,
          translator: book.translator,
          categoryId: book.categoryId,
          category: book.categoryName || book.category,
          categoryName: book.categoryName || book.category,
          bookStatus: book.bookStatus,
          shelfTime: book.shelfTime,
          intro: book.intro,
          availableCount: book.availableCount,
          totalCount: book.totalCount,
          borrowCount: book.borrowCount,
          createTime: book.createTime,
          updateTime: book.updateTime,
          publisher: book.publisher,
          isbn: book.isbn,
          copyrightHolder: book.copyrightHolder,
          publishCount: book.publishCount,
          publishUnit: book.publishUnit,
          publishWebsite: book.publishWebsite,
          publishBatch: book.publishBatch,
          publishDate: book.publishDate,
          price: book.price
        }))
        
        // 使用后端返回的总数
        total.value = Number(data.total) || 0
        
        console.log('后端筛选后的数据:', tableData.value.length, '条')
      } else {
        tableData.value = []
        total.value = 0
      }
      
      // 如果当前页没有数据且不是第一页，跳转到第一页
      if (tableData.value.length === 0 && currentPage.value > 1) {
        console.log('当前页无数据，跳转到第一页')
        currentPage.value = 1
        // 重新加载数据
        setTimeout(() => {
          loadData()
        }, 0)
      }
    } else {
      ElMessage.error(response.message || '获取新书推荐失败')
      tableData.value = []
      total.value = 0
    }
  } catch (error: any) {
    console.error('加载新书数据失败:', error)
    ElMessage.error(error.message || '加载数据失败，请重试')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-new-books {
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

.search-section > :first-child {
  width: 200px !important;
  flex-shrink: 0;
}

.action-buttons-group {
  display: flex;
  align-items: center;
  margin-left: auto; 
}

/* 批量操作按钮禁用状态样式 */
.disabled-btn {
  cursor: pointer;
}

/* 批量操作下拉菜单样式 */
.batch-dropdown-menu {
  min-width: 120px; 
  padding: 4px 0;
  text-align: center;
}

.batch-dropdown-item {
  padding: 8px 16px;
  text-align: center;
}

.batch-dropdown-item:hover {
  background-color: #f5f5f5;
}

.batch-dropdown-item.is-disabled {
  color: #c0c4cc;
  cursor: not-allowed;
}

.batch-dropdown-item.is-disabled:hover {
  background-color: transparent;
}

.dropdown-text {
  display: block;
  width: 100%;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.filter-group :deep(.el-select) {
  width: 150px;
  flex-shrink: 0;
}

.filter-label {
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  flex-shrink: 0;
}

.book-info-cell {
  display: flex;
  align-items: flex-start;
  gap: 5px;
  padding: 8px 0;
}

.category-cell {
  text-align: left;
  padding: 8px 0;
}

.shelf-time-cell {
  text-align: center;
  padding: 8px 0;
  font-size: 12px;
  color: #606266;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-start;
  padding-left: 10px
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

.action-text.publish {
  color: #E6A23C;
}

.action-text.publish:hover {
  color: #409EFF;
}

.action-text.shelve {
  color: #67C23A;
}

.action-text.shelve:hover {
  color: #409EFF;
}

.action-text.delete {
  color: #F56C6C;
}

.action-text.delete:hover {
  color: #E6A23C;
}

/* 分页容器样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 批量操作按钮样式 */
:deep(.el-dropdown .el-button) {
  padding: 10px 10px 10px 15px;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  color: var(--el-text-color-placeholder);
  cursor: not-allowed;
}

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

/* 分类列左对齐 */
:deep(.el-table .el-table__cell:has(.category-cell)) {
  text-align: left;
}
</style>