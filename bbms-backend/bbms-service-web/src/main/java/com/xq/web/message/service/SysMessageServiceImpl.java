package com.xq.web.message.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.dto.PageDTO;
import com.xq.web.message.dto.MessageQueryParam;
import com.xq.web.message.entity.SysMessage;
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
    public PageDTO<SysMessage> getMessageList(MessageQueryParam param) {
        Page<SysMessage> page = new Page<>(param.getPageNum(), param.getPageSize());
        QueryWrapper<SysMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", param.getUserId()).orderByDesc("send_time");
        IPage<SysMessage> resultPage = sysMessageMapper.selectPage(page, wrapper);
        List<SysMessage> list = resultPage.getRecords();
        return PageDTO.<SysMessage>builder()
                .list(list)
                .total(resultPage.getTotal())
                .pageNum(param.getPageNum().longValue())
                .pageSize(param.getPageSize().longValue())
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
