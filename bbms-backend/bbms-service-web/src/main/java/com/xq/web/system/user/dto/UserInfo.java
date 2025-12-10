package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfo {
    @Schema(description = "用户ID", example = "123456789")
    private Long userId;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "用户昵称", example = "张三")
    private String userName;

    @Schema(description = "用户UID", example = "U10001")
    private String uid;
}
