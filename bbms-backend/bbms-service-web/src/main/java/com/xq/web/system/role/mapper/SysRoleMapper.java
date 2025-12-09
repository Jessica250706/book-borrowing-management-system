package com.xq.web.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.web.system.role.dto.SysRoleDetailDTO;
import com.xq.web.system.role.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 角色Mapper
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色编码查询角色
     */
    @Select("SELECT role_id, role_code, role_name, max_borrow_num, max_borrow_days, max_renew_days, remark " +
            "FROM sys_role WHERE role_code = #{roleCode}")
    SysRoleDetailDTO selectByRoleCode(String roleCode);

    /**
     * 根据角色ID查询角色
     */
    @Select("SELECT role_id, role_code, role_name, max_borrow_num, max_borrow_days, max_renew_days, remark " +
            "FROM sys_role WHERE role_id = #{roleId}")
    SysRoleDetailDTO selectByRoleId(Long roleId);

}
