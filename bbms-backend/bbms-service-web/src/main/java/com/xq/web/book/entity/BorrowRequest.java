// BorrowRequest.java
package com.xq.web.book.entity;

import lombok.Data;

/**
 * 借阅请求参数
 * 用于书籍借阅和预约操作的请求参数封装
 */
@Data
public class BorrowRequest {
    /**
     * 用户ID
     * 关联sys_user表，必填参数
     */
    private Long userId;
    
    /**
     * 借阅天数
     * 默认为30天，可根据用户角色设置不同天数（如学生20天、老师30天）
     */
    private Integer borrowDays = 30;
}