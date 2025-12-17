// @/apis/Return/type.ts
/**
 * 获取待归还列表请求参数
 */
export interface GetReturnBookListParams {
  currentPage?: number;
  pageSize?: number;
  keyword?: string;
  categoryCode?: string;
}

/**
 * 确认归还请求参数（单条/批量）
 */
export interface ReturnBooksParams {
  ids: number[]; // 借阅记录ID列表（单条传[123]，批量传[123,456]）
}

/**
 * 分页信息DTO
 */
export interface PageInfoDTO {
  currentPage?: number;
  pageSize?: number;
  total?: number;
  totalPages?: number;
}

/**
 * 书籍分类DTO
 */
export interface BookCategoryDTO {
  categoryId?: number;
  categoryCode?: string;
  categoryName?: string;
  parentId?: number;
  orderNum?: number;
}

/**
 * 书籍信息DTO
 */
export interface BookInfoDTO {
  bookId?: number;
  bookName?: string;
  coverUrl?: string;
  author?: string;
  translator?: string;
  categoryId?: number;
  category?: string;
  bookStatus?: number;
  totalCount?: number;
  availableCount?: number;
  shelfTime?: string;
  intro?: string;
  publisher?: string;
  isbn?: string;
  publishDate?: string;
  price?: number;
}

/**
 * 用户信息DTO
 */
export interface UserInfoDTO {
  userId?: number;
  avatar?: string;
  userName?: string;
  displayName?: string;
  uid?: string;
}

/**
 * 归还记录DTO
 */
export interface CurrentReturnDTO {
  borrowId?: number; // 借阅记录ID（核心主键）
  bookInfo?: BookInfoDTO; // 书籍信息
  bookCategory?: BookCategoryDTO; // 分类信息
  userInfo?: UserInfoDTO; // 借阅人信息
  borrowTime?: string; // 借阅时间
  expectedReturnTime?: string; // 预计归还时间
  actualReturnTime?: string; // 实际归还时间
  returnApplyTime?: string; // 申请归还时间
  returnConfirmStatus?: number; // 0-待确认 1-已确认
  renewCount?: number; // 续借次数
  borrowStatus?: number; // 0-借阅中 1-已归还 2-已超时 3-归还待确认
  operations?: string[]; // 可操作项
}

/**
 * 列表响应结构
 */
export interface PageDTOCurrentReturnDTO {
  pageInfo?: PageInfoDTO;
  records?: CurrentReturnDTO[];
}

/**
 * 获取列表接口响应
 */
export interface CurrentReturnListResponse {
  code?: number; // 0-成功 其他-失败
  data?: PageDTOCurrentReturnDTO;
  message?: string;
}

/**
 * 确认归还接口响应
 */
export interface ConfirmReturnResponse {
  code?: number; // 0-成功 其他-失败
  data?: object;
  message?: string;
}