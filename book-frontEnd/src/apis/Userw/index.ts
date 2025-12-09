import axios from 'axios';
import type { BaseResponse } from '../Commonw/type';
import type { CurrentUserDTO, UserListResponse } from './type';

const request = axios.create({
  baseURL: 'http://localhost:8089/api',
  timeout: 5000,
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => {
  return request.get<BaseResponse<CurrentUserDTO>>('/user/current');
};

/**
 * 获取用户列表（管理员）
 * @param params 分页和筛选参数
 */
export const getUserList = (params?: {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
  roleFilter?: string;
  orderBy?: string;
  orderDirection?: string;
}) => {
  return request.get<BaseResponse<UserListResponse>>('/user/list', { params });
};