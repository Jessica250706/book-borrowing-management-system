// @/apis/Return/index.ts
import request from '@/apis/request';
import type {
  GetReturnBookListParams,
  ReturnBooksParams,
  CurrentReturnListResponse,
  ConfirmReturnResponse,
} from './type';

/**
 * 获取管理员端待确认归还书籍列表（分页+筛选）
 * @param params 分页/筛选参数
 * @returns 列表数据
 */
export const getReturnBookList = (
  params: GetReturnBookListParams
): Promise<CurrentReturnListResponse> => {
  return request.get('/api/borrow/return/current/list', { params });
};

/**
 * 确认归还书籍（支持单条/批量）
 * @param data 借阅ID列表
 * @returns 操作结果
 */
export const confirmReturnBooks = (
  data: ReturnBooksParams
): Promise<ConfirmReturnResponse> => {
  return request.put('/api/borrow/confirm-return', data);
};