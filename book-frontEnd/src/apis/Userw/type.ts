import type { PageInfoDTO } from '../Commonw/type';

/** 当前用户信息 */
export interface CurrentUserDTO {
  userId?: number;
  username?: string;
  account?: string;
  uid?: string;
  roleCode?: string;
  roleName?: string;
  creditScore?: number;
  token?: string;
  avatar?: string;
}

/** 用户列表项 */
export interface UserItemDTO {
  userId?: number;
  username?: string;
  account?: string;
  uid?: string;
  avatar?: string;
  registerTime?: string;
  lastLoginTime?: string;
  accountStatus?: AccountStatusDTO;
  roleInfo?: RoleInfoDTO;
  creditInfo?: CreditInfoDTO;
}

/** 用户列表响应 */
export interface UserListResponse {
  pageInfo?: PageInfoDTO;
  records?: UserItemDTO[];
}

/** 账号状态 */
export interface AccountStatusDTO {
  code?: number;
  desc?: string;
}

/** 信用信息 */
export interface CreditInfoDTO {
  level?: string;
  score?: number;
  show?: boolean;
}

/** 角色信息 */
export interface RoleInfoDTO {
  roleCode?: string;
  roleId?: number;
  roleName?: string;
}