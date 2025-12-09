package com.xq.web.borrow.renew.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("book_renew")
public class BookRenew {

    @TableId(value = "renew_id", type = IdType.AUTO)
    private Long renewId;

    @TableField("borrow_id")
    private Long borrowId;

    @TableField("user_id")
    private Long userId;

    @TableField("renew_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date renewTime;

    @TableField("renew_days")
    private Integer renewDays;

    @TableField("before_return_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beforeReturnTime;

    @TableField("after_return_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date afterReturnTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}