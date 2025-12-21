<template>
  <div class="book-create-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <el-icon class="back-arrow" @click="handleBack">
          <Back />
        </el-icon>
        <span class="page-title">{{ pageTitle }}</span>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 基本信息板块 -->
      <div class="section">
        <div class="section-header">
          <div class="blue-line"></div>
          <span class="section-title">基本信息</span>
        </div>
        
        <div class="basic-info-grid">
          <!-- 左列 -->
          <div class="left-column">
            <!-- 书籍名称 -->
            <div class="form-item">
              <label class="form-label required">书籍名称：</label>
              <el-input
                v-model="bookForm.bookName"
                placeholder="请输入书籍名称"
                maxlength="50"
                show-word-limit
                clearable
              />
            </div>
            
            <!-- 作者 -->
            <div class="form-item author-item">
              <label class="form-label required">作者：</label>
              <el-input
                v-model="bookForm.author"
                placeholder="请输入作者"
                maxlength="30"
                show-word-limit
                clearable
              />
            </div>
            
            <!-- 译者-->
            <div class="form-item translator-item">
              <label class="form-label">译者：</label>
              <el-input
                v-model="bookForm.translator"
                placeholder="请输入译者"
                maxlength="30"
                show-word-limit
                clearable
              />
            </div>

            <!-- 书籍状态 -->
            <div class="form-item">
              <label class="form-label required">书籍状态：</label>
              <el-select
                v-model="bookForm.bookStatus"
                placeholder="请选择书籍状态"
                :disabled="!isEditMode" 
              >
                <el-option
                  v-for="status in statusOptions"
                  :key="status.value"
                  :label="status.label"
                  :value="status.value"
                />
              </el-select>
            </div>
          </div>
          
          <!-- 右列 -->
          <div class="right-column">
            <!-- 书籍封面 -->
            <div class="form-item">
              <label class="form-label">书籍封面：</label>
              <div class="cover-upload">
                <el-upload
                  class="avatar-uploader"
                  action="#"
                  :show-file-list="false"
                  :before-upload="beforeCoverUpload"
                  :http-request="handleCoverUpload"
                >
                  <img v-if="bookForm.coverUrl" :src="bookForm.coverUrl" class="cover-image" />
                  <div v-else class="upload-placeholder">
                    <el-icon class="upload-icon"><Plus /></el-icon>
                    <div class="upload-text">上传封面</div>
                  </div>
                </el-upload>
              </div>
            </div>
            
            <!-- 书籍分类 -->
            <div class="form-item">
              <label class="form-label required">书籍分类：</label>
              <el-select
                v-model="bookForm.categoryId"
                placeholder="所有分类"
                style="width: 100%"
              >
                <el-option
                  v-for="category in categoryOptions"
                  :key="category.value"
                  :label="category.label"
                  :value="category.value"
                />
              </el-select>
            </div>

            <!-- 书籍总量 -->
            <div class="form-item">
              <label class="form-label required">书籍总量：</label>
              <div class="total-count-input">
                <el-input
                  v-model.number="bookForm.totalCount"
                  placeholder="请输入书籍总量"
                  type="number"
                  min="1"
                  max="999"
                  style="width: 100%"
                />
                <span class="unit">本</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 简介 -->
        <div class="form-item full-width">
          <label class="form-label required">简介：</label>
          <el-input
            v-model="bookForm.intro"
            type="textarea"
            :rows="4"
            placeholder="请输入简介"
            maxlength="300"
            show-word-limit
            resize="none"
          />
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
            <div class="form-item">
              <label class="form-label">出版社：</label>
              <el-input
                v-model="bookForm.publisher"
                placeholder="请输入出版社"
                clearable
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">版权持有：</label>
              <el-input
                v-model="bookForm.copyrightHolder"
                placeholder="请输入版权持有"
                clearable
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">发行单位：</label>
              <el-input
                v-model="bookForm.publishUnit"
                placeholder="请输入发行单位"
                clearable
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">发行批次：</label>
              <el-input
                v-model="bookForm.publishBatch"
                placeholder="请输入发行批次"
                clearable
              />
            </div>
          </div>
          
          <!-- 右列 -->
          <div class="right-column">
            <div class="form-item">
              <label class="form-label">ISBN：</label>
              <el-input
                v-model="bookForm.isbn"
                placeholder="请输入ISBN"
                clearable
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">发行数量：</label>
              <el-input
                v-model.number="bookForm.publishCount"
                placeholder="请输入发行数量"
                type="number"
                min="1"
                max="999"
                clearable
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">发行网站：</label>
              <el-input
                v-model="bookForm.publishWebsite"
                placeholder="请输入发行网站"
                clearable
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">发行时间：</label>
              <el-date-picker
                v-model="bookForm.publishDate"
                type="date"
                placeholder="选择发行时间"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" class="finish-btn" @click="handleFinish">
        完成
      </el-button>
      <el-button type="primary" class="save-draft-btn" @click="handleSaveDraft">
        保存草稿
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { getBookDetail } from '@/apis/book'
import { ElMessage } from 'element-plus'
import type { UploadProps, UploadRequestOptions } from 'element-plus'
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue'
import { 
  Back, 
  Plus, 
} from '@element-plus/icons-vue'

