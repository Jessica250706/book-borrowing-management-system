package com.xq.web.message.controller;

import com.xq.dto.PageDTO;
import com.xq.dto.ResultVo;
import com.xq.web.message.dto.MessageQueryParam;
import com.xq.web.message.dto.SysMessageDTO;
import com.xq.web.message.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 消息通知管理
 * @module 消息通知
 */
@RestController
@RequestMapping("api/message")
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
     * @return 已标记为已读的消息数量
     */
    @PutMapping("/markAllRead")
    public ResultVo<Integer> markAllRead() {
        Long userId = com.xq.common.context.UserContext.getUserId();
        
        // 从 SecurityContextHolder 的 Authentication 中获取 SysUser，提取 roleId
        Long roleId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.xq.web.system.user.entity.SysUser) {
            com.xq.web.system.user.entity.SysUser user = (com.xq.web.system.user.entity.SysUser) auth.getPrincipal();
            roleId = user.getRoleId();
        }
        
        int count = sysMessageService.markAllRead(userId, roleId);
        return ResultVo.success("一键已读成功，已标记" + count + "条消息", count);
    }
}
