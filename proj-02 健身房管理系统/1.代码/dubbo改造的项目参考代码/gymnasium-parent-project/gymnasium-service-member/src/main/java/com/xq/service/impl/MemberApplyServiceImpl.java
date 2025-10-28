package com.xq.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.MemberApplyMapper;
import com.xq.pojo.member_apply.MemberApply;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class MemberApplyServiceImpl extends ServiceImpl<MemberApplyMapper, MemberApply> implements IService<MemberApply> {
}
