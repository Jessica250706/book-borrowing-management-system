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
            :model-value="filterForm.status"
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
              bookImg: row.bookImg,
              bookName: row.bookName,
              author: row.author,
              translator: row.translator
            }" 
            :show-draft-icon="row.status === '未发布' && row.isDraft"
          />
        </div>
      </template>

      <template #column-category="{ row }">
        <div class="category-cell">
          {{ row.category }}
        </div>
      </template>

      <template #column-status="{ row }">
        <el-tag
          :type="getStatusType(row.status)"
          effect="light"
        >
          {{ row.status }}
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
            v-if="row.status === '未发布' && !row.isDraft" 
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

// 导入搜索组件
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import BookCategorySelect from '@/components/BookScreen/BookCategorySelect.vue';
import BookStatusSelect from '@/components/BookScreen/BookStatusSelect.vue';

// 导入对话框组件
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';

// 导入API
import { publishBook, deleteBook } from '@/apis/book';

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
  categoryId: number  
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
  { label: '未发布', value: '未发布' },
  { label: '待上架', value: '待上架' },
  { label: '可借阅', value: '可借阅' },
  { label: '已借光', value: '已借光' }
])

// 分类选项配置
const categoryOptions = ref<CategoryOption[]>([
  { label: '所有分类', value: '' },
  { label: 'A、马克思主义、列宁主义、毛泽东思想、邓小平理论', value: '0' },
  { label: 'B、哲学、宗教', value: '1' },
  { label: 'C、社会科学总论', value: '2' },
  { label: 'D、政治、法律', value: '3' },
  { label: 'E、军事', value: '4' },
  { label: 'F、经济', value: '5' },
  { label: 'G、文化、科学、教育、体育', value: '6' },
  { label: 'H、语言、文字', value: '7' },
  { label: 'I、文学', value: '8' },
  { label: 'J、艺术', value: '9' },
  { label: 'K、历史、地理', value: '10' },
  { label: 'N、自然科学总论', value: '11' },
  { label: 'O、数理科学和化学', value: '12' },
  { label: 'P、天文学、地球科学', value: '13' },
  { label: 'Q、生物科学', value: '14' },
  { label: 'R、医药、卫生', value: '15' },
  { label: 'S、农业科学', value: '16' },
  { label: 'T、工业技术', value: '17' },
  { label: 'U、交通运输', value: '18' },
  { label: 'V、航空、航天', value: '19' },
  { label: 'X、环境科学、安全科学', value: '20' },
  { label: 'Z、综合性图书', value: '21' }
])

// 响应式数据
const loading = ref(false)
const allTableData = ref<Book[]>([])
const total = ref(0)
const selectedRows = ref<Book[]>([])
const currentPage = ref(1)
const pageSize = ref(10)

// 筛选表单
const filterForm = reactive<FilterForm>({
  bookName: '',
  status: '',
  categoryId: ''
})

// 分页配置
const paginationConfig = reactive({
  pageSizes: [10, 20, 30, 50],
  layout: "total, sizes, prev, pager, next, jumper"
})

// 状态映射
const statusMap = {
  '未发布': 0,
  '待上架': 1, 
  '可借阅': 2,
  '已借光': 3
}

const reverseStatusMap = {
  0: '未发布',
  1: '待上架',
  2: '可借阅',
  3: '已借光'
}

// 计算当前页要显示的数据
const tableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return allTableData.value.slice(start, end)
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
const statusList = ['未发布', '待上架', '可借阅', '已借光']

