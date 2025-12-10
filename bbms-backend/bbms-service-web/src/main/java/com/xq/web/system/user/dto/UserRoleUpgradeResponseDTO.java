package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户角色升级响应DTO
 */
@Data
@Schema(description = "用户角色升级响应数据")
public class UserRoleUpgradeResponseDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名称")
    private String username;

    @Schema(description = "用户UID")
    private String uid;

    @Schema(description = "新角色ID")
    private Long newRoleId;

    @Schema(description = "新角色编码")
    private String newRoleCode;

    @Schema(description = "新角色名称")
    private String newRoleName;

    @Schema(description = "操作者ID")
    private Long operatorId;

    @Schema(description = "操作者名称")
    private String operatorName;

    @Schema(description = "是否有未归还书籍（升级时）")
    private Boolean hadBorrowingBooks;

    @Schema(description = "操作时间")
    private String operateTime;

    @Schema(description = "操作备注")
    private String remark;
}
