import type { PageInfoDTO } from '../Commonw/type';

/** 书籍列表项 */
export interface BookInfo {
  bookId?: number;
  bookName?: string;
  coverUrl?: string;
  author?: string;
  category?: string;
  bookStatus?: number;
  borrowStatus?: string;
  availableCount?: number;
  totalCount?: number;
  publishDate?: string;
}

/** 书籍列表响应 */
export interface BookListResponse {
  pageInfo?: PageInfoDTO;
  records?: BookInfo[];
}

/** 书籍详情 */
export interface BookDetailDTO {
  bookId?: number;
  bookName?: string;
  coverUrl?: string;
  author?: string;
  translator?: string;
  category?: string;
  intro?: string;
  publisher?: string;
  isbn?: string;
  price?: number;
  totalCount?: number;
  availableCount?: number;
  borrowCount?: number;
  bookStatus?: number;
  borrowStatus?: string;
  canBorrow?: boolean;
  publishDate?: string;
  shelfTime?: string;
}

/** 借阅结果 */
export interface BorrowResultDTO {
  borrowId?: number;
  bookId?: number;
  bookName?: string;
  author?: string;
  coverUrl?: string;
  category?: string;
  borrowTime?: string;
  expectedReturnTime?: string;
  borrowDays?: number;
  borrowStatus?: number;
}