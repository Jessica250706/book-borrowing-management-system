package com.xq.web.borrow.record.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 当前借阅列表返回DTO
 */
@Data
public class CurrentBorrowDTO {
    private Long id;                    // 借阅记录ID
    private String bookName;           // 书籍名称
    private String bookCover;          // 书籍封面URL
    private String bookAuthor;         // 书籍作者
    private String category;           // 分类
    private Integer remainingDays;     // 剩余借阅天数（正数表示剩余天数，负数表示超期天数）
    private LocalDateTime latestReturnTime; // 最晚归还时间
    private Integer renewableDays;     // 可续借天数
    private List<String> operations;   // 操作列表（如：["renew", "return"]）
}
