package com.xq.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.LostMapper;
import com.xq.pojo.lost.Lost;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class LostServiceImpl extends ServiceImpl<LostMapper, Lost> implements IService<Lost> {
}