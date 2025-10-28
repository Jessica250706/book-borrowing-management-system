package com.xq.web.sys_user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.sys_user.entity.PageParam;
import com.xq.web.sys_user.entity.SysUser;
import com.xq.web.sys_user.mapper.SysUserMapper;
import com.xq.web.sys_user.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public IPage<SysUser> list(PageParam param) {
        //构造分页对象
        IPage<SysUser> page = new Page<>();
        page.setSize(param.getPageSize());
        page.setCurrent(param.getCurrentPage());
        //构造查询条件
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(param.getNickName())){
            query.lambda().like(SysUser::getNickName,param.getNickName());
        }
        if(StringUtils.isNotEmpty(param.getPhone())){
            query.lambda().like(SysUser::getPhone,param.getPhone());
        }
        return this.baseMapper.selectPage(page,query);
    }

    @Override
    public SysUser loadUser(String username) {
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername,username);
        return this.baseMapper.selectOne(query);
    }
}
