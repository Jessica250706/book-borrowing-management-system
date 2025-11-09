package com.xq.web.borrow.record.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
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
     * 读者端：获取当前借阅列表（条件+分页）
     */
    @GetMapping("/current/list")
    public ResultVo getCurrentBorrowList(@ModelAttribute CurrentBorrowQueryParam param,
                                         @RequestAttribute Long userId) {  // 改为Long类型
        param.setUserId(userId); // 从token中获取当前用户ID
        IPage<BookBorrow> list = borrowService.getCurrentBorrowList(param);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 读者端：归还书籍（批量）
     * @param param 批量操作参数
     * @return
     */
    @PostMapping("/return")
    public ResultVo returnBooks(@RequestBody BatchOperateParam param) {
        boolean success = borrowService.returnBooks(param);
        return success ? ResultUtils.success("归还成功") : ResultUtils.error("归还失败");
    }

    /**
     * 管理员端：确认归还（批量）
     * @param param 批量操作参数
     * @param adminId 管理员ID
     * @return
     */
    @PostMapping("/confirm-return")
    public ResultVo confirmReturn(@RequestBody BatchOperateParam param, @RequestParam Long adminId) {  // 改为Long类型
        boolean success = borrowService.confirmReturn(param, adminId.intValue());  // 转换为Integer
        return success ? ResultUtils.success("确认成功") : ResultUtils.error("确认失败");
    }

    /**
     * 获取借阅记录（条件+分页）
     * 读者端：查看自己的借阅记录
     * 管理员端：查看所有借阅记录
     * @param param 查询参数
     * @return
     */
    @GetMapping("/record/list")
    public ResultVo getBorrowRecordList(@ModelAttribute BorrowParam param,
                                        @RequestAttribute Long userId) {  // 改为Long类型
        // 如果是读者端，设置用户ID；管理员端不设置用户ID，查询所有记录
        // 这里需要根据用户角色来判断，暂时先设置为当前用户ID
        param.setUserId(userId);
        IPage<BookBorrow> list = borrowService.getBorrowRecordList(param);
        return ResultUtils.success("查询成功", list);
    }
}
