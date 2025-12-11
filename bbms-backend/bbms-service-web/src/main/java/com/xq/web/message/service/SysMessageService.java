package com.xq.web.message.service;

import com.xq.dto.PageDTO;
import com.xq.web.message.dto.MessageQueryParam;
import com.xq.web.message.entity.SysMessage;

/**
 * 消息通知服务接口
 */
public interface SysMessageService {

    /**
     * 获取消息通知列表（条件+分页）
     * @param param 查询参数
     * @return 消息分页DTO
     */
    PageDTO<SysMessage> getMessageList(MessageQueryParam param);

    /**
     * 一键已读（批量操作）
     * @param userId 用户ID
     */
    void markAllRead(Long userId);

    /**
     * 单条已读
     * @param messageId 消息ID
     */
    void markRead(Long messageId);

    // 发送消息方法仅限后端业务调用，不暴露给前端
    void sendMessage(SysMessage message);
}
