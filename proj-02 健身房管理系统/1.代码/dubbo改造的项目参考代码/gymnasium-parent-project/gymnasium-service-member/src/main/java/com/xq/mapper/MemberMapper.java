package com.xq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.pojo.member.Member;
import com.xq.pojo.member.RechargeParam;
import org.apache.ibatis.annotations.Param;

public interface MemberMapper extends BaseMapper<Member> {

    int addMoney(@Param("param") RechargeParam param);

    void subMoney(@Param("param") RechargeParam param);
}
