package com.xq.web.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.dto.PageDTO;
import com.xq.web.system.user.dto.CreditScoreTrendDTO;
import com.xq.web.system.user.dto.RegisterRequestVO;
import com.xq.web.system.user.dto.UserListRequestVO;
import com.xq.web.system.user.dto.UserListResponseDTO;
import com.xq.web.system.user.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    /**
     * 验证用户账号密码
     */
    SysUser validateUser(String account, String password);

    /**
     * 注册用户
     *
     * @param user 用户参数
     * @return 注册成功的用户信息
     */
    SysUser registerUser(RegisterRequestVO user);

    /**
     * 验证角色ID是否有效
     *
     * @param roleId 角色ID
     * @return 是否有效
     */
    boolean validateRoleId(Long roleId);

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

    /**
     * 获取用户列表（分页+条件查询）
     * @param request 查询条件
     * @return 用户分页列表
     */
    PageDTO<UserListResponseDTO> getUserList(UserListRequestVO request);

    /**
     * 升级用户为管理员（包含自动归还书籍逻辑）
     *
     * @param userId     目标用户ID
     * @param newRoleId  新角色ID（必须是管理员角色）
     * @param operatorId 操作管理员ID
     * @param autoReturn 是否自动归还书籍
     * @param remark     操作备注
     * @return 是否升级成功
     */
    boolean upgradeUserToAdmin(Long userId, Long newRoleId, Long operatorId,
                               Boolean autoReturn, String remark);

    /**
     * 自动归还用户所有借阅中的书籍
     *
     * @param userId     用户ID
     * @param operatorId 操作管理员ID
     * @return 归还成功的书籍数量
     */
    int autoReturnAllBorrowingBooks(Long userId, Long operatorId);

    /**
     * 获取用户最近五个月的信誉分趋势数据
     *
     * @param userId 用户ID
     * @return 信誉分趋势数据列表
     */
    List<CreditScoreTrendDTO> getCreditScoreTrend(Long userId);
}