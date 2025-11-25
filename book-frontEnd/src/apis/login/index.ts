import request from '@/apis/request'
import type {
  RegisterParams,
  LoginParams,
  UserInfoResponse,
  ApiResponse
} from './type'

/**
 * 用户注册
 * @param registerData 注册参数
 * @returns 注册结果
 */
export const registerApi = (registerData: RegisterParams): Promise<ApiResponse<UserInfoResponse>> => {
  return request.post('/api/user/register', registerData)
}

/**
 * 用户登录
 * @param loginData 登录参数
 * @returns 登录结果
 */
export const loginApi = (loginData: LoginParams): Promise<ApiResponse<UserInfoResponse>> => {
  return request.post('/api/user/login', null, {
    params: {
      account: loginData.account,
      password: loginData.password
    }
  })
}

/**
 * 获取当前用户信息
 * @returns 用户信息
 */
export const getCurrentUserApi = (): Promise<ApiResponse<UserInfoResponse>> => {
  return request.get('/api/user/current')
}

/**
 * 退出登录
 * @returns 退出结果
 */
export const logoutApi = (): Promise<ApiResponse> => {
  return request.post('/auth/logout')
}

/**
 * 刷新token
 * @returns 新的token
 */
export const refreshTokenApi = (): Promise<ApiResponse<{ token: string }>> => {
  return request.post('/api/user/refresh-token')
}

/**
 * 发送验证码
 * @param account 账号
 * @param type 验证码类型
 * @returns 发送结果
 */
export const sendCaptchaApi = (account: string, type: string = 'register'): Promise<ApiResponse> => {
  return request.post('/api/user/send-captcha', null, { // 修改路径，后端需要实现
    params: { account, type }
  })
}

/**
 * 验证验证码
 * @param account 账号
 * @param captcha 验证码
 * @param type 验证码类型
 * @returns 验证结果
 */
export const verifyCaptchaApi = (account: string, captcha: string, type: string = 'register'): Promise<ApiResponse> => {
  return request.post('/api/user/verify-captcha', null, { // 修改路径，后端需要实现
    params: { account, captcha, type }
  })
}