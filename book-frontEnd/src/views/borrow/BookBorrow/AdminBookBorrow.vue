<template>
  <div class="admin-book-borrow">
    <!-- 页面标题和操作区域 -->
    <div class="page-header">
      <div class="search-section">
        <BookSearchInput @search="handleSearchInput" style="width: 150px" />
        <div class="filter-group">
          <span class="filter-label">书籍状态:</span>
          <BookStatusSelect @change="handleStatusChange" style="width: 150px" />
        </div>
        <div class="filter-group">
          <span class="filter-label">书籍分类:</span>
          <BookCategorySelect @change="handleCategoryChange" style="width: 150px" />
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
      :show-index="true"
      :show-actions="true"
      :pagination="true"
      @selection-change="handleSelectionChange"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      @action-click="handleActionClick"
    >
      <!-- 自定义书籍信息列 -->
      <template #column-bookInfo="{ row }">
        <div class="book-info-cell">
          <el-image
            :src="row.bookImg"
            :alt="row.bookName"
            class="book-image"
            fit="cover"
          />
          <div class="book-details">
            <div class="book-name-row">
              <span class="book-name" :title="row.bookName">
                {{ formatBookName(row.bookName) }}
              </span>
              <el-tooltip
                v-if="row.status === '未发布'"
                content="当前书籍尚未完成编辑，请继续填写信息"
                placement="top"
              >
                <span class="draft-indicator">ⓘ</span>
              </el-tooltip>
            </div>
            <div class="book-author">
              {{ formatAuthor(row.author, row.translator, row.authorNationality, row.translatorNationality) }}
            </div>
          </div>
        </div>
      </template>

      <!-- 自定义分类列 -->
      <template #column-category="{ row }">
        <div class="category-cell">
          {{ row.category }}
        </div>
      </template>

      <!-- 自定义状态列 -->
      <template #column-status="{ row }">
        <el-tag
          :type="getStatusType(row.status)"
          effect="light"
        >
          {{ row.status }}
        </el-tag>
      </template>

      <!-- 自定义上架时间列 -->
      <template #column-shelfTime="{ row }">
        <div class="shelf-time-cell">
          {{ formatShelfTime(row.shelfTime) }}
        </div>
      </template>

      <!-- 自定义操作列 -->
      <template #actions="{ row }">
        <div class="action-buttons">
          <span class="action-text" @click="handleDetail(row)">详情</span>
          <span class="action-text" @click="handleEdit(row)">编辑</span>
          <span class="action-text delete" @click="handleDelete(row)">删除</span>
        </div>
      </template>
    </Table>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Table from '@/components/mytable/Table.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 导入搜索组件
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';

const router = useRouter()

// 类型定义
interface Book {
  id: number
  bookImg: string
  bookName: string
  author: string
  translator: string
  authorNationality: string
  translatorNationality: string
  category: string
  status: string
  shelfTime: string
  description: string
  availableCount: number
  totalCount: number
  borrowedCount: number
  isDraft: boolean
}

interface FilterForm {
  bookName: string
  status: string
  categoryId: string
}

// 响应式数据
const loading = ref(false)
const tableData = ref<Book[]>([])
const total = ref(0)
const selectedRows = ref<Book[]>([])

// 筛选表单
const filterForm = reactive<FilterForm>({
  bookName: '',
  status: '',
  categoryId: ''
})

// 分类列表
const categoryList = [
  'A、马克思主义、列宁主义、毛泽东思想、邓小平理论',
  'B、哲学、宗教', 
  'C、社会科学总论',
  'D、政治、法律',
  'E、军事',
  'F、经济',
  'G、文化、科学、教育、体育',
  'H、语言、文字',
  'I、文学',
  'J、艺术',
  'K、历史、地理',
  'N、自然科学总论',
  'O、数理科学和化学',
  'P、天文学、地球科学',
  'Q、生物科学',
  'R、医药、卫生',
  'S、农业科学',
  'T、工业技术',
  'U、交通运输',
  'V、航空、航天',
  'X、环境科学、安全科学',
  'Z、综合性图书'
]

// 状态数组
const statusList = ['未发布', '可借阅', '已借光', '待上架']

// 国籍列表
const nationalityList = ['中国', '美国', '英国', '法国', '德国', '日本', '俄罗斯', '加拿大', '澳大利亚', '韩国']

