package com.xq.web.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.system.user.dto.RegisterRequestVO;
import com.xq.web.system.user.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    /**
     * 验证用户账号密码
     */
    SysUser validateUser(String account, String password);

    /**
     * 注册用户（完整版）
     */
    SysUser registerUser(RegisterRequestVO user);

    /**
     * 注册用户（简化版）
     */
    SysUser registerUser(String account, String password, String username);

    /**
     * 用户登录验证
     * @param account 账号（用户名/邮箱/手机号）
     * @param password 密码
     * @return 用户信息，如果验证失败返回null
     */
    SysUser login(String account, String password);

    /**
     * 记录登录成功
     * @param userId 用户ID
     */
    void recordLoginSuccess(Long userId);

    /**
     * 记录登录失败
     * @param account 账号
     */
    void recordLoginFailure(String account);

    /**
     * 检查账号是否被锁定（登录错误次数过多）
     * @param account 账号
     * @return 是否被锁定
     */
    boolean isAccountLocked(String account);

    /**
     * 解锁账号
     * @param userId 用户ID
     */
    void unlockAccount(Long userId);

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

    /**
     * 更新用户角色
     *
     * @param targetUserId 目标用户ID
     * @param newRoleId 新角色ID
     * @param operatorId 操作管理员ID
     * @param remark 操作备注
     * @return 更新结果
     */
    boolean updateUserRole(Long targetUserId, Long newRoleId, Long operatorId, String remark);

    /**
     * 检查用户是否有借阅中的书籍
     *
     * @param userId 用户ID
     * @return 是否有借阅中的书籍
     */
    boolean hasBorrowingBooks(Long userId);

    /**
     * 获取用户详细信息（包含角色信息）
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getUserDetail(Long userId);
}