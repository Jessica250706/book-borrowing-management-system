package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "借阅信用信息")
public class CreditInfoDTO {

    @Schema(description = "信用分数")
    private Integer creditScore;

    @Schema(description = "信用等级")
    private String creditLevel;

    @Schema(description = "当前借阅数量")
    private Integer currentBorrowCount;

    @Schema(description = "当前预约数量")
    private Integer currentReserveCount;

    @Schema(description = "是否可以借阅更多书籍")
    private Boolean canBorrowMore;

    @Schema(description = "是否可以预约更多书籍")
    private Boolean canReserveMore;

    @Schema(description = "最大可借阅本数")
    private Integer maxBorrowNum;

    @Schema(description = "剩余可借阅本数")
    private Integer remainingBorrowNum;
}