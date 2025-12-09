import axios from 'axios';
import type { BaseResponse } from '../Commonw/type';
import type { BookListResponse, BookDetailDTO, BorrowResultDTO } from './type';

const request = axios.create({
  baseURL: 'http://localhost:8089/api',
  timeout: 5000,
});

// 请求拦截器（同之前的配置，实际项目中可抽离到公共配置）
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/**
 * 获取书籍列表
 * @param params 分页和筛选参数
 */
export const getBookList = (params?: {
  currentPage?: number;
  pageSize?: number;
  bookName?: string;
  categoryName?: string;
  bookStatus?: number;
  author?: string;
}) => {
  return request.get<BaseResponse<BookListResponse>>('/book/list', { params });
};

/**
 * 获取书籍详情
 * @param bookId 书籍ID
 */
export const getBookDetail = (bookId: number) => {
  return request.get<BaseResponse<BookDetailDTO>>(`/book/${bookId}`);
};

/**
 * 借阅书籍
 * @param bookId 书籍ID
 */
export const borrowBook = (bookId: string) => {
  return request.post<BaseResponse<BorrowResultDTO>>(`/book/borrow/${bookId}`);
};

/**
 * 预约书籍
 * @param bookId 书籍ID
 */
export const reserveBook = (bookId: string) => {
  return request.post<BaseResponse>(`/book/reserve/${bookId}`);
};