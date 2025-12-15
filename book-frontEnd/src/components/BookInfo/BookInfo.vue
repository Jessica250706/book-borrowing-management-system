<template>
  <!-- 书籍信息组件：包含图片、书名、作者、译者 -->
  <div class="book-info-container">
    <!-- 书籍图片 -->
    <div class="book-image-wrapper">
      <img 
        v-if="hasValidCover" 
        class="book-img" 
        :src="book.bookImg" 
        :alt="book.bookName"
        @error="handleImgError"
      >
      <!-- 无封面时显示占位符 -->
      <div v-else class="cover-placeholder">
        <el-icon class="cover-icon"><Picture /></el-icon>
        <div class="cover-text">暂无封面</div>
      </div>
    </div>
    <!-- 书籍文本信息（书名+作者+译者） -->
    <div class="book-text">
      <div class="book-name-wrapper">
        <div class="book-name">{{ book.bookName }}</div>
        <!-- 草稿状态图标（保持不变） -->
        <el-tooltip
          v-if="showDraftIcon"
          content="当前书籍尚未完成编辑，请继续填写信息"
          placement="top"
        >
          <span class="draft-indicator">ⓘ</span>
        </el-tooltip>
      </div>
      <div class="book-author">作者: {{ book.author || '佚名' }}</div>
      <!-- 只在有译者时显示译者行 -->
      <div v-if="hasTranslator" class="book-translator">译者: {{ book.translator }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { Picture } from '@element-plus/icons-vue'

// 接收父组件传入的书籍数据
const props = defineProps<{
  book: {
    bookImg: string; // 书籍图片URL
    bookName: string; // 书名
    author: string; // 作者
    translator: string; // 译者
  };
  showDraftIcon?: boolean; // 是否显示草稿图标
}>();

// 图片加载状态
const imageError = ref(false);

// 检查是否有有效的封面
const hasValidCover = computed(() => {
  const bookImg = props.book?.bookImg;
  return bookImg && bookImg.trim() !== '' && !imageError.value;
});

// 检查是否有译者
const hasTranslator = computed(() => {
  const translator = props.book?.translator;
  return translator && translator.trim() !== '';
});

// 是否显示草稿图标
const showDraftIcon = computed(() => props.showDraftIcon || false);

// 图片加载失败处理
const handleImgError = (e: Event) => {
  imageError.value = true;
  const img = e.target as HTMLImageElement;
  img.style.display = 'none'; // 隐藏图片
  
  // 在图片位置显示占位符
  const parent = img.parentElement;
  if (parent) {
    const placeholder = document.createElement('div');
    placeholder.className = 'cover-placeholder';
    placeholder.innerHTML = `
      <svg class="cover-icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
        <path d="M959.877 128l0.123 0.123v767.775l-0.123 0.122H64.102l-0.122-0.122V128.123l0.122-0.123h895.775zM960 64H64C28.795 64 0 92.795 0 128v768c0 35.205 28.795 64 64 64h896c35.205 0 64-28.795 64-64V128c0-35.205-28.795-64-64-64zM832 288.01c0 53.023-42.988 96.01-96.01 96.01S639.98 341.033 639.98 288.01 682.968 192 735.99 192 832 234.988 832 288.01zM896 832H128V704l224-384 256 320h64l224-192z"/>
      </svg>
      <div class="cover-text">暂无封面</div>
    `;
    
    parent.appendChild(placeholder);
  }
};
</script>

<style scoped>
.book-info-container {
  display: flex;
  align-items: flex-start; 
  gap: 12px; /* 图片与文本间距 */
  padding: 4px 0;
}

.book-image-wrapper {
  position: relative;
  width: 48px;
  height: 64px;
  flex-shrink: 0;
  margin-top: 2px;
}

.book-img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保持图片比例，避免拉伸 */
  border-radius: 2px;
}

/* 封面占位符样式 */
.cover-placeholder {
  width: 100%;
  height: 100%;
  border: 1px dashed #d9d9d9;
  border-radius: 2px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
}

.cover-icon {
  font-size: 20px;
  margin-bottom: 4px;
  color: #c0c4cc;
}

.cover-text {
  font-size: 10px;
  color: #8c939d;
  white-space: nowrap;
}

.book-text {
  display: flex;
  text-align: left;
  flex-direction: column;
  gap: 3px; /* 文本行之间间距 */
}

.book-name-wrapper {
  display: flex;
  align-items: center; 
  gap: 4px;
  min-height: 20px; 
  margin-bottom: 8px;
}

.book-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap; 
  overflow: hidden;
  text-overflow: ellipsis; /* 超出部分显示省略号 */
  max-width: 180px; /* 稍微减小宽度以容纳图标 */
  line-height: 1.2; /* 设置行高 */
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
  flex-shrink: 0;
  line-height: 1; 
}

.book-author {
  font-size: 12px;
  color: #666;
  line-height: 1.2;
}

.book-translator {
  font-size: 12px;
  color: #666;
  line-height: 1.2;
}
</style>