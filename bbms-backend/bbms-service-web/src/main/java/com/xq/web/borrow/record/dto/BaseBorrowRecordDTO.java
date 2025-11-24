package com.xq.web.borrow.record.dto;

import com.xq.web.system.user.dto.UserInfoVO;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 借阅记录基础数据传输对象
 */
@Data
@Schema(description = "借阅记录")
public class BaseBorrowRecordDTO {
    @Schema(description = "序号", example = "1")
    private Integer serialNumber;

    @Schema(description = "书籍信息")
    private BookInfoVO bookInfo;

    @Schema(description = "分类名称", example = "文学")
    private String categoryName;

    @Schema(description = "操作用户信息（仅管理员可见）")
    private UserInfoVO userInfo;

    @Schema(description = "操作类型", example = "借阅")
    private String operationType;

    @Schema(description = "操作日期", example = "2024-01-15 10:30:00")
    private String operationDate;
}

