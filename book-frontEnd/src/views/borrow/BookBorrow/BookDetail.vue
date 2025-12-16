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
        <template v-else-if="isAdmin">
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

    <!-- 错误状态 -->
    <div v-else-if="!bookDetail.bookId" class="error-container">
      <el-empty description="书籍信息加载失败" />
      <el-button type="primary" @click="fetchBookDetail">重新加载</el-button>
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
            <div class="book-name" :style="bookNameStyle">
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
              <span class="value">{{ formatCategoryWithLetter(bookDetail.categoryName) }}</span>
            </div>
            
            <!-- 状态 -->
            <div class="info-row">
              <span class="label">状态：</span>
              <span class="status-badge" :class="getStatusClass(bookDetail.bookStatus)">
                {{ getStatusText(bookDetail.bookStatus) }}
              </span>
            </div>
            
            <!-- 库存（管理员可见） -->
            <div v-if="isAdmin" class="info-row">
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
          <!-- 有预览文件时显示预览 -->
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
          
          <!-- 无预览文件时显示提示 -->
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
import { useUserStore } from '@/store/modules/user'

// 导入API
import { getBookDetail, borrowBook, reserveBook, cancelReserve, deleteBook } from '@/apis/book'
import type { BookDetailDTO, ActionResponse } from '@/apis/book/type'
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue'


const router = useRouter()
const route = useRoute()
const userStore = useUserStore()


// 用户身份判断
const isReader = computed(() => userStore.isReader)
const isAdmin = computed(() => userStore.isAdmin)

const isLoggedIn = computed(() => userStore.isLoggedIn)

const loading = ref(true)

// 书籍详情数据
const bookDetail = reactive<BookDetailDTO>({
  bookId: 0,
  bookName: '',
  coverUrl: '',
  author: '',
  translator: '',
  categoryId: 0,
  bookStatus: 0,
  totalCount: 0,
  availableCount: 0,
  shelfTime: '',
  intro: '',
  publisher: '',
  isbn: '',
  copyrightHolder: '',
  publishCount: 0,
  publishUnit: '',
  publishWebsite: '',
  publishBatch: '',
  publishDate: '',
  borrowCount: 0,
  categoryName: '',
  isReservedByCurrentUser: false,
  isBorrowedByCurrentUser: false,
  reserveCount: 0
})

// 添加分类映射
const categoryMap: Record<string, string> = {
  'A、马克思主义、列宁主义、毛泽东思想、邓小平理论': 'A',
  'B、哲学、宗教': 'B',
  'C、社会科学总论': 'C',
  'D、政治、法律': 'D',
  'E、军事': 'E',
  'F、经济': 'F',
  'G、文化、科学、教育、体育': 'G',
  'H、语言、文字': 'H',
  'I、文学': 'I',
  'J、艺术': 'J',
  'K、历史、地理': 'K',
  'N、自然科学总论': 'N',
  'O、数理科学和化学': 'O',
  'P、天文学、地球科学': 'P',
  'Q、生物科学': 'Q',
  'R、医药、卫生': 'R',
  'S、农业科学': 'S',
  'T、工业技术': 'T',
  'U、交通运输': 'U',
  'V、航空、航天': 'V',
  'X、环境科学、安全科学': 'X',
  'Z、综合性图书': 'Z'
}

// 添加分类名称和字母的映射
const categoryNameToLetter: Record<string, string> = {
  '马克思主义、列宁主义、毛泽东思想、邓小平理论': 'A',
  '哲学、宗教': 'B',
  '社会科学总论': 'C',
  '政治、法律': 'D',
  '军事': 'E',
  '经济': 'F',
  '文化、科学、教育、体育': 'G',
  '语言、文字': 'H',
  '文学': 'I',
  '艺术': 'J',
  '历史、地理': 'K',
  '自然科学总论': 'N',
  '数理科学和化学': 'O',
  '天文学、地球科学': 'P',
  '生物科学': 'Q',
  '医药、卫生': 'R',
  '农业科学': 'S',
  '工业技术': 'T',
  '交通运输': 'U',
  '航空、航天': 'V',
  '环境科学、安全科学': 'X',
  '综合性图书': 'Z'
}

