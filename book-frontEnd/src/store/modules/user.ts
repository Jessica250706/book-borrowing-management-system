// src\store\modules\user.ts
import { defineStore } from 'pinia';
import { ref, reactive, computed } from 'vue';
import type { UserInfoResponse } from '@/apis/login/type';

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref('');
  const userInfo = reactive({
    userId: -1,
    username: '',
    account: '',
    uid: '',
    roleCode: '',
    roleName: '',
    creditScore: 0,
    avatar: '',
    token: '',
    roleId: -1,
  });

  // Getters
  const isLoggedIn = computed(() => !!token.value);
  const userId = computed(() => userInfo.userId);
  const userName = computed(() => userInfo.username);
  const roleCode = computed(() => userInfo.roleCode);
  const userFullInfo = computed(() => userInfo);
  const isAdmin = computed(() => {
    const roleCode = userInfo.roleCode;
    // 管理员角色包括 ADMIN 和 SYS_ADMIN
    return roleCode === 'ADMIN' || roleCode === 'SYS_ADMIN';
  });

  const isSystemAdmin = computed(() => {
    return userInfo.roleCode === 'SYS_ADMIN';
  });

  const isReader = computed(() => {
    const roleCode = userInfo.roleCode;
    // 读者角色包括各种 READER_ 开头的角色
    return roleCode.startsWith('READER_');
  });

  const readerType = computed(() => {
    const roleCode = userInfo.roleCode;
    if (roleCode === 'READER_SOCIAL') return 'social';
    if (roleCode === 'READER_STUDENT') return 'student';
    if (roleCode === 'READER_TEACHER') return 'teacher';
    if (roleCode === 'ADMIN') return 'admin';
    if (roleCode === 'SYS_ADMIN') return 'sysAdmin';
    return 'unknown';
  });

  // Actions
  function setToken(newToken: string) {
    token.value = newToken;
    userInfo.token = newToken;
    // 存储到 localStorage
    localStorage.setItem('token', newToken);
  }

  function setUserInfo(info: Partial<UserInfoResponse>) {
    Object.assign(userInfo, {
      userId: info.userId || -1,
      username: info.username || '',
      account: info.account || '',
      uid: info.uid || '',
      roleCode: info.roleCode || '',
      roleName: info.roleName || '',
      creditScore: info.creditScore || 0,
      avatar: info.avatar || '',
      token: info.token || token.value,
      roleId: info.roleId || -1,
    });

    // 存储到 localStorage
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
  }

  function clearUser() {
    token.value = '';
    Object.assign(userInfo, {
      userId: -1,
      username: '',
      account: '',
      uid: '',
      roleCode: '',
      roleName: '',
      creditScore: 0,
      avatar: '',
      token: '',
      roleId: -1,
    });

    // 清除 localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('rememberMe');
    localStorage.removeItem('savedAccount');
  }

  // 初始化用户状态（从 localStorage 恢复）
  function initUserState() {
    const storedToken = localStorage.getItem('token');
    const storedUserInfo = localStorage.getItem('userInfo');

    if (storedToken) {
      token.value = storedToken;
      userInfo.token = storedToken;
    }

    if (storedUserInfo) {
      try {
        const parsedInfo = JSON.parse(storedUserInfo);
        Object.assign(userInfo, parsedInfo);
      } catch (error) {
        console.error('解析用户信息失败:', error);
        localStorage.removeItem('userInfo');
      }
    }
  }

  // 检查 token 是否有效
  function checkTokenValid() {
    return !!token.value;
  }

  // 退出登录
  function logout() {
    clearUser();
    // 可以在这里添加调用退出接口的逻辑
  }

  // 初始化
  initUserState();

  return {
    // State
    token,
    userInfo,

    // Getters
    isLoggedIn,
    userId,
    userName,
    roleCode,
    userFullInfo,
    isAdmin, 
    isSystemAdmin,
    isReader, 
    readerType, 

    // Actions
    setToken,
    setUserInfo,
    clearUser,
    checkTokenValid,
    logout,
    initUserState,
  };
});
