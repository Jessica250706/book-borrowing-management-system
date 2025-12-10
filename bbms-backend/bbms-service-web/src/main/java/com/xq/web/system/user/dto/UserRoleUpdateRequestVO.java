package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 用户角色更新请求VO
 */
@Data
@Schema(description = "用户角色更新请求")
public class UserRoleUpdateRequestVO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", required = true)
    private Long roleId;

    @Schema(description = "操作备注")
    private String remark;
}
