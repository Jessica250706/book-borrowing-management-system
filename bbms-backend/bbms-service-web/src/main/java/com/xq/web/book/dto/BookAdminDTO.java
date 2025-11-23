package com.xq.web.book.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 书籍管理返回DTO（管理员端）
 */
@Data
public class BookAdminDTO {
    private Long bookId;          // 书籍ID
    private String bookName;      // 书名
    private String coverUrl;       // 封面图片URL
    private String author;         // 作者
    private String translator;     // 译者
    private Long categoryId;       // 分类ID
    private String category;       // 分类名称
    private String intro;          // 书籍简介
    private String publisher;      // 出版社
    private String isbn;          // ISBN
    private String copyrightHolder; // 版权持有方
    private Integer publishCount;  // 发行数量
    private String publishUnit;    // 发行单位
    private String publishWebsite; // 发行网站
    private String publishBatch;   // 发行批次
    private BigDecimal price;     // 价格
    private Integer totalCount;    // 总数量
    private Integer availableCount; // 可借阅数量
    private Integer borrowCount;   // 借阅次数
    private Integer bookStatus;    // 书籍状态（0-未发布，1-待上架，2-可借阅，3-已借光）
    private String borrowStatus;   // 借阅状态描述
    private Boolean canBorrow;     // 是否可借阅
    private Date publishDate;      // 出版日期
    private Date shelfTime;        // 上架时间
    private Date createTime;      // 创建时间
    private Date updateTime;      // 更新时间
}