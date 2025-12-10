package com.xq.web.system.menu.service;

import com.xq.web.system.menu.dto.MenuDTO;

import java.util.List;

public interface SysMenuService {
    /**
     * 根据用户ID获取菜单列表
     */
    List<MenuDTO> getMenusByUserId(Long userId);

    /**
     * 根据角色ID获取菜单列表
     */
    List<MenuDTO> getMenusByRoleId(Long roleId);

    /**
     * 获取所有菜单（树形结构）
     */
    List<MenuDTO> getAllMenus();

    /**
     * 获取用户菜单树（包含层级关系）
     */
    List<MenuDTO> getMenuTreeByUserId(Long userId);
}
