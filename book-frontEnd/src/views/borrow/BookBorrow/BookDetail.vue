<template>
  <div class="book-detail-page">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <div class="header-left">
        <el-icon class="back-arrow" @click="handleBack">
          <Back />
        </el-icon>
        <span class="page-title">书籍详情</span>
      </div>
      <div class="header-right">
        <!-- 读者端操作按钮 -->
      <template v-if="isReader">
        <el-button 
          v-if="getStatusText(bookDetail.bookStatus) === '可借阅'" 
          type="primary" 
          class="borrow-btn"
          @click="handleBorrow"
        >
          借阅
        </el-button>
        <el-button 
          v-else-if="['待上架', '已借光'].includes(getStatusText(bookDetail.bookStatus)) && !bookDetail.isReservedByCurrentUser" 
          type="primary" 
          class="reserve-btn"
          @click="handleReserve"
        >
          预约
        </el-button>
        <el-button 
          v-else-if="bookDetail.isReservedByCurrentUser" 
          type="primary" 
          class="cancel-reserve-btn"
          @click="handleCancelReserve"
        >
          取消预约
        </el-button>
      </template>

        <!-- 管理员端操作按钮 -->
        <template v-else>
          <el-button type="primary" class="edit-btn" @click="handleEdit">
            编辑
          </el-button>
          <el-button type="danger" class="delete-btn" @click="handleDelete">
            删除
          </el-button>
        </template>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" color="#409EFF" :size="32">
        <Loading />
      </el-icon>
      <div class="loading-text">加载中...</div>
    </div>
    <!-- 主要内容区域 -->
    <div v-else class="main-content">
      <!-- 基本信息板块 -->
      <div class="section">
        <div class="basic-info-content">
          <!-- 左侧书籍封面 -->
          <div class="book-cover-section">
            <img 
              v-if="bookDetail.coverUrl" 
              :src="bookDetail.coverUrl" 
              :alt="bookDetail.bookName"
              class="book-cover"
              @error="handleCoverError"
            />
            <div v-else class="cover-placeholder">
              <el-icon class="cover-icon"><Picture /></el-icon>
              <div class="cover-text">暂无封面</div>
            </div>
          </div>
          
          <!-- 右侧书籍信息 -->
          <div class="book-info-section">
            <!-- 书籍名称 -->
            <div class="book-name">
              {{ bookDetail.bookName || '未知书名' }}
            </div>
            
            <!-- 作者和译者 -->
            <div class="info-row">
              <span class="label">作者：</span>
              <span class="value">{{ bookDetail.author || '未知作者' }}</span>
              <span v-if="bookDetail.translator" class="translator">
                译者：{{ bookDetail.translator }}
              </span>
            </div>
            
            <!-- 分类 -->
            <div class="info-row">
              <span class="label">分类：</span>
              <span class="value">{{ bookDetail.categoryName || '未分类' }}</span>
            </div>
            
            <!-- 状态 -->
            <div class="info-row">
              <span class="label">状态：</span>
              <span class="status-badge" :class="getStatusClass(bookDetail.bookStatus)">
                {{ getStatusText(bookDetail.bookStatus) }}
              </span>
            </div>
            
            <!-- 库存（仅管理员可见） -->
            <div v-if="!isReader" class="info-row">
              <span class="label">库存：</span>
              <span class="value">
                {{ bookDetail.availableCount || 0 }}本（共{{ bookDetail.totalCount || 0 }}本）
              </span>
            </div>
            
            <!-- 借阅次数 -->
            <div class="info-row">
              <span class="label">借阅次数：</span>
              <span class="value">{{ bookDetail.borrowCount || 0 }}人次</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 简介板块 -->
      <div class="section">
        <div class="section-header">
          <div class="blue-line"></div>
          <span class="section-title">简介</span>
        </div>
        
        <div class="intro-content">
          {{ bookDetail.intro || '暂无简介' }}
        </div>
      </div>
      
      <!-- 版权信息板块 -->
      <div class="section">
        <div class="section-header">
          <div class="blue-line"></div>
          <span class="section-title">版权信息</span>
        </div>
        
        <div class="copyright-info-grid">
          <!-- 左列 -->
          <div class="left-column">
            <div class="info-item">
              <span class="label">出版社：</span>
              <span class="value">{{ bookDetail.publisher || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">版权持有：</span>
              <span class="value">{{ bookDetail.copyrightHolder || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">发行单位：</span>
              <span class="value">{{ bookDetail.publishUnit || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">发行批次：</span>
              <span class="value">{{ bookDetail.publishBatch || '未知' }}</span>
            </div>
          </div>
          
          <!-- 右列 -->
          <div class="right-column">
            <div class="info-item">
              <span class="label">ISBN：</span>
              <span class="value">{{ bookDetail.isbn || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">发行数量：</span>
              <span class="value">{{ bookDetail.publishCount || '未知' }}本</span>
            </div>
            <div class="info-item">
              <span class="label">发行网站：</span>
              <span class="value">{{ bookDetail.publishWebsite || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">发行时间：</span>
              <span class="value">{{ formatDate(bookDetail.publishDate) || '未知' }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 预览板块 -->
      <div class="section">
        <div class="section-header">
          <div class="blue-line"></div>
          <span class="section-title">预览</span>
        </div>
        
        <div class="preview-section">
          <div v-if="previewFile" class="preview-content">
            <!-- 图片预览 -->
            <div v-if="previewFile.type.startsWith('image/')" class="image-preview">
              <img :src="previewFile.url" :alt="previewFile.name" class="preview-image" />
              <div class="preview-overlay">
                <span class="preview-text">图片预览</span>
              </div>
            </div>
            
            <!-- PDF预览 -->
            <div v-else-if="previewFile.type === 'application/pdf'" class="pdf-preview">
              <embed 
                :src="previewFile.url" 
                type="application/pdf" 
                class="pdf-embed"
                width="100%" 
                height="500"
              />
              <div class="pdf-overlay">
                <span class="preview-text">PDF预览</span>
              </div>
            </div>
            
            <!-- 其他文件类型 -->
            <div v-else class="unknown-preview">
              <el-icon class="unknown-icon"><Document /></el-icon>
              <div class="unknown-info">
                <div class="unknown-name">{{ previewFile.name }}</div>
                <div class="unknown-type">不支持在线预览</div>
              </div>
            </div>
          </div>
          <div v-else class="no-preview">
            <el-icon class="no-preview-icon"><Document /></el-icon>
            <div class="no-preview-text">暂无预览文件</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Back, 
  Picture,
  Document,
  Loading
} from '@element-plus/icons-vue'

// 导入API和自定义弹窗
import { getBookDetail, borrowBook, reserveBook, cancelReserve, deleteBook } from '@/apis/book'
import type { BookDetailDTO, ActionResponse } from '@/apis/book/type'
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue'
import { fa } from 'element-plus/es/locales.mjs'

const router = useRouter()
const route = useRoute()

// 用户身份判断
const isReader = ref(false)  // 默认为管理员，实际要从接口获得身份信息

const loading = ref(true) // 默认加载中

// 书籍详情数据
const bookDetail = reactive<BookDetailDTO>({
  bookId: 0,
  bookName: '加载中...',
  coverUrl: '',
  author: '加载中...',
  translator: '',
  categoryId: 0,
  bookStatus: 0,
  totalCount: 0,
  availableCount: 0,
  shelfTime: '',
  intro: '加载中...',
  publisher: '',
  isbn: '',
  copyrightHolder: '',
  publishCount: 0,
  publishUnit: '',
  publishWebsite: '',
  publishBatch: '',
  publishDate: '',
  borrowCount: 0,
  categoryName: '加载中...',
  isReservedByCurrentUser: false,
  isBorrowedByCurrentUser: false
})

// 预览文件
const previewFile = ref<any>(null)

// 获取状态文本
const getStatusText = (status: number | undefined) => {
  if (isReader.value) {
    // 如果用户已预约，优先显示"已预约"状态
    if (bookDetail.isReservedByCurrentUser) {
      return '已预约'
    }
    
    // 读者端状态映射
    const readerStatusMap: { [key: number]: string } = {
      0: '未发布',    // 理论上读者看不到
      1: '待上架',
      2: bookDetail.isBorrowedByCurrentUser ? '已借阅' : '可借阅',
      3: '已借光'
    }
    return status !== undefined ? readerStatusMap[status] || '未知状态' : '未知状态'
  } else {
    // 管理员端状态映射
    const adminStatusMap: { [key: number]: string } = {
      0: '未发布',
      1: '待上架', 
      2: '可借阅',
      3: '已借光'
    }
    return status !== undefined ? adminStatusMap[status] || '未知状态' : '未知状态'
  }
}

// 获取状态样式类
const getStatusClass = (status: number | undefined) => {
  const statusText = getStatusText(status)
  
  const classMap: { [key: string]: string } = {
    '未发布': 'unpublished',    // 黄色
    '待上架': 'pending',        // 蓝色
    '已预约': 'reserved',       // 灰色
    '可借阅': 'available',      // 绿色
    '已借光': 'out-of-stock',   // 红色
    '已借阅': 'borrowed'        // 橙色
  }
  return classMap[statusText] || 'default'
}

// 处理返回
const handleBack = () => {
  router.back()
}

// 处理借阅
const handleBorrow = async () => {
  try {
    const result = await showConfirmDialog({
      title: '借阅',
      message: '是否借阅书籍？书籍可借阅天数为30天。',
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        const response = await borrowBook({
          bookId: bookDetail.bookId!,
          borrowDays: 30
        })
        
        if (response.data?.code === 200) {
          ElMessage.success('借阅成功')
          fetchBookDetail()
        } else {
          ElMessage.error(response.data?.message || '借阅失败')
        }
      }
    })
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '借阅失败，请重试')
    }
  }
}

// 处理预约
const handleReserve = async () => {
  try {
    const result = await showConfirmDialog({
      title: '预约',
      message: '是否预约书籍？若预约成功，则书籍有库存时会发送消息提醒。',
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        const response = await reserveBook(bookDetail.bookId!)
        
        if (response.data?.code === 200) {
          ElMessage.success('预约成功')
          fetchBookDetail()
        } else {
          ElMessage.error(response.data?.message || '预约失败')
        }
      }
    })
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '预约失败，请重试')
    }
  }
}

// 处理取消预约
const handleCancelReserve = async () => {
  try {
    const result = await showConfirmDialog({
      title: '取消预约',
      message: '是否取消预约书籍？',
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        const response = await cancelReserve(bookDetail.bookId!)
        
        if (response.data?.code === 200) {
          ElMessage.success('取消预约成功')
          fetchBookDetail()
        } else {
          ElMessage.error(response.data?.message || '取消预约失败')
        }
      }
    })
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消预约失败，请重试')
    }
  }
}

// 处理编辑
const handleEdit = () => {
  router.push({
    path: '/borrow/BookBorrow/BookCreate',
    query: {
      id: bookDetail.bookId?.toString(),
      edit: 'true'
    }
  })
}

// 处理删除
const handleDelete = async () => {
  try {
    let message = '是否要删除书籍？'
    
    if (bookDetail.borrowCount && bookDetail.borrowCount > 0) {
      message = `当前有${bookDetail.borrowCount}人已借阅此书，是否要删除书籍？`
    }

    const result = await showConfirmDialog({
      title: '删除',
      message: message,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        const response = await deleteBook(bookDetail.bookId!)
        
        if (response.data?.code === 200) {
          ElMessage.success('删除成功')
          router.back()
        } else {
          ElMessage.error(response.data?.message || '删除失败')
        }
      }
    })
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败，请重试')
    }
  }
}

