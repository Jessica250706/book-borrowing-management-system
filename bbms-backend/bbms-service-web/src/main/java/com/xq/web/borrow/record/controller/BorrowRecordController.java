package com.xq.web.borrow.record.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.borrow.record.dto.BorrowRecordListVO;
import com.xq.web.borrow.record.dto.CurrentBorrowListVO;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;
import com.xq.web.borrow.record.service.BookBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 借阅记录管理
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowRecordController {

    @Autowired
    private BookBorrowService borrowService;

    /**
     * 获取当前借阅列表（条件+分页）
     * 读者端
     */
    @GetMapping("/current/list")
    public ResultVo<CurrentBorrowListVO> getCurrentBorrowList(@ModelAttribute CurrentBorrowQueryParam param,
                                                              @RequestAttribute Long userId) {
        param.setUserId(userId); // 从token中获取当前用户ID
        CurrentBorrowListVO result = borrowService.getCurrentBorrowList(param);
        return ResultUtils.success("查询成功", result);
    }

    /**
     * 归还书籍（批量）
     * 读者端
     * @param param 批量操作参数
     * @return
     */
    @PostMapping("/return")
    public ResultVo returnBooks(@RequestBody BatchOperateParam param) {
        boolean success = borrowService.returnBooks(param);
        return success ? ResultUtils.successMsg("归还成功") : ResultUtils.errorMsg("归还失败");
    }

    /**
     * 确认归还（批量）
     * 管理员端
     * @param param 批量操作参数
     * @param adminId 管理员ID
     * @return
     */
    @PostMapping("/confirm-return")
    public ResultVo confirmReturn(@RequestBody BatchOperateParam param, @RequestParam Long adminId) {  // 改为Long类型
        boolean success = borrowService.confirmReturn(param, adminId.intValue());  // 转换为Integer
        return success ? ResultUtils.successMsg("确认成功") : ResultUtils.errorMsg("确认失败");
    }

    /**
     * 获取借阅记录（条件+分页）
     * 读者端：查看自己的借阅记录
     * 管理员端：查看所有借阅记录
     */
    @GetMapping("/record/list")
    public ResultVo<BorrowRecordListVO<?>> getBorrowRecordList(@ModelAttribute BorrowParam param,
                                                               @RequestAttribute Long userId,
                                                               @RequestAttribute String userRole) {  // 从token中获取用户角色
        // 根据用户角色决定查询逻辑
        Object result;
        if ("admin".equals(userRole)) {
            // 管理员端：查询所有记录，返回管理员端DTO
            result = borrowService.getAdminBorrowRecordList(param);
        } else {
            // 读者端：只查询当前用户的记录，返回读者端DTO
            param.setUserId(userId);
            result = borrowService.getUserBorrowRecordList(param);
        }
        return ResultUtils.success("查询成功", result);
    }
}