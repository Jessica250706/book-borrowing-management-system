package com.xq.web.system.role.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.system.role.entity.RoleParam;
import com.xq.web.system.role.entity.SysRole;

public interface SysRoleService extends IService<SysRole> {
    IPage<SysRole> list(RoleParam param);

    /**
     * 根据角色ID获取角色详情
     * @param roleId 角色ID
     * @return 角色信息
     */
    SysRole getRoleDetailById(Long roleId);

    /**
     * 根据角色编码获取角色详情
     * @param roleCode 角色编码
     * @return 角色信息
     */
    SysRole getRoleDetailByCode(String roleCode);
}