// 获取书籍详情
const fetchBookDetail = async () => {
  try {
    const bookId = route.query.id as string
    if (!bookId) {
      ElMessage.error('书籍ID不存在')
      setMockData()
      return
    }

    // 先设置加载状态或使用骨架屏
    loading.value = true
    
    const response = await getBookDetail(parseInt(bookId))
    
    if (response.data?.code === 200 && response.data.data) {
      Object.assign(bookDetail, response.data.data)
    } else {
      setMockData()
    }
  } catch (error: any) {
    setMockData()
  } finally {
    loading.value = false
  }
}

// 设置模拟数据
const setMockData = () => {
  const mockBook: BookDetailDTO = {
    bookId: parseInt(route.query.id as string) || 1,
    bookName: route.query.name as string || '示例书籍名称',
    coverUrl: '',
    author: route.query.author as string || '[中国]佚名',
    translator: '佚名',
    categoryId: 1,
    bookStatus: 2,
    totalCount: 10,
    availableCount: 5,
    shelfTime: '2025-11-15 10:00:00',
    intro: '这是一本示例书籍的简介内容，用于展示书籍详情页面的布局和功能。书籍简介可以包含书籍的主要内容、特色、作者介绍等信息。这是一本示例书籍的简介内容，用于展示书籍详情页面的布局和功能。书籍简介可以包含书籍的主要内容、特色、作者介绍等信息。这是一本示例书籍的简介内容，用于展示书籍详情页面的布局和功能。书籍简介可以包含书籍的主要内容、特色、作者介绍等信息。',
    publisher: '示例出版社',
    isbn: '978-7-123-45678-9',
    copyrightHolder: '示例版权持有方',
    publishCount: 5000,
    publishUnit: '示例发行单位',
    publishWebsite: 'https://example.com',
    publishBatch: '第一批',
    publishDate: '2024-01-01',
    borrowCount: 5,
    categoryName: '文学',
    isReservedByCurrentUser: false,
    isBorrowedByCurrentUser: false
  }
  
  Object.assign(bookDetail, mockBook)
  
  // 预览文件模拟数据 - 随机选择一种类型
  const previewOptions = [
    {
      name: '书籍内页预览.jpg',
      type: 'image/jpeg',
      url: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&h=800&fit=crop',
      size: 2048000
    },
    {
      name: '示例文档.pdf',
      type: 'application/pdf',
      url: 'https://pdfobject.com/pdf/sample.pdf', // PDFObject库的示例文件
      size: 890000
    },
    null // 无预览
  ]
  
  previewFile.value = previewOptions[Math.floor(Math.random() * previewOptions.length)]
}

