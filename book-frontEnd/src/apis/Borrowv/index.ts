import axios from 'axios';
import type { BaseResponse } from '../Commonw/type';
import type {
    CurrentBorrowResponse,
    BorrowRecordResponse,
    ReturnBooksParams,
} from './type';

// 创建axios实例（可统一配置baseURL、拦截器等）
const request = axios.create({
  baseURL: 'http://localhost:8089/api',
  timeout: 5000,
});

// 请求拦截器：自动添加token
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/**
 * 获取当前借阅列表（读者端）
 * @param params 分页和筛选参数
 */
export const getCurrentBorrowList = (params?: {
  current?: number;
  size?: number;
  keyword?: string;
  categoryId?: number;
  sortField?: string;
  sortOrder?: string;
}) => {
  return request.get<BaseResponse<CurrentBorrowResponse>>(
    '/borrow/current/list',
    { params }
  );
};

/**
 * 获取借阅记录列表（支持管理员/用户）
 * @param params 分页、筛选和权限相关参数
 */
export const getBorrowRecordList = (params?: {
  current?: number;
  size?: number;
  bookId?: number;
  borrowStatus?: number; // 0-借阅中，1-已归还等
  startTime?: string;
  endTime?: string;
  keyword?: string; // 书名/作者/用户名
  userName?: string; // 管理员查询用
}) => {
  return request.get<BaseResponse<BorrowRecordResponse>>(
    '/borrow/record/list',
    { params }
  );
};

/**
 * 归还书籍（批量）
 * @param data 要归还的借阅记录ID列表
 */
export const returnBooks = (data: ReturnBooksParams) => {
  return request.put<BaseResponse>('/borrow/return', data);
};

/**
 * 管理员确认归还（批量）
 * @param data 要确认的借阅记录ID列表
 */
export const confirmReturnBooks = (data: ReturnBooksParams) => {
  return request.put<BaseResponse>('/borrow/confirm-return', data);
};