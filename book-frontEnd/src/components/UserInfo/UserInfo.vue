<template>
  <div class="user-info-container">
    <!-- 圆形用户头像 -->
    <div class="avatar-container">
      <img 
        class="user-avatar" 
        :src="user.avatarUrl" 
        :alt="user.name"
        @error="handleImgError"
      >
    </div>
    
    <!-- 用户信息文本 -->
    <div class="user-text">
      <div class="user-name">{{ user.name }}</div>
      <div class="user-id">ID：{{ user.id }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';

// 定义接收的用户数据结构（包含头像、用户名、ID）
const props = defineProps<{
  user: {
    avatarUrl: string; // 头像图片地址
    name: string; // 用户名（即姓名）
    id: string | number; // 用户ID
  };
}>();

// 头像加载失败时显示默认图
const handleImgError = (e: Event) => {
  const img = e.target as HTMLImageElement;
  img.src = 'https://picsum.photos/200/200?random=1'; // 默认头像地址
};
</script>

<style scoped>
.user-info-container {
  display: flex;
  align-items: center; /* 头像与文本垂直居中对齐 */
  gap: 12px;
  padding: 8px 0;
}

/* 圆形头像容器 */
.avatar-container {
  width: 40px;
  height: 40px;
  border-radius: 50%; /* 圆形 */
  overflow: hidden; /* 裁剪超出圆形的部分 */
  background-color: #f0f0f0; /* 加载前的背景色 */
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保持图片比例，填满圆形 */
}

.user-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.user-id {
  font-size: 12px;
  color: #999;
}
</style>