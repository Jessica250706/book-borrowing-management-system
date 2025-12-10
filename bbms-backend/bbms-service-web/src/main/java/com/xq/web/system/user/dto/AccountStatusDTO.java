package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "账号状态信息")
public class AccountStatusDTO {

    @Schema(description = "状态码（0-冻结，1-正常，2-停用，3-注销）")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "是否在冻结期内")
    private Boolean inFreezePeriod;

    @Schema(description = "冻结开始时间")
    private LocalDateTime freezeTime;

    @Schema(description = "冻结结束时间")
    private LocalDateTime unfreezeTime;

    @Schema(description = "登录错误次数")
    private Integer loginErrorCount;

    @Schema(description = "是否需要解锁")
    private Boolean needUnlock;

    @Schema(description = "账号是否可用（正常状态且不在冻结期内）")
    private Boolean accountAvailable;
}
