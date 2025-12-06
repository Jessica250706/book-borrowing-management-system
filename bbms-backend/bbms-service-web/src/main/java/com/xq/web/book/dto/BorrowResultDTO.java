package com.xq.web.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 借阅结果DTO
 * 用于返回借阅操作的详细信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowResultDTO {
    
    /**
     * 借阅记录ID
     */
    private Long borrowId;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 图书名称
     */
    private String bookName;
    
    /**
     * 图书作者
     */
    private String author;
    
    /**
     * 图书封面
     */
    private String coverUrl;
    
    /**
     * 分类名称
     */
    private String category;
    
    /**
     * 借阅开始时间
     */
    private Date borrowTime;
    
    /**
     * 预期归还时间
     */
    private Date expectedReturnTime;
    
    /**
     * 借阅天数
     */
    private Integer borrowDays;
    
    /**
     * 借阅状态（0-借阅中，1-已归还）
     */
    private Integer borrowStatus;
}
