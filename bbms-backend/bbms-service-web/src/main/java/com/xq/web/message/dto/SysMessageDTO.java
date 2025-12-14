package com.xq.web.message.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * 消息通知返回DTO（供前端展示）
 */
@Data
@Schema(description = "消息通知")
public class SysMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "序号（分页用）", example = "1")
    private Integer sequence;

    @Schema(description = "消息ID", example = "1001")
    private Long messageId;

    @Schema(description = "用户ID", example = "123")
    private Long userId;

    @Schema(description = "消息类型", example = "1", allowableValues = {"1", "2", "3", "4"})
    private Integer messageType;

    @Schema(description = "消息标题", example = "预约上架")
    private String messageTitle;

    @Schema(description = "消息内容", example = "您预约的《三体》已上架。")
    private String messageContent;

    @Schema(description = "阅读状态", example = "0", allowableValues = {"0", "1"})
    private Integer readStatus;

    @Schema(description = "阅读时间（格式化）", example = "2025-12-13 10:30:00")
    private String readTime;

    @Schema(description = "发送时间（格式化）", example = "2025-12-13 09:00:00")
    private String sendTime;

    @Schema(description = "关联的图书ID", example = "2001")
    private Long bookId;

    @Schema(description = "状态文本（如‘未读’/‘已读’）", example = "未读")
    private String statusText;
}
