import request from '@/utils/request';
import type {
    Response,
    Request as GetBooksParams,
    ActionResponse,
    BookListDTO
} from './type';

// 获取书籍列表
export const getBooks = (params: GetBooksParams) => {
    return request<Response>({
        url: '/api/book/list',
        method: 'GET',
        params: {
            currentPage: params.currentPage || 1,
            pageSize: params.pageSize || 12,
            bookName: params.bookName,
            categoryId: params.categoryId,
            bookStatus: params.bookStatus,
            author: params.author
        }
    });
};

// 搜索书籍
export const searchBooks = (params: {
    keyword: string;
    currentPage: number;
    pageSize: number;
}) => {
    return request<Response>({
        url: '/api/book/search',
        method: 'GET',
        params: {
            keyword: params.keyword,
            currentPage: params.currentPage || 1,
            pageSize: params.pageSize || 12
        }
    });
};

// 获取书籍详情
export const getBookDetail = (bookId: number) => {
    return request<Response>({
        url: `/api/book/${bookId}`,
        method: 'GET'
    });
};

// 借阅书籍
export const borrowBook = (data: {
    bookId: number;
    borrowDays: number;
}) => {
    return request<ActionResponse>({
        url: '/api/book/borrow',
        method: 'POST',
        data
    });
};

// 预约书籍
export const reserveBook = (bookId: number) => {
    return request<ActionResponse>({
        url: `/api/book/reserve/${bookId}`,
        method: 'POST'
    });
};

// 取消预约
export const cancelReserve = (bookId: number) => {
    return request<ActionResponse>({
        url: `/api/book/reserve/${bookId}`,
        method: 'DELETE'
    });
};

// 发布书籍
export const publishBook = (bookId: number) => {
    return request<ActionResponse>({
        url: `/api/book/${bookId}/publish`,
        method: 'PUT'
    });
};

// 删除书籍
export const deleteBook = (bookId: number) => {
    return request<ActionResponse>({
        url: `/api/book/${bookId}`,
        method: 'DELETE'
    });
};

// 创建书籍
export const createBook = (data: BookListDTO) => {
    return request<ActionResponse>({
        url: '/api/book',
        method: 'POST',
        data
    });
};

// 更新书籍
export const updateBook = (bookId: number, data: BookListDTO) => {
    return request<ActionResponse>({
        url: `/api/book/${bookId}`,
        method: 'PUT',
        data
    });
};