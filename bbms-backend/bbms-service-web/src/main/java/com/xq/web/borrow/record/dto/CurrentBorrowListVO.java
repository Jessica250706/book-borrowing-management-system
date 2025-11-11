package com.xq.web.borrow.record.dto;

import com.xq.dto.PageInfoDTO;
import lombok.Data;
import java.util.List;

/**
 * 当前借阅列表响应VO
 */
@Data
public class CurrentBorrowListVO {
    private PageInfoDTO pageInfo;          // 分页信息
    private List<CurrentBorrowDTO> records; // 借阅记录列表
}
