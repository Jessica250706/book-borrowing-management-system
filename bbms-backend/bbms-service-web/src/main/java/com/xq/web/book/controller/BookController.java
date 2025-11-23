package com.xq.web.book.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.common.annotation.RequireAdmin;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.book.dto.BookDetailDTO;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.entity.BookQueryParam;
import com.xq.web.book.entity.BorrowRequest;
import com.xq.web.book.service.BookInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 图书管理
 * 提供图书的增删改查、借阅、预约等功能的API接口
 * @module 图书管理系统
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookInfoService bookInfoService;

    /**
     * 新书推荐
     * 获取推荐新书列表，按上架时间倒序排列
     *
     * @param currentPage 当前页码，从1开始，默认为1
     * @param pageSize 每页显示数量，默认为10，最大不超过100
     * @return 推荐新书列表，包含分页信息
     */
    @GetMapping("/new")
    public ResultVo<IPage<BookInfo>> getNewBooks(
            @RequestParam(defaultValue = "1") Long currentPage,
            @RequestParam(defaultValue = "10") Long pageSize) {
        IPage<BookInfo> newBooks = bookInfoService.getNewBooks(currentPage, pageSize);
        return ResultUtils.success("获取新书推荐成功", newBooks);
    }

    /**
     * 获取书籍列表
     * 支持按条件查询和分页显示书籍信息
     *
     * @param param 查询参数，包含书籍名称、作者、分类、状态等筛选条件和分页信息
     * @return 书籍列表，包含分页信息和筛选后的书籍数据
     */
    @GetMapping("/list")
    public ResultVo<IPage<BookInfo>> getBookList(BookQueryParam param) {
        IPage<BookInfo> bookList = bookInfoService.getBookList(param);
        return ResultUtils.success("查询成功", bookList);
    }

    /**
     * 获取书籍详情
     * 根据书籍ID获取详细的书籍信息，包括基本信息、借阅状态等
     *
     * @param bookId 书籍ID，必填
     * @return 书籍详细信息，包含所有字段数据
     */
    @GetMapping("/{bookId}")
    public ResultVo<BookDetailDTO> getBookDetail(@PathVariable Long bookId) {
        BookInfo book = bookInfoService.getById(bookId);
        if (book == null) {
            return ResultUtils.errorMsg("图书不存在");
        }
        
        // 使用Service层转换DTO
        BookDetailDTO dto = bookInfoService.convertToDetailDTO(book);
        return ResultUtils.success("查询成功", dto);
    }

    /**
     * 创建书籍
     * 添加新的书籍信息到系统中，需要管理员权限
     *
     * @param book 书籍信息对象，包含书名、作者、分类、总数等基本信息
     * @return 创建成功的书籍信息，包含系统生成的ID
     */
    @PostMapping
    @RequireAdmin
    public ResultVo<BookInfo> createBook(@RequestBody BookInfo book) {
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
            return ResultUtils.success("创建书籍成功!", book);
        }
        return ResultUtils.errorMsg("创建书籍失败!");
    }

    /**
     * 修改书籍信息
     * 更新已存在书籍的基本信息，需要管理员权限
     *
     * @param bookId 书籍ID，必填
     * @param book 书籍信息对象，包含需要更新的字段
     * @return 更新后的书籍信息
     */
    @PutMapping("/{bookId}")
    @RequireAdmin
    public ResultVo<BookInfo> updateBook(@PathVariable Long bookId, @RequestBody BookInfo book) {
        book.setBookId(bookId);
        book.setUpdateTime(new Date());
        boolean update = bookInfoService.updateById(book);
        if (update) {
            return ResultUtils.success("修改书籍成功!", book);
        }
        return ResultUtils.errorMsg("修改书籍失败!");
    }

    /**
     * 删除书籍
     * 从系统中删除指定的书籍，需要管理员权限
     *
     * @param bookId 书籍ID，必填
     * @return 操作结果信息
     */
    @DeleteMapping("/{bookId}")
    @RequireAdmin
    public ResultVo<Void> deleteBook(@PathVariable Long bookId) {
        boolean remove = bookInfoService.removeById(bookId);
        if (remove) {
            return ResultUtils.successMsg("删除书籍成功!");
        }
        return ResultUtils.errorMsg("删除书籍失败!");
    }

    /**
     * 借阅书籍
     * 用户借阅指定的书籍，需要检查书籍可借数量和用户借阅权限
     *
     * @param bookId 书籍ID，必填
     * @param request 借阅请求参数，包含用户ID和借阅天数
     * @return 借阅结果信息，包含借阅记录详情
     */
    @PostMapping("/{bookId}/borrow")
    public ResultVo<Void> borrowBook(@PathVariable Long bookId, @RequestBody BorrowRequest request) {
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
     * 当书籍已借完时，用户可以预约该书籍，书籍归还后会收到通知
     *
     * @param bookId 书籍ID，必填
     * @param request 预约请求参数，包含用户ID
     * @return 预约结果信息
     */
    @PostMapping("/{bookId}/reserve")
    public ResultVo<Void> reserveBook(@PathVariable Long bookId, @RequestBody BorrowRequest request) {
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
     * 用户取消之前预约的书籍
     *
     * @param bookId 书籍ID，必填
     * @param request 取消预约请求参数，包含用户ID
     * @return 取消预约结果信息
     */
    @PutMapping("/{bookId}/cancel-reserve")
    public ResultVo<Void> cancelReserve(@PathVariable Long bookId, @RequestBody BorrowRequest request) {
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
     * 将未发布或待上架状态的书籍发布为可借阅状态，需要管理员权限
     *
     * @param bookId 书籍ID，必填
     * @return 发布结果信息
     */
    @PutMapping("/{bookId}/publish")
    @RequireAdmin
    public ResultVo<Void> publishBook(@PathVariable Long bookId) {
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

    /**
     * 下架书籍
     * 将可借阅状态的书籍下架，暂停借阅功能，需要管理员权限
     *
     * @param bookId 书籍ID，必填
     * @return 下架结果信息
     */
    @PutMapping("/{bookId}/unpublish")
    @RequireAdmin
    public ResultVo<Void> unpublishBook(@PathVariable Long bookId) {
        try {
            boolean success = bookInfoService.unpublishBook(bookId);
            if (success) {
                return ResultUtils.successMsg("下架书籍成功!");
            }
            return ResultUtils.errorMsg("下架书籍失败!");
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

}