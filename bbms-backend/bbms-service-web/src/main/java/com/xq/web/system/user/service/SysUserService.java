package com.xq.web.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.system.user.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    /**
     * 验证用户账号密码
     */
    SysUser validateUser(String account, String password);

    /**
     * 注册用户（完整版）
     */
    boolean registerUser(SysUser user);

    /**
     * 注册用户（简化版）
     */
    boolean registerUser(String account, String password, String username);

    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(Long userId);

    /**
     * 根据用户ID获取完整用户信息（包含角色信息）
     */
    SysUser getUserWithRoleInfo(Long userId);

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码（管理员操作）
     */
    boolean resetPassword(Long userId, String newPassword);
}