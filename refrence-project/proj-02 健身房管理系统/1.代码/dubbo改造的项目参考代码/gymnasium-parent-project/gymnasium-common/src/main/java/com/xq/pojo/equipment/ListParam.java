package com.xq.pojo.equipment;

import lombok.Data;

@Data
public class ListParam {
    private Long currentPage;
    private Long pageSize;
    private String name;
}
