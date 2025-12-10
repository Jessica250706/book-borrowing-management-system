package com.xq.web.borrow.record.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

/**
 * 批量操作参数
 */
@Data
@Schema(description = "批量操作参数")
public class BatchOperateParam {

    @NotEmpty(message = "借阅记录ID列表不能为空")
    @Schema(description = "借阅记录ID列表")
    private List<Long> ids;
}
