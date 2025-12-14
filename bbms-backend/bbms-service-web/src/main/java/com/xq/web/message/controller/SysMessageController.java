package com.xq.web.message.controller;

import com.xq.dto.PageDTO;
import com.xq.web.message.dto.MessageQueryParam;
import com.xq.web.message.dto.SysMessageDTO;
import com.xq.web.message.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息通知管理
 * @module 消息通知
 */
@RestController
@RequestMapping("/message")
public class SysMessageController {
    @Autowired
    private SysMessageService sysMessageService;

    /**
     * 获取消息通知列表（分页）
     * 获取当前用户的消息通知列表，支持条件查询和分页
     * @param param 查询参数，包含分页信息和筛选条件
     * @return 消息分页DTO
     */
    @GetMapping("/list")
    public PageDTO<SysMessageDTO> list(MessageQueryParam param) {
        return sysMessageService.getMessageList(param);
    }

    /**
     * 一键已读（批量操作）
     * 批量将当前用户所有未读消息设为已读
     */
    @PutMapping("/markAllRead")
    public void markAllRead() {
        Long userId = com.xq.common.context.UserContext.getUserId();
        sysMessageService.markAllRead(userId);
    }
}
