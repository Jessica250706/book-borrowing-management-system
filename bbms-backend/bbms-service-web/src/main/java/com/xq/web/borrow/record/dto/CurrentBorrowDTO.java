package com.xq.web.borrow.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

/**
 * 当前借阅列表返回DTO
 */
@Data
@Schema(description = "当前借阅信息")
public class CurrentBorrowDTO {
    @Schema(description = "借阅记录ID", example = "123")
    private Long id;

    @Schema(description = "书籍ID", example = "1")
    private String bookId;

    @Schema(description = "书籍名称", example = "三体")
    private String bookName;

    @Schema(description = "书籍封面URL", example = "https://example.com/cover.jpg")
    private String bookCover;

    @Schema(description = "书籍作者", example = "刘慈欣")
    private String author;

    @Schema(description = "分类编码", example = "A")
    private String categoryCode;

    @Schema(description = "剩余借阅天数", example = "15")
    private Integer remainingDays;

    @Schema(description = "最晚归还时间", example = "2024-02-15 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date latestReturnTime;

    @Schema(description = "可续借天数", example = "7")
    private Integer renewableDays;

    @Schema(description = "续借次数", example = "0")
    private Integer renewCount;

    @Schema(description = "借阅状态", example = "0")
    private Integer borrowStatus;

    @Schema(description = "操作列表", example = "[\"renew\", \"return\", \"detail\"]",
            allowableValues = {"renew", "return", "detail"})
    private List<String> operations;

    @Schema(description = "书籍是否已删除(0=未删除,1=已删除)", example = "0")
    private Byte deleted;
}
