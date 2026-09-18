package com.xq.web.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.web.system.user.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;

public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID删除角色关联
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID删除用户关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 统计用户拥有的角色数量
     */
    int countUserRoles(@Param("userId") Long userId);
}
