package com.xq.web.system.menu.dto;

import lombok.Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MenuDTO {

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 父级菜单ID（0表示一级菜单）
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 权限字段（用于角色权限控制）
     */
    private String code;

    /**
     * 路由名称（唯一标识路由）
     */
    private String name;

    /**
     * 路由path（对应页面访问路径）
     */
    private String path;

    /**
     * 组件路径（对应前端组件文件路径）
     */
    private String url;

    /**
     * 菜单类型（0-目录，1-菜单，2-按钮）
     */
    private String type;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 上级菜单名称（冗余字段，便于前端显示）
     */
    private String parentName;

    /**
     * 序号（控制菜单显示顺序）
     */
    private Integer orderNum;

    // ============= 非数据库字段 =============

    /**
     * 子菜单列表
     */
    private List<MenuDTO> children;

    /**
     * 是否隐藏（按钮类型默认隐藏）
     */
    private Boolean hidden;

    /**
     * 路由元信息
     */
    private Map<String, Object> meta;

    // ============= 业务方法 =============

    /**
     * 判断是否为目录类型
     */
    public boolean isDirectory() {
        return "0".equals(type);
    }

    /**
     * 判断是否为菜单类型
     */
    public boolean isMenu() {
        return "1".equals(type);
    }

    /**
     * 判断是否为按钮类型
     */
    public boolean isButton() {
        return "2".equals(type);
    }

    /**
     * 判断是否为一级菜单（无父级菜单）
     */
    public boolean isRootMenu() {
        return parentId != null && parentId == 0;
    }

    /**
     * 判断是否为子菜单
     */
    public boolean isSubMenu() {
        return parentId != null && parentId > 0;
    }

    /**
     * 初始化元信息
     */
    public void initMeta() {
        if (this.meta == null) {
            this.meta = new HashMap<>();
        }
        this.meta.put("title", this.title);
        this.meta.put("icon", this.icon);
        // 按钮类型默认隐藏
        this.hidden = isButton();

        // 如果是目录，不设置隐藏
        if (isDirectory()) {
            this.hidden = false;
        }
    }

    /**
     * 添加子菜单
     */
    public void addChild(MenuDTO child) {
        if (this.children == null) {
            this.children = new java.util.ArrayList<>();
        }
        this.children.add(child);
    }

    /**
     * 判断是否有子菜单
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
}