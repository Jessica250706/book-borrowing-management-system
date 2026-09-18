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
     * 书籍名称，必填，1-50字符，可重复
     * 草稿状态可为空
     */
    private String bookName;
    
    /**
     * 书籍封面，必填，比例1:1.42，格式jpg/png/svg/webp，≤2MB
     * 草稿状态可为空，后期支持自动生成默认封面
     */
    private String coverUrl;
    
    /**
     * 作者，必填，1-30字符，可包括国籍
     * 草稿状态可为空
     */
    private String author;
    
    /**
     * 译者，1-30字符
     */
    private String translator;
    
    /**
     * 分类ID，必选，参见分类枚举
     * 草稿状态可为空
     */
    private Long categoryId;
    
    /**
     * 分类名称
     * 冗余字段，便于前端显示，非数据库字段
     */
    @TableField(exist = false)
    private String category;
    
    /**
     * 书籍状态，后端自动赋值，0草稿，1未发布，2待上架，3可借阅，4已借光
     * 0-草稿（保存草稿，必填项可为空，仅管理员可见）
     * 1-未发布（完成保存，必填项已校验，仅管理员可见）
     * 2-待上架（已发布但未到上架时间，读者可见可预约）
     * 3-可借阅（已发布且有库存，读者可见可借阅）
     * 4-已借光（已发布但库存为0，读者可见可预约）
     */
    private Integer bookStatus;
    
    /**
     * 书籍总数，必填，1~999本
     * 草稿状态可为空
     */
    private Integer totalCount;
    
    /**
     * 可借数量
     * 计算公式：总数-已借数量
     */
    private Integer availableCount;

    /**
     * 上架时间，必选，格式yyyy-MM-dd HH:mm:ss
     * 草稿状态可为空
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shelfTime;


    /**
     * 书籍简介，必填，1~300字
     * 草稿状态可为空
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

    /**
     * 软删除标记（0=未删除，1=已删除）
     * 用于软删除，保留历史数据
     * MyBatis Plus 会自动处理软删除逻辑
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("deleted")
    private Byte deleted;
}