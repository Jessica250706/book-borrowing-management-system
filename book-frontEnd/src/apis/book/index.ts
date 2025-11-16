import request from '@/utils/request';
import type {
    GetBooksParams,
    GetBooksResponse,
    BorrowBookParams,
    BorrowBookResponse,
    BookDetailResponse
} from './type';

// 获取书籍列表
export const getBooks = (params?: GetBooksParams): Promise<GetBooksResponse> => {
    return request({
        url: '/api/book/list',
        method: 'GET',
        params: {
            currentPage: 1,
            pageSize: 12,
            bookStatus: 1, // 假设1表示可借阅
            ...params
        }
    });
};

// 获取书籍详情
export const getBookDetail = (bookId: number): Promise<BookDetailResponse> => {
    return request({
        url: `/api/book/${bookId}`,
        method: 'GET'
    });
};

// 借阅书籍
export const borrowBook = (data: BorrowBookParams): Promise<BorrowBookResponse> => {
    const { bookId, ...bodyData } = data;

    return request({
        url: `/api/book/${bookId}/borrow`,
        method: 'POST',
        data: {
            borrowDays: 30,
            ...bodyData
        }
    });
};

// 预约书籍
export const reserveBook = (bookId: number): Promise<BorrowBookResponse> => {
    return request({
        url: `/api/book/${bookId}/reserve`,
        method: 'POST',
        data: {}
    });
};

// 取消预约
export const cancelReserve = (bookId: number): Promise<BorrowBookResponse> => {
    return request({
        url: `/api/book/${bookId}/cancelReserve`,
        method: 'POST',
        data: {}
    });
};