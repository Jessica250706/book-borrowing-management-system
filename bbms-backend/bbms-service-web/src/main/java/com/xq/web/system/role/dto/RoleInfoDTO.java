package com.xq.web.system.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色信息")
public class RoleInfoDTO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "最大可借阅本数")
    private Integer maxBorrowNum;

    @Schema(description = "最大可借阅天数")
    private Integer maxBorrowDays;

    @Schema(description = "最大可续借天数")
    private Integer maxRenewDays;
}