package com.xq.web.book.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书籍分类实体类
 */
@Data
public class BookCategoryDTO {

    /**
     * 分类id
     */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    /**
     * 分类编码
     */
    @TableField("category_code")
    private String categoryCode;

    /**
     * 分类名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 父分类id
     */
    @TableField("parent_id")
    private Long parentId = 0L;

    /**
     * 排序序号
     */
    @TableField("order_num")
    private Integer orderNum = 0;
}