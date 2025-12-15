package com.xq.web.system.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 信誉分趋势数据DTO
 */
@Data
public class CreditScoreTrendDTO {

    /**
     * 时间段标签（格式：yyyy-MM 或 MM-dd ~ MM-dd）
     */
    private String periodLabel;

    /**
     * 月份（格式：YYYY-MM）
     */
    private String month;

    /**
     * 该月平均信誉分
     */
    private Integer averageScore;

    /**
     * 该月最高信誉分
     */
    private Integer highestScore;

    /**
     * 该月最低信誉分
     */
    private Integer lowestScore;

    /**
     * 该月信誉分变动次数
     */
    private Integer changeCount;

    /**
     * 时间段的开始时间
     */
    private Date periodStart;

    /**
     * 时间段的结束时间
     */
    private Date periodEnd;

    /**
     * 月份的开始时间
     */
    @JsonFormat(pattern = "yyyy-MM")
    private Date monthStart;

    /**
     * 月份的结束时间
     */
    @JsonFormat(pattern = "yyyy-MM")
    private Date monthEnd;

    // 为了兼容原有代码，添加getter/setter方法
    public void setMonthStart(Date monthStart) {
        this.periodStart = monthStart;
    }

    public Date getMonthStart() {
        return periodStart;
    }

    public void setMonthEnd(Date monthEnd) {
        this.periodEnd = monthEnd;
    }

    public Date getMonthEnd() {
        return periodEnd;
    }

    // 设置月份时也设置periodLabel
    public void setMonth(String month) {
        this.month = month;
        this.periodLabel = month;
    }
}
