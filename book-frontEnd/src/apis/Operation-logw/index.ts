import axios from 'axios';
import type { BaseResponse } from '../Commonw/type';
import type { OperationLogResponse } from './type';

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
 * 分页获取所有操作日志（管理员）
 * @param params 分页参数
 */
export const getAllOperationLogs = (params: {
  pageNum: number;
  pageSize: number;
}) => {
  return request.get<BaseResponse<OperationLogResponse>>('/operation-log/all/page', { params });
};

/**
 * 分页获取用户操作日志
 * @param params 分页和筛选参数
 */
export const getUserOperationLogs = (params?: {
  pageNum?: number;
  pageSize?: number;
  startTime?: string;
  endTime?: string;
  operationType?: number;
}) => {
  return request.get<BaseResponse<OperationLogResponse>>('/operation-log/user/page', { params });
};