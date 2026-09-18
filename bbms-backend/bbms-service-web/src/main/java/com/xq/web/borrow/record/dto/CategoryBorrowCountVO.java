package com.xq.web.borrow.record.dto;

import lombok.Data;

/**
 * 书籍类别借阅统计VO
 */
@Data
public class CategoryBorrowCountVO {
    /**
     * 类别ID
     */
    private Long categoryId;

    /**
     * 类别编码
     */
    private String categoryCode;

    /**
     * 类别名称
     */
    private String categoryName;

    /**
     * 借阅次数
     */
    private Integer borrowCount;

    /**
     * 占比（百分比）
     */
    private Double percentage;
}
