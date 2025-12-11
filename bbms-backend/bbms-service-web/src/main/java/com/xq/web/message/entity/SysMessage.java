package com.xq.web.message.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_message")
public class SysMessage {
    @TableId
    private Long messageId;
    private Long userId;
    private Integer messageType;
    private String messageTitle;
    private String messageContent;
    private Integer readStatus;
    private Date readTime;
    private Date sendTime;
    private Date createTime;
    private Date updateTime;
}
