package com.xq.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.SysUserRoleMapper;
import com.xq.pojo.sys_user_role.SysUserRole;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements IService<SysUserRole> {
}
