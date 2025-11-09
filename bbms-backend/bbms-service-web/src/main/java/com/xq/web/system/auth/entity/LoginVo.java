package com.xq.web.system.auth.entity;

import lombok.Data;

/**
 * 登录返回信息
 */
@Data
public class LoginVo {
    private String token;
    private Long userId;
    private String username;
    private String roleCode;
    private String roleName;
    private Boolean isAdmin;
    private Long expiresIn; // token过期时间（秒）
}