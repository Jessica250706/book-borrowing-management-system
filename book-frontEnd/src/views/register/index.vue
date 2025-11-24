<template>
  <div class="register-container">
    <!-- 背景图片层 -->
    <div class="register-background"></div>
    
    <!-- 半透明遮罩层 -->
    <div class="background-overlay"></div>
    
    <div class="register-card">
      <!-- 头部 -->
      <div class="register-header">
        <div class="logo-section">
          <el-icon class="logo-icon"><Reading /></el-icon>
          <h1 class="system-name">图书借阅管理系统</h1>
        </div>
        <p class="welcome-text">欢迎注册新账户</p>
      </div>

      <!-- 注册表单 -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @keyup.enter="handleRegister"
      >
        <el-form-item prop="account">
          <el-input
            v-model="registerForm.account"
            placeholder="请输入账号"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="Avatar"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form-item>

        <div class="login-link">
          <span>已有账户?</span>
          <el-button type="text" @click="goToLogin">立即登录</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Avatar, Reading } from '@element-plus/icons-vue'
import { registerApi } from '@/apis/login'
import type { RegisterFormData } from '@/apis/login/type'

const router = useRouter()
const registerFormRef = ref<FormInstance>()
const loading = ref(false)

const registerForm = reactive<RegisterFormData>({
  account: '',
  username: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 10, message: '用户名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    loading.value = true
    const response = await registerApi({
      account: registerForm.account,
      username: registerForm.username,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword
    })

    if (response.code === 200) {
      ElMessage.success('注册成功，请登录')
      // 注册成功后跳转到登录页
      router.push('/login')
    } else {
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error: any) {
    if (error.errors) {
      // 表单验证错误，不显示消息
      return
    }
    ElMessage.error(error.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped lang="scss">
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.register-background {
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

.register-card {
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
    background: linear-gradient(90deg, #67C23A, #409EFF, #E6A23C);
    border-radius: 20px 20px 0 0;
  }
}

.register-header {
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
      color: #67C23A;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
    }

    .system-name {
      color: #303133;
      margin: 0;
      font-size: 26px;
      font-weight: 700;
      background: linear-gradient(135deg, #67C23A, #409EFF);
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

.register-form {
  .register-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 12px;
    background: linear-gradient(135deg, #67C23A, #5daf34);
    border: none;
    transition: all 0.3s ease;
    
    &:hover {
      background: linear-gradient(135deg, #5daf34, #529b2f);
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(103, 194, 58, 0.4);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
}

.login-link {
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
    color: #409EFF;
    font-weight: 600;
    
    &:hover {
      color: #67a8ff;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .register-container {
    padding: 20px;
  }

  .register-card {
    padding: 36px 28px;
    margin: 0 10px;
    border-radius: 16px;
  }

  .register-header {
    .logo-section {
      flex-direction: column;
      gap: 12px;

      .system-name {
        font-size: 22px;
      }
    }
  }

  .register-background {
    background-attachment: scroll; /* 移动端取消固定背景 */
  }
}

// 动画效果
.register-card {
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
      box-shadow: 0 0 0 2px #67C23A;
      background: rgba(255, 255, 255, 1);
    }
    
    &.is-focus {
      box-shadow: 0 0 0 2px #67C23A;
      background: rgba(255, 255, 255, 1);
    }
  }
}

// 背景图片加载时的备用背景
.register-background {
  background-color: #2c3e50; /* 备用背景色 */
}

// 加载动画
.register-btn:loading {
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