// 封面图片加载失败处理
const handleCoverError = (e: Event) => {
  const img = e.target as HTMLImageElement
  img.src = 'https://via.placeholder.com/150x213?text=暂无封面'
}

// 格式化日期
const formatDate = (dateString: string | undefined) => {
  if (!dateString) return ''
  return dateString.split(' ')[0]
}

// 组件挂载时获取数据
onMounted(() => {
  fetchBookDetail()
})
</script>

<style scoped>
.book-detail-page {
  background-color: #fff;
  min-height: 100vh;
  border-radius: 10px;
  overflow: hidden;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
}

.loading-text {
  margin-top: 16px;
  color: #666;
  font-size: 14px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  padding: 15px 20px 0 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-arrow {
  font-size: 18px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.back-arrow:hover {
  color: #333;
}

.page-title {
  font-size: 16px;
  color: #666;
}

.header-right {
  display: flex;
  gap: 12px;
}

.borrow-btn,
.reserve-btn,
.cancel-reserve-btn,
.edit-btn,
.delete-btn {
  width: 90px;
  height: 40px;
  font-size: 14px;
  font-weight: 500;
}

.main-content {
  background: white;
  padding: 20px 20px 20px 30px;
  max-width: 900px;
}

.section {
  margin-bottom: 32px;
}

.section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.blue-line {
  width: 4px;
  height: 25px;
  background-color: #409EFF;
  margin-right: 8px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #666;
}

/* 基本信息样式 */
.basic-info-content {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  margin-top: 10px;
}

.book-cover-section {
  flex-shrink: 0;
}

.book-cover {
  width: 150px;
  height: 213px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #e0e0e0;
}

.cover-placeholder {
  width: 150px;
  height: 213px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}

.cover-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.cover-text {
  font-size: 12px;
}

.book-info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.book-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 40px;
  text-align: left;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 1px;
  font-size: 14px;
  min-height: 24px;
}

.label {
  color: #757575;
  text-align: left;
}

.value {
  color: #757575;
}

.translator {
  color: #757575;
  margin-left: 30px;
}

/* 状态徽章样式 */
.status-badge {
  padding: 2px 8px;
  border-radius: 4px; 
  font-size: 12px;
  color: white;
  font-weight: 500;
  flex-shrink: 0;
}

.status-badge.unpublished {
  background-color: #f3d05c; /* 黄色 */
}

.status-badge.pending {
  background-color: #619cff; /* 蓝色 */
}

.status-badge.reserved {
  background-color: #757575; /* 灰色 */
}

.status-badge.available {
  background-color: #66d07a; /* 绿色 */
}

.status-badge.out-of-stock {
  background-color: #ed4044; /* 红色 */
}

.status-badge.borrowed {
  background-color: #e286f3;
}

.status-badge.default {
  background-color: #b9cbf3;
}

/* 简介样式 */
.intro-content {
  line-height: 1.6;
  color: #757575;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 14px;
  text-align: left;
}

/* 版权信息样式 */
.copyright-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.left-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
}

