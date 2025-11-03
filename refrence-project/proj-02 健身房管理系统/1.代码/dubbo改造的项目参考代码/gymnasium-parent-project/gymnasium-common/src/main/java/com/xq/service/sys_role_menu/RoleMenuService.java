package com.xq.service.sys_role_menu;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.pojo.sys_role_menu.RoleMenu;
import com.xq.pojo.sys_role_menu.SaveMenuParam;


public interface RoleMenuService extends IService<RoleMenu> {

    //保存角色权限
    void saveMenu(SaveMenuParam param);
}
