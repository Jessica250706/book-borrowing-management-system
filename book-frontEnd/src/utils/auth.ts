import { tokenValidator } from './token'

/**
 * 认证状态管理工具
 */
export class AuthManager {
    /**
     * 检查认证状态
     */
    static async checkAuthStatus(): Promise<{
        isAuthenticated: boolean
        token: string | null
        userInfo: any
        remainingTime: number
    }> {
        const token = localStorage.getItem('token')

        if (!token) {
            return {
                isAuthenticated: false,
                token: null,
                userInfo: null,
                remainingTime: 0
            }
        }

        const isValid = await tokenValidator.validateToken(token)
        const userInfo = tokenValidator.getTokenUserInfo(token)
        const remainingTime = tokenValidator.getTokenRemainingTime(token)

        return {
            isAuthenticated: isValid,
            token: isValid ? token : null,
            userInfo: isValid ? userInfo : null,
            remainingTime
        }
    }

    /**
     * 定期检查 token 状态（用于自动刷新）
     */
    static startTokenMonitor(interval: number = 5 * 60 * 1000): ReturnType<typeof setInterval> {
        return setInterval(async () => {
            const token = localStorage.getItem('token')
            if (token) {
                const { isAuthenticated, remainingTime } = await this.checkAuthStatus()

                if (!isAuthenticated) {
                    console.warn('Token 已失效，请重新登录')
                } else if (remainingTime < 10) {
                    console.log('Token 即将过期，准备刷新')
                }
            }
        }, interval)
    }

    /**
     * 停止 token 监控
     */
    static stopTokenMonitor(timer: ReturnType<typeof setInterval>): void {
        clearInterval(timer)
    }
}