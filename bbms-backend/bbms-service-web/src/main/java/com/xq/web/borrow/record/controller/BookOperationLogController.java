package com.xq.web.borrow.record.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.borrow.record.entity.BookOperationLog;
import com.xq.web.borrow.record.service.BookOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书籍操作日志控制器
 * @module 书籍操作日志
 */
@RestController
@RequestMapping("/api/operation-log")
@Tag(name = "操作日志管理", description = "书籍操作日志查询接口")
public class BookOperationLogController {

    @Autowired
    private BookOperationLogService bookOperationLogService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户操作日志", description = "根据用户ID获取操作日志")
    public ResultVo<List<BookOperationLog>> getUserLogs(@PathVariable Long userId) {
        List<BookOperationLog> logs = bookOperationLogService.getLogsByUserId(userId);
        return ResultUtils.success("查询成功", logs);
    }

    @GetMapping("/user/{userId}/page")
    @Operation(summary = "分页获取用户操作日志", description = "分页查询用户操作日志")
    public ResultVo<IPage<BookOperationLog>> getUserLogsPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BookOperationLog> page = bookOperationLogService.getUserLogsPage(userId, pageNum, pageSize);
        return ResultUtils.success("查询成功", page);
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "获取书籍操作日志", description = "根据书籍ID获取操作日志")
    public ResultVo<List<BookOperationLog>> getBookLogs(@PathVariable Long bookId) {
        List<BookOperationLog> logs = bookOperationLogService.getLogsByBookId(bookId);
        return ResultUtils.success("查询成功", logs);
    }

    @GetMapping("/all/page")
    @Operation(summary = "分页获取所有操作日志", description = "分页查询所有操作日志")
    public ResultVo<IPage<BookOperationLog>> getAllLogsPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BookOperationLog> page = bookOperationLogService.getAllLogsPage(pageNum, pageSize);
        return ResultUtils.success("查询成功", page);
    }
}