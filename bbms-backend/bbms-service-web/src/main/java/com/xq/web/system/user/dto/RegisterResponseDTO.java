package com.xq.web.system.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 注册响应DTO
 */
@Data
@Schema(description = "注册响应数据")
public class RegisterResponseDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录账号")
    private String account;

    @Schema(description = "用户唯一标识")
    private String uid;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "信誉分")
    private Integer creditScore;

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;

    // 添加一些便捷方法
    public boolean isAdmin() {
        return "ADMIN".equals(roleCode) || "SYS_ADMIN".equals(roleCode);
    }

    public boolean isReader() {
        return roleCode != null && roleCode.startsWith("READER_");
    }
}
