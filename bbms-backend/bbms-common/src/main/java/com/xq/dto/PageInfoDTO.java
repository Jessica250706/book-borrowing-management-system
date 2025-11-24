package com.xq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页信息DTO - 通用分页信息
 * 用于所有需要分页的接口返回
 */
@Data
@Schema(description = "分页信息")
public class PageInfoDTO {
    @Schema(description = "当前页码", example = "1")
    private Long currentPage;

    @Schema(description = "每页大小", example = "20")
    private Long pageSize;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "总页数", example = "10")
    private Long totalPages;

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