package com.xq.web.borrow.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfoVO {
    @Schema(description = "用户ID", example = "123456789")
    private String userId;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "用户昵称", example = "张三")
    private String username;

    @Schema(description = "显示昵称（截断后）", example = "张三...")
    private String displayName;

    @Schema(description = "用户UID", example = "U10001")
    private String uid;
}
