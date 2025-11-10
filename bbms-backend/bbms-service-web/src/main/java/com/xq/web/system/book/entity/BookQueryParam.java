// BookQueryParam.java
package com.xq.web.system.book.entity;

import lombok.Data;

@Data
public class BookQueryParam {
    private Long currentPage = 1L;
    private Long pageSize = 10L;
    private String bookName;
    private Integer categoryId;
    private Integer bookStatus;
    private String author;
}