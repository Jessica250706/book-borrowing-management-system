package com.xq.dto;

import lombok.Data;

/**
 * 分页信息DTO - 通用分页信息
 * 用于所有需要分页的接口返回
 */
@Data
public class PageInfoDTO {
    private Long currentPage;    // 当前页码
    private Long pageSize;       // 每页大小
    private Long total;          // 总记录数
    private Long totalPages;     // 总页数

    public PageInfoDTO() {
    }

    public PageInfoDTO(Long currentPage, Long pageSize, Long total) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = calculateTotalPages(total, pageSize);
    }

    public PageInfoDTO(Long currentPage, Long pageSize, Long total, Long totalPages) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = totalPages;
    }

    /**
     * 计算总页数
     */
    private Long calculateTotalPages(Long total, Long pageSize) {
        if (pageSize == null || pageSize == 0) {
            return 0L;
        }
        return (total + pageSize - 1) / pageSize;
    }
}