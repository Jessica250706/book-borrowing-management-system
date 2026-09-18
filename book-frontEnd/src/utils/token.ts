import { ElMessage } from 'element-plus'
import { refreshTokenApi } from '@/apis/login'

/**
 * Token 验证工具类
 */
class TokenValidator {
    /**
     * 验证 token 是否有效
     */
    async validateToken(token: string): Promise<boolean> {
        if (!token) {
            return false
        }

        // 1. 基础格式验证
        if (!this.validateTokenFormat(token)) {
            this.clearInvalidToken()
            return false
        }

        // 2. 解析 token 检查过期时间
        const payload = this.parseJwt(token)
        if (!payload) {
            this.clearInvalidToken()
            return false
        }

        // 3. 检查 token 是否过期
        if (this.isTokenExpired(payload)) {
            // 尝试刷新 token
            const refreshed = await this.tryRefreshToken(token)
            if (!refreshed) {
                this.clearInvalidToken()
                ElMessage.warning('登录已过期，请重新登录')
                return false
            }
            return true
        }

        // 4. 检查 token 是否即将过期（提前刷新）
        if (this.isTokenExpiringSoon(payload)) {
            // 异步刷新 token，不阻塞当前操作
            this.tryRefreshToken(token).then(success => {
                if (success) {
                    console.log('Token 已自动刷新')
                }
            })
        }

        return true
    }

    /**
     * 验证 token 格式
     */
    private validateTokenFormat(token: string): boolean {
        if (typeof token !== 'string') {
            return false
        }

        // JWT token 通常由三部分组成，用点分隔
        const parts = token.split('.')
        if (parts.length !== 3) {
            return false
        }

        // 检查每个部分是否都是有效的 base64url
        try {
            parts.forEach(part => {
                // 将 base64url 转换为 base64
                const base64 = part.replace(/-/g, '+').replace(/_/g, '/')
                // 添加填充
                const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=')
                atob(padded)
            })
            return true
        } catch {
            return false
        }
    }

    /**
     * 解析 JWT token
     */
    private parseJwt(token: string): any {
        try {
            const parts = token.split('.')
            if (parts.length !== 3) {
                throw new Error('非法的 JWT 结构')
            }

            const base64Url = parts[1]
            if (typeof base64Url !== 'string') {
                throw new Error('Base64Url 部分缺失')
            }

            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
            const jsonPayload = decodeURIComponent(
                atob(base64)
                    .split('')
                    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
            )
            return JSON.parse(jsonPayload)
        } catch (error) {
            console.error('解析 token 失败:', error)
            return null
        }
    }

    /**
     * 检查 token 是否过期
     */
    private isTokenExpired(payload: any): boolean {
        if (!payload.exp) {
            return true
        }

        const currentTime = Math.floor(Date.now() / 1000)
        return payload.exp < currentTime
    }

    /**
     * 检查 token 是否即将过期（30分钟内）
     */
    private isTokenExpiringSoon(payload: any, thresholdMinutes: number = 30): boolean {
        if (!payload.exp) {
            return true
        }

        const currentTime = Math.floor(Date.now() / 1000)
        const thresholdSeconds = thresholdMinutes * 60
        return (payload.exp - currentTime) < thresholdSeconds
    }

    /**
     * 尝试刷新 token
     */
    private async tryRefreshToken(oldToken: string): Promise<boolean> {
        try {
            const response = await refreshTokenApi()

            if (response.code === 200 && response.data.token) {
                // 保存新 token
                localStorage.setItem('token', response.data.token)

                // 更新 store 中的 token
                const { useUserStore } = await import('@/store')
                const store = useUserStore()
                store.setToken(response.data.token)

                ElMessage.success('Token 已刷新')
                return true
            } else {
                return false
            }
        } catch (error) {
            console.error('刷新 token 失败:', error)
            return false
        }
    }

    /**
     * 清除无效的 token
     */
    private clearInvalidToken(): void {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')

        import('@/store').then(({ useUserStore }) => {
            const store = useUserStore()
            store.clearUser()
        })
    }

    /**
     * 获取 token 剩余时间（分钟）
     */
    getTokenRemainingTime(token: string): number {
        const payload = this.parseJwt(token)
        if (!payload || !payload.exp) {
            return 0
        }

        const currentTime = Math.floor(Date.now() / 1000)
        const remainingSeconds = payload.exp - currentTime
        return Math.max(0, Math.floor(remainingSeconds / 60))
    }

    /**
     * 获取 token 中的用户信息
     */
    getTokenUserInfo(token: string): { userId?: number; username?: string; role?: string } {
        const payload = this.parseJwt(token)
        if (!payload) {
            return {}
        }

        return {
            userId: payload.userId || payload.sub,
            username: payload.username,
            role: payload.role
        }
    }
}

// 创建单例实例
export const tokenValidator = new TokenValidator()