package com.xq.web.system.auth.controller;

import com.xq.jwt.JwtUtils;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.system.auth.entity.LoginParam;
import com.xq.web.system.auth.entity.LoginVo;
import com.xq.web.system.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResultVo login(@Valid @RequestBody LoginParam param) {
        LoginVo loginVo = authService.login(param);
        return ResultUtils.success("登录成功", loginVo);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResultVo register(@Valid @RequestBody LoginParam param) {
        boolean success = authService.register(param);
        return success ? ResultUtils.success("注册成功") : ResultUtils.error("注册失败");
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResultVo logout(@RequestAttribute Long userId) {
        // 可以在这里处理token黑名单等逻辑
        return ResultUtils.success("登出成功");
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public ResultVo refreshToken(@RequestHeader("Authorization") String token) {
        LoginVo loginVo = authService.refreshToken(token);
        return ResultUtils.success("token刷新成功", loginVo);
    }
}