// 导入API
import { createBook, saveBookDraft, updateBook } from '@/apis/book'
// 添加文件上传API导入
import { uploadCover } from '@/apis/file'

import defaultCoverImg from '@/assets/default.jpg'

const router = useRouter()
const route = useRoute()

// 页面标题计算属性
const pageTitle = computed(() => {
  return isEditMode.value ? '编辑书籍' : '创建书籍'
})

// 判断是否为编辑模式
const isEditMode = computed(() => route.query.edit === 'true')

// 获取书籍详情用于编辑
const fetchBookForEdit = async () => {
  if (!isEditMode.value) return
  
  try {
    let bookId = route.params.id || route.query.id
    if (!bookId) {
      ElMessage.error('未获取到书籍ID')
      return
    }
    
    const response = await getBookDetail(parseInt(bookId.toString())) as any
    
    if (response.code === 200 && response.data) {
      const bookData = response.data
      
      // 根据分类名称找到对应的categoryId
      let categoryId = undefined
      if (bookData.category) {
        categoryId = findCategoryIdByName(bookData.category)
        console.log('分类映射:', bookData.category, '->', categoryId)
      }
      
      // 处理上架时间 - 将时间戳转换为字符串格式
      let shelfTime = ''
      if (bookData.shelfTime) {
        const date = new Date(Number(bookData.shelfTime))
        if (!isNaN(date.getTime())) {
          const year = date.getFullYear()
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const day = String(date.getDate()).padStart(2, '0')
          const hours = String(date.getHours()).padStart(2, '0')
          const minutes = String(date.getMinutes()).padStart(2, '0')
          const seconds = String(date.getSeconds()).padStart(2, '0')
          shelfTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
        }
      }
      
      // 获取实际状态
      const actualStatus = Number(bookData.bookStatus) || 0
      
      // 如果是草稿状态（0），在编辑页面中改为未发布状态（1）进行展示
      const displayStatus = actualStatus === 0 ? 1 : actualStatus
      
      // 填充表单数据
      Object.assign(bookForm, {
        bookId: Number(bookData.bookId) || 0,
        bookName: bookData.bookName || '',
        coverUrl: bookData.coverUrl || '',
        author: bookData.author || '',
        translator: bookData.translator || '',
        categoryId: categoryId,
        // 使用API返回的分类字符串
        category: bookData.category || '',
        bookStatus: displayStatus, // 使用显示状态（草稿状态改为未发布）
        originalBookStatus: actualStatus, // 保存原始状态用于判断
        totalCount: bookData.totalCount !== undefined ? Number(bookData.totalCount) : undefined,
        // 使用转换后的上架时间
        shelfTime: shelfTime,
        intro: bookData.intro || '',
        publisher: bookData.publisher || '',
        isbn: bookData.isbn || '',
        copyrightHolder: bookData.copyrightHolder || '',
        publishCount: bookData.publishCount !== undefined && bookData.publishCount !== null 
          ? Number(bookData.publishCount) 
          : undefined,
        publishUnit: bookData.publishUnit || '',
        publishWebsite: bookData.publishWebsite || '',
        publishBatch: bookData.publishBatch || '',
        publishDate: bookData.publishDate || '',
        price: bookData.price !== undefined ? Number(bookData.price) : undefined,
        availableCount: bookData.availableCount || 0
      })
      
      console.log(`编辑书籍：原始状态=${actualStatus}，显示状态=${displayStatus}`)
      
    } else {
      ElMessage.error(response.message || '获取书籍详情失败')
    }
  } catch (error: any) {
    console.error('获取书籍详情失败:', error)
    ElMessage.error('获取书籍详情失败，请重试')
  }
}

