import request from '@/utils/request'
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
  return request.post('/user/register', registerData)
}

/**
 * 用户登录
 * @param loginData 登录参数
 * @returns 登录结果
 */
export const loginApi = (loginData: LoginParams): Promise<ApiResponse<UserInfoResponse>> => {
  return request.post('/user/login', null, {
    params: {
      account: loginData.account,
      password: loginData.password
    }
  })
}

/**
 * 获取用户信息
 * @returns 用户信息
 */
export const getUserInfoApi = (): Promise<ApiResponse<UserInfoResponse>> => {
  return request.get('/user/userInfo')
}

/**
 * 退出登录
 * @returns 退出结果
 */
export const logoutApi = (): Promise<ApiResponse> => {
  return request.post('/user/logout')
}

/**
 * 刷新token
 * @returns 新的token
 */
export const refreshTokenApi = (): Promise<ApiResponse<{ token: string }>> => {
  return request.post('/user/refreshToken')
}

/**
 * 发送验证码
 * @param account 账号
 * @param type 验证码类型
 * @returns 发送结果
 */
export const sendCaptchaApi = (account: string, type: string = 'register'): Promise<ApiResponse> => {
  return request.post('/user/sendCaptcha', null, {
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
  return request.post('/user/verifyCaptcha', null, {
    params: { account, captcha, type }
  })
}