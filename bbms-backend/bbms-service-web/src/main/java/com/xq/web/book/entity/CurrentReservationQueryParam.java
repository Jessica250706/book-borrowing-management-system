package com.xq.web.book.entity;

import com.xq.dto.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "当前预约查询参数")
public class CurrentReservationQueryParam extends PageParam {

    @Schema(description = "搜索关键词（书名/作者）")
    private String keyword;

    @Schema(description = "书籍分类编码")
    private String categoryCode;
}
