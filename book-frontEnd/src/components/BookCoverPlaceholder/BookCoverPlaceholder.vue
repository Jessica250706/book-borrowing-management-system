<template>
  <div class="book-cover-placeholder">
    <div class="placeholder-content">
      <!-- 替换为 Element Plus 存在的图标（如 Picture / BookMark / Library 等） -->
      <el-icon class="book-icon"><Picture /></el-icon>
      <span class="placeholder-text">暂无封面</span>
    </div>
  </div>
</template>

<script setup lang="ts">
// 导入 Element Plus 实际存在的图标（Picture 是通用且存在的）
import { Picture } from '@element-plus/icons-vue';
</script>

<style scoped>
/* 占位符的样式（与图片宽高完全一致） */
.book-cover-placeholder {
  width: 100px;    /* 和图片宽度一致 */
  height: 140px;   /* 和图片高度一致 */
  background-color: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 2px; /* 可选：和图片保持相同圆角 */
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 占位符内部文字/图标的布局（适配宽高） */
.placeholder-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  text-align: center;
}

.book-icon {
  font-size: 24px; /* 适配宽高调整图标大小 */
  margin-bottom: 8px;
  color: #c0c4cc;
}

.placeholder-text {
  font-size: 12px; /* 适配宽高调整文字大小 */
  color: #8c939d;
}
</style>