package com.xq.web.borrow.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@TableName("book_borrow")
public class BookBorrow {

    /**
     * 借阅id
     */
    @TableId(value = "borrow_id", type = IdType.AUTO)
    private Long borrowId;

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
     * 借阅时间
     */
    @TableField("borrow_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime borrowTime;

    /**
     * 预计归还时间（=借阅时间+可借天数）
     */
    @TableField("expected_return_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectedReturnTime;

    /**
     * 实际归还时间（null-未归还）
     */
    @TableField("actual_return_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualReturnTime;

    /**
     * 续借次数
     */
    @TableField("renew_count")
    private Integer renewCount = 0;

    /**
     * 累计续借天数
     */
    @TableField("renew_days")
    private Integer renewDays = 0;

    /**
     * 借阅状态（0-借阅中，1-已归还，2-已超时，3-归还待确认）
     */
    @TableField("borrow_status")
    private Integer borrowStatus;

    /**
     * 操作类型：1-借阅，2-续借，3-归还
     */
    @TableField("operation_type")
    private Integer operationType;

    /**
     * 读者申请归还时间
     */
    @TableField("return_apply_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnApplyTime;

    /**
     * 归还确认状态（0-待确认，1-已确认，仅管理员操作）
     */
    @TableField("return_confirm_status")
    private Integer returnConfirmStatus = 0;

    /**
     * 确认管理员id（关联sys_user表，return_confirm_status=1时必填）
     */
    @TableField("confirm_admin_id")
    private Long confirmAdminId;

    /**
     * 确认时间
     */
    @TableField("confirm_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;

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
     * 分类ID
     */
    @TableField(exist = false)
    private Long categoryId;

    /**
     * 分类名称
     */
    @TableField(exist = false)
    private String categoryName;

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
     * 用户头像
     */
    @TableField(exist = false)
    private String avatar;

    /**
     * 管理员名称（确认操作的管理员）
     */
    @TableField(exist = false)
    private String adminName;

    /**
     * 角色最大续借天数（非数据库字段，从sys_role表关联获取）
     */
    @TableField(exist = false)
    private Integer roleMaxRenewDays;

    /**
     * 角色最大借阅天数（非数据库字段，从sys_role表关联获取）
     */
    @TableField(exist = false)
    private Integer roleMaxBorrowDays;

    /**
     * 角色最大借阅本数（非数据库字段，从sys_role表关联获取）
     */
    @TableField(exist = false)
    private Integer roleMaxBorrowNum;

    // ============= 业务方法 - 状态相关 =============

    /**
     * 判断是否借阅中
     */
    public boolean isBorrowing() {
        return borrowStatus != null && borrowStatus == 0;
    }

    /**
     * 判断是否已归还
     */
    public boolean isReturned() {
        return borrowStatus != null && borrowStatus == 1;
    }

    /**
     * 判断是否已超时
     */
    public boolean isOverdue() {
        return borrowStatus != null && borrowStatus == 2;
    }

    /**
     * 判断是否归还待确认
     */
    public boolean isReturnPending() {
        return borrowStatus != null && borrowStatus == 3;
    }

    /**
     * 判断是否需要管理员确认归还
     */
    public boolean needAdminConfirm() {
        return returnConfirmStatus != null && returnConfirmStatus == 0;
    }

    /**
     * 判断是否已确认归还
     */
    public boolean isReturnConfirmed() {
        return returnConfirmStatus != null && returnConfirmStatus == 1;
    }

    /**
     * 判断是否可续借
     */
    public boolean canRenew() {
        return isBorrowing() && !isOverdue();
    }

    /**
     * 判断是否可归还
     */
    public boolean canReturn() {
        return isBorrowing() || isOverdue();
    }

    /**
     * 判断是否超时
     */
    public boolean isActuallyOverdue() {
        if (expectedReturnTime == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(expectedReturnTime);
    }

    /**
     * 获取剩余借阅天数
     */
    public Integer getRemainingDays() {
        if (expectedReturnTime == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expectedReturnTime)) {
            return 0;
        }
        return Math.toIntExact(Duration.between(now, expectedReturnTime).toDays());
    }

    /**
     * 获取剩余借阅小时数
     */
    public Long getRemainingHours() {
        if (expectedReturnTime == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expectedReturnTime)) {
            return 0L;
        }
        return java.time.Duration.between(now, expectedReturnTime).toHours();
    }

    /**
     * 获取超时天数
     */
    public Long getOverdueDays() {
        if (expectedReturnTime == null || !isActuallyOverdue()) {
            return 0L;
        }
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(expectedReturnTime, now).toDays();
    }

    // ============= 业务方法 - 操作类型相关 =============

    /**
     * 判断是否为借阅操作
     */
    public boolean isBorrowOperation() {
        return operationType != null && operationType == 1;
    }

    /**
     * 判断是否为续借操作
     */
    public boolean isRenewOperation() {
        return operationType != null && operationType == 2;
    }

    /**
     * 判断是否为归还操作
     */
    public boolean isReturnOperation() {
        return operationType != null && operationType == 3;
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
                return "借阅";
            case 2:
                return "续借";
            case 3:
                return "归还";
            default:
                return "未知";
        }
    }

    /**
     * 获取借阅状态文本
     */
    public String getBorrowStatusText() {
        if (borrowStatus == null) {
            return "未知";
        }
        switch (borrowStatus) {
            case 0:
                return "借阅中";
            case 1:
                return "已归还";
            case 2:
                return "已超时";
            case 3:
                return "归还待确认";
            default:
                return "未知";
        }
    }

    /**
     * 获取确认状态文本
     */
    public String getConfirmStatusText() {
        if (returnConfirmStatus == null) {
            return "未知";
        }
        switch (returnConfirmStatus) {
            case 0:
                return "待确认";
            case 1:
                return "已确认";
            default:
                return "未知";
        }
    }

    // ============= 业务方法 - 操作相关 =============

    /**
     * 执行借阅操作
     */
    public void doBorrow(Long userId, Long bookId, LocalDateTime expectedReturnTime) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowTime = LocalDateTime.now();
        this.expectedReturnTime = expectedReturnTime;
        this.borrowStatus = 0; // 借阅中
        this.operationType = 1; // 借阅操作
        this.returnConfirmStatus = 0; // 待确认
        this.renewCount = 0;
        this.renewDays = 0;
    }

    /**
     * 执行续借操作
     */
    public void doRenew(Integer renewDays) {
        this.renewCount = (this.renewCount == null ? 0 : this.renewCount) + 1;
        this.renewDays = (this.renewDays == null ? 0 : this.renewDays) + renewDays;
        this.expectedReturnTime = this.expectedReturnTime.plusDays(renewDays);
        this.operationType = 2; // 续借操作
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 执行归还申请操作
     */
    public void doReturnApply() {
        this.returnApplyTime = LocalDateTime.now();
        this.borrowStatus = 3; // 归还待确认
        this.operationType = 3; // 归还操作
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 执行管理员确认归还操作
     */
    public void doReturnConfirm(Long adminId) {
        this.actualReturnTime = LocalDateTime.now();
        this.borrowStatus = 1; // 已归还
        this.returnConfirmStatus = 1; // 已确认
        this.confirmAdminId = adminId;
        this.confirmTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 标记为超时
     */
    public void markAsOverdue() {
        this.borrowStatus = 2; // 已超时
        this.updateTime = LocalDateTime.now();
    }

    // ============= 业务方法 - 验证相关 =============

    /**
     * 验证是否可以续借
     */
    public boolean validateRenew(Integer maxRenewDays) {
        if (!canRenew()) {
            return false;
        }
        if (maxRenewDays != null && renewDays != null && renewDays >= maxRenewDays) {
            return false;
        }
        return true;
    }

    /**
     * 验证是否可以归还
     */
    public boolean validateReturn() {
        return canReturn() && !isReturnPending();
    }

    /**
     * 验证是否可以确认归还
     */
    public boolean validateReturnConfirm() {
        return isReturnPending() && needAdminConfirm();
    }

    // ============= 静态方法 =============

    /**
     * 创建新的借阅记录
     */
    public static BookBorrow createBorrowRecord(Long userId, Long bookId, Integer borrowDays) {
        BookBorrow record = new BookBorrow();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedReturnTime = now.plusDays(borrowDays);

        record.doBorrow(userId, bookId, expectedReturnTime);
        return record;
    }

    /**
     * 创建续借记录
     */
    public static BookBorrow createRenewRecord(BookBorrow original, Integer renewDays) {
        BookBorrow renewRecord = new BookBorrow();
        renewRecord.setUserId(original.getUserId());
        renewRecord.setBookId(original.getBookId());
        renewRecord.setBorrowTime(original.getBorrowTime());
        renewRecord.setExpectedReturnTime(original.getExpectedReturnTime().plusDays(renewDays));
        renewRecord.setRenewCount(original.getRenewCount() + 1);
        renewRecord.setRenewDays(original.getRenewDays() + renewDays);
        renewRecord.setBorrowStatus(original.getBorrowStatus());
        renewRecord.setOperationType(2); // 续借操作
        renewRecord.setReturnConfirmStatus(original.getReturnConfirmStatus());
        return renewRecord;
    }

    @Override
    public String toString() {
        return "BookBorrow{" +
                "borrowId=" + borrowId +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", borrowStatus=" + borrowStatus +
                ", operationType=" + operationType +
                ", borrowTime=" + borrowTime +
                ", expectedReturnTime=" + expectedReturnTime +
                ", actualReturnTime=" + actualReturnTime +
                ", renewCount=" + renewCount +
                ", renewDays=" + renewDays +
                '}';
    }
}