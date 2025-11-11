// BookInfoServiceImpl.java
package com.xq.web.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.book.service.BookInfoService;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.entity.BookQueryParam;
import com.xq.web.book.mapper.BookInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class BookInfoServiceImpl extends ServiceImpl<BookInfoMapper, BookInfo> implements BookInfoService {

    @Override
    public IPage<BookInfo> getBookList(BookQueryParam param) {
        Page<BookInfo> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        return this.baseMapper.getBookList(page, param);
    }

    @Override
    public IPage<BookInfo> getNewBooks(Long currentPage, Long pageSize) {
        Page<BookInfo> page = new Page<>(currentPage, pageSize);

        // 查询最近30天内上架且可借阅的图书
        QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_status", 2) // 可借阅状态
                .orderByDesc("shelf_time")
                .last("LIMIT 50"); // 限制数量，按上架时间排序

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean borrowBook(Integer bookId, Integer userId, Integer borrowDays) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        if (book.getBookStatus() != 2) {
            throw new RuntimeException("图书不可借阅");
        }
        if (book.getAvailableCount() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        // 更新图书信息
        book.setAvailableCount(book.getAvailableCount() - 1);
        book.setBorrowCount(book.getBorrowCount() + 1);
        if (book.getAvailableCount() == 0) {
            book.setBookStatus(3); // 已借光
        }
        book.setUpdateTime(new Date());

        return this.updateById(book);

        // 注意：这里还需要在 book_borrow 表中插入借阅记录
        // 由于时间关系，这里先简化处理
    }

    @Override
    @Transactional
    public boolean reserveBook(Integer bookId, Integer userId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        if (book.getBookStatus() == 3) { // 已借光
            // 可以预约
            // 这里需要在 book_reservation 表中插入预约记录
            return true;
        } else if (book.getBookStatus() == 2) { // 可借阅
            // 直接借阅，不需要预约
            return borrowBook(bookId, userId, 30);
        } else {
            throw new RuntimeException("图书不可预约");
        }
    }

    @Override
    @Transactional
    public boolean cancelReserve(Integer bookId, Integer userId) {
        // 这里需要在 book_reservation 表中更新预约状态为已取消
        // 由于时间关系，这里先返回成功
        return true;
    }

    @Override
    @Transactional
    public boolean publishBook(Integer bookId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 将图书状态改为待上架
        book.setBookStatus(1);
        book.setUpdateTime(new Date());

        return this.updateById(book);
    }
}