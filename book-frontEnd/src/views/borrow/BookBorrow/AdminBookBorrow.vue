<template>
  <div class="admin-book-borrow">
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
        <el-button type="primary" @click="handleCreateBook" style="margin-left: auto;">创建书籍</el-button>
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
          {{ getDisplayCategory(row.category || row.categoryName) }}
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
        :hide-on-single-page="false"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Table from '@/components/mytable/Table.vue'
import BookInfo from '@/components/BookInfo/BookInfo.vue'
import { ElMessage } from 'element-plus'

// 导入搜索组件
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';

// 导入对话框组件
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';

// 导入API
import { getBooks, publishBook, deleteBook } from '@/apis/book';

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

// 状态选项配置
const statusOptions = ref<StatusOption[]>([
  { label: '所有状态', value: '' },
  { label: '未发布', value: '1' },      // 1-未发布
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

// 添加清理分类名称的函数 
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
  0: '未发布', //草稿状态，但是显示为“未发布”
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

//直接查找匹配
const getDisplayCategory = (categoryName: string): string => {
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
  
  // 如果没有找到，返回原始名称
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
    1: 'warning',   // 未发布 - 黄色
    2: 'primary',   // 待上架 - 蓝色  
    3: 'success',   // 可借阅 - 绿色
    4: 'danger'     // 已借光 - 红色
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

// 事件处理函数
const handleStatusUpdate = (val: string) => {
  filterForm.bookStatus = val
  loadData() 
}

const handleCategoryUpdate = (val: string) => {
  filterForm.categoryId = val
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

// 发布书籍
const handlePublish = async (row: Book) => {
  if (!row.bookId) {
    ElMessage.error('书籍ID不存在')
    return
  }
  
  try {
    await showConfirmDialog({
      title: '发布',
      message: `是否发布书籍？`,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await publishBook(row.bookId!) as any
          
          console.log('发布API响应:', response)
          
          // 使用 response.code 访问
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

// 删除书籍
const handleDelete = async (row: Book) => {
  if (!row.bookId) {
    ElMessage.error('书籍ID不存在')
    return
  }
  
  try {
    let message = '是否要删除书籍？'
    let title = '删除'
    
    if (row.bookStatus === 3 && (row.borrowCount || 0) > 0) {
      message = `当前有${row.borrowCount}人已借阅此书，是否要删除书籍？`
    }

    await showConfirmDialog({
      title,
      message,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await deleteBook(row.bookId!) as any
          
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

const handleCreateBook = () => {
  router.push('/borrow/BookBorrow/BookCreate')
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
    
    // 构建查询参数
    const params: any = {
      currentPage: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 添加筛选条件
    if (filterForm.bookName && filterForm.bookName.trim()) {
      params.keyword = filterForm.bookName.trim()
    }
    
    // 传递状态参数
    if (filterForm.bookStatus) {
      params.bookStatus = parseInt(filterForm.bookStatus)
    }
    
    // 传递分类名称参数
    if (filterForm.categoryId && filterForm.categoryId !== '') {
      const categoryName = getCategoryNameById(filterForm.categoryId)
      if (categoryName && categoryName.trim()) {
        params.categoryName = categoryName.trim()
      }
    }
    
    // 调用API获取数据
    const response = await getBooks(params) as any
    
    if (response.code === 200) {
      const data = response.data
      
      // 处理分页数据
      if (data && data.records) {
        tableData.value = data.records.map((book: any) => ({
          bookId: book.bookId,
          bookName: book.bookName,
          coverUrl: book.coverUrl,
          author: book.author,
          translator: book.translator,
          categoryId: book.categoryId,
          category: book.categoryName || book.category,
          categoryName: book.categoryName,
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
        
        total.value = Number(data.total) || 0
      } else {
        tableData.value = []
        total.value = 0
      }
    } else {
      ElMessage.error(response.message || '获取书籍列表失败')
      tableData.value = []
      total.value = 0
    }
  } catch (error: any) {
    console.error('加载数据失败:', error)
    
    let errorMsg = '加载数据失败，请重试'
    if (error.response?.data?.message) {
      errorMsg = error.response.data.message
    } else if (error.message) {
      errorMsg = error.message
    }
    
    ElMessage.error(errorMsg)
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
.admin-book-borrow {
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