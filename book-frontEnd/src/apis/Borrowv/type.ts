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

/** 借阅记录状态枚举 */
export const BorrowStatus = {
  BORROWING: 0, // 借阅中
  RETURNED: 1, // 已归还
  OVERDUE: 2, // 逾期
  RESERVED: 3, // 已预约
} as const;

export type BorrowStatusType = typeof BorrowStatus[keyof typeof BorrowStatus];


/** 借阅记录详情 */
export interface BorrowRecordDTO extends BaseBorrowRecordDTO {
  borrowId?: number; // 借阅记录ID
  borrowTime?: string; // 借阅时间
  returnTime?: string; // 实际归还时间
  expectedReturnTime?: string; // 预计归还时间
  status?: BorrowStatusType; // 借阅状态（0-借阅中，1-已归还等）
  overdueDays?: number; // 逾期天数（如有）
  renewCount?: number; // 续借次数
}

/** 获取剩余可续借天数接口 */
export interface RenewDaysResponse {
    borrowId: number;
    bookId: number;
    bookName: string;
    userId: number;
    maxRenewDays: number;
    alreadyRenewedDays: number;
    remainingRenewDays: number;
    canRenew: boolean;
    reason: string;
    suggestedRenewDays: number;
}