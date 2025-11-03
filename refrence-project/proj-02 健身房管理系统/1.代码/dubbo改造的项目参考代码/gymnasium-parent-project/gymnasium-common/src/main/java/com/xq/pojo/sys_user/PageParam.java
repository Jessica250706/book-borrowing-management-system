package com.xq.pojo.sys_user;

import lombok.Data;

@Data
public class PageParam {

    private Long currentPage;
    private Long pageSize;
    private String phone;
    private String nickName;
}
