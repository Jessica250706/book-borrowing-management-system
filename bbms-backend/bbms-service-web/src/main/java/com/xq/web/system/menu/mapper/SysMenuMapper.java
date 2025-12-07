// src/main/java/com/xq/web/system/menu/mapper/MenuMapper.java
package com.xq.web.system.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.web.system.menu.dto.MenuDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<MenuDTO> {
    /**
     * 根据用户ID查询菜单列表
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<MenuDTO> selectMenusByUserId(Long userId);

    /**
     * 根据角色ID查询菜单列表
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<MenuDTO> selectMenusByRoleId(Long roleId);
}