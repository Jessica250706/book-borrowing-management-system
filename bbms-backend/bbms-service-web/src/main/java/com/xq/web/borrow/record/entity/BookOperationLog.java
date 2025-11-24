package com.xq.web.borrow.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书籍操作日志表实体类
 *
 * @author xq
 */
@Data
@TableName("book_operation_log")
public class BookOperationLog {

    /**
     * 日志id
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 用户id（关联sys_user表）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 书籍id（关联book_info表）
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 操作类型：1-预约，2-取消预约，3-借阅，4-续借，5-归还
     */
    @TableField("operation_type")
    private Integer operationType;

    /**
     * 操作时间
     */
    @TableField("operation_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    /**
     * 操作描述
     */
    @TableField("operation_desc")
    private String operationDesc;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // ============= 非数据库字段 =============

    /**
     * 用户名
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户uid
     */
    @TableField(exist = false)
    private String uid;

    /**
     * 书籍名称
     */
    @TableField(exist = false)
    private String bookName;

    /**
     * 作者
     */
    @TableField(exist = false)
    private String author;

    /**
     * 书籍封面
     */
    @TableField(exist = false)
    private String coverUrl;

    /**
     * 分类名称
     */
    @TableField(exist = false)
    private String categoryName;

    // ============= 业务方法 - 操作类型相关 =============

    /**
     * 判断是否为预约操作
     */
    public boolean isReservationOperation() {
        return operationType != null && operationType == 1;
    }

    /**
     * 判断是否为取消预约操作
     */
    public boolean isCancelReservationOperation() {
        return operationType != null && operationType == 2;
    }

    /**
     * 判断是否为借阅操作
     */
    public boolean isBorrowOperation() {
        return operationType != null && operationType == 3;
    }

    /**
     * 判断是否为续借操作
     */
    public boolean isRenewOperation() {
        return operationType != null && operationType == 4;
    }

    /**
     * 判断是否为归还操作
     */
    public boolean isReturnOperation() {
        return operationType != null && operationType == 5;
    }

    /**
     * 获取操作类型文本
     */
    public String getOperationTypeText() {
        if (operationType == null) {
            return "未知";
        }
        switch (operationType) {
            case 1:
                return "预约";
            case 2:
                return "取消预约";
            case 3:
                return "借阅";
            case 4:
                return "续借";
            case 5:
                return "归还";
            default:
                return "未知";
        }
    }

    /**
     * 获取操作类型图标或颜色（用于前端显示）
     */
    public String getOperationTypeColor() {
        if (operationType == null) {
            return "default";
        }
        switch (operationType) {
            case 1: // 预约
                return "blue";
            case 2: // 取消预约
                return "orange";
            case 3: // 借阅
                return "green";
            case 4: // 续借
                return "purple";
            case 5: // 归还
                return "cyan";
            default:
                return "default";
        }
    }

    // ============= 业务方法 - 创建日志 =============

    /**
     * 创建预约操作日志
     */
    public static BookOperationLog createReservationLog(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = new BookOperationLog();
        log.setUserId(userId);
        log.setBookId(bookId);
        log.setOperationType(1); // 预约
        log.setOperationTime(LocalDateTime.now());
        log.setOperationDesc(operationDesc);
        return log;
    }

    /**
     * 创建取消预约操作日志
     */
    public static BookOperationLog createCancelReservationLog(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = new BookOperationLog();
        log.setUserId(userId);
        log.setBookId(bookId);
        log.setOperationType(2); // 取消预约
        log.setOperationTime(LocalDateTime.now());
        log.setOperationDesc(operationDesc);
        return log;
    }

    /**
     * 创建借阅操作日志
     */
    public static BookOperationLog createBorrowLog(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = new BookOperationLog();
        log.setUserId(userId);
        log.setBookId(bookId);
        log.setOperationType(3); // 借阅
        log.setOperationTime(LocalDateTime.now());
        log.setOperationDesc(operationDesc);
        return log;
    }

    /**
     * 创建续借操作日志
     */
    public static BookOperationLog createRenewLog(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = new BookOperationLog();
        log.setUserId(userId);
        log.setBookId(bookId);
        log.setOperationType(4); // 续借
        log.setOperationTime(LocalDateTime.now());
        log.setOperationDesc(operationDesc);
        return log;
    }

    /**
     * 创建归还操作日志
     */
    public static BookOperationLog createReturnLog(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = new BookOperationLog();
        log.setUserId(userId);
        log.setBookId(bookId);
        log.setOperationType(5); // 归还
        log.setOperationTime(LocalDateTime.now());
        log.setOperationDesc(operationDesc);
        return log;
    }

    /**
     * 创建通用操作日志
     */
    public static BookOperationLog createLog(Long userId, Long bookId, Integer operationType, String operationDesc) {
        BookOperationLog log = new BookOperationLog();
        log.setUserId(userId);
        log.setBookId(bookId);
        log.setOperationType(operationType);
        log.setOperationTime(LocalDateTime.now());
        log.setOperationDesc(operationDesc);
        return log;
    }

    // ============= 业务方法 - 描述生成 =============

    /**
     * 生成默认操作描述
     */
    public String generateDefaultDescription() {
        String typeText = getOperationTypeText();
        if (userName != null && bookName != null) {
            return String.format("用户【%s】%s了书籍《%s》", userName, typeText, bookName);
        } else {
            return String.format("用户%s了书籍", typeText);
        }
    }

    /**
     * 设置默认描述（如果描述为空）
     */
    public void setDefaultDescriptionIfEmpty() {
        if (operationDesc == null || operationDesc.trim().isEmpty()) {
            this.operationDesc = generateDefaultDescription();
        }
    }

    // ============= 业务方法 - 验证相关 =============

    /**
     * 验证日志数据完整性
     */
    public boolean validate() {
        return userId != null && bookId != null && operationType != null;
    }

    /**
     * 准备保存（设置默认值）
     */
    public void prepareForSave() {
        if (operationTime == null) {
            operationTime = LocalDateTime.now();
        }
        setDefaultDescriptionIfEmpty();
    }

    @Override
    public String toString() {
        return "BookOperationLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", operationType=" + operationType +
                ", operationTime=" + operationTime +
                ", operationDesc='" + operationDesc + '\'' +
                '}';
    }
}
