package com.xq.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.MemberRechargeMapper;
import com.xq.pojo.member.RechargeParamList;
import com.xq.pojo.member_recharge.MemberRecharge;
import com.xq.service.member_recharge.MemberRechargeService;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class MemberRechargeServiceImpl extends ServiceImpl<MemberRechargeMapper, MemberRecharge> implements MemberRechargeService {

    @Override
    public IPage<MemberRecharge> getRechargeList(RechargeParamList paramList) {
        //构造分页对象
        IPage<MemberRecharge> page = new Page<>(paramList.getCurrentPage(),paramList.getPageSize());
        return this.baseMapper.getRechargeList(page);
    }

    @Override
    public IPage<MemberRecharge> getRechargeByMember(RechargeParamList paramList) {
        //构造分页对象
        IPage<MemberRecharge> page = new Page<>(paramList.getCurrentPage(),paramList.getPageSize());
        return this.baseMapper.getRechargeByMember(page,paramList.getMemberId());
    }
}
