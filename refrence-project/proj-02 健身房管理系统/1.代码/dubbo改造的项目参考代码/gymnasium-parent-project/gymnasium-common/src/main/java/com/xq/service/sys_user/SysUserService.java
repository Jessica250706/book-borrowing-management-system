package com.xq.service.sys_user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.pojo.sys_user.PageParam;
import com.xq.pojo.sys_user.SysUser;


public interface SysUserService extends IService<SysUser> {

    IPage<SysUser> list(PageParam param);

    //根据员工姓名查询员工信息
    SysUser loadUser(String username);

}