// 根据分类名称查找对应的categoryId
const findCategoryIdByName = (categoryName: string): number | undefined => {
  if (!categoryName) return undefined
  
  // 清理分类名称（去掉"A、"这样的前缀）
  const cleanedName = cleanCategoryName(categoryName)
  
  // 在categoryOptions中查找
  const foundCategory = categoryOptions.find(option => {
    const optionCleanedName = cleanCategoryName(option.label)
    return optionCleanedName.includes(cleanedName) || 
           cleanedName.includes(optionCleanedName) ||
           option.label.includes(categoryName)
  })
  
  return foundCategory ? foundCategory.value : undefined
}

// 分类映射
const categoryMap: { [key: number]: string } = {
  0: 'A、马克思主义、列宁主义、毛泽东思想、邓小平理论',
  1: 'B、哲学、宗教',
  2: 'C、社会科学总论',
  3: 'D、政治、法律',
  4: 'E、军事',
  5: 'F、经济',
  6: 'G、文化、科学、教育、体育',
  7: 'H、语言、文字',
  8: 'I、文学',
  9: 'J、艺术',
  10: 'K、历史、地理',
  11: 'N、自然科学总论',
  12: 'O、数理科学和化学',
  13: 'P、天文学、地球科学',
  14: 'Q、生物科学',
  15: 'R、医药、卫生',
  16: 'S、农业科学',
  17: 'T、工业技术',
  18: 'U、交通运输',
  19: 'V、航空、航天',
  20: 'X、环境科学、安全科学',
  21: 'Z、综合性图书'
}

// 书籍表单数据
const bookForm = reactive({
  bookId: undefined as number | undefined, 
  bookName: '',
  coverUrl: '',
  author: '',
  translator: '',
  categoryId: undefined as number | undefined,
  category: '', 
  bookStatus: 0, // 显示状态
  originalBookStatus: 0, // 原始状态（用于判断是否是草稿）
  totalCount: undefined as number | undefined,
  availableCount: 0,
  shelfTime: '',
  intro: '',
  publisher: '',
  isbn: '',
  copyrightHolder: '',
  publishCount: undefined as number | undefined,
  publishUnit: '',
  publishWebsite: '',
  publishBatch: '',
  publishDate: '',
  price: undefined as number | undefined,
  coverServerUrl: '',
  coverUrlForDisplay: ''
})

// 状态选项
const statusOptions = [
  { label: '未发布', value: 1 },
  { label: '待上架', value: 2 },
  { label: '可借阅', value: 3 },
  { label: '已借光', value: 4 }
]

// 分类选项
const categoryOptions = [
  { label: 'A、马克思主义、列宁主义、毛泽东思想、邓小平理论', value: 0 },
  { label: 'B、哲学、宗教', value: 1 },
  { label: 'C、社会科学总论', value: 2 },
  { label: 'D、政治、法律', value: 3 },
  { label: 'E、军事', value: 4 },
  { label: 'F、经济', value: 5 },
  { label: 'G、文化、科学、教育、体育', value: 6 },
  { label: 'H、语言、文字', value: 7 },
  { label: 'I、文学', value: 8 },
  { label: 'J、艺术', value: 9 },
  { label: 'K、历史、地理', value: 10 },
  { label: 'N、自然科学总论', value: 11 },
  { label: 'O、数理科学和化学', value: 12 },
  { label: 'P、天文学、地球科学', value: 13 },
  { label: 'Q、生物科学', value: 14 },
  { label: 'R、医药、卫生', value: 15 },
  { label: 'S、农业科学', value: 16 },
  { label: 'T、工业技术', value: 17 },
  { label: 'U、交通运输', value: 18 },
  { label: 'V、航空、航天', value: 19 },
  { label: 'X、环境科学、安全科学', value: 20 },
  { label: 'Z、综合性图书', value: 21 }
]

