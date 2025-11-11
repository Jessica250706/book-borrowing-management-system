package com.xq.dto;

import lombok.Data;

import java.util.List;

/**
 * 通用分页响应DTO
 * 包含分页信息和数据列表
 */
@Data
public class PageDTO<T> {
    private PageInfoDTO pageInfo;    // 分页信息
    private List<T> records;         // 数据列表

    public PageDTO() {
    }

    public PageDTO(PageInfoDTO pageInfo, List<T> records) {
        this.pageInfo = pageInfo;
        this.records = records;
    }

    public PageDTO(Long currentPage, Long pageSize, Long total, List<T> records) {
        this.pageInfo = new PageInfoDTO(currentPage, pageSize, total);
        this.records = records;
    }

    /**
     * 通用分页创建方法
     */
    public static <T> PageDTO<T> of(Long currentPage, Long pageSize, Long total, List<T> records) {
        return new PageDTO<>(currentPage, pageSize, total, records);
    }

    /**
     * 从基本参数创建
     */
    public static <T> PageDTO<T> of(Integer currentPage, Integer pageSize, Long total, List<T> records) {
        return new PageDTO<>(
                currentPage != null ? currentPage.longValue() : 1L,
                pageSize != null ? pageSize.longValue() : 10L,
                total,
                records
        );
    }
}