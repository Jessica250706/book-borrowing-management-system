/** 路由元信息 */
export interface MapObject {
  key?: { [key: string]: any };
  [property: string]: any;
}

/** 菜单DTO */
export interface MenuDTO {
  children?: MenuDTO[];
  code?: string;
  hidden?: boolean;
  icon?: string;
  menuId?: number;
  meta?: MapObject;
  name?: string;
  orderNum?: number;
  parentId?: number;
  parentName?: string;
  path?: string;
  title?: string;
  type?: string; // 0-目录，1-菜单，2-按钮
  url?: string;
  [property: string]: any;
}