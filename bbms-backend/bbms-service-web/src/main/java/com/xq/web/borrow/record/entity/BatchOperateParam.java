package com.xq.web.borrow.record.entity;

import lombok.Data;
import java.util.List;

/**
 * 批量操作参数
 */
@Data
public class BatchOperateParam {
    private List<Long> ids;  // 借阅ID列表（改为Long类型）
}
