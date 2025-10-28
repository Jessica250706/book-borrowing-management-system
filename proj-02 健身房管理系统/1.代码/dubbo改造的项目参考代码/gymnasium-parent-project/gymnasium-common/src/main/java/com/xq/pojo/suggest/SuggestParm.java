package com.xq.pojo.suggest;

import lombok.Data;

@Data
public class SuggestParm {
    private Long currentPage;
    private Long pageSize;
    private String title;
}