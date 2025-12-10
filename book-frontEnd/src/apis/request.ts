import axios from 'axios';
import { message } from 'ant-design-vue';
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

const service: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API || '/', // 使用 import.meta.env
    timeout: 10000,
});

// 请求拦截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('token');
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
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
            message.error(data.message || '请求失败');
            return Promise.reject(new Error(data.message || '请求失败'));
        }
    },
    (error) => {
        message.error(error.message || '网络错误');
        return Promise.reject(error);
    }
);

export default service;