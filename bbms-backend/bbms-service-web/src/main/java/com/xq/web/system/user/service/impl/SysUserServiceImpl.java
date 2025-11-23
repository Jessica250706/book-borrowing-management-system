package com.xq.web.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.utils.PasswordUtils;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.role.mapper.SysRoleMapper;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.mapper.SysUserMapper;
import com.xq.web.system.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public SysUser validateUser(String account, String password) {
        // 根据账号（用户名、邮箱、手机号）查询用户
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", account)
                .or().eq("account", account); // 修改为 account 字段
        SysUser user = this.getOne(queryWrapper);

        // 验证用户是否存在和密码是否正确
        if (user != null && PasswordUtils.matches(password, user.getPassword())) {
            // 查询角色信息并设置到用户对象中
            setRoleInfo(user);
            return user;
        }
        return null;
    }

    @Override
    @Transactional
    public boolean registerUser(SysUser user) {
        // 检查账号是否已存在
        if (isAccountExists(user.getAccount())) {
            throw new RuntimeException("账号已存在");
        }

        // 检查用户名是否已存在
        if (isUsernameExists(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 设置默认角色（社会人员）
        SysRole defaultRole = getDefaultRole();
        user.setRoleId(defaultRole.getRoleId());
        user.setRoleCode(defaultRole.getRoleCode());

        // 加密密码
        String encodedPassword = PasswordUtils.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 设置用户默认信息
        prepareUserForCreate(user);

        // 生成UID
        String uid = generateUid(defaultRole.getRoleCode());
        user.setUid(uid);

        return this.save(user);
    }

    /**
     * 注册用户（简化版）
     */
    @Override
    @Transactional
    public boolean registerUser(String account, String password, String username) {
        SysUser user = new SysUser();
        user.setAccount(account);
        user.setPassword(password);
        user.setUsername(username);
        return registerUser(user);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);
    }

    /**
     * 检查账号是否已存在
     */
    private boolean isAccountExists(String account) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 检查用户名是否已存在
     */
    private boolean isUsernameExists(String username) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 获取默认角色（社会人员）
     */
    private SysRole getDefaultRole() {
        SysRole role = sysRoleMapper.selectByRoleCode("READER_SOCIAL");
        if (role == null) {
            throw new RuntimeException("默认角色不存在，请检查数据库角色数据");
        }
        return role;
    }

    /**
     * 设置用户创建前的默认信息
     */
    private void prepareUserForCreate(SysUser user) {
        LocalDateTime now = LocalDateTime.now();

        if (user.getRegisterTime() == null) {
            user.setRegisterTime(now);
        }
        if (user.getCreditScore() == null) {
            user.setCreditScore(100);
        }
        if (user.getAccountStatus() == null) {
            user.setAccountStatus(1); // 正常状态
        }
        if (user.getLoginErrorCount() == null) {
            user.setLoginErrorCount(0);
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(now);
        }
        if (user.getUpdateTime() == null) {
            user.setUpdateTime(now);
        }
    }

    /**
     * 生成用户UID
     */
    private String generateUid(String roleCode) {
        String prefix;
        switch (roleCode) {
            case "READER_SOCIAL":
                prefix = "SOC";
                break;
            case "READER_STUDENT":
                prefix = "STU";
                break;
            case "READER_TEACHER":
                prefix = "TEA";
                break;
            case "ADMIN":
                prefix = "ADM";
                break;
            case "SYS_ADMIN":
                prefix = "SYS";
                break;
            default:
                prefix = "USR";
        }
        return prefix + System.currentTimeMillis() % 100000;
    }

    /**
     * 设置角色信息到用户对象
     */
    private void setRoleInfo(SysUser user) {
        if (user.getRoleId() != null) {
            SysRole role = sysRoleMapper.selectByRoleId(user.getRoleId());
            if (role != null) {
                user.setRoleCode(role.getRoleCode());
                user.setRoleName(role.getRoleName());
            }
        }
    }

    /**
     * 根据用户ID获取完整用户信息（包含角色信息）
     */
    public SysUser getUserWithRoleInfo(Long userId) {
        SysUser user = this.getById(userId);
        if (user != null) {
            setRoleInfo(user);
        }
        return user;
    }

    /**
     * 修改密码
     */
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtils.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        String encodedNewPassword = PasswordUtils.encode(newPassword);
        user.setPassword(encodedNewPassword);
        return this.updateById(user);
    }

    /**
     * 重置密码（管理员操作）
     */
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String encodedPassword = PasswordUtils.encode(newPassword);
        user.setPassword(encodedPassword);
        return this.updateById(user);
    }
}
