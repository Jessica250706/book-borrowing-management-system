import type { PageInfoDTO } from '../Commonw/type';

/** 当前借阅书籍列表项 */
export interface CurrentBorrowDTO {
  id?: number;
  bookName?: string;
  bookCover?: string;
  bookAuthor?: string;
  category?: string;
  remainingDays?: number;
  latestReturnTime?: string;
  renewableDays?: number;
  operations?: string[]; // 如：["renew", "return"]
}

/** 当前借阅列表响应 */
export interface CurrentBorrowResponse {
  pageInfo?: PageInfoDTO;
  records?: CurrentBorrowDTO[];
}

/** 借阅记录列表项 */
export interface BaseBorrowRecordDTO {
  serialNumber?: number;
  bookInfo?: {
    bookId?: number;
    bookName?: string;
    coverUrl?: string;
    author?: string;
  };
  categoryName?: string;
  userInfo?: {
    userId?: number;
    userName?: string;
    avatar?: string;
  };
  operationType?: string;
  operationDate?: string;
}

/** 借阅记录列表响应 */
export interface BorrowRecordResponse {
  pageInfo?: PageInfoDTO;
  records?: BaseBorrowRecordDTO[];
}

/** 归还书籍请求参数 */
export interface ReturnBooksParams {
  ids: number[]; // 借阅记录ID列表
}