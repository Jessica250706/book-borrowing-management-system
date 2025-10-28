package com.xq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.pojo.goods_order.GoodsOrder;
import com.xq.pojo.home.EchartItem;

import java.util.List;

public interface GoodsOrderMapper extends BaseMapper<GoodsOrder> {

    //查询热销商品
    List<EchartItem> hotGoods();
    //查询热销卡
    List<EchartItem> hotCards();
    //查询热销课程
    List<EchartItem> hotCourse();
}
