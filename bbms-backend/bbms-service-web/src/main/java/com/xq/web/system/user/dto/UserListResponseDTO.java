package com.xq.web.system.user.dto;

import com.xq.web.system.role.dto.RoleInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户列表响应DTO
 */
@Data
@Schema(description = "用户列表响应数据")
public class UserListResponseDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "用户名称")
    private String username;

    @Schema(description = "用户UID")
    private String uid;

    @Schema(description = "角色信息")
    private RoleInfoDTO roleInfo;

    @Schema(description = "借阅信用信息（仅读者显示）")
    private CreditInfoDTO creditInfo;

    @Schema(description = "账号状态")
    private AccountStatusDTO accountStatus;

    @Schema(description = "注册时间")
    private String registerTime;

    @Schema(description = "是否可以升级权限（读者可升级为管理员）")
    private Boolean canUpgradeRole;

    @Schema(description = "是否有未归还书籍（用于升级权限时的提示）")
    private Boolean hasBorrowingBooks;
}
