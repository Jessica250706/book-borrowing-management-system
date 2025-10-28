package com.xq.pojo.lost;

import lombok.Data;

@Data
public class LostParm {
    private Long currentPage;
    private Long pageSize;
    private String lostName;
}