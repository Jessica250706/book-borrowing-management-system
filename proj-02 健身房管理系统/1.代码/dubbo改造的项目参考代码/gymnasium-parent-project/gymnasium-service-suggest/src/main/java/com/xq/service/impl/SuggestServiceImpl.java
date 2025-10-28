package com.xq.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.SuggestMapper;
import com.xq.pojo.suggest.Suggest;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class SuggestServiceImpl extends ServiceImpl<SuggestMapper, Suggest> implements IService<Suggest> {
}