// 表格列配置
const columns = [
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

// 格式化书籍名称（最多10个字符）
const formatBookName = (name: string): string => {
  if (name.length <= 10) return name
  return name.substring(0, 10) + '...'
}

// 格式化作者信息
const formatAuthor = (author: string, translator: string, authorNationality: string, translatorNationality: string): string => {
  let result = `[${authorNationality}]${author}`
  if (translator && translatorNationality) {
    result += `著 [${translatorNationality}]${translator}译`
  } else if (translator) {
    result += `著 ${translator}译`
  } else {
    result += '著'
  }
  return result
}

// 格式化上架时间
const formatShelfTime = (shelfTime: string): string => {
  if (!shelfTime) return '-'
  const date = new Date(shelfTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`
}

// 模拟数据生成
// 修改第261行附近的 generateMockData 方法如下：
const generateMockData = (): Book[] => {
  const authors = ['史蒂芬·霍金', '加西亚·马尔克斯', '尤瓦尔·赫拉利', '唐纳德·诺曼', '托马斯·科尔曼']
  const bookNames = [
    '时间简史从宇宙大爆炸到黑洞',
    '百年孤独一个家族的兴衰史',
    '人类简史从动物到上帝',
    '设计心理学人与物的交互设计',
    '算法导论计算机科学基础'
  ]
  
   return Array.from({ length: 50 }, (_, index) => {
    const categoryIndex = index % categoryList.length
    const statusIndex = index % statusList.length
    const authorIndex = index % authors.length
    const bookNameIndex = index % bookNames.length
    const nationalityIndex = index % nationalityList.length

    const status = statusList[statusIndex] ?? '未发布'
    const isDraft = status === '未发布'
    const availableCount = status === '可借阅' ? 5 : (status === '已借光' ? 0 : 3)
    const totalCount = 5
    const borrowedCount = status === '已借光' ? 5 : (status === '可借阅' ? 0 : 2)

    const author = authors[authorIndex] || ''
    const authorNationality = nationalityList[nationalityIndex] || ''
    const translator = index % 3 === 0 ? '张明' : ''
    const translatorNationality = index % 3 === 0 ? '中国' : ''

    return {
      id: index + 1,
      bookImg: `https://picsum.photos/100/142?random=book${index}`,
      bookName: `${bookNames[bookNameIndex]}${index + 1}`,
      author,
      translator,
      authorNationality,
      translatorNationality,
      category: categoryList[categoryIndex] ?? '未分类', // 保证非 undefined
      status: status ?? '未发布', // 保证非 undefined
      shelfTime: `2024-${String((index % 12) + 1).padStart(2, '0')}-${String((index % 28) + 1).padStart(2, '0')} ${String(index % 24).padStart(2, '0')}:${String((index * 2) % 60).padStart(2, '0')}:${String((index * 3) % 60).padStart(2, '0')}`,
      description: `这是书籍${index + 1}的简介，这是一本关于${categoryList[categoryIndex]}的优秀书籍。`,
      availableCount,
      totalCount,
      borrowedCount,
      isDraft
    }
  })
}

// 获取状态对应的标签类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    '可借阅': 'success',
    '待上架': 'warning',
    '已借光': 'danger',
    '未发布': 'info'
  }
  return typeMap[status] || 'info'
}

// 事件处理函数
const handleSelectionChange = (selection: Book[]) => {
  selectedRows.value = selection
}

const handleSizeChange = (size: number) => {
  console.log('每页大小改变:', size)
  loadData()
}

const handleCurrentChange = (page: number) => {
  console.log('当前页码改变:', page)
  loadData()
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
  router.push('/borrow/BookBorrow/BookDetail')
  ElMessage.info(`查看详情: ${row.bookName}`)
}

const handleEdit = (row: Book) => {
  router.push('/borrow/BookBorrow/BookCreate')
  ElMessage.success(`编辑书籍: ${row.bookName}`)
}

const handleDelete = async (row: Book) => {
  try {
    let message = '是否删除书籍？'
    if (row.borrowedCount > 0) {
      message = `当前有${row.borrowedCount}人已借阅此书，是否要删除书籍？`
    }
    
    await ElMessageBox.confirm(
      message,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const index = tableData.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      tableData.value.splice(index, 1)
      total.value -= 1
      ElMessage.success('删除成功')
    }
  } catch {
    ElMessage.info('取消删除')
  }
}

const handleCreateBook = () => {
  router.push('/borrow/BookBorrow/BookCreate')
}

// 搜索组件事件处理
const handleSearchInput = (val: string) => {
  filterForm.bookName = val
  handleSearch()
}

const handleCategoryChange = (val: string) => {
  filterForm.categoryId = val
  handleSearch()
}

const handleStatusChange = (val: string) => {
  filterForm.status = val
  handleSearch()
}

const handleSearch = () => {
  console.log('搜索条件:', filterForm)
  loadData()
}

// 加载数据
const loadData = () => {
  loading.value = true
  setTimeout(() => {
    const data = generateMockData()
    // 模拟筛选
    let filteredData = data
    if (filterForm.bookName) {
      filteredData = filteredData.filter(book => 
        book.bookName.includes(filterForm.bookName)
      )
    }
    if (filterForm.status) {
      filteredData = filteredData.filter(book => 
        book.status === filterForm.status
      )
    }
    if (filterForm.categoryId) {
      filteredData = filteredData.filter(book => 
        book.category === categoryList[parseInt(filterForm.categoryId)]
      )
    }
    
    tableData.value = filteredData
    total.value = filteredData.length
    loading.value = false
  }, 500)
}

// 生命周期
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-book-borrow {
  padding: 20px;
  background-color: #f5f7fa;
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
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.book-image {
  width: 100px;
  height: 142px;
  border-radius: 4px;
  flex-shrink: 0;
  object-fit: cover;
}

.book-details {
  flex: 1;
  min-width: 0;
}

.book-name-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.book-name {
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.draft-indicator {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  color: #909399;
  border-radius: 50%;
  font-size: 14px;
  font-style: italic;
  cursor: help;
}

.book-author {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
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
  justify-content: center;
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

.action-text.delete {
  color: #F56C6C;
}

.action-text.delete:hover {
  color: #E6A23C;
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

/* 确保分类列左对齐 */
:deep(.el-table .el-table__cell:has(.category-cell)) {
  text-align: left;
}
</style>