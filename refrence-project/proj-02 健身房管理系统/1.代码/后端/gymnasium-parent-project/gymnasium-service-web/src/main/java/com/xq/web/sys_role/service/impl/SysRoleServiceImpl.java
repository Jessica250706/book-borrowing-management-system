package com.xq.web.sys_role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.sys_menu.entity.MakeMenuTree;
import com.xq.web.sys_menu.entity.SysMenu;
import com.xq.web.sys_menu.service.SysMenuService;
import com.xq.web.sys_role.entity.RoleAssignParam;
import com.xq.web.sys_role.entity.RoleParam;
import com.xq.web.sys_role.entity.RolePermissionVo;
import com.xq.web.sys_role.entity.SysRole;
import com.xq.web.sys_role.mapper.SysRoleMapper;
import com.xq.web.sys_role.service.SysRoleService;
import com.xq.web.sys_user.entity.SysUser;
import com.xq.web.sys_user.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public IPage<SysRole> list(RoleParam param) {
        //构造分页对象
        IPage<SysRole> page = new Page<>();
        page.setSize(param.getPageSize());
        page.setCurrent(param.getCurrentPage());
        //构造查询条件
        QueryWrapper<SysRole> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(param.getRoleName())){
            query.lambda().like(SysRole::getRoleName,param.getRoleName());
        }
        return this.baseMapper.selectPage(page,query);
    }

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysMenuService sysMenuService;

    @Override
    public RolePermissionVo getMenuTree(RoleAssignParam param) {
        //查询用户信息
        SysUser user = sysUserService.getById(param.getUserId());
        List<SysMenu> list = null;
        if(StringUtils.isNotEmpty(user.getIsAdmin()) && user.getIsAdmin().equals("1")){
            //超级管理员，直接查询所有的菜单信息
            list = sysMenuService.list();
        }else{
            list = sysMenuService.getMenuByUserId(user.getUserId());
        }
        //组装树形数据
        List<SysMenu> menuList = MakeMenuTree.makeTree(list,0L);
        //查询角色原来的菜单分配信息
        List<SysMenu> roleList = sysMenuService.getMenuByRoleId(param.getRoleId());
        List<Long> ids = new ArrayList<>();
        Optional.ofNullable(roleList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null)
                .forEach(item -> {
                    ids.add(item.getMenuId());
                });
        //组装数据
        RolePermissionVo vo = new RolePermissionVo();
        vo.setListmenu(menuList);
        vo.setCheckList(ids.toArray());
        return vo;
    }
}
