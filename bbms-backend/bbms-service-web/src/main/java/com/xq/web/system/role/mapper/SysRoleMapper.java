package com.xq.web.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.web.system.role.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 角色Mapper
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    /**
     * 根据角色编码查询角色
     */
    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode}")
    SysRole selectByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据角色ID查询角色
     */
    @Select("SELECT * FROM sys_role WHERE role_id = #{roleId}")
    SysRole selectByRoleId(@Param("roleId") Long roleId);
}