// 监听器 
watch(() => bookForm.categoryId, (newVal: number | undefined) => {
  if (newVal !== undefined && newVal !== null) {
    const rawName = categoryMap[newVal] || ''
    bookForm.category = cleanCategoryName(rawName)
  } else {
    bookForm.category = ''
  }
})

// 清理分类名称的函数
const cleanCategoryName = (categoryName: string): string => {
  if (!categoryName) return ''
  // 去掉"A、"这样的前缀
  return categoryName.replace(/^[A-Z]、/, '')
}

// 必填字段验证
const validateRequiredFields = () => {
  const requiredFields = [
    { field: bookForm.bookName, message: '请输入书籍名称' },
    { field: bookForm.author, message: '请输入作者' },
    { field: bookForm.categoryId, message: '请选择书籍分类' },
    { field: bookForm.totalCount, message: '请输入书籍总量' },
    { field: bookForm.intro, message: '请输入简介' }
  ]

  for (const { field, message } of requiredFields) {
    if (!field && field !== 0) {
      ElMessage.error(message)
      return false
    }
  }

  // 验证书籍名称长度
  if (bookForm.bookName.length < 1 || bookForm.bookName.length > 50) {
    ElMessage.error('书籍名称长度应为1-50字符')
    return false
  }

  // 验证作者长度
  if (bookForm.author.length < 1 || bookForm.author.length > 30) {
    ElMessage.error('作者长度应为1-30字符')
    return false
  }

  // 验证书籍总数范围
  if (bookForm.totalCount && (bookForm.totalCount < 1 || bookForm.totalCount > 999)) {
    ElMessage.error('书籍总量应为1-999本')
    return false
  }

  // 验证简介长度
  if (bookForm.intro.length < 1 || bookForm.intro.length > 300) {
    ElMessage.error('简介长度应为1-300字')
    return false
  }

  // 验证译者长度
  if (bookForm.translator && bookForm.translator.length > 30) {
    ElMessage.error('译者长度不应超过30字符')
    return false
  }

  // 验证发行数量范围
  if (bookForm.publishCount && (bookForm.publishCount < 1 || bookForm.publishCount > 999)) {
    ElMessage.error('发行数量应为1-999')
    return false
  }

  // 验证价格（如果填写）
  if (bookForm.price !== undefined && bookForm.price !== null) {
    if (bookForm.price < 0) {
      ElMessage.error('价格不能为负数')
      return false
    }
    
    // 转换为字符串并检查小数位数
    const priceStr = bookForm.price.toString()
    const decimalPart = priceStr.split('.')[1]
    
    if (decimalPart && decimalPart.length > 2) {
      ElMessage.error('价格最多保留两位小数')
      return false
    }
  }

  return true
}

