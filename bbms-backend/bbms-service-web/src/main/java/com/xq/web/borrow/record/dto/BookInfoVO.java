package com.xq.web.borrow.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BookInfoVO {
    @Schema(description = "书籍ID", example = "123456789")
    private String bookId;

    @Schema(description = "书籍名称", example = "三体")
    private String bookName;

    @Schema(description = "显示名称（截断后）", example = "三体：地球往事...")
    private String displayName;

    @Schema(description = "封面URL")
    private String coverUrl;

    @Schema(description = "作者信息", example = "[中国]刘慈欣")
    private String authorInfo;
}
