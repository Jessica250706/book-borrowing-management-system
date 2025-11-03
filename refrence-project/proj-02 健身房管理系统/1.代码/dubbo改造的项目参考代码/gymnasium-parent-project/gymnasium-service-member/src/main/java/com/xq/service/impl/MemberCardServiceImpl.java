package com.xq.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.MemberCardMapper;
import com.xq.pojo.member_card.MemberCard;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class MemberCardServiceImpl extends ServiceImpl<MemberCardMapper, MemberCard> implements IService<MemberCard> {
}
