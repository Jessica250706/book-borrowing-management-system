package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data; /**
 * 借阅信用信息DTO
 */
@Data
@Schema(description = "借阅信用信息")
public class CreditInfoDTO {

    @Schema(description = "信用分数")
    private Integer score;

    @Schema(description = "信用等级：差(0-59)、良(60-79)、优(80-100)")
    private String level;

    @Schema(description = "是否显示（读者才显示）")
    private Boolean show;
}
