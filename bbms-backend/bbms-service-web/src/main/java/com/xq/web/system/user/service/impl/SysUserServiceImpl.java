package com.xq.web.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.utils.PasswordUtils;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.role.mapper.SysRoleMapper;
import com.xq.web.system.user.dto.RegisterRequestVO;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.mapper.SysUserMapper;
import com.xq.web.system.user.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    // 最大登录错误次数
    private static final int MAX_LOGIN_ERROR_COUNT = 5;

    @Override
    public SysUser validateUser(String account, String password) {
        // 根据账号（用户名、邮箱、手机号）查询用户
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", account)
                .or().eq("account", account);
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
    public SysUser login(String account, String password) {
        // 检查账号是否被锁定
        if (isAccountLocked(account)) {
            throw new RuntimeException("账号因登录错误次数过多已被锁定，请稍后重试或联系管理员");
        }

        // 验证用户
        SysUser user = validateUser(account, password);

        if (user != null) {
            // 登录成功，重置错误次数
            recordLoginSuccess(user.getUserId());
            return user;
        } else {
            // 登录失败，记录错误次数
            recordLoginFailure(account);
            return null;
        }
    }

    @Override
    @Transactional
    public void recordLoginSuccess(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLoginErrorCount(0); // 重置错误次数
        this.updateById(user);
    }

    @Override
    @Transactional
    public void recordLoginFailure(String account) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", account)
                .or().eq("account", account);
        SysUser user = this.getOne(queryWrapper);

        if (user != null) {
            // 更新错误次数
            Integer errorCount = user.getLoginErrorCount();
            if (errorCount == null) {
                errorCount = 0;
            }
            user.setLoginErrorCount(errorCount + 1);

            // 如果错误次数达到上限，锁定账号
            if (user.getLoginErrorCount() >= MAX_LOGIN_ERROR_COUNT) {
                user.setAccountStatus(0); // 锁定账号
            }

            this.updateById(user);
        }
    }

    @Override
    public boolean isAccountLocked(String account) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", account)
                .or().eq("account", account);
        SysUser user = this.getOne(queryWrapper);

        if (user != null) {
            // 检查错误次数是否达到上限
            if (user.getLoginErrorCount() != null && user.getLoginErrorCount() >= MAX_LOGIN_ERROR_COUNT) {
                return true;
            }
            // 检查账号状态
            return user.getAccountStatus() != null && user.getAccountStatus() == 0;
        }
        return false;
    }

    @Override
    @Transactional
    public void unlockAccount(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setAccountStatus(1); // 正常状态
        user.setLoginErrorCount(0); // 重置错误次数
        user.setFreezeTime(null);
        user.setUnfreezeTime(null);
        this.updateById(user);
    }

    @Override
    @Transactional
    public SysUser registerUser(RegisterRequestVO request) {
        // 检查账号是否已存在
        if (isAccountExists(request.getAccount())) {
            throw new RuntimeException("账号已存在");
        }

        // 检查用户名是否已存在
        if (isUsernameExists(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 获取默认角色（社会人员）
        SysRole defaultRole = getDefaultRole();
        if (defaultRole == null) {
            throw new RuntimeException("默认角色不存在，请检查数据库角色数据");
        }

        // 创建用户实体
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);

        // 设置角色信息
        user.setRoleId(defaultRole.getRoleId());
        user.setRoleCode(defaultRole.getRoleCode());

        // 加密密码
        String encodedPassword = PasswordUtils.encode(request.getPassword());
        user.setPassword(encodedPassword);

        // 设置用户默认信息
        prepareUserForCreate(user);

        // 生成UID
        String uid = generateUid();
        user.setUid(uid);

        // 保存用户到数据库
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new RuntimeException("用户保存失败");
        }

        // 重新查询用户以获取完整信息（包括数据库生成的ID等）
        SysUser savedUser = this.getById(user.getUserId());
        setRoleInfo(savedUser);

        return savedUser;
    }

    /**
     * 注册用户（简化版）
     */
    @Override
    @Transactional
    public SysUser registerUser(String account, String password, String username) {
        RegisterRequestVO request = new RegisterRequestVO();
        request.setAccount(account);
        request.setPassword(password);
        request.setUsername(username);
        request.setConfirmPassword(password); // 简化版确认密码与密码相同

        return registerUser(request);
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
     * 生成用户UID（不依赖角色身份，支持身份变更）
     */
    private String generateUid() {
        long timestamp = System.currentTimeMillis();
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "U" + timestamp + randomNum;
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

    /**
     * 更新用户角色（支持身份变更）
     */
    @Transactional
    public boolean updateUserRole(Long userId, Long newRoleId) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 查询新角色信息
        SysRole newRole = sysRoleMapper.selectByRoleId(newRoleId);
        if (newRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 更新用户角色
        user.setRoleId(newRoleId);
        user.setRoleCode(newRole.getRoleCode());

        return this.updateById(user);
    }
}
