package com.xq.web.borrow.renew.controller;

import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.renew.service.BookRenewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 续借管理
 */
@RestController
@RequestMapping("/api/borrow/renew")
public class BookRenewController {

    @Autowired
    private BookRenewService renewService;

    /**
     * 续借书籍（批量）
     * @param param 批量操作参数
     * @param userId 用户ID
     * @return
     */
    @PostMapping
    public ResultVo renewBooks(@RequestBody BatchOperateParam param, @RequestParam Long userId) {
        boolean success = renewService.renewBooks(param, userId);
        return success ? ResultUtils.success("续借成功") : ResultUtils.error("续借失败");
    }

    /**
     * 获取剩余可续借天数
     * @param borrowId 借阅ID
     * @return
     */
    @GetMapping("/days")
    public ResultVo getRemainingRenewDays(@RequestParam Long borrowId) {
        Integer remainingDays = renewService.getRemainingRenewDays(borrowId);
        return ResultUtils.success("查询成功", remainingDays);
    }
}