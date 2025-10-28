package com.xq.service.member_recharge;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.pojo.member.RechargeParamList;
import com.xq.pojo.member_recharge.MemberRecharge;

public interface MemberRechargeService  extends IService<MemberRecharge> {

    IPage<MemberRecharge> getRechargeList(RechargeParamList paramList);

    IPage<MemberRecharge> getRechargeByMember(RechargeParamList paramList);
}
