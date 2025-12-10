package com.xq.web.operationLog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xq.web.book.dto.BookCategoryDTO;
import com.xq.web.book.dto.BookInfoDTO;
import com.xq.web.system.user.dto.UserInfo;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * 借阅记录基础数据传输对象
 */
@Data
@Schema(description = "借阅记录")
public class BaseBorrowRecordDTO {

    @Schema(description = "日志ID")
    private Long logId;

    @Schema(description = "书籍信息")
    private BookInfoDTO bookInfo;

    @Schema(description = "分类信息")
    private BookCategoryDTO bookCategory;

    @Schema(description = "操作用户信息（仅管理员可见）")
    private UserInfo userInfo;

    @Schema(description = "操作类别（1-预约，2-取消预约，3-借阅，4-续借，5-归还）")
    private Integer operationType;

    @Schema(description = "操作类型描述")
    private String operationTypeDesc;

    @Schema(description = "操作日期", example = "2024-01-15 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationDate;

    /**
     * 设置操作类型时自动设置描述
     */
    public BaseBorrowRecordDTO setOperationType(Integer operationType) {
        this.operationType = operationType;
        this.operationTypeDesc = getOperationTypeDescription(operationType);
        return this;
    }

    private String getOperationTypeDescription(Integer operationType) {
        if (operationType == null) return "未知操作";
        switch (operationType) {
            case 1: return "预约";
            case 2: return "取消预约";
            case 3: return "借阅";
            case 4: return "续借";
            case 5: return "归还";
            default: return "其他操作";
        }
    }
}