// 辅助函数：为分类名称添加字母前缀
const formatCategoryWithLetter = (categoryName: string | undefined): string => {
  if (!categoryName) return '未分类'
  
  // 如果已经有字母前缀，直接返回
  if (/^[A-Z]、/.test(categoryName)) {
    return categoryName
  }
  
  // 查找对应的字母
  for (const [name, letter] of Object.entries(categoryNameToLetter)) {
    if (categoryName.includes(name)) {
      return `${letter}、${categoryName}`
    }
  }
  
  // 如果没有找到匹配，尝试从完整的分类选项映射中查找
  const fullCategory = Object.keys(categoryMap).find(key => 
    key.includes(categoryName) || categoryName.includes(cleanCategoryName(key))
  )
  
  if (fullCategory) {
    const letter = categoryMap[fullCategory]
    return `${letter}、${categoryName}`
  }
  
  // 最后返回原始名称
  return categoryName
}

// 清理分类名称（去掉字母前缀）
const cleanCategoryName = (categoryName: string): string => {
  if (!categoryName) return ''
  return categoryName.replace(/^[A-Z]、/, '')
}

// 预览文件
const previewFile = ref<any>(null)

// 根据用户身份获取借阅天数
const getUserBorrowDays = (): number => {
  const readerType = userStore.readerType;
  console.log('用户类型 (readerType):', readerType);

  // 根据用户类型返回借阅天数
  const borrowDaysMap: Record<string, number> = {
    'social': 15,        // 社会人员
    'student': 30,       // 学生
    'teacher': 60,       // 老师
  };

  return borrowDaysMap[readerType] ?? 0; 
};

// 获取状态文本
const getStatusText = (status: number | undefined) => {
  if (isReader.value) {
    // 读者端状态映射 
    const readerStatusMap: { [key: number]: string } = {
      0: '未发布',
      1: '未发布',  
      2: '待上架',
      3: '可借阅',
      4: '已借光'
    }
    return status !== undefined ? readerStatusMap[status] || '未知状态' : '未知状态'
  } else {
    // 管理员端状态映射
    const adminStatusMap: { [key: number]: string } = {
      0: '未发布',
      1: '未发布',
      2: '待上架', 
      3: '可借阅',
      4: '已借光'
    }
    return status !== undefined ? adminStatusMap[status] || '未知状态' : '未知状态'
  }
}

// 获取状态样式类
const getStatusClass = (status: number | undefined) => {
  const statusText = getStatusText(status)
  
  const classMap: { [key: string]: string } = {
    '未发布': 'unpublished',
    '待上架': 'pending',
    '已预约': 'reserved',
    '可借阅': 'available',
    '已借光': 'out-of-stock',
    '已借阅': 'borrowed'
  }
  return classMap[statusText] || 'default'
}

// 计算书籍名称的样式
const bookNameStyle = computed(() => {
  return {
    'margin-bottom': isAdmin.value ? '48px' : '76px'
  }
})

// 处理返回
const handleBack = () => {
  router.back()
}

// 处理登录
const handleLogin = () => {
  router.push('/login')
}

// 处理借阅
const handleBorrow = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    handleLogin()
    return
  }

  // 获取用户借阅天数
  const borrowDays = getUserBorrowDays();
  
  let message = '是否借阅书籍？';
  if (borrowDays > 0) {
    message = `是否借阅书籍？书籍可借阅天数为${borrowDays}天。`;
  } else {
    message = '是否借阅书籍？系统将根据您的身份自动设置借阅天数。';
  }

  try {
    const result = await showConfirmDialog({
      title: '借阅',
      message: message,
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          const response = await borrowBook(bookDetail.bookId!) as any;
          
          console.log('借阅API响应:', response);
          
          if (response.code === 200) {
            ElMessage.success(response.message || '借阅成功');
            fetchBookDetail(); // 重新加载数据
          } else {
            ElMessage.error(response.message || '借阅失败');
          }
        } catch (error: any) {
          console.error('借阅失败:', error);
          let errorMsg = '借阅失败，请重试';
          if (error.response?.data?.message) {
            errorMsg = error.response.data.message;
          } else if (error.message) {
            errorMsg = error.message;
          }
          ElMessage.error(errorMsg);
        }
      }
    });
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '借阅失败，请重试');
    }
  }
}

