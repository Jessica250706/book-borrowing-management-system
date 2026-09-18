package com.xq.web.borrow.record.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户借阅统计VO
 */
@Data
public class UserBorrowStatisticsVO {
    /**
     * 本月借阅数量（本）
     */
    private Integer monthBorrowCount;

    /**
     * 累计借阅数量（本）
     */
    private Integer totalBorrowCount;

    /**
     * 借阅频率（本/月）
     */
    private BigDecimal borrowFrequency;

    /**
     * 平均阅读时长（天/本）
     */
    private BigDecimal averageReadingDays;
}
