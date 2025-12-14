package com.xq.web.book.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.common.annotation.RequireAdmin;
import org.springframework.http.ResponseEntity;
import com.xq.common.context.UserContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.book.dto.BookDetailDTO;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.entity.BookQueryParam;
import com.xq.web.book.dto.ReserveResultDTO;
import com.xq.web.book.dto.BorrowResultDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.xq.web.book.service.BookInfoService;
import com.xq.web.book.dto.BookInfoDTO;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * 图书管理
 * 提供图书的增删改查、借阅、预约等功能的API接口
 * @module 图书管理系统
 */
@RestController
@RequestMapping("/api/book")
@Tag(name = "图书管理", description = "图书管理相关接口")
public class BookController {

    private final BookInfoService bookInfoService;

    @Autowired
    public BookController(BookInfoService bookInfoService) {
        this.bookInfoService = bookInfoService;
    }

    /**
     * 新书推荐
     * 获取推荐新书列表，按上架时间倒序排列
     * 基础过滤：状态2全部显示，状态3、4仅显示近30天内上架的书籍
     *
     * @param keyword 关键词搜索（同时搜索书籍名称和作者）
     * @param categoryName 分类名称（模糊查询）
     * @param bookStatus 书籍状态（2待上架全部显示，3或4仅显示30天内）
     * @param currentPage 当前页码，从1开始，默认为1
     * @param pageSize 每页显示数量，默认为10，最大不超过100
     * @return 推荐新书列表，含有分页信息
     */
    @Tag(name = "获取书籍", description = "书籍查询相关接口")
    @GetMapping("/new")
    public ResultVo<IPage<BookInfo>> getNewBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Integer bookStatus,
            @RequestParam(defaultValue = "1") Long currentPage,
            @RequestParam(defaultValue = "10") Long pageSize) {
        BookQueryParam param = new BookQueryParam();
        param.setKeyword(keyword);
        param.setCategoryName(categoryName);
        param.setBookStatus(bookStatus);
        param.setCurrentPage(currentPage);
        param.setPageSize(pageSize);
        
        IPage<BookInfo> newBooks = bookInfoService.getNewBooks(param);
        return ResultUtils.success("获取新书推荐成功", newBooks);
    }

    /**
     * 获取书籍列表
     * 支持按条件查询和分页显示书籍信息，返回结果含有分类名称
     *
     * @param param 查询参数，含有以下可选筛选条件：
     *             - keyword: 关键词搜索（同时搜索书籍名称和作者）
     *             - bookStatus: 书籍状态（0草稿，1未发布，2待上架，3可借阅，4已借光）
     *             - categoryName: 分类名称（模糊查询）
     *             - currentPage: 当前页码，默认为1
     *             - pageSize: 每页显示数量，默认为10
     * @return 书籍列表，含有分页信息和筛选后的书籍数据（category 字段为分类名称）
     */
    @Tag(name = "获取书籍", description = "书籍查询相关接口")
    @GetMapping("/list")
    public ResultVo<IPage<BookInfo>> getBookList(BookQueryParam param) {
        IPage<BookInfo> bookList = bookInfoService.getBookList(param);
        return ResultUtils.success("查询成功", bookList);
    }

    /**
     * 获取书籍详情
     * 根据书籍ID获取详细的书籍信息，含括基本信息、借阅状态等
     *
     * @param bookId 书籍ID，必填
     * @return 书籍详细信息，含有所有字段数据
     */
    @Tag(name = "获取书籍", description = "书籍查询相关接口")
    @GetMapping("/{bookId}")
    public ResultVo<BookDetailDTO> getBookDetail(@PathVariable Long bookId) {
        BookInfo book = bookInfoService.getById(bookId);
        // 额外校验：如果不存在或被软删除，视为不可访问
        if (book == null || (book.getDeleted() != null && book.getDeleted().byteValue() == 1)) {
            return ResultUtils.errorMsg("图书不存在或已被删除");
        }

        // 使用Service层转换DTO
        BookDetailDTO dto = bookInfoService.convertToDetailDTO(book);
        return ResultUtils.success("查询成功", dto);
    }

    /**
     * 保存书籍草稿
     * 保存书籍草稿，不校验必填项，需要管理员权限
     *
     * @param book 书籍信息对象，必填项可为空
     * @return 保存成功的书籍信息，bookStatus=0（草稿状态）
     */
    @Tag(name = "增删改", description = "书籍管理相关接口")
    @PostMapping("/draft")
    @RequireAdmin
    public ResultVo<BookInfo> saveDraft(@RequestBody BookInfo book) {
        try {
            book.setBookStatus(0); // 强制设置为草稿状态
            BookInfo created = bookInfoService.createBook(book);
            return ResultUtils.success("保存草稿成功!", created);
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

    /**
     * 创建书籍并保存
     * 完成书籍编辑并保存，校验必填项，需要管理员权限
     *
     * @param book 书籍信息对象，必填项必须填写完整（书籍名称、封面、作者、分类、总数、简介、上架时间）
     * @return 保存成功返回 HTTP 200 + 书籍信息；验证失败返回 HTTP 400 + 错误信息
     */
    @Tag(name = "增删改", description = "书籍管理相关接口")
    @PostMapping
    @RequireAdmin
    public ResponseEntity<?> createBook(@RequestBody @jakarta.validation.Valid BookInfo book) {
        try {
            book.setBookStatus(1); // 强制设置为未发布状态（需校验）
            BookInfo created = bookInfoService.createBook(book);
            return ResponseEntity.ok(ResultUtils.success("保存成功!", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ResultUtils.errorMsg(e.getMessage()));
        }
    }

    /**
     * 修改书籍信息
     * 更新已存在书籍的基本信息，需要管理员权限
     *
     * @param bookId 书籍ID，必填
     * @param book 书籍信息对象，含有需要更新的字段
     * @return 更新后的书籍信息
     */
    @Tag(name = "增删改", description = "书籍管理相关接口")
    @PutMapping("/{bookId}")
    @RequireAdmin
    public ResultVo<BookInfo> updateBook(@PathVariable Long bookId, @RequestBody @jakarta.validation.Valid BookInfo book) {
        try {
            book.setBookId(bookId);
            book.setUpdateTime(new Date());
            BookInfo updated = bookInfoService.updateBookInfo(book);
            return ResultUtils.success("修改书籍成功!", updated);
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

    /**
     * 借阅书籍
     * 用户点击借阅，系统根据用户身份自动设置借阅天数
     * 社会人员：15天，学生：30天，老师：60天
     *
     * @param bookId 书籍ID，必填
     * @return 借阅成功返回 HTTP 200 + 借阅记录详情
     */
    @Tag(name = "借阅预约书籍", description = "书籍借阅预约相关接口")
    @PostMapping("/borrow/{bookId}")
    public ResultVo<BorrowResultDTO> borrowBook(@PathVariable Long bookId) {
        try {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                return ResultUtils.errorMsg("用户未登录");
            }
            //系统根据用户身份自动设置借阅天数，传 null 让 Service 自动决定
            BorrowResultDTO result = bookInfoService.borrowBook(bookId, userId, null);
            return ResultUtils.success("借阅成功!", result);
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

    /**
     * 预约书籍
     * 当书籍无库存时，创建预约记录（HTTP 201 Created）
     * 当书籍有库存时，返回库存信息提示用户借阅（HTTP 200 OK）
     *
     * @param bookId 书籍ID，必填
     * @return HTTP 200: 有库存，返回可用数量
     *         HTTP 201: 预约成功，返回预约详情
     *         HTTP 404: 图书不存在
     */
    @Tag(name = "借阅预约书籍", description = "书籍借阅预约相关接口")
    @PostMapping("/reserve/{bookId}")
    public ResponseEntity<?> reserveBook(@PathVariable Long bookId) {
        try {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResultUtils.errorMsg("用户未登录"));
            }
            
            ReserveResultDTO result = bookInfoService.reserveBook(bookId, userId);
            
            // 有库存：返回 HTTP 200 OK
            if (result.getAvailableCount() != null) {
                return ResponseEntity.ok(
                    ResultUtils.success("图书有库存，请选择借阅", result)
                );
            }
            
            // 无库存预约成功：返回 HTTP 201 Created
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResultUtils.success("预约成功，书籍归还后会通知您", result));
                    
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultUtils.errorMsg(e.getMessage()));
        }
    }

    /**
     * 取消预约书籍
     * 用户取消之前预约的书籍
     *
     * @param bookId 书籍ID，必填
     * @return 取消预约结果信息
     */
    @Tag(name = "借阅预约书籍", description = "书籍借阅预约相关接口")
    @PutMapping("/cancel-reserve/{bookId}")

    public ResultVo<Void> cancelReserve(@PathVariable Long bookId) {
        try {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                return ResultUtils.errorMsg("用户未登录");
            }
            boolean success = bookInfoService.cancelReserve(bookId, userId);
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
     * 将未发布状态（状态1）的书籍发布为待上架状态（状态2），需要管理员权限
     *
     * @param param 批量操作参数
     * @return 发布结果信息
     */
    @Tag(name = "发布下架书籍", description = "书籍发布状态管理接口")
    @PutMapping("/publish")
    @RequireAdmin
    public ResultVo<List<BookInfoDTO>> publishBooks(@RequestBody BatchOperateParam param) {
        try {
            List<BookInfoDTO> books = bookInfoService.publishBooks(param.getIds());
            return ResultUtils.success("发布书籍成功!", books);
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

    /**
     * 上架书籍
     * 将待上架状态（状态2）的书籍上架为可借阅状态（状态3），需要管理员权限
     *
     * @param param 批量操作参数
     * @return 上架结果信息
     */
    @Tag(name = "发布下架书籍", description = "书籍发布状态管理接口")
    @PutMapping("/shelve")
    @RequireAdmin
    public ResultVo<List<BookInfoDTO>> shelveBooks(@RequestBody BatchOperateParam param) {
        try {
            List<BookInfoDTO> books = bookInfoService.shelveBooks(param.getIds());
            return ResultUtils.success("上架书籍成功!", books);
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

    /**
     * 下架书籍
     * 将待上架（状态2）或可借阅（状态3）状态的书籍下架为未发布状态（状态1），需要管理员权限
     *
     * @param param 批量操作参数
     * @return 下架结果信息
     */
    @Tag(name = "发布下架书籍", description = "书籍发布状态管理接口")
    @PutMapping("/unpublish")
    @RequireAdmin
    public ResultVo<List<BookInfoDTO>> unpublishBooks(@RequestBody BatchOperateParam param) {
        try {
            List<BookInfoDTO> books = bookInfoService.unpublishBooks(param.getIds());
            return ResultUtils.success("下架书籍成功!", books);
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        }
    }

}