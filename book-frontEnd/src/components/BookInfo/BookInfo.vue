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
      <div class="book-name-wrapper">
        <div class="book-name">{{ book.bookName }}</div>
        <!-- 草稿状态图标 -->
        <el-tooltip
          v-if="showDraftIcon"
          content="当前书籍尚未完成编辑，请继续填写信息"
          placement="top"
        >
          <span class="draft-indicator">ⓘ</span>
        </el-tooltip>
      </div>
      <div class="book-author">作者: {{ book.author || '佚名' }}</div>
      <div class="book-translator">译者: {{ book.translator || '佚名' }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
  //import { defineProps } from 'vue';
import {  computed } from 'vue';

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

// 默认不显示草稿图标
const showDraftIcon = computed(() => props.showDraftIcon || false);

// 图片加载失败时显示默认图
const handleImgError = (e: Event) => {
  const img = e.target as HTMLImageElement;
  img.src = 'https://img1.baidu.com/it/u=3363823393,2631112139&fm=253&fmt=auto&app=120&f=JPEG?w=680&h=1024'; // 默认图片URL
};
</script>

<style scoped>
.book-info-container {
  display: flex;
  align-items: flex-start; 
  gap: 12px; /* 图片与文本间距 */
  padding: 4px 0;
}
.book-img {
  width: 48px;
  height: 64px;
  object-fit: cover; /* 保持图片比例，避免拉伸 */
  border-radius: 2px;
  margin-top: 2px; 
}
.book-text {
  display: flex;
  text-align: left;
  flex-direction: column;
  gap: 2px; /* 文本行之间间距 */
}
.book-name-wrapper {
  display: flex;
  align-items: center; 
  gap: 4px;
  min-height: 20px; 
}
.book-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap; /* 书名不换行 */
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
.book-author, .book-translator {
  font-size: 12px;
  color: #666;
  line-height: 1.2;
}
</style>