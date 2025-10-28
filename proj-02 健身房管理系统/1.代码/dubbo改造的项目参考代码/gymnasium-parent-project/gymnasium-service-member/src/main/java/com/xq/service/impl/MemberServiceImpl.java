package com.xq.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.MemberApplyMapper;
import com.xq.mapper.MemberCardMapper;
import com.xq.mapper.MemberMapper;
import com.xq.pojo.member.JoinParam;
import com.xq.pojo.member.Member;
import com.xq.pojo.member.RechargeParam;
import com.xq.pojo.member_apply.MemberApply;
import com.xq.pojo.member_card.MemberCard;
import com.xq.pojo.member_recharge.MemberRecharge;
import com.xq.pojo.member_recharge.MemberRole;
import com.xq.service.member.MemberService;
import com.xq.service.member_recharge.MemberRechargeService;
import com.xq.service.member_role.MemberRoleService;
import com.xq.service.sys_user.SysUserService;
import org.apache.commons.lang.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Reference
    private MemberRoleService memberRoleService;

    @Override
    @Transactional
    public void addMember(Member member) {
        //新增会员
        int insert = this.baseMapper.insert(member);
        //设置角色信息
        if(insert > 0){
            MemberRole role = new MemberRole();
            role.setMemberId(member.getMemberId());
            role.setRoleId(member.getRoleId());
            memberRoleService.save(role);
        }
    }

    @Override
    @Transactional
    public void editMember(Member member) {
        //编辑会员本身
        int i = this.baseMapper.updateById(member);
        if(i>0){
            //删除原有的旧的关联信息
            QueryWrapper<MemberRole> query = new QueryWrapper();
            query.lambda().eq(MemberRole::getMemberId,member.getMemberId());
            memberRoleService.remove(query);
            //新建新的关联信息
            MemberRole role = new MemberRole();
            role.setMemberId(member.getMemberId());
            role.setRoleId(member.getRoleId());
            memberRoleService.save(role);
        }
    }

    @Override
    public void deleteMember(Long memberId) {
        //删除会员本身的信息
        int i = this.baseMapper.deleteById(memberId);
        //删除关联关系
        if(i>0){
            QueryWrapper<MemberRole> query = new QueryWrapper();
            query.lambda().eq(MemberRole::getMemberId,memberId);
            this.memberRoleService.remove(query);
        }
    }

    @Resource
    private MemberCardMapper memberCardMapper;

    @Resource
    private MemberApplyMapper memberApplyMapper;

    @Reference
    SysUserService sysUserService;

    @Override
    @Transactional
    public void joinApply(JoinParam joinParam) throws ParseException {
        Member select = this.baseMapper.selectById(joinParam.getMemberId());
        //更新会员信息
        MemberCard card = memberCardMapper.selectById(joinParam.getCardId());//获取会员卡相关的信息
        Member member = new Member();
        member.setMemberId(joinParam.getMemberId());
        member.setCardType(card.getTitle());
        member.setCardDay(card.getCardDay());
        String endTime = select.getEndTime();
        Calendar calendar = Calendar.getInstance();
        if(StringUtils.isNotEmpty(endTime)){
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(select.getEndTime());
            calendar.setTime(date);
            calendar.add(Calendar.DATE,card.getCardDay());
        }else{
            Date data = new Date();
            calendar.setTime(data);
            calendar.add(Calendar.DATE,card.getCardDay());
        }
        member.setEndTime(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        this.baseMapper.updateById(member);
        //插入办卡明细
        MemberApply memberApply = new MemberApply();
        memberApply.setCardDay(card.getCardDay());
        memberApply.setCardType(card.getTitle());
        memberApply.setMemberId(joinParam.getMemberId());
        memberApply.setCreateTime(new Date());
        memberApply.setPrice(card.getPrice());
        memberApplyMapper.insert(memberApply);
    }

    @Reference
    MemberRechargeService memberRechargeService;


    @Override
    @Transactional
    public void recharge(RechargeParam param) {
        //SysUser user = sysUserService.getById(param.getUserId());
        //生成充值明细
        MemberRecharge recharge = new MemberRecharge();
        recharge.setMemberId(param.getMemberId());
        recharge.setMoney(param.getMoney());
        recharge.setCreateTime(new Date());
        //recharge.setCreateUser(user.getUsername());
        boolean save = memberRechargeService.save(recharge);
        if(save){
            //会员账号金额更新
            this.baseMapper.addMoney(param);
        }
    }

    //根据会员名称查询会员信息
    @Override
    public Member loadUser(String username) {
        QueryWrapper<Member> query = new QueryWrapper<>();
        query.lambda().eq(Member::getUsername,username);
        return this.baseMapper.selectOne(query);
    }
}
