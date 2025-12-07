package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data; /**
 * 账号状态DTO
 */
@Data
@Schema(description = "账号状态信息")
public class AccountStatusDTO {

    @Schema(description = "状态码：0-冻结，1-正常，2-停用，3-注销")
    private Integer code;

    @Schema(description = "状态描述")
    private String desc;
}
