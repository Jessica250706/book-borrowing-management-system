package com.xq.web.book.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.entity.BookQueryParam;
import com.xq.web.book.entity.BorrowRequest;
import com.xq.web.book.service.BookInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 图书管理
 * @module 图书管理
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookInfoService bookInfoService;

    /**
     * 新书推荐 - 获取推荐新书列表
     */
    @GetMapping("/new")
    public ResultVo getNewBooks(
            @RequestParam(defaultValue = "1") Long currentPage,
            @RequestParam(defaultValue = "10") Long pageSize) {
        IPage<BookInfo> newBooks = bookInfoService.getNewBooks(currentPage, pageSize);
        return ResultUtils.success("获取新书推荐成功", newBooks);
    }

    /**
     * 获取书籍列表（条件+分页）
     */
    @GetMapping("/list")
    public ResultVo getBookList(BookQueryParam param) {
        IPage<BookInfo> bookList = bookInfoService.getBookList(param);
        return ResultUtils.success("查询成功", bookList);
    }

    /**
     * 书籍详情 - 获取书籍详细数据
     */
    @GetMapping("/{bookId}")
    public ResultVo getBookDetail(@PathVariable Integer bookId) {
        BookInfo book = bookInfoService.getById(bookId);
        if (book == null) {
            return ResultUtils.errorMsg("图书不存在");
        }
        return ResultUtils.success("查询成功", book);
    }

    /**
     * 创建书籍
     */
    @PostMapping
    public ResultVo createBook(@RequestBody BookInfo book) {
        // 设置默认值
        if (book.getBookStatus() == null) {
            book.setBookStatus(0); // 默认未发布
        }
        if (book.getAvailableCount() == null) {
            book.setAvailableCount(book.getTotalCount());
        }
        book.setShelfTime(new Date()); // 设置上架时间
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());

        boolean save = bookInfoService.save(book);
        if (save) {
            return ResultUtils.successMsg("创建书籍成功!");
        }
        return ResultUtils.errorMsg("创建书籍失败!");
    }

    /**
     * 修改书籍
     */
    @PutMapping("/{bookId}")
    public ResultVo updateBook(@PathVariable Integer bookId, @RequestBody BookInfo book) {
        book.setBookId(bookId);
        book.setUpdateTime(new Date());
        boolean update = bookInfoService.updateById(book);
        if (update) {
            return ResultUtils.successMsg("修改书籍成功!");
        }
        return ResultUtils.errorMsg("修改书籍失败!");
    }

    /**
     * 删除书籍
     */
    @DeleteMapping("/{bookId}")
    public ResultVo deleteBook(@PathVariable Integer bookId) {
        boolean remove = bookInfoService.removeById(bookId);
        if (remove) {
            return ResultUtils.successMsg("删除书籍成功!");
        }
        return ResultUtils.errorMsg("删除书籍失败!");
    }

    /**
     * 借阅书籍
     */
    @PostMapping("/{bookId}/borrow")  // 改为 POST
    public ResultVo borrowBook(@PathVariable Integer bookId, @RequestBody BorrowRequest request) {
        try {
            boolean success = bookInfoService.borrowBook(bookId, request.getUserId(), request.getBorrowDays());
            if (success) {
                return ResultUtils.successMsg("借阅成功!");
            }
            return ResultUtils.errorMsg("借阅失败!");
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

    /**
     * 预约书籍
     */
    @PostMapping("/{bookId}/reserve")  // 改为 POST
    public ResultVo reserveBook(@PathVariable Integer bookId, @RequestBody BorrowRequest request) {
        try {
            boolean success = bookInfoService.reserveBook(bookId, request.getUserId());
            if (success) {
                return ResultUtils.successMsg("预约成功!");
            }
            return ResultUtils.errorMsg("预约失败!");
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

    /**
     * 取消预约书籍
     */
    @PutMapping("/{bookId}/cancel-reserve")
    public ResultVo cancelReserve(@PathVariable Integer bookId, @RequestBody BorrowRequest request) {
        try {
            boolean success = bookInfoService.cancelReserve(bookId, request.getUserId());
            if (success) {
                return ResultUtils.successMsg("取消预约成功!");
            }
            return ResultUtils.errorMsg("取消预约失败!");
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

    /**
     * 发布书籍
     */
    @PutMapping("/{bookId}/publish")
    public ResultVo publishBook(@PathVariable Integer bookId) {
        try {
            boolean success = bookInfoService.publishBook(bookId);
            if (success) {
                return ResultUtils.successMsg("发布书籍成功!");
            }
            return ResultUtils.errorMsg("发布书籍失败!");
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }
}