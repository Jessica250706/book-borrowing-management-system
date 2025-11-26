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
            <div class="form-item">
              <label class="form-label required">作者：</label>
              <el-input
                v-model="bookForm.author"
                placeholder="请输入作者"
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
                disabled
              >
                <el-option
                  v-for="status in statusOptions"
                  :key="status.value"
                  :label="status.label"
                  :value="status.value"
                />
              </el-select>
            </div>
            
            <!-- 上架时间 -->
            <div class="form-item">
              <label class="form-label required">上架时间：</label>
              <el-date-picker
                v-model="bookForm.shelfTime"
                type="datetime"
                placeholder="选择上架时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
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
            
            <!-- 译者 -->
            <div class="form-item">
              <label class="form-label">译者：</label>
              <el-input
                v-model="bookForm.translator"
                placeholder="请输入译者"
                maxlength="30"
                show-word-limit
                clearable
              />
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
            
            <!-- 书籍总数 -->
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
      
      <!-- 预览板块 -->
      <div class="section">
        <div class="section-header">
          <div class="blue-line"></div>
          <span class="section-title">预览</span>
        </div>
        
        <div class="preview-section">
          <el-upload
            v-if="!previewFile"
            class="preview-upload"
            drag
            action="#"
            :show-file-list="false"
            :before-upload="beforePreviewUpload"
            :http-request="handlePreviewUpload"
            accept=".jpg,.png,.pdf"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em class="upload-link">点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                只能上传jpg/png/pdf文件，且大小不超过2GB
              </div>
            </template>
          </el-upload>
          
          <!-- 预览文件显示 -->
          <div v-if="previewFile" class="preview-content">
            <!-- 文件信息 -->
            <div class="preview-file">
              <div class="file-info">
                <el-icon class="file-icon"><Document /></el-icon>
                <span class="file-name">{{ previewFile.name }}</span>
                <span class="file-size">({{ formatFileSize(previewFile.size) }})</span>
                <el-icon class="delete-icon" @click="removePreviewFile">
                  <Close />
                </el-icon>
              </div>
            </div>
            
            <!-- 文件预览 -->
            <div class="file-preview">
              <!-- 图片预览 -->
              <div v-if="previewUrl && previewFile.type.startsWith('image/')" class="image-preview">
                <img :src="previewUrl" :alt="previewFile.name" class="preview-image" />
                <div class="preview-overlay">
                  <span class="preview-text">图片预览</span>
                </div>
              </div>
              
              <!-- PDF预览 -->
              <div v-else-if="previewUrl && previewFile.type === 'application/pdf'" class="pdf-preview">
                <embed 
                  :src="previewUrl" 
                  type="application/pdf" 
                  class="pdf-embed"
                  width="100%" 
                  height="500"
                />
                <div class="pdf-overlay">
                  <span class="preview-text">PDF预览 ({{ formatFileSize(previewFile.size) }})</span>
                </div>
              </div>
              
              <!-- 未知文件类型 -->
              <div v-else class="unknown-preview">
                <el-icon class="unknown-icon"><Document /></el-icon>
                <div class="unknown-info">
                  <div class="unknown-name">{{ previewFile.name }}</div>
                  <div class="unknown-type">不支持在线预览</div>
                  <div class="unknown-size">{{ formatFileSize(previewFile.size) }}</div>
                </div>
              </div>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { getBookDetail, updateBook } from '@/apis/book'
import { ElMessage } from 'element-plus'
import type { UploadProps, UploadRequestOptions } from 'element-plus'
import { 
  Back, 
  Plus, 
  UploadFilled, 
  Document, 
  Close 
} from '@element-plus/icons-vue'

// 导入API
import { createBook } from '@/apis/book'

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
    const bookId = route.query.id as string
    if (!bookId) return
    
    const response = await getBookDetail(parseInt(bookId))
    if (response.data?.code === 200 && response.data.data) {
      // 将获取到的数据填充到表单中
      const bookData = response.data.data
      Object.keys(bookForm).forEach(key => {
        if (key in bookData) {
          (bookForm as any)[key] = bookData[key as keyof typeof bookData]
        }
      })
    }
  } catch (error) {
    console.error('获取书籍详情失败:', error)
    ElMessage.error('获取书籍详情失败')
  }
}

// 书籍表单数据
const bookForm = reactive({
  bookId: undefined as number | undefined, 
  bookName: '',
  coverUrl: '',
  author: '',
  translator: '',
  categoryId: undefined as number | undefined,
  bookStatus: 0, // 0-未发布
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
  publishDate: ''
})

// 预览文件
const previewFile = ref<File | null>(null)
const previewUrl = ref<string>('') // 添加预览URL