// 处理预约
const handleReserve = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    handleLogin()
    return
  }

  try {
    const result = await showConfirmDialog({
      title: '预约',
      message: '是否预约书籍？若预约成功，则书籍有库存时会发送消息提醒。',
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          const response = await reserveBook(bookDetail.bookId!) as any;
          
          console.log('预约API响应:', response);
          
          if (response.code === 200 || response.code === 201) {
            ElMessage.success('预约成功');
            fetchBookDetail();
          } else {
            ElMessage.error(response.message || '预约失败');
          }
        } catch (error: any) {
          console.error('预约失败:', error);
          let errorMsg = '预约失败，请重试';
          if (error.response?.data?.message) {
            errorMsg = error.response.data.message;
          } else if (error.message) {
            errorMsg = error.message;
          }
          ElMessage.error(errorMsg);
        }
      }
    });
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '预约失败，请重试');
    }
  }
}

// 处理取消预约
const handleCancelReserve = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    handleLogin()
    return
  }

  try {
    const result = await showConfirmDialog({
      title: '取消预约',
      message: '是否取消预约书籍？',
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        try {
          const response = await cancelReserve(bookDetail.bookId!) as any;
          
          console.log('取消预约API响应:', response);
          
          if (response.code === 200) {
            ElMessage.success('取消预约成功');
            fetchBookDetail();
          } else {
            ElMessage.error(response.message || '取消预约失败');
          }
        } catch (error: any) {
          console.error('取消预约失败:', error);
          let errorMsg = '取消预约失败，请重试';
          if (error.response?.data?.message) {
            errorMsg = error.response.data.message;
          } else if (error.message) {
            errorMsg = error.message;
          }
          ElMessage.error(errorMsg);
        }
      }
    });
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消预约失败，请重试');
    }
  }
}

// 处理编辑
const handleEdit = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    handleLogin()
    return
  }

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
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    handleLogin()
    return
  }

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
        try {
          const response = await deleteBook(bookDetail.bookId!) as any;
          
          console.log('删除API响应:', response);
          
          if (response.code === 200) {
            ElMessage.success('删除成功');
            router.back();
          } else {
            ElMessage.error(response.message || '删除失败');
          }
        } catch (error: any) {
          console.error('删除失败:', error);
          let errorMsg = '删除失败，请重试';
          if (error.response?.data?.message) {
            errorMsg = error.response.data.message;
          } else if (error.message) {
            errorMsg = error.message;
          }
          ElMessage.error(errorMsg);
        }
      }
    });
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败，请重试');
    }
  }
}

