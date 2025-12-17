/**
 * 获取书籍列表的请求参数类型
 */
export interface Request {
    currentPage?: number;
    pageSize?: number;
    bookName?: string;
    keyword?: string;
    categoryName?: string;
    bookStatus?: number;
    author?: string;
}

/**
 * 基础响应类型
 */
export interface BaseResponse<T = any> {
    code: number;
    data?: T;
    message?: string;
    [property: string]: any;
}

/**
 * 书籍列表响应类型
 */
export interface BookListResponse extends BaseResponse<BookListVO> { }

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
 * 书籍详情DTO
 */
export interface BookDetailDTO {
    bookId?: number;
    bookName?: string;
    coverUrl?: string;
    author?: string;
    translator?: string;
    categoryId?: number;
    bookStatus?: number;
    totalCount?: number;
    availableCount?: number;
    shelfTime?: string;
    intro?: string;
    publisher?: string;
    isbn?: string;
    copyrightHolder?: string;
    publishCount?: number;
    publishUnit?: string;
    publishWebsite?: string;
    publishBatch?: string;
    publishDate?: string;
    price?: number;
    borrowCount?: number;
    createTime?: string;
    updateTime?: string;
    categoryName?: string;
    isReservedByCurrentUser?: boolean;
    isBorrowedByCurrentUser?: boolean;
    reserveCount?: number;
    expectedAvailableTime?: string;
    rating?: number;
    commentCount?: number;
    [property: string]: any;
}

/**
 * 操作接口（借阅/预约）响应类型
 */
export interface ActionResponse extends BaseResponse<any> { }

/**
 * 删除检查结果DTO
 */
export interface DeleteCheckDTO {
    bookId?: number;
    bookName?: string;
    hasBorrowRecord?: boolean;
    borrowCount?: number;
    [property: string]: any;
}

/**
 * 批量操作响应类型
 */
export interface BatchActionResponse extends BaseResponse<any> { }