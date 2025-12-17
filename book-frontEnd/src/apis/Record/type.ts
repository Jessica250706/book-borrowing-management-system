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

/** 当前用户信息响应 */
export interface CurrentUserResponse {
  code?: number;
  data?: {
    userId?: number;
    username?: string;
    account?: string;
    uid?: string;
    roleCode?: string;
    roleName?: string;
    creditScore?: number;
    token?: string;
    avatar?: string;
  };
  message?: string;
}

/** 书籍分类接口响应 */
export interface CategoryResponse {
  code?: number;
  data?: Array<{
    categoryId?: number;
    categoryCode?: string;
    categoryName?: string;
    parentId?: number;
    orderNum?: number;
  }>;
  message?: string;
}