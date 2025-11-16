/**
 * 封装返回值数据 - ResultVo
 */
export interface Response<T = any> {
    code?: number | null;
    data?: T | null;
    message?: string | null;
    [property: string]: any;
}

// 书籍基本信息
export interface Book {
    id: number;
    bookName: string;
    author: string;
    bookImg: string;
    translator?: string;
    status: string;
    shelfTime: string;
    category: string;
    description: string;
    publisher?: string;
    isbn?: string;
    totalCount?: number;
    availableCount?: number;
}

// 分页数据
export interface PageData<T> {
    records: T[];
    total: number;
    size: number;
    current: number;
    pages: number;
}

// 获取书籍列表请求参数
export interface GetBooksParams {
    currentPage?: number;    // 当前页码
    pageSize?: number;       // 每页大小
    bookName?: string;       // 书籍名称
    categoryId?: string;     // 分类ID
    bookStatus?: string;     // 书籍状态
    author?: string;         // 作者
}

// 获取书籍列表响应数据
export interface GetBooksResponse extends Response<PageData<Book>> { }

// 借阅书籍请求参数
export interface BorrowBookParams {
    bookId: number;
    userId?: number;
    borrowDays?: number;
}

// 借阅书籍响应数据
export interface BorrowBookResponse extends Response<any> { }

// 书籍详情响应数据
export interface BookDetailResponse extends Response<Book> { }

// 预约书籍响应数据
export interface ReserveBookResponse extends Response<any> { }

// 取消预约响应数据
export interface CancelReserveResponse extends Response<any> { }