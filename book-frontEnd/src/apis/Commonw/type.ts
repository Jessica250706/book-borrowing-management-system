/** 基础响应类型 */
export interface BaseResponse<T = any> {
  renewDays: number;
  code?: number;
  data?: T;
  message?: string;
}

/** 分页信息类型 */
export interface PageInfoDTO {
  currentPage?: number;
  pageSize?: number;
  total?: number;
  totalPages?: number;
}