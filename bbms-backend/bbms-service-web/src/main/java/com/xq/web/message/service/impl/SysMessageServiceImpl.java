package com.xq.web.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.dto.PageDTO;
import com.xq.web.message.dto.MessageQueryParam;
import com.xq.web.message.entity.SysMessage;
import com.xq.web.message.dto.SysMessageDTO;

import com.xq.web.message.mapper.SysMessageMapper;
import com.xq.web.message.service.SysMessageService;
import com.xq.web.system.user.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // 统一用 SecurityContextHolder principal 判断
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        Long roleId = null;
        if (authentication != null && authentication.getPrincipal() instanceof SysUser) {
            SysUser user = (SysUser) authentication.getPrincipal();
            currentUserId = user.getUserId();
            roleId = user.getRoleId();
        }
        org.slf4j.LoggerFactory.getLogger(SysMessageServiceImpl.class)
            .info("[getMessageList] currentUserId={}, roleId={}", currentUserId, roleId);

        Page<SysMessage> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysMessage> wrapper = new QueryWrapper<>();

        // roleId为4或5的用户可看管理员池消息
        if (roleId != null && (roleId == 4L || roleId == 5L)) {
            if (currentUserId != null) {
                wrapper.in("user_id", java.util.Arrays.asList(currentUserId, 0L));
            } else {
                wrapper.eq("user_id", 0L);
            }
        } else {
            if (currentUserId == null) {
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
     * 普通用户标记自己的消息，管理员标记管理员池的消息
     * @param userId 当前用户ID
     * @param roleId 当前用户角色ID
     * @return 已标记为已读的消息数量
     */
    @Override
    public int markAllRead(Long userId, Long roleId) {
        SysMessage update = new SysMessage();
        update.setReadStatus(1);
        update.setReadTime(new Date());
        QueryWrapper<SysMessage> wrapper = new QueryWrapper<>();

        // 管理员(roleId=4或5)标记管理员池(user_id=0)的消息，普通用户标记自己的消息
        if (roleId != null && (roleId == 4L || roleId == 5L)) {
            // 管理员：标记管理员池子的未读消息
            wrapper.eq("user_id", 0L).eq("read_status", 0);
        } else {
            // 普通用户：标记自己的未读消息
            wrapper.eq("user_id", userId).eq("read_status", 0);
        }

        return sysMessageMapper.update(update, wrapper);
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
        message.setReadStatus(0);
        sysMessageMapper.insert(message);
    }
}
