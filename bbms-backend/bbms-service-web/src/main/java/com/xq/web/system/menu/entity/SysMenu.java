package com.xq.web.system.menu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@TableName("sys_menu")
public class SysMenu {

    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.AUTO)
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

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // ============= 非数据库字段 =============

    /**
     * 子菜单列表
     */
    @TableField(exist = false)
    private List<SysMenu> children;

    /**
     * 是否隐藏（按钮类型默认隐藏）
     */
    @TableField(exist = false)
    private Boolean hidden;

    /**
     * 路由元信息
     */
    @TableField(exist = false)
    private Map<String, Object> meta;

    // ============= 业务方法 - 类型相关 =============

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

    // ============= 业务方法 - 路径相关 =============

    /**
     * 获取完整的路由路径
     */
    public String getFullPath() {
        if (path == null) {
            return "";
        }
        // 如果是子菜单，需要组合路径（根据实际路由规则调整）
        if (isSubMenu()) {
            return path.startsWith("/") ? path : "/" + path;
        }
        return path;
    }

    /**
     * 判断是否为外部链接
     */
    public boolean isExternalLink() {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    /**
     * 判断是否为内部组件
     */
    public boolean isInternalComponent() {
        return url != null && !isExternalLink();
    }

    // ============= 业务方法 - 元信息相关 =============

    /**
     * 初始化元信息
     * 为前端路由准备meta信息，包括标题、图标等
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
     * 获取菜单标题
     */
    public String getMenuTitle() {
        return title != null ? title : "";
    }

    /**
     * 获取菜单图标
     */
    public String getMenuIcon() {
        return icon != null ? icon : "";
    }

    /**
     * 设置自定义元信息
     */
    public void setMetaValue(String key, Object value) {
        if (this.meta == null) {
            this.meta = new HashMap<>();
        }
        this.meta.put(key, value);
    }

    /**
     * 获取元信息值
     */
    public Object getMetaValue(String key) {
        return this.meta != null ? this.meta.get(key) : null;
    }

    // ============= 业务方法 - 权限相关 =============

    /**
     * 获取权限代码
     */
    public String getPermissionCode() {
        return code != null ? code : "";
    }

    /**
     * 判断是否具有指定权限
     */
    public boolean hasPermission(String permissionCode) {
        return code != null && code.equals(permissionCode);
    }

    // ============= 业务方法 - 父子关系相关 =============

    /**
     * 添加子菜单
     */
    public void addChild(SysMenu child) {
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

    /**
     * 获取子菜单数量
     */
    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }

    // ============= 业务方法 - 排序相关 =============

    /**
     * 获取排序序号（默认0）
     */
    public Integer getSortOrder() {
        return orderNum != null ? orderNum : 0;
    }

    /**
     * 设置排序序号
     */
    public void setSortOrder(Integer order) {
        this.orderNum = order != null ? order : 0;
    }

    // ============= 静态方法 =============

    /**
     * 创建目录菜单
     */
    public static SysMenu createDirectory(String title, String name, String path, String icon) {
        SysMenu menu = new SysMenu();
        menu.setTitle(title);
        menu.setName(name);
        menu.setPath(path);
        menu.setType("0");
        menu.setIcon(icon);
        menu.setParentId(0L);
        menu.initMeta();
        return menu;
    }

    /**
     * 创建普通菜单
     */
    public static SysMenu createMenu(Long parentId, String title, String name, String path,
                                     String url, String code, String icon) {
        SysMenu menu = new SysMenu();
        menu.setParentId(parentId);
        menu.setTitle(title);
        menu.setName(name);
        menu.setPath(path);
        menu.setUrl(url);
        menu.setCode(code);
        menu.setType("1");
        menu.setIcon(icon);
        menu.initMeta();
        return menu;
    }

    /**
     * 创建按钮菜单
     */
    public static SysMenu createButton(Long parentId, String title, String code) {
        SysMenu menu = new SysMenu();
        menu.setParentId(parentId);
        menu.setTitle(title);
        menu.setCode(code);
        menu.setType("2");
        menu.initMeta();
        return menu;
    }

    @Override
    public String toString() {
        return "SysMenu{" +
                "menuId=" + menuId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", path='" + path + '\'' +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                '}';
    }
}
