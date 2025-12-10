package com.xq.web.borrow.renew.controller;

import com.xq.common.context.UserContext;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.renew.dto.RemainingRenewDaysDTO;
import com.xq.web.borrow.renew.service.BookRenewService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 续借管理
 * @module 图书续借
 */
@RestController
@RequestMapping("/api/borrow/renew")
public class BookRenewController {

    @Autowired
    private BookRenewService renewService;

    /**
     * 续借书籍（批量）
     * @param param 批量操作参数
     * @return 续借结果
     */
    @PutMapping
    public ResultVo renewBooks(@RequestBody BatchOperateParam param) {
        Long userId = UserContext.getUserId();
        boolean success = renewService.renewBooks(param, userId);
        return success ? ResultUtils.successMsg("续借成功") : ResultUtils.errorMsg("续借失败");
    }

    /**
     * 获取剩余可续借天数
     * @param borrowId 借阅ID
     * @return 剩余可续借天数
     */
    @GetMapping("/days/{borrowId}")
    public ResultVo<Integer> getRemainingRenewDays(@PathVariable Long borrowId) {
        try {
            // 计算剩余天数 = 最大可续借天数 - 已续借天数
            Integer remainingDays = renewService.getRemainingRenewDays(borrowId);

            // 如果不可续借，返回0天
            if (remainingDays == null) {
                return ResultUtils.success("查询成功", 0);
            }

            return ResultUtils.success("查询成功", Math.max(0, remainingDays));

        } catch (Exception e) {
            return ResultUtils.error("查询失败", 0);
        }
    }
}