// 状态选项
const statusOptions = [
  { label: '未发布', value: 0 },
  { label: '待上架', value: 1 },
  { label: '可借阅', value: 2 },
  { label: '已借光', value: 3 }
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

// 必填字段验证
const validateRequiredFields = () => {
  const requiredFields = [
    { field: bookForm.bookName, message: '请输入书籍名称' },
    { field: bookForm.author, message: '请输入作者' },
    { field: bookForm.categoryId, message: '所有分类' },
    { field: bookForm.totalCount, message: '请输入书籍总量' },
    { field: bookForm.shelfTime, message: '请选择上架时间' },
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

  return true
}

const handleFinish = async () => {
  if (!validateRequiredFields()) {
    return;
  }

  try {
    if (bookForm.isbn === '' || bookForm.isbn === '""') {
      bookForm.isbn = null as any;
    }
    
    bookForm.availableCount = bookForm.totalCount || 0;

    const submitData = {
      ...bookForm,
      shelfTime: bookForm.shelfTime,
      publishDate: bookForm.publishDate, 
    };

    let apiResponse;
    if (isEditMode.value && submitData.bookId) {
      apiResponse = await updateBook(submitData.bookId, submitData);
    } else {
      const createData = { ...submitData };
      delete createData.bookId;
      apiResponse = await createBook(createData);
    }
    
    ElMessage.success(isEditMode.value ? '书籍更新成功' : '书籍创建成功');
    router.back();

  } catch (error: any) {
    console.error('操作失败:', error);
    if (error.response?.data) {
      console.error('错误响应数据:', error.response.data);
      ElMessage.error(error.response.data.message || '操作失败');
    } 
    else if (error.message?.includes('ISBN')) {
      ElMessage.error('ISBN 重复，请使用其他 ISBN 或留空');
    } 
    else {
      ElMessage.error(error.message || '操作失败，请重试');
    }
  }
};

// 处理保存草稿
const handleSaveDraft = async () => {
  // 保存草稿不验证必填项，但需要设置状态为未发布
  bookForm.bookStatus = 0 // 未发布状态
  
  try {
    // 格式化日期为 ISO 格式，处理 null 值
    const submitData = {
      ...bookForm,
      shelfTime: bookForm.shelfTime ? new Date(bookForm.shelfTime).toISOString() : undefined,
      publishDate: bookForm.publishDate ? new Date(bookForm.publishDate + 'T00:00:00').toISOString() : undefined
    }
    
    const response = await createBook(submitData)
    
    if (response.data?.code === 200) {
      ElMessage.success('草稿保存成功')
      router.back()
    } else {
      ElMessage.error(response.data?.message || '保存草稿失败')
    }
  } catch (error: any) {
    console.error('保存草稿失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '保存草稿失败，请重试')
  }
}

// 处理返回
const handleBack = () => {
  router.back()
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
    // 这里模拟上传过程，实际项目中需要调用上传接口
    // const formData = new FormData()
    // formData.append('file', file)
    // const response = await uploadFile(formData)
    // bookForm.coverUrl = response.data.url
    
    // 模拟上传成功，生成预览URL
    const reader = new FileReader()
    reader.onload = (e) => {
      bookForm.coverUrl = e.target?.result as string
    }
    reader.readAsDataURL(file)
    
    ElMessage.success('封面上传成功')
  } catch (error) {
    ElMessage.error('封面上传失败')
  }
}

// 预览文件上传前的验证
const beforePreviewUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const allowedTypes = ['image/jpeg', 'image/png', 'application/pdf']
  const isAllowedType = allowedTypes.includes(rawFile.type) || 
                       rawFile.name.endsWith('.jpg') || 
                       rawFile.name.endsWith('.png') || 
                       rawFile.name.endsWith('.pdf')
  const isLt2GB = rawFile.size / 1024 / 1024 / 1024 < 2

  if (!isAllowedType) {
    ElMessage.error('预览文件只能是 jpg/png/pdf 格式!')
    return false
  }
  if (!isLt2GB) {
    ElMessage.error('预览文件大小不能超过 2GB!')
    return false
  }
  return true
}

// 处理预览文件上传
const handlePreviewUpload = async (options: UploadRequestOptions) => {
  const { file } = options
  
  try {
    // 这里模拟上传过程，实际项目中需要调用上传接口
    // const formData = new FormData()
    // formData.append('file', file)
    // await uploadPreviewFile(formData)
    
    previewFile.value = file
    
    // 生成预览URL
    if (file.type.startsWith('image/')) {
      // 图片文件直接生成预览
      const reader = new FileReader()
      reader.onload = (e) => {
        previewUrl.value = e.target?.result as string
      }
      reader.readAsDataURL(file)
    } else if (file.type === 'application/pdf') {
      // PDF文件生成预览URL
      const reader = new FileReader()
      reader.onload = (e) => {
        previewUrl.value = e.target?.result as string
      }
      reader.readAsDataURL(file)
    }
    
    ElMessage.success('预览文件上传成功')
  } catch (error) {
    ElMessage.error('预览文件上传失败')
  }
}

// 移除预览文件
const removePreviewFile = () => {
  previewFile.value = null
  previewUrl.value = ''
  ElMessage.info('已移除预览文件')
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 组件挂载时
onMounted(() => {
  bookForm.bookStatus = 0
  if (isEditMode.value) {
    fetchBookForEdit()
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
  padding: 25px 20px 20px 30px;
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

.basic-info-grid .left-column .form-item:nth-child(2) {
  margin-top: 139px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr; /* 两列布局 */
  gap: 20px; /* 列间距 */
  align-items: center;
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

/* 调整书籍封面的标签顶部对齐 */
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

/* 预览上传区域 */
.preview-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.preview-upload {
  width: 100%;
  max-width: 100%;
}

:deep(.preview-upload .el-upload-dragger) {
  width: 100%;
  height: 250px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

:deep(.preview-upload .el-icon--upload) {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

:deep(.preview-upload .el-upload__text) {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.upload-link {
  color: #409EFF;
}

:deep(.preview-upload .el-upload__tip) {
  text-align: left;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  margin-left: 5px;
}

/* 预览内容容器 */
.preview-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 文件信息 */
.preview-file {
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  padding: 12px;
  background-color: #fafafa;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-icon {
  font-size: 20px;
  color: #409EFF;
}

.file-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.file-size {
  font-size: 12px;
  color: #999;
}

.delete-icon {
  font-size: 16px;
  color: #999;
  cursor: pointer;
  transition: color 0.2s;
}

.delete-icon:hover {
  color: #F56C6C;
}

/* 文件预览区域 */
.file-preview {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fafafa;
  min-height: 300px;
  position: relative;
  overflow: hidden;
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
  margin-bottom: 4px;
}

.unknown-size {
  color: #999;
  font-size: 12px;
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