// 国籍列表
const nationalityList = ['中国', '美国', '英国', '法国', '德国', '日本', '俄罗斯', '加拿大', '澳大利亚', '韩国']

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
const generateMockData = (): Book[] => {
  const authors = ['史蒂芬·霍金', '加西亚·马尔克斯', '尤瓦尔·赫拉利', '唐纳德·诺曼', '托马斯·科尔曼']
  const bookNames = [
    '时间',
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
    const isDraft = status === '未发布' && index < 10 && index % 2 === 0
    const availableCount = status === '可借阅' ? 5 : (status === '已借光' ? 0 : 3)
    const totalCount = 5
    const borrowedCount = status === '可借阅' ? (index % 3) : 0

    const author = authors[authorIndex] || ''
    const authorNationality = nationalityList[nationalityIndex] || ''
    const translator = index % 3 === 0 ? '张明' : ''
    const translatorNationality = index % 3 === 0 ? '中国' : ''

    return {
      id: index + 1,
      bookImg: `https://picsum.photos/100/142?random=book ${index}`,
      bookName: `${bookNames[bookNameIndex]}${index + 1}`,
      author,
      translator,
      authorNationality,
      translatorNationality,
      category: categoryList[categoryIndex] ?? '未分类',
      categoryId: categoryIndex,  
      status: status ?? '未发布',
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
    '未发布': 'warning',    // 黄色
    '待上架': 'primary',    // 蓝色  
    '可借阅': 'success',    // 绿色
    '已借光': 'danger'      // 红色
  }
  return typeMap[status] || 'info'
}

// 事件处理函数
const handleStatusUpdate = (val: string) => {
  filterForm.status = val
  handleSearch() 
}

const handleCategoryUpdate = (val: string) => {
  filterForm.categoryId = val
  handleSearch() 
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
  // 使用查询参数方式跳转
  router.push({
    path: '/borrow/BookBorrow/BookDetail',
    query: {
      id: row.id.toString()
    }
  })
  ElMessage.success(`查看详情: ${row.bookName}`)
}

const handleEdit = (row: Book) => {
  router.push(`/borrow/BookBorrow/BookCreate?edit=true&id=${row.id}`)
  ElMessage.success(`编辑书籍: ${row.bookName}`)
}

// 发布书籍
const handlePublish = async (row: Book) => {
  try {
    await showConfirmDialog({
      title: '发布',
      message: `是否发布书籍？`,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await publishBook(row.id)
          
          // 通过 response.data 访问返回的数据
          if (response.data?.code === 200) {
            const book = allTableData.value.find(item => item.id === row.id)
            if (book) {
              book.status = '待上架' // 从未发布变为待上架
              book.isDraft = false
              book.shelfTime = new Date().toISOString()
            }
            ElMessage.success('发布成功')
          } else {
            ElMessage.error(response.data?.message || '发布失败')
          }
        } catch (error: any) {
          console.error('发布失败:', error)
          ElMessage.error(error.response?.data?.message || error.message || '发布失败，请重试')
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
  try {
    let message = '是否要删除书籍？'
    let title = '删除'
    
    // 根据书籍状态显示不同的提示信息
    if (row.status === '可借阅' && row.borrowedCount > 0) {
      message = `当前有${row.borrowedCount}人已借阅此书，是否要删除书籍？`
    }

    await showConfirmDialog({
      title,
      message,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          loading.value = true
          const response = await deleteBook(row.id)
          
          if (response.data?.code === 200) {
            // 从本地数据中删除
            const index = allTableData.value.findIndex(item => item.id === row.id)
            if (index !== -1) {
              allTableData.value.splice(index, 1)
              total.value = allTableData.value.length
              
              // 如果当前页没有数据了，跳转到上一页
              if (tableData.value.length === 0 && currentPage.value > 1) {
                currentPage.value -= 1
              }
            }
            ElMessage.success('删除成功')
          } else {
            ElMessage.error(response.data?.message || '删除失败')
          }
        } catch (error: any) {
          console.error('删除书籍失败:', error)
          ElMessage.error(error.response?.data?.message || error.message || '删除失败，请重试')
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
  loadData()
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
  loadData()
}

// 搜索组件事件处理
const handleSearchInput = (val: string) => {
  filterForm.bookName = val
  handleSearch()
}

const handleSearch = () => {
  currentPage.value = 1;
  loadData()
}

// 加载数据
const loadData = () => {
  loading.value = true;
  setTimeout(() => {
    let data = generateMockData();
    
    // 应用筛选条件
    if (filterForm.bookName) {
      data = data.filter(book => book.bookName.includes(filterForm.bookName));
    }
    
    // 状态筛选
    if (filterForm.status) {
      data = data.filter(book => book.status === filterForm.status);
    }
    
    // 分类筛选
    if (filterForm.categoryId && filterForm.categoryId !== '') {
      const selectedCategoryId = parseInt(filterForm.categoryId);
      if (!isNaN(selectedCategoryId)) {
        data = data.filter(book => book.categoryId === selectedCategoryId); 
      }
    }
    
    allTableData.value = data;
    total.value = data.length;
    loading.value = false;
  }, 500);
};

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