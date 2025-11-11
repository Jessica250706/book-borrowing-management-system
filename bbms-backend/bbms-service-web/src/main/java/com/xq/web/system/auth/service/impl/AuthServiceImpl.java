package com.xq.web.system.auth.service.impl;

import com.xq.utils.JwtUtils;
import com.xq.web.system.auth.entity.LoginParam;
import com.xq.web.system.auth.entity.LoginVo;
import com.xq.web.system.auth.service.AuthService;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginVo login(LoginParam param) {
        // 验证用户账号密码
        SysUser user = userService.validateUser(param.getAccount(), param.getPassword());
        if (user == null) {
            throw new RuntimeException("账号或密码错误");
        }

        // 检查账号状态
        if (user.getAccountStatus() == 0) {
            throw new RuntimeException("账号已被冻结");
        }

        // 生成token
        String token = jwtUtils.generateUserToken(
                user.getUserId(),
                user.getUsername(),
                user.getRoleId(),
                user.getRoleCode()
        );

        // 构建返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setUserId(user.getUserId());
        loginVo.setUsername(user.getUsername());
        loginVo.setRoleCode(user.getRoleCode());
        loginVo.setRoleName(user.getRoleName());
        loginVo.setIsAdmin(isAdminRole(user.getRoleCode()));
        loginVo.setExpiresIn(24 * 60 * 60L); // 24小时

        // 更新最后登录时间
        userService.updateLastLoginTime(user.getUserId());

        return loginVo;
    }

    @Override
    @Transactional
    public boolean register(LoginParam param) {
        // 注册逻辑
        return userService.registerUser(param.getAccount(), param.getPassword());
    }

    @Override
    public LoginVo refreshToken(String token) {
        // 刷新token逻辑
        Long userId = jwtUtils.getUserId(token);
        SysUser user = userService.getById(userId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String newToken = jwtUtils.generateUserToken(
                user.getUserId(),
                user.getUsername(),
                user.getRoleId(),
                user.getRoleCode()
        );

        LoginVo loginVo = new LoginVo();
        loginVo.setToken(newToken);
        loginVo.setUserId(user.getUserId());
        loginVo.setUsername(user.getUsername());
        loginVo.setRoleCode(user.getRoleCode());
        loginVo.setRoleName(user.getRoleName());
        loginVo.setIsAdmin(isAdminRole(user.getRoleCode()));
        loginVo.setExpiresIn(24 * 60 * 60L);

        return loginVo;
    }

    @Override
    public boolean validateUser(String account, String password) {
        SysUser user = userService.validateUser(account, password);
        return user != null;
    }

    private boolean isAdminRole(String roleCode) {
        return "ADMIN".equals(roleCode) || "SYS_ADMIN".equals(roleCode);
    }
}