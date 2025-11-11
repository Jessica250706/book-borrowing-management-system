// BookInfo.java
package com.xq.web.book.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("book_info")
public class BookInfo {
    @TableId(type = IdType.AUTO)
    private Integer bookId;

    private String bookName;
    private String coverUrl;
    private String author;
    private String translator;
    private Integer categoryId;
    private Integer bookStatus;  // 0-未发布，1-待上架，2-可借阅，3-已借光
    private Integer totalCount;
    private Integer availableCount;
    private Date shelfTime;
    private String intro;
    private String publisher;
    private String isbn;
    private String copyrightHolder;
    private Integer publishCount;
    private String publishUnit;
    private String publishWebsite;
    private String publishBatch;
    private Date publishDate;
    private BigDecimal price;
    private Integer borrowCount;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}