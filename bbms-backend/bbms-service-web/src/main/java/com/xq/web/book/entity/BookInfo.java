// BookInfo.java
package com.xq.web.book.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;/**
 * 书籍信息实体类
 * 对应数据库表book_info，包含书籍的基本信息、出版信息、借阅状态等
 */
@Data
@TableName("book_info")
public class BookInfo {
    /**
     * 书籍ID
     * 主键，自增长
     */
    @TableId(type = IdType.AUTO)
    private Long bookId;

    /**
     * 书籍名称
     * 长度限制1-50字符
     */
    private String bookName;
    
    /**
     * 书籍封面URL
     * 建议比例1:1.42，支持jpg/png/svg/webp格式
     */
    private String coverUrl;
    
    /**
     * 作者
     * 包含国籍，长度限制1-30字符
     */
    private String author;
    
    /**
     * 译者
     * 长度限制1-30字符，可选
     */
    private String translator;
    
    /**
     * 分类ID
     * 关联book_category表
     */
    private Long categoryId;
    
    /**
     * 分类名称
     * 冗余字段，便于前端显示
     */
    private String category;
    
    /**
     * 书籍状态
     * 0-未发布，1-待上架，2-可借阅，3-已借光
     */
    private Integer bookStatus;
    
    /**
     * 书籍总数
     * 范围1-999本
     */
    private Integer totalCount;
    
    /**
     * 可借数量
     * 计算公式：总数-已借数量
     */
    private Integer availableCount;

    /**
     * 上架时间
     * 格式yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shelfTime;


    /**
     * 书籍简介
     * 长度限制1-300字
     */
    private String intro;
    
    /**
     * 出版社
     * 可选
     */
    private String publisher;
    
    /**
     * ISBN编号
     * 唯一标识，可选
     */
    private String isbn;
    
    /**
     * 版权持有方
     * 可选
     */
    private String copyrightHolder;
    
    /**
     * 发行数量
     * 范围1-999，可选
     */
    private Integer publishCount;
    
    /**
     * 发行单位
     * 可选
     */
    private String publishUnit;
    
    /**
     * 发行网站
     * 可选
     */
    private String publishWebsite;
    
    /**
     * 发行批次
     * 可选
     */
    private String publishBatch;

    /**
     * 发行时间
     * 格式yyyy-MM-dd，可选
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publishDate;
    
    /**
     * 数字图书定价
     * 保留两位小数，可选
     */
    private BigDecimal price;
    
    /**
     * 累计借阅次数
     * 统计字段，默认为0
     */
    private Integer borrowCount;

    /**
     * 创建时间
     * 插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     * 插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}