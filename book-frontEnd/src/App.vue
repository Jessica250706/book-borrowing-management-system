<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { AuthManager } from '@/utils/auth'

// 使用 ReturnType 替代 NodeJS.Timeout
let tokenMonitor: ReturnType<typeof setInterval>

onMounted(() => {
  // 启动 token 监控，每5分钟检查一次
  tokenMonitor = AuthManager.startTokenMonitor()
})

onUnmounted(() => {
  // 清理监控
  if (tokenMonitor) {
    AuthManager.stopTokenMonitor(tokenMonitor)
  }
})
</script>