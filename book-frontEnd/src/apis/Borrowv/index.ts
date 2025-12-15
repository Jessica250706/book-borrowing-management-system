import axios from 'axios';
import type {
  BaseResponse,
  PageDTOCurrentReturnDTO,
  PageDTOCurrentBorrowDTO,
  RenewDaysInfo,
  BatchIdsParam,
  GetCurrentReturnListParams,
  GetCurrentBorrowListParams
} from './type';

// 创建基础请求实例
const request = axios.create({
  baseURL: 'http://localhost:8089', 
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器：添加token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token'); // 从本地存储获取token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

/**
 * 读者端 - 获取当前借阅列表（关键：修正参数名与后端一致）
 * @param params 分页和筛选参数
 */
export const getCurrentBorrowList = async (
  params: GetCurrentBorrowListParams
): Promise<BaseResponse<PageDTOCurrentBorrowDTO>> => {
  const response = await request.get<BaseResponse<PageDTOCurrentBorrowDTO>>(
    '/api/borrow/current/list', // 确认后端接口路径正确！
    { params }
  );
  return response.data;
};

/**
 * 获取指定借阅的剩余可续借天数
 * @param borrowId 借阅ID
 */
export const getRemainingRenewDays = async (
  borrowId: number
): Promise<BaseResponse<RenewDaysInfo>> => {
  const response = await request.get<BaseResponse<RenewDaysInfo>>(
    `/api/borrow/renew/days/${borrowId}`
  );
  return response.data;
};

/**
 * 读者端 - 批量提交归还申请
 * @param data 包含借阅ID列表的参数
 */
export const returnBooks = async (
  data: BatchIdsParam
): Promise<BaseResponse> => {
  const response = await request.put<BaseResponse>(
    '/api/borrow/return',
    data
  );
  return response.data;
};

/**
 * 批量续借书籍
 * @param data 包含借阅ID列表的参数
 */
export const renewBooks = async (
  data: BatchIdsParam
): Promise<BaseResponse> => {
  const response = await request.put<BaseResponse>(
    '/api/borrow/renew',
    data
  );
  return response.data;
};

/**
 * 管理员端 - 获取当前归还待确认列表
 * @param params 分页和筛选参数
 */
export const getCurrentReturnList = async (
  params: GetCurrentReturnListParams
): Promise<BaseResponse<PageDTOCurrentReturnDTO>> => {
  const response = await request.get<BaseResponse<PageDTOCurrentReturnDTO>>(
    '/api/borrow/return/current/list',
    { params }
  );
  return response.data;
};

/**
 * 管理员端 - 批量确认归还
 * @param data 包含借阅ID列表的参数
 */
export const confirmReturn = async (
  data: BatchIdsParam
): Promise<BaseResponse> => {
  const response = await request.put<BaseResponse>(
    '/api/borrow/confirm-return',
    data
  );
  return response.data;
};

export default {
  getCurrentReturnList,
  confirmReturn,
  getRemainingRenewDays,
  returnBooks,
  getCurrentBorrowList,
  renewBooks
};