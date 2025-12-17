/** 复用通用类型，避免重复定义 */
import type { PageInfoDTO, BaseApiResponse } from '@/apis/Borrowv/type';

/** 1. 获取预约列表请求参数（Query参数，接口文档定义） */
export interface GetCurrentReserveListParams {
  currentPage?: number; // 当前页码，默认1，>=1
  pageSize?: number;    // 每页大小，默认20，>=1
  keyword?: string;     // 搜索关键词（书名/作者）
  categoryCode?: string;// 书籍分类编码（如A、F）
}

/** 2. 预约列表项类型（接口返回的CurrentReservationDTO） */
export interface CurrentReservationDTO {
  reservationId?: number;    // 预约ID
  bookId?: number;           // 书籍ID
  bookName?: string;         // 书名
  coverUrl?: string;         // 封面URL
  author?: string;           // 作者
  translator?: string;       // 译者
  categoryCode?: string;     // 分类编码
  reservationTime?: string;  // 预约时间
  invalidTime?: string;      // 预约失效时间
  reservationStatus?: number;// 预约状态（0-等待中，1-已确认，2-已取消，3-已过期）
  remindStatus?: number;     // 提醒状态（0-未提醒，1-已提醒）
  deleted?: number;          // 书籍是否删除（0-未删除，1-已删除）
}

/** 3. 预约列表响应数据（PageDTO<CurrentReservationDTO>） */
export interface PageDTOCurrentReservationDTO {
  pageInfo?: PageInfoDTO;                  // 分页信息
  records?: CurrentReservationDTO[];       // 预约列表数据
}

/** 4. 取消预约参数（Path参数，接口文档定义） */
export interface CancelReserveParams {
  bookId: number; // 书籍ID，必填
}

/** 5. 取消预约响应类型 */
export interface CancelReserveResponse {
  code?: number;
  data?: null;
  message?: string;
}

/** 6. 预约书籍参数（之前的预约接口，保留适配） */
export interface ReserveBookParams {
  bookId: number; // 书籍ID，必填
}