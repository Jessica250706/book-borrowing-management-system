package com.xq.pojo.course;

import lombok.Data;

@Data
public class PageParam {
    private Long currentPage;
    private Long pageSize;
    private String userType;
    private Long userId;
}
