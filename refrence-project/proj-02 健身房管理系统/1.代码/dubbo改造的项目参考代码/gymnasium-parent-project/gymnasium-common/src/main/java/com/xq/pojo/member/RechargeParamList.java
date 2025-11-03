package com.xq.pojo.member;

import lombok.Data;

@Data
public class RechargeParamList {
    private Long currentPage;
    private Long pageSize;
    private Long memberId;
    private String userType;
}