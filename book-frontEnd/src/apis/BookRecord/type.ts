// 借阅记录数据类型
export interface BorrowRecord {
  id: number;
  bookName: string;
  bookImg: string;
  author: string;
  translator: string;
  category: string;
  operationType: '借阅' | '归还' | '续借' | '预约' | '取消预约';
  operationTime: string;
}

// 搜索参数类型
export interface RecordSearchParams {
  keyword?: string;
  category?: string;
  operationType?: string;
  page?: number;
  pageSize?: number;
}

// 接口返回结果类型
export interface PageResult<T> {
  list: T[];
  total: number;
}