package com.xq.web.borrow.record.entity;

import lombok.Data;

/**
 * 读者端当前借阅查询参数
 */
@Data
public class CurrentBorrowQueryParam {
    private Long currentPage;         // 当前页码
    private Long pageSize;            // 页面容量
    private String keyword;           // 搜索关键词（书名/作者）
    private Long categoryId;          // 书籍分类id
    private String categoryCode;      // 书籍分类编码
    private String sortField = "expectedReturnTime"; // 默认按预计归还时间排序
    private String sortOrder = "asc"; // 默认升序（先到期的在前）
}
