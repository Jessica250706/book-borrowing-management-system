// 注册请求参数
export interface RegisterParams {
  account: string
  username: string
  password: string
  confirmPassword: string
  roleId?: number
  captcha?: string
}

// 登录请求参数
export interface LoginParams {
  account: string
  password: string
}

// 用户信息响应 - 根据后端 RegisterResponseDTO 调整
export interface UserInfoResponse {
  userId?: number
  username?: string
  account?: string
  uid?: string
  roleCode?: string
  roleName?: string
  creditScore?: number
  token?: string
  avatar?: string
  roleId?: number
}

// 登录表单数据
export interface LoginFormData {
  account: string
  password: string
  rememberMe?: boolean
}

// 注册表单数据
export interface RegisterFormData {
  account: string
  username: string
  password: string
  confirmPassword: string
  roleId?: number
  captcha?: string
}