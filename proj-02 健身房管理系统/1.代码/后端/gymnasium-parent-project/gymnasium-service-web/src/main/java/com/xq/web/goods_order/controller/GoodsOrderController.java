package com.xq.web.goods_order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.goods.entity.Goods;
import com.xq.web.goods.entity.GoodsParam;
import com.xq.web.goods.service.GoodsService;
import com.xq.web.goods_order.entity.GoodsOrder;
import com.xq.web.goods_order.entity.OrderItem;
import com.xq.web.goods_order.entity.OrderParam;
import com.xq.web.goods_order.service.GoodsOrderService;
import com.xq.web.home.entity.Echart;
import com.xq.web.home.entity.EchartItem;
import com.xq.web.sys_user.entity.SysUser;
import com.xq.web.sys_user.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class GoodsOrderController {

    @Autowired
    private GoodsOrderService goodsOrderService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    GoodsService goodsService;

    //下单
    @PostMapping("/down")
    public ResultVo down(@RequestBody OrderParam param){
        //查询用户信息
        SysUser user = sysUserService.getById(param.getUserId());
        List<OrderItem> list = param.getOrderList();
        List<GoodsOrder> orderList = new ArrayList<>();
        for(int i = 0;i<list.size();i++){
            Long goodsId = list.get(i).getGoodsId();
            Integer num = list.get(i).getNum();
            //查询商品信息
            Goods goods = goodsService.getById(goodsId);
            GoodsOrder order = new GoodsOrder();
            BeanUtils.copyProperties(goods,order);
            order.setNum(list.get(i).getNum());
            BigDecimal number = BigDecimal.valueOf(list.get(i).getNum());
            BigDecimal price = goods.getPrice();
            BigDecimal total = number.multiply(price);
            BigDecimal totalPrice = total.setScale(2, BigDecimal.ROUND_HALF_UP);
            order.setTotalPrice(totalPrice);
            order.setControlUser(user.getNickName());
            orderList.add(order);
        }
        if(orderList.size() > 0){
            goodsOrderService.saveBatch(orderList);
        }
        return ResultUtils.success("下单成功");
    }

    //查询订单列表
    @GetMapping("/list")
    public ResultVo list(GoodsParam param){
        //构造分页对象
        IPage<GoodsOrder> page = new Page<>(param.getCurrentPage(),param.getPageSize());
        //构造查询条件
        QueryWrapper<GoodsOrder> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(param.getName())){
            query.lambda().like(GoodsOrder::getName,param.getName());
        }
        IPage<GoodsOrder> list = goodsOrderService.page(page,query);
        return ResultUtils.success("查询成功",list);
    }



}
