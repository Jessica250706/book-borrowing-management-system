<template>
  <!-- 书籍信息组件：包含图片、书名、作者、译者 -->
  <div class="book-info-container">
    <!-- 书籍图片 -->
    <img 
      class="book-img" 
      :src="book.bookImg" 
      :alt="book.bookName"
      @error="handleImgError"
    >
    <!-- 书籍文本信息（书名+作者+译者） -->
    <div class="book-text">
      <div class="book-name">{{ book.bookName }}</div>
      <div class="book-author">作者: {{ book.author || '佚名' }}</div>
      <div class="book-translator">译者: {{ book.translator || '佚名' }}</div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { defineProps } from 'vue';
// 接收父组件传入的书籍数据（新增author、translator字段）
const props = defineProps<{
  book: {
    bookImg: string; // 书籍图片URL
    bookName: string; // 书名
    author: string; // 作者
    translator: string; // 译者
  };
}>();
// 图片加载失败时显示默认图
const handleImgError = (e: Event) => {
  const img = e.target as HTMLImageElement;
  img.src = 'https://img1.baidu.com/it/u=3363823393,2631112139&fm=253&fmt=auto&app=120&f=JPEG?w=680&h=1024'; // 默认图片URL
};
</script>
<style scoped>
.book-info-container {
  display: flex;
  align-items: flex-start; /* 图片与文本顶部对齐 */
  gap: 12px; /* 图片与文本间距 */
  padding: 4px 0;
}
.book-img {
  width: 48px;
  height: 64px;
  object-fit: cover; /* 保持图片比例，避免拉伸 */
  border-radius: 2px;
  margin-top: 2px; /* 微调垂直对齐 */
}
.book-text {
  display: flex;
  flex-direction: column;
  gap: 2px; /* 文本行之间间距 */
}
.book-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap; /* 书名不换行 */
  overflow: hidden;
  text-overflow: ellipsis; /* 超出部分显示省略号 */
  max-width: 200px;
}
.book-author, .book-translator {
  font-size: 12px;
  color: #666;
}
</style>