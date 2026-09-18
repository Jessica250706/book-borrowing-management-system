import axios from 'axios';
import type {
  GetCurrentReserveListParams,
  PageDTOCurrentReservationDTO,
  CancelReserveParams,
  CancelReserveResponse,
  ReserveBookParams
} from './type';
import type { BaseApiResponse } from '@/apis/Borrowv/type';

// 创建请求实例（保持原有token拦截逻辑）
const request = axios.create({
  baseURL: 'http://localhost:8089',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器：自动添加token（无需手动传）
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

/**
 * 核心接口：获取当前用户的预约列表（正确接口：GET /api/reservation/current/list）
 * @param params 分页+筛选参数（Query传参）
 */
export const getCurrentReserveList = async (
  params: GetCurrentReserveListParams
): Promise<BaseApiResponse<PageDTOCurrentReservationDTO>> => {
  const response = await request.get<BaseApiResponse<PageDTOCurrentReservationDTO>>(
    '/api/reservation/current/list', // 正确接口路径
    { params } // Query参数拼接（符合接口文档要求）
  );
  return response.data;
};

/**
 * 取消预约接口（正确接口：PUT /api/book/cancel-reserve/{bookId}）
 * @param params 书籍ID（Path传参）
 */
export const cancelReserveBook = async (
  params: CancelReserveParams
): Promise<CancelReserveResponse> => {
  const response = await request.put<CancelReserveResponse>(
    `/api/book/cancel-reserve/${params.bookId}`, // Path拼接bookId
    {} // 无请求体，符合接口文档
  );
  return response.data;
};

/**
 * 预约书籍接口（保留，适配之前的POST接口）
 * @param params 书籍ID（Path传参）
 */
export const reserveBook = async (
  params: ReserveBookParams
): Promise<BaseApiResponse> => {
  const response = await request.post<BaseApiResponse>(
    `/api/book/reserve/${params.bookId}`,
    {}
  );
  return response.data;
};

// 导出接口供页面使用
export default {
  getCurrentReserveList,
  cancelReserveBook,
  reserveBook
};