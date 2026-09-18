import axios from 'axios';
import { message } from 'ant-design-vue';
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import router from '@/router';

// 标记是否正在退出登录
let isLoggingOut = false;

// 创建axios实例
const service: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API || '/',
    timeout: 10000,
});

// 请求拦截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 检查是否正在退出登录
        if (isLoggingOut) {
            console.log('正在退出登录，静默取消请求:', config.url);

            return new Promise(() => {
                
            });
        }

        // 检查是否需要认证
        const isPublicApi =
            config.url?.includes('/login') ||
            config.url?.includes('/register') ||
            config.url?.includes('/public') ||
            config.url?.includes('/captcha');

        // 对于需要认证的接口，检查token是否存在
        if (!isPublicApi) {
            const token = localStorage.getItem('token');

            if (!token) {
                console.log('用户未登录，静默取消请求:', config.url);
                return new Promise(() => { });
            }

            // 添加token到请求头
            if (config.headers) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse) => {
        const { data } = response;

        // 根据后端 Response 结构判断成功
        if (data.code === 200 || data.code === 0 || data.code === null || data.code === undefined) {
            return data;
        } else {
            // 处理特定的业务错误码
            if (data.code === 401 || data.code === 403) {
                handleAuthError();
                return Promise.reject(new Error(data.message || '登录已过期'));
            }

            message.error(data.message || '请求失败');
            return Promise.reject(new Error(data.message || '请求失败'));
        }
    },
    (error) => {
        // 处理HTTP状态码错误
        if (error.response?.status === 401 || error.response?.status === 403) {
            handleAuthError();
            return Promise.reject(new Error('登录已过期，请重新登录'));
        }

        // 网络错误
        if (error.message?.includes('Network Error')) {
            message.error('网络连接失败，请检查网络');
        } else if (error.message && !error.message.includes('timeout')) {
            // 如果不是超时错误，才显示消息
            message.error(error.message || '网络错误');
        }

        return Promise.reject(error);
    }
);

// 处理认证错误
function handleAuthError() {
    // 清理本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');

    // 避免重复跳转
    if (router.currentRoute.value.path !== '/login') {
        message.error('登录已过期，请重新登录');
        setTimeout(() => {
            router.replace('/login');
        }, 1500);
    }
}

// 导出设置退出状态的函数
export const setLoggingOutStatus = (status: boolean) => {
    isLoggingOut = status;
    console.log('设置退出登录状态:', status);
};

export default service;