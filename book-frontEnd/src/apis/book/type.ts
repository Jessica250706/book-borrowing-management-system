/**
 * 获取书籍列表的请求参数类型
 */
export interface Request {
    currentPage?: number;
    pageSize?: number;
    bookName?: string;
    categoryId?: number;
    bookStatus?: number;
    author?: string;
}

/**
 * 书籍列表响应类型
 */
export interface Response {
    code?: number;
    data?: BookListVO;
    message?: string;
    [property: string]: any;
}

/**
 * 书籍列表VO
 */
export interface BookListVO {
    current?: number;
    pageInfo?: PageInfoDTO;
    records?: BookListDTO[];
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
 * 书籍列表项DTO
 */
export interface BookListDTO {
    author?: string;
    availableCount?: number;
    bookId?: number;
    bookName?: string;
    bookStatus?: number;
    borrowCount?: number;
    categoryId?: number;
    categoryName?: string;
    copyrightHolder?: string;
    coverUrl?: string;
    createTime?: string;
    intro?: string;
    isbn?: string;
    isBorrowedByCurrentUser?: boolean;
    isReservedByCurrentUser?: boolean;
    price?: number;
    publishBatch?: string;
    publishCount?: number;
    publishDate?: string;
    publisher?: string;
    publishUnit?: string;
    publishWebsite?: string;
    shelfTime?: string;
    sortWeight?: number;
    totalCount?: number;
    translator?: string;
    updateTime?: string;
    [property: string]: any;
}

/**
 * 操作接口（借阅/预约）响应类型
 */
export interface ActionResponse {
    code: number;
    message?: string;
    data?: any;
}