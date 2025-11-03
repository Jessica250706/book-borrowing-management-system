package com.xq.pojo.goods;

import lombok.Data;

@Data
public class GoodsParam {

    private Long currentPage;
    private Long pageSize;
    private String name;
}
