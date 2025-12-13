package com.xq.web.message.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.dto.PageDTO;
import com.xq.web.message.dto.MessageQueryParam;
import com.xq.web.message.entity.SysMessage;
import com.xq.web.message.dto.SysMessageDTO;
import com.xq.web.message.mapper.SysMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class SysMessageServiceImpl implements SysMessageService {
    @Autowired
    private SysMessageMapper sysMessageMapper;

    /**
     * 获取消息通知列表（条件+分页）
     * @param param 查询参数
     * @return 消息分页DTO
     */
    @Override
    public PageDTO<SysMessageDTO> getMessageList(MessageQueryParam param) {
        // 填充分页默认值
        Integer pageNum = (param.getPageNum() == null || param.getPageNum() < 1) ? 1 : param.getPageNum();
        Integer pageSize = (param.getPageSize() == null || param.getPageSize() < 1) ? 20 : param.getPageSize();

        // 从 UserContext 获取当前用户ID
        Long currentUserId = com.xq.common.context.UserContext.getUserId();

        Page<SysMessage> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysMessage> wrapper = new QueryWrapper<>();

        // 管理员应能看到发给自己的消息以及发往管理员池（user_id=0）的消息
        boolean isAdmin = com.xq.common.context.UserContext.getIsAdmin();
        if (isAdmin) {
            if (currentUserId != null) {
                wrapper.in("user_id", java.util.Arrays.asList(currentUserId, 0));
            } else {
                // 若没有当前用户id，仍展示管理员池消息
                wrapper.eq("user_id", 0);
            }
        } else {
            // 普通用户仅能看到发给自己的消息
            if (currentUserId == null) {
                // 安全兜底：若都没有，返回空页
                return PageDTO.<SysMessageDTO>builder().list(java.util.Collections.emptyList()).total(0L).pageNum(pageNum.longValue()).pageSize(pageSize.longValue()).build();
            }
            wrapper.eq("user_id", currentUserId);
        }

        wrapper.orderByDesc("send_time");
        IPage<SysMessage> resultPage = sysMessageMapper.selectPage(page, wrapper);
        List<SysMessage> list = resultPage.getRecords();
        List<SysMessageDTO> dtoList = new java.util.ArrayList<>();
        long total = resultPage.getTotal();
        for (int i = 0; i < list.size(); i++) {
            SysMessage msg = list.get(i);
            SysMessageDTO dto = new SysMessageDTO();
            dto.setSequence((int)((pageNum - 1) * pageSize + i + 1));
            dto.setMessageId(msg.getMessageId());
            dto.setUserId(msg.getUserId());
            dto.setMessageType(msg.getMessageType());
            dto.setMessageTitle(msg.getMessageTitle());
            dto.setMessageContent(msg.getMessageContent());
            dto.setReadStatus(msg.getReadStatus());
            dto.setBookId(msg.getBookId());
            // 格式化时间
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dto.setReadTime(msg.getReadTime() == null ? null : sdf.format(msg.getReadTime()));
            dto.setSendTime(msg.getSendTime() == null ? null : sdf.format(msg.getSendTime()));
            // 状态文本
            dto.setStatusText(msg.getReadStatus() != null && msg.getReadStatus() == 1 ? "已读" : "未读");
            dtoList.add(dto);
        }
        return PageDTO.<SysMessageDTO>builder()
            .list(dtoList)
            .total(total)
            .pageNum(Long.valueOf(pageNum))
            .pageSize(Long.valueOf(pageSize))
            .build();
    }

    /**
     * 一键已读（批量操作）
     * @param userId 用户ID
     */
    @Override
    public void markAllRead(Long userId) {
        SysMessage update = new SysMessage();
        update.setReadStatus(1);
        update.setReadTime(new Date());
        QueryWrapper<SysMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("read_status", 0);
        sysMessageMapper.update(update, wrapper);
    }

    /**
     * 单条已读
     * @param messageId 消息ID
     */
    @Override
    public void markRead(Long messageId) {
        SysMessage update = new SysMessage();
        update.setReadStatus(1);
        update.setReadTime(new Date());
        QueryWrapper<SysMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("message_id", messageId);
        sysMessageMapper.update(update, wrapper);
    }

    /**
     * 发送消息（仅限后端业务调用）
     */
    @Override
    public void sendMessage(SysMessage message) {
        message.setSendTime(new Date());
        message.setCreateTime(new Date());
        message.setUpdateTime(new Date());
        message.setReadStatus(0);
        sysMessageMapper.insert(message);
    }
}
