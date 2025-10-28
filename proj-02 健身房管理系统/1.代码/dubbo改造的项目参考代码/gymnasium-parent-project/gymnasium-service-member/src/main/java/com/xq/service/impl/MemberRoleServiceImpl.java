package com.xq.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.MemberRoleMapper;
import com.xq.pojo.member_recharge.MemberRole;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class MemberRoleServiceImpl extends ServiceImpl<MemberRoleMapper, MemberRole> implements IService<MemberRole> {
}
