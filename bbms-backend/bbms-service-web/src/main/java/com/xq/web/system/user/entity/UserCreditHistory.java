package com.xq.web.system.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信誉分历史记录实体类
 *
 * @author xq
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_credit_history")
public class UserCreditHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 历史记录ID
     */
    @TableId(value = "history_id", type = IdType.AUTO)
    private Long historyId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 分数变化值（正数为增加，负数为减少）
     */
    @TableField("change_value")
    private Integer changeValue;

    /**
     * 变更后分数
     */
    @TableField("current_score")
    private Integer currentScore;

    /**
     * 变更原因
     */
    @TableField("change_reason")
    private String changeReason;

    /**
     * 变更时间
     */
    @TableField("change_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime changeTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // ============= 非数据库字段 =============

    /**
     * 用户信息（关联查询）
     */
    @TableField(exist = false)
    private SysUser user;

    /**
     * 变更前分数（计算字段）
     */
    @TableField(exist = false)
    private Integer previousScore;

    /**
     * 变化类型描述（计算字段）
     */
    @TableField(exist = false)
    private String changeTypeDesc;

    /**
     * 变更时间格式化（用于前端显示）
     */
    @TableField(exist = false)
    private String changeTimeFormatted;

    // ============= 业务方法 =============

    /**
     * 获取变更前分数
     */
    public Integer getPreviousScore() {
        if (previousScore == null && currentScore != null && changeValue != null) {
            previousScore = currentScore - changeValue;
        }
        return previousScore;
    }

    /**
     * 设置变更值并自动计算变更前分数
     */
    public void setChangeValue(Integer changeValue) {
        this.changeValue = changeValue;
        if (currentScore != null && changeValue != null) {
            this.previousScore = currentScore - changeValue;
        }
    }

    /**
     * 设置当前分数并自动计算变更前分数
     */
    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
        if (changeValue != null && currentScore != null) {
            this.previousScore = currentScore - changeValue;
        }
    }

    /**
     * 获取变化类型描述
     */
    public String getChangeTypeDesc() {
        if (changeTypeDesc == null && changeValue != null) {
            if (changeValue > 0) {
                changeTypeDesc = "增加";
            } else if (changeValue < 0) {
                changeTypeDesc = "减少";
            } else {
                changeTypeDesc = "不变";
            }
        }
        return changeTypeDesc;
    }

    /**
     * 获取变化的绝对值
     */
    public Integer getChangeAbsoluteValue() {
        return changeValue != null ? Math.abs(changeValue) : 0;
    }

    /**
     * 判断是否是加分记录
     */
    public boolean isIncrease() {
        return changeValue != null && changeValue > 0;
    }

    /**
     * 判断是否是减分记录
     */
    public boolean isDecrease() {
        return changeValue != null && changeValue < 0;
    }

    /**
     * 判断分数是否不变
     */
    public boolean isUnchanged() {
        return changeValue != null && changeValue == 0;
    }

    /**
     * 获取信誉等级（变更后）
     */
    public String getCreditLevel() {
        if (currentScore == null) {
            return "未知";
        }
        if (currentScore >= 90) {
            return "优秀";
        } else if (currentScore >= 80) {
            return "良好";
        } else if (currentScore >= 70) {
            return "一般";
        } else if (currentScore >= 60) {
            return "较差";
        } else {
            return "极差";
        }
    }

    /**
     * 获取变更原因类型（根据关键词判断）
     */
    public String getChangeReasonType() {
        if (changeReason == null) {
            return "其他";
        }

        String reason = changeReason.toLowerCase();
        if (reason.contains("归还") || reason.contains("按时")) {
            return "按时归还";
        } else if (reason.contains("超时") || reason.contains("逾期")) {
            return "借阅超时";
        } else if (reason.contains("预约") || reason.contains("取消")) {
            return "预约相关";
        } else if (reason.contains("违规") || reason.contains("破坏")) {
            return "违规操作";
        } else if (reason.contains("奖励") || reason.contains("加分")) {
            return "奖励加分";
        } else if (reason.contains("处罚") || reason.contains("减分")) {
            return "处罚减分";
        } else if (reason.contains("注册") || reason.contains("初始")) {
            return "初始信誉";
        } else if (reason.contains("修改") || reason.contains("调整")) {
            return "人工调整";
        } else {
            return "其他原因";
        }
    }

    /**
     * 获取变更原因图标（前端使用）
     */
    public String getChangeIcon() {
        if (isIncrease()) {
            return "increase-icon";
        } else if (isDecrease()) {
            return "decrease-icon";
        } else {
            return "unchanged-icon";
        }
    }

    /**
     * 获取变更颜色（前端使用）
     */
    public String getChangeColor() {
        if (isIncrease()) {
            return "#52c41a"; // 绿色
        } else if (isDecrease()) {
            return "#ff4d4f"; // 红色
        } else {
            return "#8c8c8c"; // 灰色
        }
    }

    /**
     * 获取变更符号（+/-）
     */
    public String getChangeSymbol() {
        if (isIncrease()) {
            return "+";
        } else if (isDecrease()) {
            return "-";
        } else {
            return "";
        }
    }

    /**
     * 获取格式化后的变更值（带符号）
     */
    public String getFormattedChangeValue() {
        if (changeValue == null) {
            return "0";
        }
        if (changeValue > 0) {
            return "+" + changeValue;
        } else {
            return String.valueOf(changeValue);
        }
    }

    /**
     * 获取格式化后的变更时间
     */
    public String getChangeTimeFormatted() {
        if (changeTime == null) {
            return "";
        }
        return changeTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取简化的变更时间（用于列表显示）
     */
    public String getChangeTimeSimple() {
        if (changeTime == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(changeTime, now);

        if (duration.toMinutes() < 60) {
            return duration.toMinutes() + "分钟前";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + "小时前";
        } else if (duration.toDays() < 7) {
            return duration.toDays() + "天前";
        } else {
            return changeTime.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }
    }

    /**
     * 创建加分记录
     */
    public static UserCreditHistory createIncreaseRecord(Long userId, Integer increaseValue,
                                                         Integer currentScore, String reason) {
        UserCreditHistory record = new UserCreditHistory();
        record.setUserId(userId);
        record.setChangeValue(increaseValue);
        record.setCurrentScore(currentScore);
        record.setChangeReason(reason);
        record.setChangeTime(LocalDateTime.now());
        return record;
    }

    /**
     * 创建减分记录
     */
    public static UserCreditHistory createDecreaseRecord(Long userId, Integer decreaseValue,
                                                         Integer currentScore, String reason) {
        UserCreditHistory record = new UserCreditHistory();
        record.setUserId(userId);
        record.setChangeValue(-Math.abs(decreaseValue)); // 确保为负值
        record.setCurrentScore(currentScore);
        record.setChangeReason(reason);
        record.setChangeTime(LocalDateTime.now());
        return record;
    }

    /**
     * 创建初始信誉分记录
     */
    public static UserCreditHistory createInitialRecord(Long userId, Integer initialScore) {
        UserCreditHistory record = new UserCreditHistory();
        record.setUserId(userId);
        record.setChangeValue(0);
        record.setCurrentScore(initialScore);
        record.setChangeReason("用户注册初始信誉分");
        record.setChangeTime(LocalDateTime.now());
        return record;
    }

    /**
     * 创建信誉分调整记录
     */
    public static UserCreditHistory createAdjustRecord(Long userId, Integer oldScore,
                                                       Integer newScore, String operator,
                                                       String remark) {
        UserCreditHistory record = new UserCreditHistory();
        record.setUserId(userId);
        record.setChangeValue(newScore - oldScore);
        record.setCurrentScore(newScore);
        record.setChangeReason("管理员[" + operator + "]调整信誉分：" + remark);
        record.setChangeTime(LocalDateTime.now());
        return record;
    }

    /**
     * 从系统用户创建信誉分变更记录
     */
    public static UserCreditHistory fromSysUserChange(SysUser user, Integer changeValue, String reason) {
        if (user == null || user.getUserId() == null || user.getCreditScore() == null) {
            return null;
        }

        UserCreditHistory record = new UserCreditHistory();
        record.setUserId(user.getUserId());
        record.setChangeValue(changeValue);
        record.setCurrentScore(user.getCreditScore());
        record.setChangeReason(reason);
        record.setChangeTime(LocalDateTime.now());
        return record;
    }

    // ============= toString方法（简洁版） =============

    @Override
    public String toString() {
        return "UserCreditHistory{" +
                "historyId=" + historyId +
                ", userId=" + userId +
                ", changeValue=" + getFormattedChangeValue() +
                ", currentScore=" + currentScore +
                ", changeReason='" + changeReason + '\'' +
                ", changeTime=" + getChangeTimeFormatted() +
                '}';
    }
}
