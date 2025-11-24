// BookQueryParam.java
package com.xq.web.book.entity;

import lombok.Data;

/**
 * 书籍查询参数
 * 用于书籍列表查询时的条件筛选和分页参数
 */
@Data
public class BookQueryParam {
    /**
     * 当前页码
     * 从1开始计数，默认为1
     */
    private Long currentPage = 1L;
    
    /**
     * 每页显示数量
     * 默认为10，最大不超过100
     */
    private Long pageSize = 10L;
    
    /**
     * 书籍名称
     * 支持模糊查询，可选参数
     */
    private String bookName;
    
    /**
     * 分类ID
     * 关联book_category表，可选参数
     */
    private Long categoryId;
    
    /**
     * 书籍状态
     * 0-未发布，1-待上架，2-可借阅，3-已借光，可选参数
     */
    private Integer bookStatus;
    
    /**
     * 作者名称
     * 支持模糊查询，可选参数
     */
    private String author;
}