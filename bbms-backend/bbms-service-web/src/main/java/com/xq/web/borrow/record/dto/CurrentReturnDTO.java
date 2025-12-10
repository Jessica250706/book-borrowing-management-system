package com.xq.web.borrow.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xq.web.book.dto.BookCategoryDTO;
import com.xq.web.book.dto.BookInfoDTO;
import com.xq.web.system.user.dto.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CurrentReturnDTO {
    @Schema(description = "借阅记录ID", example = "123")
    private Long borrowId;

    @Schema(description = "书籍信息")
    private BookInfoDTO bookInfo;

    @Schema(description = "分类信息")
    private BookCategoryDTO bookCategory;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Schema(description = "借阅时间", example = "2024-01-15 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date borrowTime;

    @Schema(description = "预计归还时间", example = "2024-02-15 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expectedReturnTime;

    @Schema(description = "实际归还时间", example = "2024-02-10 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualReturnTime;

    @Schema(description = "读者申请归还时间", example = "2024-02-10 09:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date returnApplyTime;

    @Schema(description = "归还确认状态：0-待确认，1-已确认", example = "0")
    private Integer returnConfirmStatus;

    @Schema(description = "续借次数", example = "1")
    private Integer renewCount;

    @Schema(description = "借阅状态：0-借阅中，1-已归还，2-已超时，3-归还待确认", example = "3")
    private Integer borrowStatus;

    @Schema(description = "可进行的操作", example = "[\"detail\", \"confirmReturn\"]")
    private String[] operations;
}