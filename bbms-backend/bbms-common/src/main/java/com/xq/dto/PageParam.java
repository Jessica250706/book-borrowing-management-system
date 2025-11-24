package com.xq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页参数基类
 */
@Data
@Schema(description = "分页参数")
public class PageParam {

    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;

    /**
     * 获取页码（从1开始）
     */
    public Long getPageNum() {
        return current != null ? current : 1L;
    }

    /**
     * 获取页面大小
     */
    public Long getPageSize() {
        return size != null ? size : 10L;
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
        this.current = pageNum;
    }

    /**
     * 设置页面大小
     */
    public void setPageSize(Long pageSize) {
        this.size = pageSize;
    }
}
