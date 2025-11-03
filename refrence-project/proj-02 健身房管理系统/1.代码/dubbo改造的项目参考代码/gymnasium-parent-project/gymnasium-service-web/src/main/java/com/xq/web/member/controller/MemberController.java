package com.xq.web.member.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.pojo.member.*;
import com.xq.pojo.member_card.MemberCard;
import com.xq.pojo.member_recharge.MemberRecharge;
import com.xq.pojo.member_recharge.MemberRole;
import com.xq.service.member.MemberService;
import com.xq.service.member_card.MemberCardService;
import com.xq.service.member_recharge.MemberRechargeService;
import com.xq.service.member_role.MemberRoleService;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Reference
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    //新增会员
    @PostMapping
    public ResultVo add(@RequestBody Member member){
        // 判断会员卡号是否重复
        QueryWrapper<Member> query = new QueryWrapper<>();
        query.lambda().eq(Member::getUsername,member.getUsername());
        Member one = memberService.getOne(query);
        if(one != null){
            return ResultUtils.error("会员卡号被占用!");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberService.addMember(member);
        return ResultUtils.success("会员信息新增成功");
    }

    //修改会员
    @PutMapping
    public ResultVo edit(@RequestBody Member member){
        QueryWrapper<Member> query = new QueryWrapper<>();
        query.lambda().eq(Member::getUsername,member.getUsername());
        Member one = memberService.getOne(query);
        if(one != null && !one.getMemberId().equals(member.getMemberId())){
            return ResultUtils.error("会员卡号被占用!");
        }
        memberService.editMember(member);
        return ResultUtils.success("修改成功");
    }

    //删除
    @DeleteMapping("/{memberId}")
    public ResultVo delete(@PathVariable("memberId") Long memberId){
        memberService.deleteMember(memberId);
        return ResultUtils.success("删除成功");
    }

    //分页查询
    @GetMapping("/list")
    public ResultVo list(PageParam pageParam){
        if(pageParam.getUserType().equals("1")){
            //构造分页对象
            IPage<Member> page = new Page<>(pageParam.getCurrentPage(),pageParam.getPageSize());
            //构造查询条件
            QueryWrapper<Member> query = new QueryWrapper<>();
            if(StringUtils.isNotEmpty(pageParam.getName())){
                query.lambda().like(Member::getName,pageParam.getName());
            }
            if(StringUtils.isNotEmpty(pageParam.getPhone())){
                query.lambda().like(Member::getPhone,pageParam.getPhone());
            }
            if(StringUtils.isNotEmpty(pageParam.getUsername())){
                query.lambda().like(Member::getUsername,pageParam.getUsername());
            }
            query.lambda().eq(Member::getMemberId,pageParam.getMemberId());
            query.lambda().orderByDesc(Member::getJoinTime);
            IPage<Member> list = memberService.page(page, query);
            return ResultUtils.success("查询成功",list);
        }else{
            //构造分页对象
            IPage<Member> page = new Page<>(pageParam.getCurrentPage(),pageParam.getPageSize());
            //构造查询条件
            QueryWrapper<Member> query = new QueryWrapper<>();
            if(StringUtils.isNotEmpty(pageParam.getName())){
                query.lambda().like(Member::getName,pageParam.getName());
            }
            if(StringUtils.isNotEmpty(pageParam.getPhone())){
                query.lambda().like(Member::getPhone,pageParam.getPhone());
            }
            if(StringUtils.isNotEmpty(pageParam.getUsername())){
                query.lambda().like(Member::getUsername,pageParam.getUsername());
            }
            query.lambda().orderByDesc(Member::getJoinTime);
            IPage<Member> list = memberService.page(page, query);
            return ResultUtils.success("查询成功",list);
        }
    }

    @Reference
    MemberRoleService memberRoleService;

    //根据会员id查询对应的角色id
    @GetMapping("/getRoleByMemberId")
    public ResultVo getRoleByMemberId(Long memberId){
        QueryWrapper<MemberRole> query = new QueryWrapper<>();
        query.lambda().eq(MemberRole::getMemberId,memberId);
        MemberRole one = memberRoleService.getOne(query);
        return ResultUtils.success("查询成功",one);
    }

    @Reference
    MemberCardService memberCardService;

    //查询会员卡列表
    @GetMapping("/getCardList")
    public ResultVo getCardList(){
        QueryWrapper<MemberCard> query = new QueryWrapper<>();
        query.lambda().eq(MemberCard::getStatus,"1");
        List<MemberCard> list = memberCardService.list(query);
        return ResultUtils.success("查询成功",list);
    }

    //办卡提交
    @PostMapping("/joinApply")
    public ResultVo joinApply(@RequestBody JoinParam joinParam) throws ParseException {
        memberService.joinApply(joinParam);
        return ResultUtils.success("办卡成功");
    }

    //充值
    @PostMapping("/recharge")
    public ResultVo recharge(@RequestBody RechargeParam param){
        memberService.recharge(param);
        return ResultUtils.success("充值成功");
    }

    @Reference
    MemberRechargeService memberRechargeService;

    //充值查询
    @GetMapping("getMyRecharge")
    public ResultVo getMyRecharge(RechargeParamList param){
        //判断当前用户是会员还是员工
        if(param.getUserType().equals("1")){ //会员
            IPage<MemberRecharge> list = memberRechargeService.getRechargeByMember(param);
            return ResultUtils.success("查询成功",list);
        }else if(param.getUserType().equals("2")){//员工
            IPage<MemberRecharge> list = memberRechargeService.getRechargeList(param);
            return ResultUtils.success("查询成功",list);
        }else{
            return ResultUtils.error("用户类型不存在");
        }
    }
}
