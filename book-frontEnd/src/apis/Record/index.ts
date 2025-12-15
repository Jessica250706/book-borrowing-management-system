// src/views/AdminBorrowRecord/api/index.ts
import request from '@/apis/request';
import type { SearchParams, BorrowRecordResponse } from '@/apis/Record/type';

/**
 * 获取借阅记录列表
 * @param params 搜索和分页参数
 * @returns 借阅记录列表数据
 */
export const getBorrowRecords = async (params: SearchParams): Promise<BorrowRecordResponse> => {
  return request.get('/api/operation-log/record/list', { params });
};