package com.xq.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.GoodsMapper;
import com.xq.pojo.goods.Goods;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IService<Goods> {
}
