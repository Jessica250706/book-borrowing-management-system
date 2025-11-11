package com.xq.web.system.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.entity.SysUserDTO;

public interface SysUserService extends IService<SysUser> {

    /**
     * 验证用户账号密码
     */
    SysUser validateUser(String account, String password);

    /**
     * 注册用户
     */
    boolean registerUser(String account, String password);

    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(Long userId);
}
