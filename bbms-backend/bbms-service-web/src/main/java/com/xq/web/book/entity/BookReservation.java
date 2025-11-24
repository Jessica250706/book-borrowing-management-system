// BookReservation.java
package com.xq.web.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 图书预约实体类
 * 对应数据库表：book_reservation
 */
@Data
@TableName("book_reservation")
public class BookReservation {
    /**
     * 预约ID
     * 主键，使用雪花算法生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long reservationId;
    
    /**
     * 用户ID
     * 关联sys_user表
     */
    private Long userId;
    
    /**
     * 图书ID
     * 关联book_info表
     */
    private Long bookId;
    
    /**
     * 预约时间
     * 用户发起预约的时间
     */
    private Date reservationTime;
    
    /**
     * 预约状态
     * 0-待处理 1-已确认（可借阅） 2-已取消 3-已过期
     */
    private Integer reservationStatus;
    
    /**
     * 预约失效时间
     * 超过此时间未借阅则预约自动失效
     */
    private Date invalidTime;
    
    /**
     * 排队序号
     * 同一本书的预约排队序号
     */
    private Integer queueNumber;
    
    /**
     * 通知状态
     * 0-未发送 1-已发送
     */
    private Integer notifyStatus;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}