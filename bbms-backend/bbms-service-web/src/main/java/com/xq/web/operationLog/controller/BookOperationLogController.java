package com.xq.web.operationLog.controller;

import com.xq.common.context.UserContext;
import com.xq.dto.PageDTO;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.operationLog.dto.BaseBorrowRecordDTO;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.operationLog.service.BookOperationLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 借阅记录
 * @module 书籍操作日志
 */
@RestController
@RequestMapping("/api/operation-log")
@Tag(name = "借阅记录", description = "借阅记录页面接口")
public class BookOperationLogController {

    @Autowired
    private BookOperationLogService bookOperationLogService;

    /**
     * 获取借阅记录（条件+分页）
     * 根据用户角色返回不同的借阅记录：读者端查看自己的记录，管理员端查看所有记录
     *
     * @param param 查询参数，包含分页和筛选条件
     * @return 借阅记录列表
     */
    @GetMapping("/record/list")
    public ResultVo<PageDTO<BaseBorrowRecordDTO>> getBorrowRecordList(
            BorrowParam param) {
        Long userId = UserContext.getUserId();

        PageDTO<BaseBorrowRecordDTO> result;
        // 根据用户角色决定查询逻辑
        if (UserContext.getIsAdmin()) {
            // 管理员：查询所有记录
            result = bookOperationLogService.getAdminBorrowRecordList(param);
        } else {
            // 读者：只查询当前用户的记录
            result = bookOperationLogService.getUserBorrowRecordList(param, userId);
        }
        return ResultUtils.success("查询成功", result);
    }
}