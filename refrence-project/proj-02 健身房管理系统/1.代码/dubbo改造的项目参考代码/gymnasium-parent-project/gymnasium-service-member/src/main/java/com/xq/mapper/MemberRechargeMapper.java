package com.xq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.pojo.member_recharge.MemberRecharge;
import org.apache.ibatis.annotations.Param;

public interface MemberRechargeMapper extends BaseMapper<MemberRecharge> {

    IPage<MemberRecharge> getRechargeList(IPage<MemberRecharge> page);

    IPage<MemberRecharge> getRechargeByMember(IPage<MemberRecharge> page,@Param("memberId") Long memberId);
}
