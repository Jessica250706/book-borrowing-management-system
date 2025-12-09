import request from '@/utils/request';
import { RecordSearchParams, PageResult, BorrowRecord } from './type';

/**
 * 获取借阅记录列表
 * @param params 搜索参数
 * @returns 分页结果
 */
export const getBorrowRecords = (params: RecordSearchParams) => {
  return request.get<PageResult<BorrowRecord>>('/api/borrow-records', { params });
};

/**
 * 删除借阅记录
 * @param id 记录ID
 * @returns 操作结果
 */
export const deleteBorrowRecord = (id: number) => {
  return request.delete(`/api/borrow-records/${id}`);
};

/**
 * 获取书籍分类列表
 * @returns 分类数组
 */
export const getBookCategories = () => {
  return request.get<string[]>('/api/book-categories');
};