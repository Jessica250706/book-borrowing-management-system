<template>
  <div class="user-menu">
    <div class="user-info" @click="toggleDropdown">
      <el-avatar :size="32" class="user-avatar" :src="userAvatar">
        <el-icon><User /></el-icon>
      </el-avatar>
      <span class="user-name">{{ userName }}</span>
      <el-icon :class="['arrow-icon', { rotate: showDropdown }]">
        <ArrowDown />
      </el-icon>
    </div>

    <transition name="el-zoom-in-top">
      <div v-show="showDropdown" class="dropdown-menu">
        <div class="dropdown-item user-profile">
          <el-icon><User /></el-icon>
          <div class="profile-info">
            <div class="profile-name">{{ userName }}</div>
            <div class="profile-role">{{ roleName }}</div>
          </div>
        </div>
        <el-divider />
        <div class="dropdown-item" @click="handlePersonalCenter">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </div>
        <div class="dropdown-item logout-item" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";
import { ArrowDown, SwitchButton, User } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store";
import { getCurrentUserApi } from "@/apis/login";

// 导入自定义对话框组件
import { showConfirmDialog } from "@/components/Dialog/customDialog/CustomDialog.vue";

const router = useRouter();
const userStore = useUserStore();

// 计算属性获取用户信息
const userName = computed(() => {
  return userStore.userInfo.username || "用户";
});

const userAvatar = computed(() => {
  return userStore.userInfo.avatar;
});

const roleName = computed(() => {
  return userStore.userInfo.roleName || "普通用户";
});

// 下拉菜单状态
const showDropdown = ref(false);

// 切换下拉菜单显示/隐藏
const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

// 获取当前用户信息
const fetchCurrentUser = async () => {
  try {
    const response = await getCurrentUserApi();
    if (response.code === 200 && response.data) {
      // 更新 store 中的用户信息
      userStore.setUserInfo(response.data);
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
  }
};

// 处理个人中心
const handlePersonalCenter = () => {
  showDropdown.value = false;
  router.push("/manage/personalCenter");
};

// 处理退出登录
const handleLogout = async () => {
  showDropdown.value = false;

  try {
    await showConfirmDialog({
      title: "退出登录",
      message: "确定要退出登录吗？",
      confirmText: "确定",
      cancelText: "取消",
      onConfirm: async () => {
        try {
          // 1. 先调用后端退出接口（如果有的话）
          // try {
          //   await logoutApi();
          // } catch (error) {
          //   console.log('后端退出接口调用失败，继续前端清理:', error);
          // }
          
          // 2. 清除localStorage中的token
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
          
          // 3. 清除用户状态
          userStore.clearUser();
          
          // 4. 强制刷新页面，确保所有状态被清除，并中断所有API请求
          setTimeout(() => {
            window.location.href = '/login';
          }, 100);
          
        } catch (error) {
          console.error('退出登录过程中出错:', error);
          // 即使出错也要继续清理
          localStorage.removeItem('token');
          userStore.clearUser();
          window.location.href = '/login';
        }
      },
      onCancel: () => {
        console.log("取消退出登录");
      },
    });
  } catch (error) {
    // 用户取消操作或其他错误
    if (error === "cancel") {
      console.log("取消退出登录");
    } else {
      console.error("退出登录失败:", error);
      ElMessage.error("退出登录失败，请重试");
    }
  }
};

// 点击页面其他地方关闭下拉菜单
const closeDropdown = (event: Event) => {
  const userMenu = document.querySelector(".user-menu");
  if (userMenu && !userMenu.contains(event.target as Node)) {
    showDropdown.value = false;
  }
};

// 添加和移除事件监听
onMounted(() => {
  document.addEventListener("click", closeDropdown);
  // 组件挂载时获取一次用户信息
  if (userStore.isLoggedIn) {
    fetchCurrentUser();
  }
});

onUnmounted(() => {
  document.removeEventListener("click", closeDropdown);
});
</script>

<style scoped lang="scss">
.user-menu {
  position: relative;
  display: flex;
  align-items: center;
  margin-left: auto;
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
      background-color: #409eff;

      :deep(.el-icon) {
        color: #fff;
        font-size: 18px;
      }
    }

    .user-name {
      color: #000;
      font-size: 14px;
      margin-right: 8px;
      font-weight: 500;
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
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    padding: 5px 0;
    min-width: 140px;
    z-index: 1000;

    .dropdown-item {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 10px 16px;
      cursor: pointer;
      transition: background-color 0.3s;
      text-align: center;

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

      &.user-profile {
        padding: 12px 16px;
        cursor: default;
        text-align: center;
        justify-content: center;

        &:hover {
          background-color: transparent;
        }

        .profile-info {
          text-align: center;
          .profile-name {
            font-size: 14px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 2px;
          }

          .profile-role {
            font-size: 12px;
            color: #909399;
          }
        }
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

    :deep(.el-divider) {
      margin: 8px 0;
    }
  }
}
</style>
