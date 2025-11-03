package com.xq.web.goods.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.goods.entity.Goods;
import com.xq.web.goods.mapper.GoodsMapper;
import com.xq.web.goods.service.GoodsService;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
}
