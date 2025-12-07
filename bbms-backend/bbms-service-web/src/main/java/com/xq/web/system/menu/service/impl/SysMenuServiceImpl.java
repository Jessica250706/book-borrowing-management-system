package com.xq.web.system.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.system.menu.dto.MenuDTO;
import com.xq.web.system.menu.mapper.SysMenuMapper;
import com.xq.web.system.menu.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, MenuDTO> implements SysMenuService {
    @Override
    public List<MenuDTO> getMenusByUserId(Long userId) {
        return baseMapper.selectMenusByUserId(userId);
    }

    @Override
    public List<MenuDTO> getMenusByRoleId(Long roleId) {
        return baseMapper.selectMenusByRoleId(roleId);
    }

    @Override
    public List<MenuDTO> getAllMenus() {
        QueryWrapper<MenuDTO> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("parent_id", "order_num");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<MenuDTO> getMenuTreeByUserId(Long userId) {
        // 获取用户所有菜单
        List<MenuDTO> menus = getMenusByUserId(userId);

        // 构建菜单树
        return buildMenuTree(menus);
    }

    /**
     * 构建菜单树形结构
     */
    private List<MenuDTO> buildMenuTree(List<MenuDTO> menus) {
        // 按父级ID分组
        Map<Long, List<MenuDTO>> parentMap = menus.stream()
                .collect(Collectors.groupingBy(MenuDTO::getParentId));

        // 找到所有一级菜单（parentId = 0）
        List<MenuDTO> rootMenus = parentMap.getOrDefault(0L, new ArrayList<>());

        // 为每个一级菜单设置子菜单
        for (MenuDTO menu : rootMenus) {
            setChildren(menu, parentMap);
        }

        return rootMenus;
    }

    /**
     * 递归设置子菜单
     */
    private void setChildren(MenuDTO parent, Map<Long, List<MenuDTO>> parentMap) {
        List<MenuDTO> children = parentMap.get(parent.getMenuId());
        if (children != null && !children.isEmpty()) {
            parent.setChildren(children);
            // 递归设置子菜单的子菜单
            for (MenuDTO child : children) {
                setChildren(child, parentMap);
            }
        }
    }
}
