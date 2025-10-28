package com.xq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.GoodsOrderMapper;
import com.xq.pojo.goods_order.GoodsOrder;
import com.xq.pojo.home.EchartItem;
import com.xq.service.goods_order.GoodsOrderService;
import com.alibaba.dubbo.config.annotation.Service;

import java.util.List;

@Service
public class GoodsOrderServiceImpl extends ServiceImpl<GoodsOrderMapper, GoodsOrder> implements GoodsOrderService {

    @Override
    public List<EchartItem> hotGoods() {
        return this.baseMapper.hotGoods();
    }

    @Override
    public List<EchartItem> hotCards() {
        return this.baseMapper.hotCards();
    }

    @Override
    public List<EchartItem> hotCourse() {
        return this.baseMapper.hotCourse();
    }
}
