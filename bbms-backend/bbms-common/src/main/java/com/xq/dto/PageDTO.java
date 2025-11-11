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
     * 从MyBatis Plus的IPage创建
     */
    public static <T> PageDTO<T> fromIPage(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        return new PageDTO<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getRecords()
        );
    }
}