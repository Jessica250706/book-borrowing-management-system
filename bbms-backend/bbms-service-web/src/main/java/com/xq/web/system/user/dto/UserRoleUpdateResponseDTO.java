package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户角色更新响应DTO
 */
@Data
@Schema(description = "用户角色更新响应")
public class UserRoleUpdateResponseDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户UID")
    private String uid;

    @Schema(description = "原角色ID")
    private Long oldRoleId;

    @Schema(description = "原角色编码")
    private String oldRoleCode;

    @Schema(description = "原角色名称")
    private String oldRoleName;

    @Schema(description = "新角色ID")
    private Long newRoleId;

    @Schema(description = "新角色编码")
    private String newRoleCode;

    @Schema(description = "新角色名称")
    private String newRoleName;

    @Schema(description = "操作管理员ID")
    private Long operatorId;

    @Schema(description = "操作管理员名称")
    private String operatorName;

    @Schema(description = "操作时间")
    private Date operateTime;

    @Schema(description = "备注")
    private String remark;
}
