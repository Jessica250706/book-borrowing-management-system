package com.xq.web.system.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data; /**
 * 角色信息DTO
 */
@Data
@Schema(description = "角色信息")
public class RoleInfoDTO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;
}
