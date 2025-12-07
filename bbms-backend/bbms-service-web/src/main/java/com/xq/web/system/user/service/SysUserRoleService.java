package com.xq.web.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.system.user.entity.SysUserRole;

public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 为用户分配角色
     */
    boolean assignRoleToUser(Long userId, Long roleId);

    /**
     * 更新用户角色
     */
    boolean updateUserRole(Long userId, Long newRoleId);

    /**
     * 删除用户的所有角色
     */
    boolean removeUserRoles(Long userId);

    /**
     * 检查用户是否拥有某个角色
     */
    boolean hasRole(Long userId, Long roleId);
}