// 处理完成
const handleFinish = async () => {
  if (!validateRequiredFields()) {
    return
  }

  try {
    // 设置必填字段
    bookForm.availableCount = bookForm.totalCount || 0

    // 处理完成时，区分创建和编辑模式
    if (isEditMode.value) {
      // 编辑模式
    } else {
      // 创建模式：完成时状态固定为未发布（1）
      bookForm.bookStatus = 1
    }

    // 获取清理后的分类名称
    const rawCategoryName = categoryMap[bookForm.categoryId as number] || ''
    const cleanedCategoryName = cleanCategoryName(rawCategoryName)
    

    // 准备提交数据
    const submitData: any = {
      bookName: bookForm.bookName.trim(),
      coverUrl: bookForm.coverUrl || null, 
      author: bookForm.author.trim(),
      translator: bookForm.translator || null,
      categoryId: bookForm.categoryId, 
      category: cleanedCategoryName, 
      totalCount: bookForm.totalCount,
      shelfTime: bookForm.shelfTime,
      intro: bookForm.intro.trim(),
      publisher: bookForm.publisher || '',
      // ISBN处理：如果是空字符串，传null而不是空字符串
      isbn: bookForm.isbn && bookForm.isbn.trim() ? bookForm.isbn.trim() : null,
      copyrightHolder: bookForm.copyrightHolder || '',
      publishCount: bookForm.publishCount,
      publishUnit: bookForm.publishUnit || '',
      publishWebsite: bookForm.publishWebsite || '',
      publishBatch: bookForm.publishBatch || '',
      publishDate: bookForm.publishDate || '',
      bookStatus: bookForm.bookStatus 
    }

    // 可选的价格字段
    if (bookForm.price !== undefined && bookForm.price !== null) {
      submitData.price = Number(bookForm.price.toFixed(2))
    }

    let responseData
    if (isEditMode.value && bookForm.bookId) {
      // 编辑模式：更新书籍
      submitData.bookId = bookForm.bookId
      responseData = await updateBook(bookForm.bookId, submitData)
    } else {
      // 创建模式：创建书籍
      responseData = await createBook(submitData)
    }

    if (responseData.code === 200) {
      ElMessage.success(responseData.message || (isEditMode.value ? '书籍更新成功' : '书籍创建成功'))
      router.back()
    } else {
      ElMessage.error(responseData.message || '操作失败')
    }

  } catch (error: any) {

    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else if (error.message?.includes('ISBN')) {
      ElMessage.error('ISBN重复，请使用其他ISBN或留空')
    } else {
      ElMessage.error('操作失败，请重试')
    }
  }
}

// 处理保存草稿
const handleSaveDraft = async () => {
  try {
    // 保存草稿时，状态设为草稿（0）
    const draftStatus = 0

    // 准备草稿数据，不校验必填项
    const draftData: any = {
      bookName: bookForm.bookName || '',
      coverUrl: bookForm.coverUrl || '',
      author: bookForm.author || '',
      translator: bookForm.translator || '',
      category: bookForm.category || '',
      bookStatus: draftStatus,
      totalCount: bookForm.totalCount,
      shelfTime: bookForm.shelfTime || '',
      intro: bookForm.intro || '',
      publisher: bookForm.publisher || '',
      // ISBN处理：如果是空字符串，传null而不是空字符串
      isbn: bookForm.isbn && bookForm.isbn.trim() ? bookForm.isbn.trim() : null,
      copyrightHolder: bookForm.copyrightHolder || '',
      publishCount: bookForm.publishCount,
      publishUnit: bookForm.publishUnit || '',
      publishWebsite: bookForm.publishWebsite || '',
      publishBatch: bookForm.publishBatch || '',
      publishDate: bookForm.publishDate || ''
    }

    // 可选的价格字段
    if (bookForm.price !== undefined && bookForm.price !== null) {
      draftData.price = Number(bookForm.price.toFixed(2))
    }

    let responseData
    if (isEditMode.value && bookForm.bookId) {
      // 编辑模式：更新为草稿
      draftData.bookId = bookForm.bookId
      responseData = await updateBook(bookForm.bookId, draftData)
    } else {
      // 创建模式：保存草稿
      responseData = await saveBookDraft(draftData)
    }

    if (responseData.code === 200) {
      ElMessage.success(responseData.message || '草稿保存成功')
      // 如果是新增草稿保存成功，可以获取返回的bookId
      if (!isEditMode.value && responseData.data?.bookId) {
        bookForm.bookId = responseData.data.bookId
      }
      router.back()
    } else {
      ElMessage.error(responseData.message || '保存草稿失败')
    }
  } catch (error: any) {
    console.error('保存草稿失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('保存草稿失败，请重试')
    }
  }
}

// 检查表单是否有数据
const hasFormData = () => {
  return (
    bookForm.bookName ||
    bookForm.author ||
    bookForm.translator ||
    bookForm.categoryId !== undefined ||
    bookForm.totalCount !== undefined ||
    bookForm.shelfTime ||
    bookForm.intro ||
    bookForm.publisher ||
    bookForm.isbn ||
    bookForm.copyrightHolder ||
    bookForm.publishCount !== undefined ||
    bookForm.publishUnit ||
    bookForm.publishWebsite ||
    bookForm.publishBatch ||
    bookForm.publishDate ||
    bookForm.coverUrl
  )
}

