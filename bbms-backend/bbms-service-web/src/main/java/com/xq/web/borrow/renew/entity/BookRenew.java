package com.xq.web.borrow.renew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("book_renew")
public class BookRenew {
    @TableId(type = IdType.ASSIGN_ID)  // 改为雪花算法
    private Long renewId;          // 续借id（改为Long类型）
    private Long borrowId;         // 借阅id（改为Long类型）
    private Long userId;           // 用户id（改为Long类型）
    private Date renewTime;           // 续借时间
    private Integer renewDays;        // 续借天数
    private Date beforeReturnTime;    // 续借前预计归还时间
    private Date afterReturnTime;     // 续借后预计归还时间
    private Date createTime;          // 创建时间
}
