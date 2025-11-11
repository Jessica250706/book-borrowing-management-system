// BorrowRequest.java
package com.xq.web.book.entity;

import lombok.Data;

@Data
public class BorrowRequest {
    private Integer userId;
    private Integer borrowDays = 30; // 默认借阅30天
}