// 处理返回操作
const handleBack = async () => {
  // 检查表单是否有数据
  if (hasFormData()) {
    await showConfirmDialog({
      title: '提示',
      message: '是否将当前内容保存为草稿？',
      confirmText: '确定',
      cancelText: '取消',
      onConfirm: async () => {
        // 用户点击确定，保存草稿
        await handleSaveDraft()
      },
      onCancel: () => {
        // 用户点击取消，返回上一页
        router.back()
      }
    })
  } else {
    // 没有数据，直接返回上一页
    router.back()
  }
}

// 封面上传前的验证
const beforeCoverUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/svg+xml', 'image/webp']
  const isImage = allowedTypes.includes(rawFile.type)
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('封面图片只能是 jpg/png/svg/webp 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('封面图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理封面上传
const handleCoverUpload = async (options: UploadRequestOptions) => {
  const { file } = options
  
  try {
    // 直接上传到服务器
    const serverUrl = await uploadCover(file)
    
    if (serverUrl) {
      // 使用服务器返回的完整URL
      bookForm.coverUrl = serverUrl
      bookForm.coverServerUrl = serverUrl
      
      ElMessage.success('封面上传成功')
      console.log('服务器返回的预览URL:', serverUrl)
    } else {
      ElMessage.warning('封面上传完成，但未获取到服务器URL')
      // 可以设置一个默认封面或保持为空
      bookForm.coverUrl = defaultCoverImg
    }
  } catch (error: any) {
    console.error('封面上传失败:', error)
    ElMessage.error('封面上传失败: ' + (error.message || '未知错误'))
    // 可以设置一个默认封面
    bookForm.coverUrl = defaultCoverImg
  }
}

// 组件挂载时
onMounted(() => {
  if (isEditMode.value) {
    // 编辑模式：从服务器获取当前状态
    fetchBookForEdit()
  } else {
    // 创建模式：默认状态为1（未发布）
    bookForm.bookStatus = 1
  }
})

</script>

<style scoped>
.book-create-page {
  background-color: #fff;
  min-height: 100vh;
  border-radius: 10px;
  overflow: hidden;
}

.page-header {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 23px 20px 0 30px;
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

.main-content {
  background: white;
  padding: 25px 30px 20px 30px;
  /* max-width: 900px;  */
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

.basic-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-bottom: 20px;
}

.left-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: 25px; 
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr; /* 两列布局 */
  gap: 20px; /* 列间距 */
  align-items: center;
}

.author-item {
  margin-bottom: 82px; 
}

/* 版权信息 */
.copyright-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

/* 表单项样式 */
.form-item {
  display: flex;
  align-items: center; 
  gap: 12px;
}

.form-item.full-width {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  min-width: 80px; 
  text-align: right;
  white-space: nowrap;
}

.form-item:has(.cover-upload) .form-label {
  margin-top: 3px;
  align-self: flex-start; 
}

.form-label.required::before {
  content: "*";
  color: #F56C6C;
  margin-right: 4px;
}

:deep(.form-item .el-input),
:deep(.form-item .el-select),
:deep(.form-item .el-date-editor) {
  width: 200px !important; 
}

:deep(.form-item.full-width .el-textarea) {
  width: 100% !important;
  max-width: calc(100% - 100px);
}

.cover-upload {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  gap: 16px;
}

.cover-upload-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.avatar-uploader {
  width: 120px;
  height: 170px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.2s;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #8c939d;
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
}

/* 书籍总数输入框 */
.total-count-input {
  position: relative;
  width: 200px;
}

.unit {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  font-size: 14px;
  z-index: 1;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 20px 0;
  background-color: #fff;
  max-width: 900px;
  margin: 0 auto;
  padding-left: 20px;
  padding-right: 20px;
}

.finish-btn,
.save-draft-btn {
  width: 90px;
  height: 40px;
  font-size: 14px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .basic-info-grid,
  .copyright-info-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .form-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .form-label {
    min-width: auto;
    text-align: left;
  }
  
  :deep(.form-item .el-input),
  :deep(.form-item .el-select),
  :deep(.form-item .el-date-editor) {
    width: 100% !important;
  }
  
  .cover-upload {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .finish-btn,
  .save-draft-btn {
    width: 100%;
    max-width: 200px;
  }
}
</style>