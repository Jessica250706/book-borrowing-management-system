package com.xq.web.system.menu.controller;

import com.xq.common.context.UserContext;
import com.xq.dto.ResultVo;
import com.xq.web.system.menu.dto.MenuDTO;
import com.xq.web.system.menu.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理
 * @module 系统菜单
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 根据用户ID获取菜单列表
     * 根据用户ID查询其拥有的菜单权限列表
     *
     * @param userId 用户ID
     * @return 用户的菜单列表
     */
    @GetMapping("/user/{userId}")
    public ResultVo<List<MenuDTO>> getMenusByUserId(@PathVariable Long userId) {
        List<MenuDTO> menus = sysMenuService.getMenusByUserId(userId);
        return ResultVo.success(menus);
    }

    /**
     * 根据用户ID获取菜单树
     * 获取用户的菜单列表并以树形结构返回，包含父子层级关系
     *
     * @param userId 用户ID
     * @return 菜单树形结构列表
     */
    @GetMapping("/user/tree/{userId}")
    public ResultVo<List<MenuDTO>> getMenuTreeByUserId(@PathVariable Long userId) {
        List<MenuDTO> menuTree = sysMenuService.getMenuTreeByUserId(userId);
        return ResultVo.success(menuTree);
    }

    /**
     * 根据角色ID获取菜单列表
     * 根据角色ID查询该角色拥有的菜单权限列表
     *
     * @param roleId 角色ID
     * @return 角色的菜单列表
     */
    @GetMapping("/role/{roleId}")
    public ResultVo<List<MenuDTO>> getMenusByRoleId(@PathVariable Long roleId) {
        List<MenuDTO> menus = sysMenuService.getMenusByRoleId(roleId);
        return ResultVo.success(menus);
    }

    /**
     * 获取所有菜单
     * 获取系统中所有的菜单，包括所有类型（目录、菜单、按钮）
     *
     * @return 所有菜单列表
     */
    @GetMapping("/all")
    public ResultVo<List<MenuDTO>> getAllMenus() {
        List<MenuDTO> menus = sysMenuService.getAllMenus();
        return ResultVo.success(menus);
    }

    /**
     * 获取当前用户的菜单
     * 根据当前登录用户的token解析用户ID，获取该用户的菜单树
     *
     * @return 当前用户的菜单树
     */
    @GetMapping("/current")
    public ResultVo<List<MenuDTO>> getCurrentUserMenus() {
        Long userId = UserContext.getUserId();
        List<MenuDTO> menuTree = sysMenuService.getMenuTreeByUserId(userId);
        return ResultVo.success(menuTree);
    }
}
