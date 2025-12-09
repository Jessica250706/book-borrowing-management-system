import axios from 'axios';
import type { BaseResponse } from '../Commonw/type';
import type { MenuDTO } from './type';

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
 * 获取所有菜单
 */
export const getAllMenus = () => {
  return request.get<BaseResponse<MenuDTO[]>>('/menu/all');
};

/**
 * 根据用户ID获取菜单列表
 * @param userId 用户ID
 */
export const getMenusByUserId = (userId: number) => {
  return request.get<BaseResponse<MenuDTO[]>>(`/menu/user/${userId}`);
};