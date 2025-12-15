// src/views/AdminBorrowRecord/types.ts
export interface PageInfoDTO {
  currentPage?: number;
  pageSize?: number;
  total?: number;
  totalPages?: number;
}

export interface BookCategoryDTO {
  categoryId?: number;
  categoryCode?: string;
  categoryName?: string;
  parentId?: number;
  orderNum?: number;
}

export interface BookInfoDTO {
  bookId?: number;
  bookName?: string;
  coverUrl?: string;
  author?: string;
  authorInfo?: string;
}

export interface UserInfo {
  userId?: number;
  avatar?: string;
  userName?: string;
  uid?: string;
}

export interface BaseBorrowRecordDTO {
  logId?: number;
  bookInfo?: BookInfoDTO;
  bookCategory?: BookCategoryDTO;
  userInfo?: UserInfo;
  operationType?: number;
  operationTypeDesc?: string;
  operationDate?: string;
}

export interface PageDTOBaseBorrowRecordDTO {
  pageInfo?: PageInfoDTO;
  records?: BaseBorrowRecordDTO[];
}

export interface BorrowRecordResponse {
  code?: number;
  data?: PageDTOBaseBorrowRecordDTO;
  message?: string;
}

export interface SearchParams {
  keyword?: string;
  categoryCode?: string;
  operationType?: number | '';
  currentPage?: number;
  pageSize?: number;
}