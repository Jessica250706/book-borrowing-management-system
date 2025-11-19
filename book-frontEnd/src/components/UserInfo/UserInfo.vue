<template>
  <div class="user-info-container">
    <!-- 圆形用户头像（放大尺寸） -->
    <div class="avatar-container">
      <img 
        class="user-avatar" 
        :src="user?.avatarUrl" 
        :alt="user?.realName || '用户头像'"
        @error="handleImgError"
        v-if="user?.avatarUrl"
      >
      <!-- 头像地址不存在时显示默认占位 -->
      <div class="default-avatar" v-else>
        <span>{{ (user?.realName || '未知')?.slice(0, 1) }}</span>
      </div>
    </div>
    
    <!-- 用户信息文本（靠左对齐，显示正确ID格式） -->
    <div class="user-text">
      <div class="user-name">{{ user?.realName || '未知用户' }}</div>
      <div class="user-id">ID：{{ user?.username || '未知ID' }}</div> <!-- 确保是“ID：”格式 -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue';
// 定义接收的用户数据结构（兼容可能的空值）
const props = defineProps({
  user: {
    type: Object,
    default: () => ({}) // 默认空对象，避免undefined
  }
});
// 头像加载失败时显示默认图
const handleImgError = (e: Event) => {
  const img = e.target as HTMLImageElement;
  img.src = 'https://picsum.photos/200/200?random=1'; // 默认头像地址
};
</script>

<style scoped>
.user-info-container {
  display: flex;
  align-items: center;
  gap: 12px; /* 头像和文本间距，适配放大后的头像 */
  justify-content: flex-start; /* 整体靠左对齐 */
}

/* 放大头像尺寸（从40px改为50px，可按需调整） */
.avatar-container {
  width: 50px; 
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  width: 100%;
  height: 100%;
  background-color: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px; /* 适配放大后的占位文字 */
  color: #666;
}

.user-text {
  display: flex;
  flex-direction: column;
  gap: 3px;
  align-items: flex-start; /* 文字靠左对齐，解决对齐问题 */
}

.user-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  line-height: 1.2;
}

.user-id {
  font-size: 13px;
  color: #909399;
  line-height: 1.2;
}
</style>