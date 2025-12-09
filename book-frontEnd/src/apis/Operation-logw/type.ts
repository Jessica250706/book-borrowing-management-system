import type { PageInfoDTO } from '../Commonw/type';

/** 操作日志项 */
export interface OperationLogDTO {
  logId?: number;
  userId?: number;
  userName?: string;
  uid?: string;
  bookId?: number;
  bookName?: string;
  categoryName?: string;
  coverUrl?: string;
  operationType?: number; // 1-预约，2-取消预约，3-借阅，4-续借，5-归还
  operationTypeText?: string;
  operationTypeColor?: string;
  operationDesc?: string;
  operationTime?: string;
  createTime?: string;
  reservationOperation?: boolean;
  cancelReservationOperation?: boolean;
  borrowOperation?: boolean;
  renewOperation?: boolean;
  returnOperation?: boolean;
}

/** 操作日志列表响应 */
export interface OperationLogResponse {
  pageInfo?: PageInfoDTO;
  records?: OperationLogDTO[];
}