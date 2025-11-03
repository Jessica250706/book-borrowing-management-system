package com.xq.pojo.goods_order;


import lombok.Data;

import java.util.List;

@Data
public class OrderParam {

    private Long userId;
    private List<OrderItem> orderList;
}
