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
/* 适配你此前定义的 读者端 50x70px 封面尺寸，保持样式统一 */
.book-cover-placeholder {
  width: 50px;
  height: 70px;
  background-color: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 2px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.placeholder-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4px;
  text-align: center;
}

.book-icon {
  color: #c0c4cc;
  margin-bottom: 4px;
  font-size: 20px; /* 匹配你此前的 cover-icon 尺寸 */
}

.placeholder-text {
  color: #8c939d;
  font-size: 10px; /* 匹配你此前的 cover-text 尺寸 */
  white-space: nowrap;
}
</style>