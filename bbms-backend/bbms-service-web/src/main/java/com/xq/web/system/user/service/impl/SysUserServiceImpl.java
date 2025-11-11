package com.xq.web.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.utils.PasswordUtils;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.mapper.SysUserMapper;
import com.xq.web.system.user.service.SysUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser validateUser(String account, String password) {
        // 根据账号（用户名、邮箱、手机号）查询用户
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", account)
                .or().eq("email", account)
                .or().eq("phone", account);
        SysUser user = this.getOne(queryWrapper);

        // 验证用户是否存在和密码是否正确
        if (user != null && PasswordUtils.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public boolean registerUser(String account, String password) {
        // 检查账号是否已存在
        if (isAccountExists(account)) {
            throw new RuntimeException("账号已存在");
        }

        // 创建新用户
        SysUser user = new SysUser();
        user.setUsername(account);
        user.setPassword(PasswordUtils.encode(password));
        user.setAccountStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now()); // 使用 LocalDateTime

        // 设置默认角色（根据业务需求调整）
        user.setRoleId(2L); // 默认普通用户角色
        user.setRoleCode("USER");
        user.setRoleName("普通用户");

        return this.save(user);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setLastLoginTime(LocalDateTime.now()); // 使用 LocalDateTime
        this.updateById(user);
    }

    /**
     * 检查账号是否已存在
     */
    private boolean isAccountExists(String account) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", account)
                .or().eq("email", account)
                .or().eq("phone", account);
        return this.count(queryWrapper) > 0;
    }
}