package com.xq.web.sys_menu.entity;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 构造菜单和路由数据的实体类
 */
public class MakeMenuTree {

    //构造菜单树
    public static List<SysMenu> makeTree(List<SysMenu> menuList,long pid){
        List<SysMenu> list = new ArrayList<>();
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && item.getParentId().equals(pid))
                .forEach(item ->{
                    SysMenu menu = new SysMenu();
                    BeanUtils.copyProperties(item,menu);
                    //递归查找下级，自己调用自己
                    List<SysMenu> children = makeTree(menuList, item.getMenuId());
                    menu.setChildren(children);
                    list.add(menu);
                });
        return list;
    }

    //构造路由数据
    public static  List<RouterVo> makeRouter(List<SysMenu> menuList,Long pid){
        //构建存放路由数据的容器
        List<RouterVo> list = new ArrayList<>();
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item ->item != null && item.getParentId().equals(pid))
                .forEach(item ->{
                    RouterVo router = new RouterVo();
                    router.setName(item.getName());
                    router.setPath(item.getPath());
                    //设置children 递归调用，自己调用自己
                    List<RouterVo> children = makeRouter(menuList, item.getMenuId());
                    router.setChildren(children);
                    if(item.getParentId() == 0L){
                        router.setComponent("Layout");
                        //判断该数据是否是菜单类型
                        if(item.getType().equals("1")){
                            router.setRedirect(item.getPath());
                            //菜单也需要设置children
                            List<RouterVo> listChild = new ArrayList<>();
                            RouterVo child = new RouterVo();
                            child.setName(item.getName());
                            child.setPath(item.getPath());
                            child.setComponent(item.getUrl());
                            child.setMeta(child.new Meta(
                                    item.getTitle(),
                                    item.getIcon(),
                                    item.getCode().split(",")
                            ));
                            listChild.add(child);
                            router.setChildren(listChild);
                            router.setPath(item.getPath());
                            router.setName(item.getName() + "parent");
                        }
                    }else {
                        router.setComponent(item.getUrl());
                    }
                    router.setMeta(
                            router.new Meta(
                                    item.getTitle(),
                                    item.getIcon(),
                                    item.getCode().split(",")
                            )
                    );
                    list.add(router);
                });
        return list;
    }
}
