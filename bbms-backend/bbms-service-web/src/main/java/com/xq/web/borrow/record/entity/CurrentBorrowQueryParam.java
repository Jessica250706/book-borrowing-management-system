package com.xq.web.borrow.record.entity;

import com.xq.dto.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 读者端当前借阅查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "当前借阅查询参数")
public class CurrentBorrowQueryParam extends PageParam {

    @Schema(description = "搜索关键词（书名/作者）")
    private String keyword;

    @Schema(description = "书籍分类ID")
    private Long categoryId;

    @Schema(description = "书籍分类编码")
    private String categoryCode;

    @Schema(description = "排序字段", example = "expectedReturnTime")
    private String sortField = "expected_return_time";

    @Schema(description = "排序顺序", example = "asc")
    private String sortOrder = "asc";
}
