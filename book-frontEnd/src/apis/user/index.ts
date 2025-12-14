import request from '@/apis/request';
import type {
    Response,
    GetUsersRequest,
    ActionResponse,
    CheckRoleChangeRequest,
    CheckRoleChangeResponse,
    UpdateUserRoleRequest,
    UpdateUserRoleResponse,
    UpdateUserStatusRequest,
    UpdateUserStatusResponse,
    CreateUserRequest,
    UpdateUserRequest,
    RoleListResponse,
} from './type';
import { ROLES } from './type';

/**
 * 用户管理相关API
 */

// 获取用户列表
export const getUsers = (params: GetUsersRequest) => {
    return request<Response>({
        url: '/api/user/list',
        method: 'GET',
        params: {
            currentPage: params.pageNum || 1,
            pageSize: params.pageSize || 10,
            keyword: params.keyword,
            roleFilter: params.roleFilter || 'ALL',
        }
    }).then(res => res.data);
};

// 获取当前用户信息
export const getCurrentUser = () => {
    return request<Response>({
        url: '/api/user/current',
        method: 'GET'
    }).then(res => res.data);
};

// 获取用户详情
export const getUserDetail = (userId: number) => {
    return request<Response>({
        url: `/api/user/${userId}`,
        method: 'GET'
    }).then(res => res.data);
};

// 检查用户是否可以修改角色
export const checkUserRoleChange = (params: CheckRoleChangeRequest) => {
    return request<CheckRoleChangeResponse>({
        url: '/api/user/role/check',
        method: 'GET',
        params
    }).then(res => res.data);
};

// 更新用户角色
export const updateUserRole = (data: UpdateUserRoleRequest) => {
    return request<UpdateUserRoleResponse>({
        url: '/api/user/role',
        method: 'PUT',
        data
    }).then(res => res.data);
};

// 更新用户状态（冻结/解冻/停用等）
export const updateUserStatus = (data: UpdateUserStatusRequest) => {
    return request<UpdateUserStatusResponse>({
        url: '/api/user/status',
        method: 'PUT',
        data
    }).then(res => res.data);
};

// 修改密码
export const updatePassword = (params: { oldPassword: string; newPassword: string }) => {
    return request<Response>({
        url: '/api/user/password',
        method: 'PUT',
        params
    }).then(res => res.data);
};

// 创建用户
export const createUser = (data: CreateUserRequest) => {
    return request<ActionResponse>({
        url: '/api/user',
        method: 'POST',
        data
    }).then(res => res.data);
};

// 更新用户信息
export const updateUser = (userId: number, data: UpdateUserRequest) => {
    return request<ActionResponse>({
        url: `/api/user/${userId}`,
        method: 'PUT',
        data
    }).then(res => res.data);
};

// 删除用户（逻辑删除）
export const deleteUser = (userId: number) => {
    return request<ActionResponse>({
        url: `/api/user/${userId}`,
        method: 'DELETE'
    }).then(res => res.data);
};

// 重置用户密码
export const resetUserPassword = (userId: number, data: { newPassword?: string }) => {
    return request<ActionResponse>({
        url: `/api/user/${userId}/password`,
        method: 'PUT',
        data: {
            newPassword: data.newPassword || '123456' // 默认密码
        }
    }).then(res => res.data);
};

// 获取角色列表
export const getRoles = () => {
    return request<RoleListResponse>({
        url: '/api/user/roles',
        method: 'GET'
    }).then(res => res.data);
};

// 获取用户借阅统计
export const getUserBorrowStats = (userId: number) => {
    return request<Response>({
        url: `/api/user/${userId}/borrow-stats`,
        method: 'GET'
    }).then(res => res.data);
};

// 获取用户借阅记录
export const getUserBorrowRecords = (params: {
    userId: number;
    currentPage?: number;
    pageSize?: number;
    status?: number;
}) => {
    const { userId, currentPage, pageSize, status } = params;
    return request<Response>({
        url: `/api/user/${userId}/borrow-records`,
        method: 'GET',
        params: {
            currentPage: currentPage || 1,
            pageSize: pageSize || 10,
            status
        }
    }).then(res => res.data);
};

// 导出用户列表
export const exportUsers = (params?: GetUsersRequest) => {
    return request({
        url: '/api/user/export',
        method: 'GET',
        params,
        responseType: 'blob' // 注意：导出文件需要使用 blob 类型
    }).then(res => res.data);
};

// 批量操作用户状态
export const batchUpdateUserStatus = (data: {
    userIds: number[];
    status: number;
    remark?: string;
}) => {
    return request<ActionResponse>({
        url: '/api/user/batch-status',
        method: 'PUT',
        data
    }).then(res => res.data);
};

// 根据角色筛选值获取角色ID
export const getRoleIdByFilter = (roleFilter: string): number => {
    switch (roleFilter) {
        case ROLES.FILTER.READER_SOCIAL:
            return ROLES.ID.READER_SOCIAL;
        case ROLES.FILTER.READER_STUDENT:
            return ROLES.ID.READER_STUDENT;
        case ROLES.FILTER.READER_TEACHER:
            return ROLES.ID.READER_TEACHER;
        case ROLES.FILTER.ADMIN:
            return ROLES.ID.ADMIN;
        case ROLES.FILTER.SYS_ADMIN:
            return ROLES.ID.SYS_ADMIN;
        default:
            return 0;
    }
};

// 检查是否是读者角色
export const isReaderRole = (roleId: number): boolean => {
    return roleId <= ROLES.ID.READER_TEACHER; // 1,2,3都是读者角色
};

export * from './type';