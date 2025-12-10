package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min; /**
 * 用户角色升级请求VO
 */
@Data
@Schema(description = "用户角色升级请求参数")
public class UserRoleUpgradeRequestVO {

    @Schema(description = "用户ID", required = true)
    @Min(value = 1, message = "用户ID不能小于1")
    private Long userId;

    @Schema(description = "新角色ID", required = true)
    @Min(value = 1, message = "角色ID不能小于1")
    private Long newRoleId;

    @Schema(description = "是否自动归还所有书籍", defaultValue = "true")
    private Boolean autoReturnBooks = true;

    @Schema(description = "操作备注")
    private String remark;
}
