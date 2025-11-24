package com.xq.web.book.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 书籍详情返回DTO
 */
@Data
public class BookDetailDTO {
    private Long bookId;          // 书籍ID
    private String bookName;      // 书名
    private String coverUrl;       // 封面图片URL
    private String author;         // 作者
    private String translator;     // 译者
    private String category;       // 分类
    private String intro;          // 书籍简介
    private String publisher;      // 出版社
    private String isbn;          // ISBN
    private BigDecimal price;     // 价格
    private Integer totalCount;    // 总数量
    private Integer availableCount; // 可借阅数量
    private Integer borrowCount;   // 累计借阅次数
    private Integer bookStatus;    // 书籍状态（0-未发布，1-待上架，2-可借阅，3-已借光）
    private String borrowStatus;   // 借阅状态描述（未发布、待上架、可借阅、已借光）
    private Boolean canBorrow;     // 是否可借阅
    private Date publishDate;      // 出版日期
    private Date shelfTime;        // 上架时间
}