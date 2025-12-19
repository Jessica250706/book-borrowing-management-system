<template>
  <div class="user-menu">
    <div class="user-info" @click="toggleDropdown">
      <el-avatar :size="32" class="user-avatar" :src="userAvatar">
        <!-- 如果有头像就显示头像，没有头像就显示用户名首字母 -->
        <span v-if="!userAvatar && userName" class="avatar-text">
          {{ getAvatarText(userName) }}
        </span>
        <!-- 如果连首字母都没有，就显示默认图标 -->
        <el-icon v-else-if="!userAvatar && !userName">
          <User />
        </el-icon>
      </el-avatar>
      <span class="user-name">{{ userName }}</span>
      <el-icon :class="['arrow-icon', { rotate: showDropdown }]">
        <ArrowDown />
      </el-icon>
    </div>

    <transition name="el-zoom-in-top">
      <div v-show="showDropdown" class="dropdown-menu">
        <div class="dropdown-item user-profile">
          <div class="profile-info">
            <div class="profile-name">{{ userName }}</div>
            <div class="profile-role">{{ roleName }}</div>
          </div>
        </div>
        <el-divider />
        
        <div v-if="showPersonalCenter" class="dropdown-item" @click="handlePersonalCenter">
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
import { useUserStore } from "@/store";
import { getCurrentUserApi } from "@/apis/login";
import { setLoggingOutStatus } from '@/apis/request';
// 导入自定义对话框组件
import { showConfirmDialog } from "@/components/Dialog/customDialog/CustomDialog.vue";

const router = useRouter();
const userStore = useUserStore();

// 计算属性获取用户信息
const userName = computed(() => {
  return userStore.userInfo.username || "用户";
});

const userAvatar = computed(() => {
  const avatar = userStore.userInfo.avatar;
  // 如果头像链接有效（非空字符串），则返回头像链接
  // 否则返回 null 以显示首字母或默认图标
  return avatar && avatar.trim() !== '' ? avatar : null;
});

const roleName = computed(() => {
  return userStore.userInfo.roleName || "普通用户";
});

// 判断是否需要显示个人中心
const showPersonalCenter = computed(() => {
  // 如果是管理员或系统管理员，不显示个人中心
  const roleCode = userStore.userInfo.roleCode;
  return !(roleCode === 'ADMIN' || roleCode === 'SYS_ADMIN');
});

// 获取头像文字（首字母）
const getAvatarText = (username: string): string => {
  if (!username) return '?';
  // 获取第一个字符的大写
  return username.charAt(0).toUpperCase();
};

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

const handleLogout = async () => {
  showDropdown.value = false;

  try {
    await showConfirmDialog({
      title: "退出登录",
      message: "确定要退出登录吗？",
      confirmText: "确定",
      cancelText: "取消",
      // 改为异步确认函数
      onConfirm: async () => {
        await performFrontendLogout();
      },
      onCancel: () => {
        console.log("取消退出登录");
      },
    });
  } catch (error) {
    if (error !== "cancel") {
      console.error("退出登录异常:", error);
    }
  }
};

// 异步退出函数
const performFrontendLogout = async () => {
  // 1. 设置正在退出状态，阻止新请求
  setLoggingOutStatus(true);
  
  // 2. 清理用户信息
  userStore.clearUser();
  
  // 3. 等待微任务完成
  await Promise.resolve();
  
  // 4. 清理存储
  const keys = ['token', 'refreshToken', 'rememberMe', 'savedAccount', 'userInfo'];
  keys.forEach(key => localStorage.removeItem(key));
  
  // 5. 清理sessionStorage（如果有）
  sessionStorage.clear();
  
  // 6. 确保页面跳转
  setTimeout(() => {
    // 使用完整的URL确保跳转
    const loginUrl = window.location.origin + '/login';
    window.location.href = loginUrl;
    
    // 重置退出状态
    setTimeout(() => {
      setLoggingOutStatus(false);
    }, 2000);
  }, 100);
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

      .avatar-text {
        color: white;
        font-size: 16px;
        font-weight: bold;
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
    min-width: 130px;
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

      &.user-profile {
        flex-direction: column;
        align-items: center;
        padding: 8px;
        cursor: default;
        text-align: center;

        &:hover {
          background-color: transparent;
        }

        .profile-info {
          text-align: center;
          .profile-name {
            font-size: 14px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 5px;
          }

          .profile-role {
            font-size: 14px;
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
      margin: 4px 0;
    }
  }
}
</style>