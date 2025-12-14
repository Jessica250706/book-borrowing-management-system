package com.xq.web.message.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "消息查询参数")
public class MessageQueryParam {
    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize;
}
