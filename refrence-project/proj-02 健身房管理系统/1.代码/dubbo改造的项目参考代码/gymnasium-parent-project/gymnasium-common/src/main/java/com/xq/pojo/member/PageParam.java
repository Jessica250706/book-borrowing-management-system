package com.xq.pojo.member;

import lombok.Data;

@Data
public class PageParam {

    private Long currentPage;
    private Long pageSize;
    private String name;
    private String phone;
    private String username;
    private String userType;
    private String memberId;
}
