/**
 * 用户管理相关类型定义
 */

/**
 * 获取用户列表的请求参数类型
 */
export interface GetUsersRequest {
    pageNum?: number;      
    pageSize?: number;
    keyword?: string;
    roleFilter?: string;   
    orderBy?: string;     
    orderDirection?: string; 
}

/**
 * 用户列表响应类型
 */
export interface Response {
    code?: number;
    data?: UserListVO;
    message?: string;
    [property: string]: any;
}

/**
 * 用户列表VO
 */
export interface UserListVO {
    current?: number;
    pageInfo?: PageInfoDTO;
    records?: UserListResponseDTO[];
    size?: number;
    total?: number;
    [property: string]: any;
}

/**
 * 分页信息DTO
 */
export interface PageInfoDTO {
    currentPage?: number;
    pageSize?: number;
    total?: number;
    totalPages?: number;
    [property: string]: any;
}

/**
 * 用户列表项DTO
 */
export interface UserListResponseDTO {
    serialNumber?: number;
    userId?: number;
    avatar?: string;
    username?: string;
    uid?: string;
    roleInfo?: RoleInfoDTO;
    creditInfo?: CreditInfoDTO;
    accountStatus?: AccountStatusDTO;
    registerTime?: string;
    canUpgradeRole?: boolean;
    hasBorrowingBooks?: boolean;
    [property: string]: any;
}

export interface RoleInfoDTO {
    roleId?: number;
    roleCode?: string;
    roleName?: string;
    [property: string]: any;
}

export interface CreditInfoDTO {
    score?: number;
    level?: string;
    show?: boolean;
    [property: string]: any;
}

export interface AccountStatusDTO {
    code?: number;
    desc?: string;
    [property: string]: any;
}

/**
 * 用户详情DTO
 */
export interface UserDetailDTO {
    userId?: number;
    uid?: string;
    username?: string;
    avatar?: string;
    email?: string;
    phone?: string;
    realName?: string;
    idCard?: string;
    gender?: number;
    birthday?: string;
    address?: string;
    school?: string;
    studentId?: string;
    major?: string;
    department?: string;
    title?: string;
    roleId?: number;
    roleCode?: string;
    roleName?: string;
    creditScore?: number;
    creditLevel?: string;
    status?: number;
    statusName?: string;
    registerTime?: string;
    lastLoginTime?: string;
    registerIp?: string;
    lastLoginIp?: string;
    totalBorrowCount?: number;
    currentBorrowCount?: number;
    overdueCount?: number;
    fineAmount?: number;
    [property: string]: any;
}

/**
 * 检查角色变更请求参数
 */
export interface CheckRoleChangeRequest {
    userId: number;
    newRoleId: number;
}

/**
 * 检查角色变更响应
 */
export interface CheckRoleChangeResponse {
    code?: number;
    data?: RoleCheckData;
    message?: string;
    [property: string]: any;
}

export interface RoleCheckData {
    canChange?: boolean;
    hasUnreturnedBooks?: boolean;
    unreturnedCount?: number;
    message?: string;
    [property: string]: any;
}

/**
 * 更新用户角色请求参数
 */
export interface UpdateUserRoleRequest {
    userId: number;
    roleId: number;
    remark?: string;
}

/**
 * 更新用户角色响应
 */
export interface UpdateUserRoleResponse {
    code?: number;
    data?: RoleUpdateData;
    message?: string;
    [property: string]: any;
}

export interface RoleUpdateData {
    userId?: number;
    username?: string;
    uid?: string;
    oldRoleId?: number;
    oldRoleCode?: string;
    oldRoleName?: string;
    newRoleId?: number;
    newRoleCode?: string;
    newRoleName?: string;
    operatorId?: number;
    operatorName?: string;
    operateTime?: string;
    remark?: string;
    [property: string]: any;
}

/**
 * 更新用户状态请求参数
 */
export interface UpdateUserStatusRequest {
    userId: number;
    status: number;
    remark?: string;
}

/**
 * 更新用户状态响应
 */
export interface UpdateUserStatusResponse {
    code?: number;
    data?: StatusUpdateData;
    message?: string;
    [property: string]: any;
}

export interface StatusUpdateData {
    userId?: number;
    username?: string;
    oldStatus?: number;
    newStatus?: number;
    operatorId?: number;
    operatorName?: string;
    operateTime?: string;
    remark?: string;
    [property: string]: any;
}

/**
 * 创建用户请求参数
 */
export interface CreateUserRequest {
    username: string;
    password: string;
    email?: string;
    phone?: string;
    realName?: string;
    roleId: number;
    [property: string]: any;
}

/**
 * 更新用户信息请求参数
 */
export interface UpdateUserRequest {
    username?: string;
    email?: string;
    phone?: string;
    realName?: string;
    gender?: number;
    birthday?: string;
    address?: string;
    school?: string;
    studentId?: string;
    major?: string;
    department?: string;
    title?: string;
    [property: string]: any;
}

/**
 * 操作接口响应类型
 */
export interface ActionResponse {
    code: number;
    message?: string;
    data?: any;
}

/**
 * 角色列表项
 */
export interface RoleItem {
    roleId: number;
    roleCode: string;
    roleName: string;
    description?: string;
    [property: string]: any;
}

/**
 * 角色列表响应
 */
export interface RoleListResponse {
    code?: number;
    data?: RoleItem[];
    message?: string;
    [property: string]: any;
}

/**
 * 用户状态常量
 */
export const USER_STATUS = {
    NORMAL: 0,      // 正常
    FROZEN: 1,      // 冻结
    DISABLED: 2,    // 停用
    DELETED: 3      // 注销
} as const;

/**
 * 角色常量
 */
export const ROLES = {
    // 角色ID映射
    ID: {
        READER_SOCIAL: 1,
        READER_STUDENT: 2,
        READER_TEACHER: 3,
        ADMIN: 4,
        SUPER_ADMIN: 5
    },
    // 角色代码
    CODE: {
        READER_SOCIAL: 'READER_SOCIAL',
        READER_STUDENT: 'READER_STUDENT',
        READER_TEACHER: 'READER_TEACHER',
        ADMIN: 'ADMIN',
        SUPER_ADMIN: 'SUPER_ADMIN'
    },
    // 角色名称
    NAME: {
        READER_SOCIAL: '社会人员',
        READER_STUDENT: '学生',
        READER_TEACHER: '老师',
        ADMIN: '管理员',
        SUPER_ADMIN: '系统管理员'
    },
    // 角色筛选值（对应接口文档的 roleFilter）
    FILTER: {
        ALL: 'ALL',
        READER_SOCIAL: 'READER_SOCIAL',
        READER_STUDENT: 'READER_STUDENT',
        READER_TEACHER: 'READER_TEACHER',
        ADMIN: 'ADMIN',
        SUPER_ADMIN: 'SUPER_ADMIN'
    }
} as const;

/**
 * 状态名称映射
 */
export const STATUS_NAME: Record<number, string> = {
    [USER_STATUS.NORMAL]: '正常',
    [USER_STATUS.FROZEN]: '冻结',
    [USER_STATUS.DISABLED]: '停用',
    [USER_STATUS.DELETED]: '注销'
};