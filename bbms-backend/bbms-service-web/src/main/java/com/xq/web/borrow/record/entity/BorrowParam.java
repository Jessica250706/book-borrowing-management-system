package com.xq.web.borrow.record.entity;

import com.xq.dto.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 借阅记录查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "借阅记录查询参数")
public class BorrowParam extends PageParam {

    @Schema(description = "搜索关键词（书名/作者/借阅用户）")
    private String keyword;

    @Schema(description = "书籍分类编码", example = "A")
    private String categoryCode;

    @Schema(description = "操作类别（1-预约，2-取消预约，3-借阅，4-续借，5-归还）")
    private Integer operationType;

}
