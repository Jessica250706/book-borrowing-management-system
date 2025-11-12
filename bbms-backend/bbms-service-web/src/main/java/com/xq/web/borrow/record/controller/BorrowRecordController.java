package com.xq.web.borrow.record.controller;

import com.xq.common.annotation.RequireAdmin;
import com.xq.common.context.UserContext;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.borrow.record.dto.CurrentBorrowListVO;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;
import com.xq.web.borrow.record.service.BookBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 借阅记录管理
 * @module 图书借阅
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowRecordController {

    @Autowired
    private BookBorrowService borrowService;

    /**
     * 获取当前借阅列表
     * 读者端获取当前用户的借阅列表，支持条件查询和分页
     *
     * @param param 查询参数，包含分页信息和筛选条件
     * @return 当前借阅列表
     */
    @GetMapping("/current/list")
    public ResultVo<CurrentBorrowListVO> getCurrentBorrowList(
            @RequestParam CurrentBorrowQueryParam param) {

        // 自动从UserContext获取用户ID，但不设置到param中
        Long userId = UserContext.getUserId();

        // 直接传递给Service，不在param中传递
        CurrentBorrowListVO result = borrowService.getCurrentBorrowList(param, userId);
        return ResultUtils.success("查询成功", result);
    }

    /**
     * 归还书籍
     * 读者端批量归还借阅的书籍
     *
     * @param param 批量操作参数，包含要归还的借阅记录ID列表
     * @return 操作结果
     */
    @PostMapping("/return")
    public ResultVo returnBooks(@RequestBody BatchOperateParam param) {
        boolean success = borrowService.returnBooks(param);
        return success ? ResultUtils.successMsg("归还成功") : ResultUtils.errorMsg("归还失败");
    }

    /**
     * 确认归还
     * 管理员端批量确认读者归还的书籍
     *
     * @param param 批量操作参数，包含要确认的借阅记录ID列表
     * @return 操作结果
     */
    @PostMapping("/confirm-return")
    @RequireAdmin  // 添加管理员权限注解
    public ResultVo confirmReturn(@RequestBody BatchOperateParam param) {
        // 自动从UserContext获取管理员ID
        Long adminId = UserContext.getUserId();
        boolean success = borrowService.confirmReturn(param, adminId.intValue());
        return success ? ResultUtils.successMsg("确认成功") : ResultUtils.errorMsg("确认失败");
    }

    /**
     * 获取借阅记录列表
     * 根据用户角色返回不同的借阅记录：读者端查看自己的记录，管理员端查看所有记录
     *
     * @param param 查询参数，包含分页和筛选条件
     * @return 借阅记录列表
     */
    @GetMapping("/record/list")
    public ResultVo<?> getBorrowRecordList(
            @RequestParam BorrowParam param) {
        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();

        Object result;
        // 根据用户角色决定查询逻辑
        if (UserContext.getIsAdmin()) {
            // 管理员：查询所有记录
            result = borrowService.getAdminBorrowRecordList(param);
        } else {
            // 读者：只查询当前用户的记录
            result = borrowService.getUserBorrowRecordList(param, userId);
        }
        return ResultUtils.success("查询成功", result);
    }
}
