<template>
  <div class="login-container">
    <!-- 背景图片层 -->
    <div class="login-background"></div>
    
    <!-- 半透明遮罩层 -->
    <div class="background-overlay"></div>
    
    <div class="login-card">
      <!-- 头部 -->
      <div class="login-header">
        <div class="logo-section">
          <el-icon class="logo-icon"><Reading /></el-icon>
          <h1 class="system-name">图书借阅管理系统</h1>
        </div>
        <p class="welcome-text">欢迎回来，请登录您的账户</p>
      </div>

      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="account">
          <el-input
            v-model="loginForm.account"
            placeholder="请输入账号"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <div class="form-options">
            <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
            <el-button type="text" class="forgot-link">忘记密码?</el-button>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <div class="register-link">
          <span>还没有账户?</span>
          <el-button type="text" @click="goToRegister">立即注册</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Reading } from '@element-plus/icons-vue'
import { loginApi } from '@/apis/login'
import { GlobalStore } from '@/store'
import { tokenValidator } from '@/utils/token'
import type { LoginFormData } from '@/apis/login/type'

const router = useRouter()
const route = useRoute()
const globalStore = GlobalStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive<LoginFormData>({
  account: '',
  password: '',
  rememberMe: false
})

const loginRules: FormRules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true
    const response = await loginApi({
      account: loginForm.account,
      password: loginForm.password
    })

    if (response.code === 200) {
      ElMessage.success('登录成功')
      
      // 保存 token 和用户信息
      if (response.data.token) {
        globalStore.setToken(response.data.token)
        
        // 验证 token 有效性
        const isValid = await tokenValidator.validateToken(response.data.token)
        if (!isValid) {
          ElMessage.error('Token 无效，请重新登录')
          return
        }
      }
      
      // 设置完整的用户信息
      globalStore.setUserInfo({
        userId: response.data.userId,
        username: response.data.username,
        account: response.data.account,
        uid: response.data.uid,
        roleCode: response.data.roleCode,
        roleName: response.data.roleName,
        creditScore: response.data.creditScore,
        avatar: response.data.avatar,
        token: response.data.token,
        roleId: response.data.roleId
      })

      // 记住我功能
      if (loginForm.rememberMe) {
        localStorage.setItem('rememberMe', 'true')
        localStorage.setItem('savedAccount', loginForm.account)
      } else {
        localStorage.removeItem('rememberMe')
        localStorage.removeItem('savedAccount')
      }

      // 检查是否有重定向路径
      const redirect = route.query.redirect as string
      if (redirect) {
        router.push(redirect)
      } else {
        router.push('/borrow/newBooks')
      }
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error: any) {
    if (error.errors) {
      return
    }
    ElMessage.error(error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}

// 初始化记住我功能
const initRememberMe = () => {
  const rememberMe = localStorage.getItem('rememberMe')
  const savedAccount = localStorage.getItem('savedAccount')
  
  if (rememberMe === 'true' && savedAccount) {
    loginForm.account = savedAccount
    loginForm.rememberMe = true
  }
}

// 检查是否已登录，如果已登录且 token 有效则跳转到首页
const checkLoginStatus = async () => {
  const token = localStorage.getItem('token')
  if (token) {
    const isValid = await tokenValidator.validateToken(token)
    if (isValid) {
      ElMessage.info('您已登录，将跳转到首页')
      router.push('/borrow/newBooks')
    } else {
      // token 无效，清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
}

onMounted(() => {
  initRememberMe()
  checkLoginStatus()
})
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('@/assets/login_bg.jpg') no-repeat center center;
  background-size: cover;
  background-attachment: fixed; /* 创建视差效果 */
  z-index: 0;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4); /* 深色遮罩，提高文字可读性 */
  z-index: 1;
}

.login-card {
  background: rgba(255, 255, 255, 0.95); /* 半透明白色背景 */
  border-radius: 20px;
  padding: 48px 40px;
  width: 100%;
  max-width: 440px;
  box-shadow: 
    0 25px 80px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(20px); /* 毛玻璃效果 */
  position: relative;
  z-index: 2;
  
  &::before {
    content: '';
    position: absolute;
    top: -1px;
    left: 50%; 
    transform: translateX(-50%);
    width: calc(100% - 20px); 
    height: 4px;
    background: linear-gradient(90deg, #409EFF, #67C23A, #E6A23C);
    border-radius: 20px 20px 0 0;
  }
}

.login-header {
  text-align: center;
  margin-bottom: 36px;

  .logo-section {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    margin-bottom: 20px;

    .logo-icon {
      font-size: 36px;
      color: #409EFF;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
    }

    .system-name {
      color: #303133;
      margin: 0;
      font-size: 26px;
      font-weight: 700;
      background: linear-gradient(135deg, #409EFF, #67C23A);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  }

  .welcome-text {
    color: #606266;
    margin: 0;
    font-size: 15px;
    font-weight: 500;
    text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
  }
}

.login-form {
  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }

  .forgot-link {
    padding: 0;
    height: auto;
    color: #409EFF;
    font-weight: 500;
    
    &:hover {
      color: #67a8ff;
    }
  }

  .login-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 12px;
    background: linear-gradient(135deg, #409EFF, #337ecc);
    border: none;
    transition: all 0.3s ease;
    
    &:hover {
      background: linear-gradient(135deg, #337ecc, #2c6bb3);
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
}

.register-link {
  text-align: center;
  margin-top: 28px;
  color: #606266;
  font-size: 15px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);

  span {
    margin-right: 8px;
  }

  button {
    padding: 0;
    height: auto;
    color: #67C23A;
    font-weight: 600;
    
    &:hover {
      color: #5daf34;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    padding: 20px;
  }

  .login-card {
    padding: 36px 28px;
    margin: 0 10px;
    border-radius: 16px;
  }

  .login-header {
    .logo-section {
      flex-direction: column;
      gap: 12px;

      .system-name {
        font-size: 22px;
      }
    }
  }

  .login-background {
    background-attachment: scroll; /* 移动端取消固定背景 */
  }
}

// 动画效果
.login-card {
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

// 表单项样式优化
:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-input) {
  .el-input__wrapper {
    border-radius: 10px;
    transition: all 0.3s ease;
    background: rgba(255, 255, 255, 0.9);
    
    &:hover {
      box-shadow: 0 0 0 2px #409EFF;
      background: rgba(255, 255, 255, 1);
    }
    
    &.is-focus {
      box-shadow: 0 0 0 2px #409EFF;
      background: rgba(255, 255, 255, 1);
    }
  }
}

:deep(.el-checkbox) {
  .el-checkbox__label {
    color: #606266;
    font-weight: 500;
  }
}

// 背景图片加载时的备用背景
.login-background {
  background-color: #2c3e50; /* 备用背景色 */
}

// 加载动画
.login-btn:loading {
  position: relative;
  overflow: hidden;
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    animation: loading 1.5s infinite;
  }
}

@keyframes loading {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}
</style>