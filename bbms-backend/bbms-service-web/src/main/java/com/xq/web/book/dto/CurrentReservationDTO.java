package com.xq.web.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "当前预约信息")
public class CurrentReservationDTO {

    @Schema(description = "预约ID")
    private Long reservationId;

    @Schema(description = "书籍ID")
    private Long bookId;

    @Schema(description = "书名")
    private String bookName;

    @Schema(description = "封面URL")
    private String coverUrl;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "译者")
    private String translator;

    @Schema(description = "分类编码")
    private String categoryCode;

    @Schema(description = "预约时间")
    private Date reservationTime;

    @Schema(description = "预约失效时间")
    private Date invalidTime;

    @Schema(description = "预约状态 0-等待中 1-已确认 2-已取消 3-已过期")
    private Integer reservationStatus;

    @Schema(description = "提醒状态 0-未提醒 1-已提醒")
    private Integer remindStatus;

    @Schema(description = "书籍是否已删除(0=未删除,1=已删除)")
    private Byte deleted;
}
