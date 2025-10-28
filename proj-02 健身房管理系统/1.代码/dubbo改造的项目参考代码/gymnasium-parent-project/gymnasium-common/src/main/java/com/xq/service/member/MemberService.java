package com.xq.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.pojo.member.JoinParam;
import com.xq.pojo.member.Member;
import com.xq.pojo.member.RechargeParam;

import java.text.ParseException;

public interface MemberService extends IService<Member> {

    void addMember(Member member);
    void editMember(Member member);
    void deleteMember(Long memberId);

    void joinApply(JoinParam joinParam) throws ParseException;

    void recharge(RechargeParam param);

    //根据用户名查询会员
    Member loadUser(String username);
}
