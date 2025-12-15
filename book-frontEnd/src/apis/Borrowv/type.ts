/** 分页信息通用类型 */
export interface PageInfoDTO {
  currentPage?: number;
  pageSize?: number;
  total?: number;
  totalPages?: number;
}

/** 基础响应类型 */
export interface BaseResponse<T = any> {
  json(): unknown;
  code?: number;
  data?: T;
  message?: string;
}

/** 书籍分类信息 */
export interface BookCategoryDTO {
  categoryId?: number;
  categoryCode?: string;
  categoryName?: string;
  parentId?: number;
  orderNum?: number;
}

/** 书籍信息 */
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
  borrowCount?: number;
}

/** 用户信息 */
export interface UserInfo {
  userId?: number;
  avatar?: string;
  userName?: string;
  displayName?: string;
  uid?: string;
}

/** 当前归还列表项类型 */
export interface CurrentReturnDTO {
  borrowId?: number;
  bookInfo?: BookInfoDTO;
  bookCategory?: BookCategoryDTO;
  userInfo?: UserInfo;
  borrowTime?: string;
  expectedReturnTime?: string;
  actualReturnTime?: string;
  returnApplyTime?: string;
  returnConfirmStatus?: number; // 0-待确认，1-已确认
  renewCount?: number;
  borrowStatus?: number; // 0-借阅中，1-已归还，2-已超时，3-归还待确认
  operations?: string[]; // ["detail", "confirmReturn"]
}

/** 当前归还列表响应数据 */
export interface PageDTOCurrentReturnDTO {
  pageInfo?: PageInfoDTO;
  records?: CurrentReturnDTO[];
}

/** 当前借阅列表项类型（补充bookId定义） */
export interface CurrentBorrowDTO {
  id?: number; // 借阅记录ID
  bookId?: string; // 书籍ID - 关键补充
  bookName?: string;
  bookCover?: string;
  author?: string; // 修正字段名，与接口一致
  categoryCode?: string;
  remainingDays?: number; // 修正字段名，与接口一致
  latestReturnTime?: string;
  renewableDays?: number;
  renewCount?: number; // 新增续借次数
  borrowStatus?: number; // 借阅状态
  operations?: string[]; // ["renew", "return"]
}

/** 当前借阅列表响应数据 */
export interface PageDTOCurrentBorrowDTO {
  pageInfo?: PageInfoDTO;
  records?: CurrentBorrowDTO[];
}

/** 剩余可续借天数信息 */
export interface RenewDaysInfo {
  borrowId?: number;
  bookId?: number;
  bookName?: string;
  userId?: number;
  maxRenewDays?: number;
  alreadyRenewedDays?: number;
  remainingRenewDays?: number;
  canRenew?: boolean;
  reason?: string;
  suggestedRenewDays?: number;
}

/** 批量操作参数（ID列表） */
export interface BatchIdsParam {
  ids: number[];
}
export type ReturnBooksParams = BatchIdsParam; // 兼容原代码的ReturnBooksParams

/** 获取当前归还列表请求参数 */
export interface GetCurrentReturnListParams {
  currentPage?: number;
  pageSize?: number;
  keyword?: string;
  categoryCode?: string;
}

/** 获取当前借阅列表请求参数 */
export interface GetCurrentBorrowListParams {
  currentPage?: number;
  pageSize?: number;
  keyword?: string;
  categoryCode?: string;
}