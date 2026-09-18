import request from '@/apis/request';
import type {
    Request as GetBooksParams,
    BaseResponse,
    BookListResponse,
    ActionResponse,
    BatchActionResponse,
    BookListDTO,
    DeleteCheckDTO,
    BookDetailDTO
} from './type';

// 获取书籍列表
export const getBooks = (params: GetBooksParams): Promise<BookListResponse> => {
    return request({
        url: '/api/book/list',
        method: 'GET',
        params: {
            currentPage: params.currentPage || 1,
            pageSize: params.pageSize || 12,
            bookName: params.bookName,
            keyword: params.keyword,
            categoryName: params.categoryName,
            bookStatus: params.bookStatus,
            author: params.author
        }
    });
};

// 获取书籍详情
export const getBookDetail = (bookId: number): Promise<BaseResponse<BookDetailDTO>> => {
    return request({
        url: `/api/book/${bookId}`,
        method: 'GET'
    });
};

// 借阅书籍
export const borrowBook = (bookId: number): Promise<ActionResponse> => {
    return request({
        url: `/api/book/borrow/${bookId}`,
        method: 'POST'
    });
};

// 预约书籍
export const reserveBook = (bookId: number): Promise<ActionResponse> => {
    return request({
        url: `/api/book/reserve/${bookId}`,
        method: 'POST'
    });
};

// 取消预约
export const cancelReserve = (bookId: number): Promise<ActionResponse> => {
    return request({
        url: `/api/book/cancel-reserve/${bookId}`,
        method: 'PUT'
    });
};

// 批量发布书籍（用于单个和批量发布）
export const batchPublishBooks = (data: { ids: number[] }): Promise<BatchActionResponse> => {
    return request({
        url: '/api/book/publish',
        method: 'PUT',
        data
    });
};

// 批量上架书籍（用于单个和批量上架）
export const batchShelveBooks = (data: { ids: number[] }): Promise<BatchActionResponse> => {
    return request({
        url: '/api/book/shelve',
        method: 'PUT',
        data
    });
};

// 批量删除检查
export const batchDeleteCheck = (data: { ids: number[] }): Promise<BaseResponse<DeleteCheckDTO[]>> => {
    return request({
        url: '/api/book-delete/check',
        method: 'POST',
        data
    });
};

// 批量删除书籍（用于单个和批量删除）
export const batchDeleteBooks = (data: { ids: number[] }): Promise<BatchActionResponse> => {
    return request({
        url: '/api/book-delete/delete',
        method: 'PUT',
        data
    });
};

// 创建书籍（完成）- 校验必填项
export const createBook = (data: any): Promise<BaseResponse> => {
    return request({
        url: '/api/book',
        method: 'POST',
        data
    });
};

// 保存书籍草稿 - 不校验必填项
export const saveBookDraft = (data: any): Promise<BaseResponse> => {
    return request({
        url: '/api/book/draft',
        method: 'POST',
        data
    });
};

// 更新书籍
export const updateBook = (bookId: number, data: any): Promise<BaseResponse> => {
    return request({
        url: `/api/book/${bookId}`,
        method: 'PUT',
        data
    });
};

// 获取新书推荐
export const getNewBooks = (params?: {
    currentPage?: number;
    pageSize?: number;
    keyword?: string;
    categoryName?: string;
    bookStatus?: number;
}): Promise<BookListResponse> => {
    return request({
        url: '/api/book/new',
        method: 'GET',
        params: {
            currentPage: params?.currentPage || 1,
            pageSize: params?.pageSize || 12,
            keyword: params?.keyword,
            categoryName: params?.categoryName,
            bookStatus: params?.bookStatus
        }
    });
};