.info-item .label {
  min-width: 80px;
  color: #757575;
  text-align: right;
}

.info-item .value {
  color: #757575;
  flex: 1;
  text-align: left;
}

/* 预览样式 */
.preview-section {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fafafa;
  min-height: 300px;
  position: relative;
  overflow: hidden;
  display: flex; 
  align-items: center; 
  justify-content: center; 
}

.preview-content {
  width: 100%;
  height: 100%;
}

/* 图片预览 */
.image-preview {
  position: relative;
  max-width: 100%;
  text-align: center;
  padding: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* PDF预览 */
.pdf-preview {
  position: relative;
  width: 100%;
  height: 500px;
}

.pdf-embed {
  border: none;
  width: 100%;
  height: 100%;
}

/* 预览覆盖层 */
.preview-overlay,
.pdf-overlay {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.preview-text {
  font-size: 12px;
}

/* 未知文件类型预览 */
.unknown-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 40px;
  height: 100%;
}

.unknown-icon {
  font-size: 48px;
  color: #909399;
}

.unknown-info {
  text-align: center;
}

.unknown-name {
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

.unknown-type {
  color: #666;
  font-size: 14px;
}

/* 无预览状态 */
.no-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
}

.no-preview-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-preview-text {
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .book-detail-page {
    padding: 0;
  }

  .page-header {
    padding: 15px 16px;
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .header-right {
    width: 100%;
    justify-content: flex-end;
  }

  .main-content {
    padding: 16px;
  }

  .basic-info-content {
    flex-direction: column;
    gap: 20px;
  }

  .book-cover-section {
    align-self: center;
  }

  .copyright-info-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .info-item .label {
    min-width: auto;
    text-align: left;
  }
}
</style>