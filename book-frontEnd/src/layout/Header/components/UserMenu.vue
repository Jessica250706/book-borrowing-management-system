<template>
  <div class="user-menu">
    <div class="user-info" @click="toggleDropdown">
      <el-avatar :size="32" class="user-avatar">
        <el-icon><User /></el-icon>
      </el-avatar>
      <span class="user-name">{{ userInfo.name }}</span>
      <el-icon :class="['arrow-icon', { 'rotate': showDropdown }]">
        <ArrowDown />
      </el-icon>
    </div>
    
    <transition name="el-zoom-in-top">
      <div v-show="showDropdown" class="dropdown-menu">
        <div class="dropdown-item logout-item" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted } from 'vue'
  import { ArrowDown, SwitchButton, User } from '@element-plus/icons-vue'
  import { useRouter } from 'vue-router'
  import { ElMessageBox, ElMessage } from 'element-plus'

  const router = useRouter()

  // 用户信息 - 实际项目中应从store或API获取
  const userInfo = ref({
    name: '用户名'
  })

  // 下拉菜单状态
  const showDropdown = ref(false)

  // 切换下拉菜单显示/隐藏
  const toggleDropdown = () => {
    showDropdown.value = !showDropdown.value
  }

  // 处理退出登录
  const handleLogout = async () => {
    showDropdown.value = false
    
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      // 执行退出登录逻辑
      // 1. 清除token/localStorage等
      localStorage.removeItem('token')
      
      // 2. 跳转到登录页
      router.push('/login')
      
      // 3. 显示成功消息
      ElMessage.success('退出登录成功')
      
    } catch (error) {
      // 用户取消操作
      console.log('取消退出登录')
    }
  }

  // 点击页面其他地方关闭下拉菜单
  const closeDropdown = (event: Event) => {
    const userMenu = document.querySelector('.user-menu')
    if (userMenu && !userMenu.contains(event.target as Node)) {
      showDropdown.value = false
    }
  }

  // 添加和移除事件监听
  onMounted(() => {
    document.addEventListener('click', closeDropdown)
  })

  onUnmounted(() => {
    document.removeEventListener('click', closeDropdown)
  })
  </script>

<style scoped lang="scss">
  .user-menu {
    position: relative;
    display: flex;
    align-items: center;
    margin-left: auto; // 确保靠右对齐
    margin-right: 20px;
    
    .user-info {
      display: flex;
      align-items: center;
      padding: 8px 12px;
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.3s;
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.1);
      }
      
      .user-avatar {
        margin-right: 8px;
        background-color: #cccccc;
        
        :deep(.el-icon) {
          color: #fff;
          font-size: 18px;
        }
      }
      
      .user-name {
        color: #000;
        font-size: 14px;
        margin-right: 8px;
      }
      
      .arrow-icon {
        color: #999999;
        transition: transform 0.3s;
        
        &.rotate {
          transform: rotate(180deg);
        }
      }
    }
    
    .dropdown-menu {
      position: absolute;
      top: 100%;
      right: 0;
      background: #fff;
      border-radius: 4px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      padding: 6px 0;
      min-width: 120px;
      z-index: 1000;
      
      .dropdown-item {
        display: flex;
        align-items: center;
        padding: 10px 16px;
        cursor: pointer;
        transition: background-color 0.3s;
        
        &:hover {
          background-color: #f5f7fa;
        }
        
        .el-icon {
          margin-right: 8px;
          font-size: 16px;
          color: #606266;
        }
        
        span {
          font-size: 14px;
          color: #606266;
        }
        
        &.logout-item {
          .el-icon {
            color: #f56c6c;
          }
          
          span {
            color: #f56c6c;
          }
        }
      }
    }
  }
</style>