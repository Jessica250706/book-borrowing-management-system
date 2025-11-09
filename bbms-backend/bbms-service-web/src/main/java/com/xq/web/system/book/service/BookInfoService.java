// BookInfoService.java
package com.xq.web.system.book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.system.book.entity.BookInfo;
import com.xq.web.system.book.entity.BookQueryParam;

public interface BookInfoService extends IService<BookInfo> {

    IPage<BookInfo> getBookList(BookQueryParam param);

    IPage<BookInfo> getNewBooks(Long currentPage, Long pageSize);

    boolean borrowBook(Integer bookId, Integer userId, Integer borrowDays);

    boolean reserveBook(Integer bookId, Integer userId);

    boolean cancelReserve(Integer bookId, Integer userId);

    boolean publishBook(Integer bookId);
}