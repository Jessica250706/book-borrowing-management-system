package com.xq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.pojo.sys_menu.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    //根据员工id查询菜单数据
    List<SysMenu> getMenuByUserId(@Param("userId") Long userId);
    //根据会员id查询菜单数据
    List<SysMenu> getMenuByMemberId(@Param("userId") Long userId);
    //根据角色id查询菜单数据
    List<SysMenu> getMenuByRoleId(@Param("roleId") Long roleId);
}
