/** 基础响应类型（复用项目统一响应格式） */
export interface BaseApiResponse<T = any> {
  code?: number;
  data?: T;
  message?: string;
}

/** 消息列表项类型 */
export interface MessageItemDTO {
  id: number; // 消息ID
  content: string; // 消息内容
  status: '已读' | '未读'; // 消息状态
  time: string; // 通知时间（格式：YYYY/MM/DD HH:mm:ss）
  type?: string; // 可选：消息类型（如系统通知、借阅提醒等）
  relatedId?: number; // 可选：关联业务ID（如借阅记录ID、书籍ID）
}

/** 消息列表分页请求参数 */
export interface GetMessageListParams {
  currentPage?: number; // 当前页码
  pageSize?: number; // 每页条数
  status?: '已读' | '未读' | 'all'; // 筛选状态（all为全部）
}

/** 消息列表分页响应数据 */
export interface MessagePageDTO {
  pageInfo: {
    currentPage: number;
    pageSize: number;
    total: number; // 总条数
    totalPages: number; // 总页数
  };
  records: MessageItemDTO[]; // 消息列表数据
}

/** 标记已读/删除消息的请求参数 */
export interface MessageOperateParams {
  ids: number[]; // 消息ID数组（支持批量操作）
}