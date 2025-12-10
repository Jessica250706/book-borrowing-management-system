package com.xq.web.book.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书籍分类实体类
 */
@Data
@TableName("book_category")
public class BookCategory {

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

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // ============= 非数据库字段 =============

    /**
     * 子分类数量
     */
    @TableField(exist = false)
    private Integer childrenCount;

    /**
     * 父分类名称
     */
    @TableField(exist = false)
    private String parentName;

    // ============= 业务方法 =============

    /**
     * 判断是否为一级分类
     */
    public boolean isRootCategory() {
        return parentId == null || parentId == 0;
    }

    /**
     * 判断是否有父分类
     */
    public boolean hasParent() {
        return parentId != null && parentId > 0;
    }
}