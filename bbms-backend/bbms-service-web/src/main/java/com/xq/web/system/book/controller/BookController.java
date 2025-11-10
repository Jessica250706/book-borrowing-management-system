package com.xq.web.system.book.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.system.book.entity.BookInfo;
import com.xq.web.system.book.entity.BookQueryParam;
import com.xq.web.system.book.entity.BorrowRequest;
import com.xq.web.system.book.service.BookInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
            return ResultUtils.error("图书不存在");
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
            return ResultUtils.success("创建书籍成功!");
        }
        return ResultUtils.error("创建书籍失败!");
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
            return ResultUtils.success("修改书籍成功!");
        }
        return ResultUtils.error("修改书籍失败!");
    }

    /**
     * 删除书籍
     */
    @DeleteMapping("/{bookId}")
    public ResultVo deleteBook(@PathVariable Integer bookId) {
        boolean remove = bookInfoService.removeById(bookId);
        if (remove) {
            return ResultUtils.success("删除书籍成功!");
        }
        return ResultUtils.error("删除书籍失败!");
    }

    /**
     * 借阅书籍
     */
    @PostMapping("/{bookId}/borrow")  // 改为 POST
    public ResultVo borrowBook(@PathVariable Integer bookId, @RequestBody BorrowRequest request) {
        try {
            boolean success = bookInfoService.borrowBook(bookId, request.getUserId(), request.getBorrowDays());
            if (success) {
                return ResultUtils.success("借阅成功!");
            }
            return ResultUtils.error("借阅失败!");
        } catch (RuntimeException e) {
            return ResultUtils.error(e.getMessage());
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
                return ResultUtils.success("预约成功!");
            }
            return ResultUtils.error("预约失败!");
        } catch (RuntimeException e) {
            return ResultUtils.error(e.getMessage());
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
                return ResultUtils.success("取消预约成功!");
            }
            return ResultUtils.error("取消预约失败!");
        } catch (RuntimeException e) {
            return ResultUtils.error(e.getMessage());
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
                return ResultUtils.success("发布书籍成功!");
            }
            return ResultUtils.error("发布书籍失败!");
        } catch (RuntimeException e) {
            return ResultUtils.error(e.getMessage());
        }
    }
}