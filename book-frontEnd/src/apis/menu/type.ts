/**
 * com.xq.web.system.menu.dto.MenuDTO
 *
 * MenuDTO
 */
export interface MenuDTO {
  /**
   * 子菜单列表
   */
  children?: MenuDTO[];
  /**
   * 权限字段（用于角色权限控制）
   */
  code?: string;
  /**
   * 是否隐藏（按钮类型默认隐藏）
   */
  hidden?: boolean;
  /**
   * 菜单图标
   */
  icon?: string;
  /**
   * 菜单ID
   */
  menuId?: number;
  /**
   * 路由元信息
   */
  meta?: MapObject;
  /**
   * 路由名称（唯一标识路由）
   */
  name?: string;
  /**
   * 序号（控制菜单显示顺序）
   */
  orderNum?: number;
  /**
   * 父级菜单ID（0表示一级菜单）
   */
  parentId?: number;
  /**
   * 上级菜单名称（冗余字段，便于前端显示）
   */
  parentName?: string;
  /**
   * 路由path（对应页面访问路径）
   */
  path?: string;
  /**
   * 菜单名称
   */
  title?: string;
  /**
   * 菜单类型（0-目录，1-菜单，2-按钮）
   */
  type?: string;
  /**
   * 组件路径（对应前端组件文件路径）
   */
  url?: string;
  [property: string]: any;
}

/**
 * 路由元信息
 *
 * MapObject
 */
export interface MapObject {
  key?: { [key: string]: any };
  [property: string]: any;
}
