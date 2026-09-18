package com.xq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Setter;

/**
 * 分页参数基类
 */
@Data
@Schema(description = "分页参数")
public class PageParam {

    @Schema(description = "当前页码", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Long currentPage = 1L;

    @Setter
    @Schema(description = "每页大小", example = "20")
    @Min(value = 1, message = "每页数量不能小于1")
    private Long pageSize = 20L;

    /**
     * 获取页码（从1开始）
     */
    public Long getPageNum() {
        return currentPage != null ? currentPage : 1L;
    }

    /**
     * 获取页面大小
     */
    public Long getPageSize() {
        return pageSize != null ? pageSize : 20L;
    }

    /**
     * 获取偏移量（用于数据库查询）
     */
    public Long getOffset() {
        return (getPageNum() - 1) * getPageSize();
    }

    /**
     * 设置页码
     */
    public void setPageNum(Long pageNum) {
        this.currentPage = pageNum;
    }

}
