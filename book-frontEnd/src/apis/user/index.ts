import request from '@/apis/request';
import type {
    Response,
    GetUsersRequest,
    ActionResponse,
    UserListDTO,
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
import { ROLES, USER_STATUS } from './type'; 
/**
 * 用户管理相关API
 */

// 获取用户列表
export const getUsers = (params: GetUsersRequest) => {
    return request<Response>({
        url: '/api/user/list',
        method: 'GET',
        params: {
            currentPage: params.currentPage || 1,
            pageSize: params.pageSize || 10,
            keyword: params.keyword,
            roleId: params.roleId,
            status: params.status
        }
    });
};

// 获取用户详情
export const getUserDetail = (userId: number) => {
    return request<Response>({
        url: `/api/user/${userId}`,
        method: 'GET'
    });
};

// 检查用户是否可以修改角色
export const checkUserRoleChange = (params: CheckRoleChangeRequest) => {
    return request<CheckRoleChangeResponse>({
        url: '/api/user/role/check',
        method: 'GET',
        params
    });
};

// 更新用户角色
export const updateUserRole = (data: UpdateUserRoleRequest) => {
    return request<UpdateUserRoleResponse>({
        url: '/api/user/role',
        method: 'PUT',
        data
    });
};

// 更新用户状态（冻结/解冻/停用等）
export const updateUserStatus = (data: UpdateUserStatusRequest) => {
    return request<UpdateUserStatusResponse>({
        url: '/api/user/status',
        method: 'PUT',
        data
    });
};

// 创建用户
export const createUser = (data: CreateUserRequest) => {
    return request<ActionResponse>({
        url: '/api/user',
        method: 'POST',
        data
    });
};

// 更新用户信息
export const updateUser = (userId: number, data: UpdateUserRequest) => {
    return request<ActionResponse>({
        url: `/api/user/${userId}`,
        method: 'PUT',
        data
    });
};

// 删除用户（逻辑删除）
export const deleteUser = (userId: number) => {
    return request<ActionResponse>({
        url: `/api/user/${userId}`,
        method: 'DELETE'
    });
};

// 重置用户密码
export const resetUserPassword = (userId: number, data: { newPassword?: string }) => {
    return request<ActionResponse>({
        url: `/api/user/${userId}/password`,
        method: 'PUT',
        data: {
            newPassword: data.newPassword || '123456' // 默认密码
        }
    });
};

// 获取角色列表
export const getRoles = () => {
    return request<RoleListResponse>({
        url: '/api/user/roles',
        method: 'GET'
    });
};

// 获取用户借阅统计
export const getUserBorrowStats = (userId: number) => {
    return request<Response>({
        url: `/api/user/${userId}/borrow-stats`,
        method: 'GET'
    });
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
    });
};

// 导出用户列表
export const exportUsers = (params?: GetUsersRequest) => {
    return request({
        url: '/api/user/export',
        method: 'GET',
        params,
        responseType: 'blob' // 注意：导出文件需要使用 blob 类型
    });
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
    });
};

// 模拟数据：获取用户列表（开发环境使用）
export const getMockUsers = (params: GetUsersRequest): Promise<Response> => {
    return new Promise(resolve => {
        setTimeout(() => {
            const mockData: UserListDTO[] = [];

            const total = 50;
            const startIndex = ((params.currentPage || 1) - 1) * (params.pageSize || 10);

            for (let i = 0; i < (params.pageSize || 10); i++) {
                const index = startIndex + i;
                if (index >= total) break;

                const roleId = (index % 5) + 1;
                const roleCode = getRoleCodeById(roleId);
                const roleName = getRoleNameById(roleId);
                const status = index % 4; // 0-3

                mockData.push({
                    userId: index + 1,
                    uid: `UID${String(index + 1).padStart(6, '0')}`,
                    username: `用户${index + 1}`,
                    avatar: `https://picsum.photos/40/40?random=${index}`,
                    email: `user${index + 1}@example.com`,
                    phone: `138${String(Math.floor(Math.random() * 100000000)).padStart(8, '0')}`,
                    roleId: roleId,
                    roleCode: roleCode,
                    roleName: roleName,
                    creditScore: roleId <= ROLES.ID.READER_TEACHER ? Math.floor(Math.random() * 101) : undefined,
                    status: status,
                    statusName: getStatusName(status),
                    registerTime: `2024-${String((index % 12) + 1).padStart(2, '0')}-${String((index % 28) + 1).padStart(2, '0')} ${String(index % 24).padStart(2, '0')}:${String((index * 2) % 60).padStart(2, '0')}:${String((index * 3) % 60).padStart(2, '0')}`,
                    lastLoginTime: `2024-${String((index % 12) + 1).padStart(2, '0')}-${String((index % 28) + 1).padStart(2, '0')} ${String(index % 24).padStart(2, '0')}:${String((index * 2) % 60).padStart(2, '0')}:${String((index * 3) % 60).padStart(2, '0')}`,
                    borrowedCount: roleId <= ROLES.ID.READER_TEACHER ? Math.floor(Math.random() * 10) : 0,
                    unreturnedCount: roleId <= ROLES.ID.READER_TEACHER ? Math.floor(Math.random() * 3) : 0
                });
            }

            resolve({
                code: 200,
                data: {
                    current: params.currentPage || 1,
                    size: params.pageSize || 10,
                    total: total,
                    records: mockData,
                    pageInfo: {
                        currentPage: params.currentPage || 1,
                        pageSize: params.pageSize || 10,
                        total: total,
                        totalPages: Math.ceil(total / (params.pageSize || 10))
                    }
                },
                message: '操作成功'
            });
        }, 300);
    });
};

// 根据角色ID获取角色代码
const getRoleCodeById = (roleId: number): string => {
    switch (roleId) {
        case ROLES.ID.READER_SOCIAL: return ROLES.CODE.READER_SOCIAL;
        case ROLES.ID.READER_STUDENT: return ROLES.CODE.READER_STUDENT;
        case ROLES.ID.READER_TEACHER: return ROLES.CODE.READER_TEACHER;
        case ROLES.ID.ADMIN: return ROLES.CODE.ADMIN;
        case ROLES.ID.SUPER_ADMIN: return ROLES.CODE.SUPER_ADMIN;
        default: return '';
    }
};

// 根据角色ID获取角色名称
const getRoleNameById = (roleId: number): string => {
    switch (roleId) {
        case ROLES.ID.READER_SOCIAL: return ROLES.NAME.READER_SOCIAL;
        case ROLES.ID.READER_STUDENT: return ROLES.NAME.READER_STUDENT;
        case ROLES.ID.READER_TEACHER: return ROLES.NAME.READER_TEACHER;
        case ROLES.ID.ADMIN: return ROLES.NAME.ADMIN;
        case ROLES.ID.SUPER_ADMIN: return ROLES.NAME.SUPER_ADMIN;
        default: return '未知角色';
    }
};

// 根据状态值获取状态名称
const getStatusName = (status: number): string => {
    switch (status) {
        case USER_STATUS.NORMAL: return '正常';
        case USER_STATUS.FROZEN: return '冻结';
        case USER_STATUS.DISABLED: return '停用';
        case USER_STATUS.DELETED: return '注销';
        default: return '未知状态';
    }
};

// 检查是否是读者角色
export const isReaderRole = (roleId: number): boolean => {
    return roleId <= ROLES.ID.READER_TEACHER; // 1,2,3都是读者角色
};

export * from './type';
