package com.xq.web.operationLog.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 借阅记录返回DTO（读者端）
 */
@Data
@Schema(description = "读者借阅记录")
public class BorrowRecordDTO {
    @Schema(description = "借阅记录ID", example = "123")
    private Long id;

    @Schema(description = "书籍名称", example = "三体")
    private String bookName;

    @Schema(description = "书籍封面URL", example = "https://example.com/cover.jpg")
    private String bookCover;

    @Schema(description = "书籍作者", example = "刘慈欣")
    private String bookAuthor;

    @Schema(description = "分类", example = "科幻文学")
    private String category;

    @Schema(description = "操作类别", example = "借阅", allowableValues = {"借阅", "归还", "续借"})
    private String operationType;

    @Schema(description = "操作时间", example = "2024-01-15 10:30:00")
    private String operationTime;
}