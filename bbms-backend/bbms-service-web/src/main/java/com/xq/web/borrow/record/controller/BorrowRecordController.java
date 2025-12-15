package com.xq.web.borrow.record.controller;

import com.xq.common.annotation.RequireAdmin;
import com.xq.common.context.UserContext;
import com.xq.dto.PageDTO;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.borrow.record.dto.*;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;
import com.xq.web.borrow.record.service.BookBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 借阅管理
 * @module 图书借阅
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowRecordController {

    @Autowired
    private BookBorrowService borrowService;

    /**
     * 获取当前借阅书籍列表（条件+分页）
     * 读者端获取当前用户的借阅列表，支持条件查询和分页
     *
     * @param param 查询参数，包含分页信息和筛选条件
     * @return 当前借阅列表
     */
    @GetMapping("/current/list")
    public ResultVo<PageDTO<CurrentBorrowDTO>> getCurrentBorrowList(
            CurrentBorrowQueryParam param) {

        // 自动从UserContext获取用户ID，但不设置到param中
        Long userId = UserContext.getUserId();

        PageDTO<CurrentBorrowDTO> result = borrowService.getCurrentBorrowList(param, userId);
        return ResultUtils.success("查询成功", result);
    }

    /**
     * 获取当前归还书籍列表（条件+分页）
     * 管理员端获取当前所有归还但尚未进行二次确认的书籍列表，支持条件查询和分页
     *
     * @param param 查询参数，包含分页信息和筛选条件
     * @return 当前借阅列表
     */
    @GetMapping("/return/current/list")
    @RequireAdmin
    public ResultVo<PageDTO<CurrentReturnDTO>> getCurrentReturnList(
            CurrentReturnQueryParam param) {
        // 自动从UserContext获取用户ID，但不设置到param中
        Long userId = UserContext.getUserId();

        PageDTO<CurrentReturnDTO> result = borrowService.getCurrentReturnList(param, userId);
        return ResultUtils.success("查询成功", result);
    }

    /**
     * 归还书籍（支持批量）
     * 读者端批量归还借阅的书籍
     *
     * @param param 批量操作参数，包含要归还的借阅记录ID列表
     * @return 操作结果
     */
    @PutMapping("/return")
    public ResultVo returnBooks(@RequestBody BatchOperateParam param) {
        boolean success = borrowService.returnBooks(param);
        return success ? ResultUtils.successMsg("归还成功") : ResultUtils.errorMsg("归还失败");
    }

    /**
     * 确认归还（支持批量）
     * 管理员端批量确认读者归还的书籍
     *
     * @param param 批量操作参数，包含要确认的借阅记录ID列表
     * @return 操作结果
     */
    @PutMapping("/confirm-return")
    @RequireAdmin  // 添加管理员权限注解
    public ResultVo confirmReturn(@RequestBody BatchOperateParam param) {
        // 自动从UserContext获取管理员ID
        Long adminId = UserContext.getUserId();
        boolean success = borrowService.confirmReturn(param, adminId.intValue());
        return success ? ResultUtils.successMsg("确认归还成功") : ResultUtils.errorMsg("确认归还失败");
    }

    /**
     * 获取用户借阅统计信息
     * 包括：本月借阅、累计借阅、借阅频率、平均阅读时长
     *
     * @return 借阅统计信息
     */
    @GetMapping("/statistics")
    public ResultVo<UserBorrowStatisticsVO> getBorrowStatistics() {
        Long userId = UserContext.getUserId();
        UserBorrowStatisticsVO statistics = borrowService.getUserBorrowStatistics(userId);
        return ResultUtils.success("查询成功", statistics);
    }

    /**
     * 获取用户借阅最多的五种书籍类别
     * 用于扇形图展示，只显示前五的书籍类别，剩余用"其他"代表
     *
     * @return 书籍类别借阅统计列表
     */
    @GetMapping("/category-statistics")
    public ResultVo<List<CategoryBorrowCountVO>> getCategoryBorrowStatistics() {
        Long userId = UserContext.getUserId();
        List<CategoryBorrowCountVO> categoryStatistics = borrowService.getCategoryBorrowStatistics(userId);
        return ResultUtils.success("查询成功", categoryStatistics);
    }

}
