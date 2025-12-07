// src\apis\menu\index.ts
import request from '@/apis/request'
import type { ApiResponse } from '../type'
import type { SysMenu } from './type'

/**
 * 根据用户ID获取菜单列表
 */
export const getMenusByUserId = (userId: number): Promise<ApiResponse<SysMenu[]>> => {
  return request.get(`/api/menu/user/${userId}`)
}

/**
 * 根据用户ID获取菜单树
 */
export const getUserMenuTreeByUserId = (userId: number): Promise<ApiResponse<SysMenu[]>> => {
  return request.get(`/api/menu/user/tree/${userId}`)
}

/**
 * 根据角色ID获取菜单列表
 */
export const getMenusByRoleId = (roleId: number): Promise<ApiResponse<SysMenu[]>> => {
  return request.get(`/api/menu/role/${roleId}`)
}

/**
 * 获取所有菜单
 */
export const getAllMenus = (): Promise<ApiResponse<SysMenu[]>> => {
  return request.get('/api/menu/all')
}

/**
 * 获取当前用户的菜单
 */
export const getCurrentUserMenus = (): Promise<ApiResponse<SysMenu[]>> => {
  return request.get('/api/menu/current')
}
