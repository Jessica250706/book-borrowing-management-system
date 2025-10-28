package com.xq.web.sys_role.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.sys_role.entity.RoleAssignParam;
import com.xq.web.sys_role.entity.RoleParam;
import com.xq.web.sys_role.entity.RolePermissionVo;
import com.xq.web.sys_role.entity.SysRole;

public interface SysRoleService extends IService<SysRole> {

    IPage<SysRole> list(RoleParam roleParam);

    //查询权限数据回显
    RolePermissionVo getMenuTree(RoleAssignParam param);
}
