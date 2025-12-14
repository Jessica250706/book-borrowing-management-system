package com.xq.web.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除前检查DTO - 返回书籍是否有未归还借阅记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "删除前检查结果")
public class DeleteCheckDTO {
    
    @Schema(description = "书籍ID")
    private Long bookId;
    
    @Schema(description = "书籍名称")
    private String bookName;
    
    @Schema(description = "是否存在未归还的借阅记录")
    private Boolean hasBorrowRecord;
    
    @Schema(description = "未归还借阅人数")
    private Long borrowCount;
}
