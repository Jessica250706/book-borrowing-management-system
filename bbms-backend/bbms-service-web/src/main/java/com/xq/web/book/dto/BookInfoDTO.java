package com.xq.web.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BookInfoDTO {
    @Schema(description = "书籍ID", example = "123456789")
    private Long bookId;

    @Schema(description = "书籍名称", example = "三体")
    private String bookName;

    @Schema(description = "封面URL")
    private String coverUrl;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "书籍是否已删除(0=未删除,1=已删除)", example = "0")
    private Byte deleted;
}
