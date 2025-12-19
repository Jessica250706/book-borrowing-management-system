/** 基础响应类型（复用用项目统一响应格式） */
export interface BaseApiResponse<T = any> {
  code?: number; // 后端返回0或200表示成功
  data?: T;
  message?: string;
}

/** 消息列表项类型 - 修复：与后端返回字段一致 */
export interface SysMessageDTO {
  sequence?: number;
  messageId: string; // 改为string，后端返回的是string类型
  userId: string;    // 改为string
  messageType: number;
  messageTitle?: string;
  messageContent: string;
  readStatus: number; // 0或1
  readTime?: string;
  sendTime: string;
  bookId?: string;    // 改为string
  statusText: '已读' | '未读';
}

/** 消息列表分页响应数据 */
export interface MessagePageDTO {
  pageInfo: {
    currentPage: string;  // 后端返回的是string
    pageSize: string;     // 后端返回的是string
    total: string;        // 后端返回的是string
    totalPages: string;   // 后端返回的是string
  };
  records: SysMessageDTO[];
}

/** 消息列表分页请求参数 - 修复：参数名改为pageNum */
export interface GetMessageListParams {
  pageNum?: number; // 页码（后端要求的参数名）
  pageSize?: number; // 每页条数
  status?: '已读' | '未读' | ''; // 筛选状态（空=全部）
  keyword?: string; // 搜索关键词，新增字段
}