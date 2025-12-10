import request from '@/apis/request';
import type { BaseResponse } from '../Commonw/type';
import type { MenuDTO } from './type';

/**
 * 获取所有菜单
 */
export const getAllMenus = () => {
  return request.get<BaseResponse<MenuDTO[]>>('/menu/all');
};

/**
 * 根据用户ID获取菜单列表
 * @param userId 用户ID
 */
export const getMenusByUserId = (userId: number) => {
  return request.get<BaseResponse<MenuDTO[]>>(`/menu/user/${userId}`);
};