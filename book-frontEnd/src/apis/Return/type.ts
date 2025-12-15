/** 归还相关接口类型定义 */

// 补充页面中用到的 ReturnBookDTO 类型
export interface ReturnBookDTO {
  /** 借阅记录ID */
  id?: number;
  /** 书籍ID */
  bookId?: number;
  /** 书籍名称 */
  bookName?: string;
  /** 书籍封面 */
  bookCover?: string;
  /** 作者 */
  author?: string;
  /** 分类编码 */
  categoryCode?: string;
  /** 借阅状态：0-借阅中，1-已归还，2-已超时，3-归还待确认 */
  borrowStatus?: number;
  /** 借阅时间 */
  borrowTime?: string;
  /** 最晚归还时间 */
  latestReturnTime?: string;
  /** 剩余天数 */
  remainDays?: number;
  /** 用户ID */
  userId?: number;
  /** 用户名 */
  userName?: string;
  /** 用户真实姓名 */
  realName?: string;
  /** 用户头像 */
  avatarUrl?: string;
  /** 用户信息 */
  user?: {
    username?: string;
    realName?: string;
    avatarUrl?: string;
  };
}

// 🔥 补充页面中用到的 GetReturnBookListParams 类型（和 GetReturnListParams 保持一致）
export interface GetReturnBookListParams {
  /** 当前页码，默认1 */
  currentPage?: number;
  /** 每页大小，默认20 */
  pageSize?: number;
  /** 搜索关键词（书名/作者） */
  keyword?: string;
  /** 书籍分类编码 */
  categoryCode?: string;
}

// 批量归还书请求参数
export interface ReturnBooksParams {
  /** 借阅ID列表（Long类型） */
  ids: number[];
}

// 确认归还书请求参数（与批量归还书参数结构一致）
export type ConfirmReturnParams = ReturnBooksParams;

// 归还操作响应结果
export interface ReturnResponse {
  /** 状态码 */
  code?: number;
  /** 数据对象 */
  data?: { [key: string]: any };
  /** 提示消息 */
  message?: string;
}

// 分页信息
export interface PageInfoDTO {
  /** 当前页码 */
  currentPage?: number;
  /** 每页大小 */
  pageSize?: number;
  /** 总记录数 */
  total?: number;
  /** 总页数 */
  totalPages?: number;
}

// 书籍分类信息
export interface BookCategoryDTO {
  /** 分类编码 */
  categoryCode?: string;
  /** 分类ID */
  categoryId?: number;
  /** 分类名称 */
  categoryName?: string;
  /** 排序序号 */
  orderNum?: number;
  /** 父分类id */
  parentId?: number;
}

// 书籍信息
export interface BookInfoDTO {
  /** 作者 */
  author?: string;
  /** 书籍ID */
  bookId?: number;
  /** 书籍名称 */
  bookName?: string;
  /** 书籍封面URL */
  coverUrl?: string;
}

// 用户信息
export interface UserInfo {
  /** 用户头像URL */
  avatar?: string;
  /** 用户UID */
  uid?: string;
  /** 用户ID */
  userId?: number;
  /** 用户昵称 */
  userName?: string;
}

// 当前归还列表项
export interface CurrentReturnDTO {
  /** 实际归还时间 */
  actualReturnTime?: string;
  /** 分类信息 */
  bookCategory?: BookCategoryDTO;
  /** 书籍信息 */
  bookInfo?: BookInfoDTO;
  /** 借阅记录ID */
  borrowId?: number;
  /** 借阅状态：0-借阅中，1-已归还，2-已超时，3-归还待确认 */
  borrowStatus?: number;
  /** 借阅时间 */
  borrowTime?: string;
  /** 预计归还时间 */
  expectedReturnTime?: string;
  /** 可进行的操作 */
  operations?: string[];
  /** 续借次数 */
  renewCount?: number;
  /** 读者申请归还时间 */
  returnApplyTime?: string;
  /** 归还确认状态：0-待确认，1-已确认 */
  returnConfirmStatus?: number;
  /** 用户信息 */
  userInfo?: UserInfo;
}

// 归还列表分页响应
export interface ReturnListResponse {
  code?: number;
  data?: {
    /** 分页信息 */
    pageInfo?: PageInfoDTO;
    /** 数据列表 */
    records?: CurrentReturnDTO[] | ReturnBookDTO[];
  };
  message?: string;
}