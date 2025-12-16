import request from '@/apis/request';
import type { 
  SearchParams, 
  BorrowRecordResponse,
  CurrentUserResponse,
  CategoryResponse 
} from '@/apis/Record/type';

/**
 * 获取借阅记录列表
 */
export const getBorrowRecords = async (params: SearchParams): Promise<BorrowRecordResponse> => {
  return request.get('/api/operation-log/record/list', { params });
};

/**
 * 获取当前登录用户信息
 */
export const getCurrentUser = async (): Promise<CurrentUserResponse> => {
  return request.get('/api/user/current');
};

/**
 * 获取所有书籍分类
 */
export const getBookCategories = async (): Promise<CategoryResponse> => {
  return request.get('/api/book/category/list');
};