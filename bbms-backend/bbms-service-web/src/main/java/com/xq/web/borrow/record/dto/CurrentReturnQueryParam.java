package com.xq.web.borrow.record.dto;

import com.xq.dto.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员端当前归还查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "当前归还查询参数")
public class CurrentReturnQueryParam extends PageParam {

    @Schema(description = "搜索关键词（书名/作者）")
    private String keyword;

    @Schema(description = "书籍分类编码")
    private String categoryCode;
}
