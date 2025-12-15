import request from '@/apis/request';

import type {
    ReturnBooksParams,
    ReturnResponse,
    ConfirmReturnParams,
    GetReturnBookListParams,
    ReturnListResponse
} from './type';

import axios from 'axios';

// 从本地缓存获取Token（适配项目实际存储方式）
const getToken = () => {
  return localStorage.getItem('token') || sessionStorage.getItem('token') || '';
};

// 确认后端实际接口前缀（务必和后端一致！）
const BASE_URL = 'http://localhost:8089/api/borrow';

// 创建axios实例（统一处理请求/响应）
const service = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
});

/**
 * 批量归还书籍
 */
export const returnBooks = async (params: ReturnBooksParams): Promise<ReturnResponse> => {
  try {
    return await service.put('/return', params);
  } catch (error) {
    return error as ReturnResponse;
  }
};

/**
 * 确认归还书籍
 */
export const confirmReturn = async (params: ConfirmReturnParams): Promise<ReturnResponse> => {
  try {
    return await service.put('/confirm-return', params);
  } catch (error) {
    return error as ReturnResponse;
  }
};

/**
 * 获取待归还书籍列表（核心修复：兼容后端返回null）
 */
export const getReturnBookList = async (params?: GetReturnBookListParams): Promise<ReturnListResponse> => {
  try {
    // 确保参数默认值，避免传null给后端
    const queryParams = {
      currentPage: params?.currentPage || 1,
      pageSize: params?.pageSize || 10,
      keyword: params?.keyword || '',
      categoryCode: params?.categoryCode || '',
      ...params
    };
    return await service.get('/return/current/list', { params: queryParams });
  } catch (error) {
    // 兜底返回标准格式，避免前端崩溃
    return {
      code: 500,
      message: '查询失败',
      data: { records: [], pageInfo: { total: 0 } }
    } as ReturnListResponse;
  }
};