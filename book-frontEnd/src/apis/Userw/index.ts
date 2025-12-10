import request from '@/apis/request';
import type { BaseResponse } from '../Commonw/type';
import type { CurrentUserDTO, UserListResponse } from './type';

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => {
  return request.get<BaseResponse<CurrentUserDTO>>('/user/current');
};

/**
 * 获取用户列表（管理员）
 * @param params 分页和筛选参数
 */
export const getUserList = (params?: {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
  roleFilter?: string;
  orderBy?: string;
  orderDirection?: string;
}) => {
  return request.get<BaseResponse<UserListResponse>>('/user/list', { params });
};