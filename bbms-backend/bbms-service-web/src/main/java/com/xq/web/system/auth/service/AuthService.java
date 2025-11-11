package com.xq.web.system.auth.service;

import com.xq.web.system.auth.entity.LoginParam;
import com.xq.web.system.auth.entity.LoginVo;

/**
 * 认证服务
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginVo login(LoginParam param);

    /**
     * 用户注册
     */
    boolean register(LoginParam param);

    /**
     * 刷新token
     */
    LoginVo refreshToken(String token);

    /**
     * 验证用户账号密码
     */
    boolean validateUser(String account, String password);
}