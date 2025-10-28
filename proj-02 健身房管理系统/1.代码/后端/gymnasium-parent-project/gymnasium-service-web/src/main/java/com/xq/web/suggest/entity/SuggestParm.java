package com.xq.web.suggest.entity;

import lombok.Data;

@Data
public class SuggestParm {
    private Long currentPage;
    private Long pageSize;
    private String title;
}