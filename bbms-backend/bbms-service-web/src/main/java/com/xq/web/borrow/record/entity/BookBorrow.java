package com.xq.web.borrow.record.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("book_borrow")
public class BookBorrow {
    @TableId(type = IdType.ASSIGN_ID)  // 改为雪花算法
    private Long borrowId;         // 借阅id（改为Long类型）
    private Long userId;           // 用户id（改为Long类型）
    private Long bookId;           // 书籍id（改为Long类型）
    private Date borrowTime;          // 借阅时间
    private Date expectedReturnTime;  // 预计归还时间
    private Date actualReturnTime;    // 实际归还时间
    private Integer renewCount;       // 续借次数
    private Integer renewDays;        // 累计续借天数
    private Integer borrowStatus;     // 借阅状态（0-借阅中，1-已归还，2-已超时）
    private Integer returnConfirmStatus; // 归还确认状态
    private Long confirmAdminId;   // 确认管理员id（改为Long类型）
    private Date confirmTime;         // 确认时间
    private Date createTime;          // 创建时间
    private Date updateTime;          // 更新时间

    // 关联查询需要的字段（用于显示书籍和用户信息）
    @TableField(exist = false)
    private String bookName;          // 书籍名称

    @TableField(exist = false)
    private String author;            // 作者

    @TableField(exist = false)
    private String coverUrl;          // 书籍封面

    @TableField(exist = false)
    private String categoryName;      // 分类名称

    @TableField(exist = false)
    private String userName;          // 用户名

    @TableField(exist = false)
    private String uid;               // 用户uid

    @TableField(exist = false)
    private String avatar;            // 用户头像
}
