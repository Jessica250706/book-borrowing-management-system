package com.xq.web.book.dto;

import lombok.Data;

/**
 * 书籍列表返回DTO
 */
@Data
public class BookListDTO {
    private Long bookId;          // 书籍ID
    private String bookName;     // 书名
    private String coverUrl;      // 封面图片URL
    private String author;        // 作者
    private String category;      // 分类
    private String isbn;          // ISBN编号
    private Integer availableCount; // 可借阅数量
    private Integer bookStatus;   // 书籍状态（0-未发布，1-待上架，2-可借阅，3-已借光）
    private String borrowStatus;  // 借阅状态描述（未发布、待上架、可借阅、已借光）
    private Boolean canBorrow;    // 是否可借阅
}