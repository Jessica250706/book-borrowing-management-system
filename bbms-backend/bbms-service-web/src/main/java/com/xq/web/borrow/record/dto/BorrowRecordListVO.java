package com.xq.web.borrow.record.dto;

import com.xq.dto.PageInfoDTO;
import lombok.Data;
import java.util.List;

/**
 * 借阅记录列表响应VO
 */
@Data
public class BorrowRecordListVO<T> {
    private PageInfoDTO pageInfo;          // 分页信息
    private List<T> records;               // 借阅记录列表
}