const fetchBookDetail = async () => {
  try {
    console.log('=== 开始获取书籍详情 ===')
    console.log('路由参数 (params):', route.params)
    console.log('查询参数 (query):', route.query)
    console.log('当前路由路径:', route.path)
    console.log('当前路由全路径:', route.fullPath)
    
    let bookId = route.params.id as string
    
    if (!bookId) {
      bookId = route.query.id as string
      console.log('从 params 未获取到 ID，尝试从 query 获取:', bookId)
    }
    
    if (!bookId) {
      ElMessage.error('书籍ID不存在')
      loading.value = false
      return
    }

    console.log('最终使用的书籍ID:', bookId)
    loading.value = true
    
    const response = await getBookDetail(parseInt(bookId)) as any
    
    console.log('API响应:', response) 
    
    if (response.code === 200 || response.code === 0) { 
      const data = response.data
      console.log('书籍详情数据:', data)
      
      // 检查数据结构
      if (!data) {
        ElMessage.error('书籍数据为空')
        loading.value = false
        return
      }
      
      Object.assign(bookDetail, {
        bookId: Number(data.bookId) || 0,
        bookName: data.bookName || '',
        coverUrl: data.coverUrl || '',
        author: data.author || '',
        translator: data.translator || '',
        category: data.category || data.categoryName || '未分类',
        categoryName: data.categoryName || data.category || '未分类',
        bookStatus: Number(data.bookStatus) || 0,
        totalCount: Number(data.totalCount) || 0,
        availableCount: Number(data.availableCount) || 0,
        borrowCount: Number(data.borrowCount) || 0,
        reserveCount: Number(data.reserveCount) || 0,
        intro: data.intro || '',
        publisher: data.publisher || '',
        isbn: data.isbn || '',
        copyrightHolder: data.copyrightHolder || '未知',
        publishCount: Number(data.publishCount) || 0,
        publishUnit: data.publishUnit || '未知',
        publishWebsite: data.publishWebsite || '未知',
        publishBatch: data.publishBatch || '未知',
        publishDate: data.publishDate || '',
        shelfTime: data.shelfTime || '',
        price: data.price || 0,
        // 用户相关状态
        isReservedByCurrentUser: Boolean(data.isReservedByCurrentUser),
        isBorrowedByCurrentUser: Boolean(data.isBorrowedByCurrentUser)
      })
      
      console.log('最终bookDetail对象:', bookDetail)
      
      // 设置预览文件
      setupPreviewFile()
      
    } else {
      const errorMsg = response.message || '获取书籍详情失败'
      ElMessage.error(errorMsg)
    }
  } catch (error: any) {
    console.error('获取书籍详情失败:', error)
    
    // 更详细的错误信息
    if (error.response) {
      console.error('响应状态码:', error.response.status)
      console.error('响应数据:', error.response.data)
      ElMessage.error(`API错误 (${error.response.status}): ${error.response.data?.message || '请求失败'}`)
    } else if (error.request) {
      console.error('请求未收到响应:', error.request)
      ElMessage.error('网络连接错误，请检查网络连接')
    } else {
      console.error('请求配置错误:', error.message)
      ElMessage.error(error.message || '网络错误，请检查连接')
    }
  } finally {
    loading.value = false
  }
}

// 根据实际数据设置预览文件
const setupPreviewFile = () => {
  // 如果后端提供了预览文件信息，可以在这里设置
  // 例如：bookDetail.previewUrl 或 bookDetail.attachmentUrl
  if (bookDetail.coverUrl) {
    previewFile.value = {
      name: '书籍封面',
      type: 'image/jpeg',
      url: bookDetail.coverUrl
    }
  } else {
    previewFile.value = null
  }
}

// 封面图片加载失败处理
const handleCoverError = (e: Event) => {
  const img = e.target as HTMLImageElement
  img.src = 'assets/default.png'
}

// 格式化日期
const formatDate = (dateString: string | undefined) => {
  if (!dateString) return ''
  return dateString.split(' ')[0]
}

// 组件挂载时获取数据
onMounted(() => {
  console.log('=== BookDetail 组件挂载 ===')
  console.log('路由参数 (params):', route.params)
  console.log('查询参数 (query):', route.query)
  console.log('路由名称:', route.name)
  console.log('路由路径:', route.path)
  console.log('完整路径:', route.fullPath)
  console.log('用户角色:', userStore.roleCode)
  
  // 获取书籍ID
  const bookId = route.params.id || route.query.id
  if (bookId) {
    fetchBookDetail()
  } else {
    console.warn('未找到书籍ID')
    ElMessage.warning('未获取到书籍信息')
    loading.value = false
  }
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

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  text-align: center;
}

.error-container .el-button {
  margin-top: 20px;
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
  padding: 20px 30px;
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
  gap: 4px;
}

.book-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 48px;
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
