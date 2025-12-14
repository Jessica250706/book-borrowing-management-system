import request from '@/apis/request';
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
            keyword: params.keyword,     
            categoryName: params.categoryName,
            bookStatus: params.bookStatus,
            author: params.author
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
export const borrowBook = (bookId: number) => {
    return request<ActionResponse>({
        url: `/api/book/borrow/${bookId}`, 
        method: 'POST'
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
        url: `/api/book/cancel-reserve/${bookId}`, 
        method: 'PUT'
    });
};

// 发布书籍
export const publishBook = (bookId: number) => {
    return request<ActionResponse>({
        url: `/api/book/publish/${bookId}`,
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

// 创建书籍（完成）- 校验必填项
export const createBook = (data: any): Promise<any> => {  
    return request({
        url: '/api/book',
        method: 'POST',
        data
    });
};

// 保存书籍草稿 - 不校验必填项
export const saveBookDraft = (data: any): Promise<any> => {  
    return request({
        url: '/api/book/draft',
        method: 'POST',
        data
    });
};

// 更新书籍
export const updateBook = (bookId: number, data: any): Promise<any> => {  
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
}) => {
    return request<Response>({
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