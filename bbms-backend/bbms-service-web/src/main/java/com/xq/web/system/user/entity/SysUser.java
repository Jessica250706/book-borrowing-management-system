package com.xq.web.system.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表实体类
 *
 * @author xq
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户昵称
     */
    @TableField("username")
    private String username;

    /**
     * 登录账号（手机号/邮箱）
     */
    @TableField("account")
    private String account;

    /**
     * 加密后的密码
     */
    @TableField("password")
    private String password;

    /**
     * 角色id
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 用户唯一标识（用于展示）
     */
    @TableField("uid")
    private String uid;

    /**
     * 用户头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 信誉分（默认100分）
     */
    @TableField("credit_score")
    private Integer creditScore;

    /**
     * 账号状态（0-冻结，1-正常）
     */
    @TableField("account_status")
    private Integer accountStatus;

    /**
     * 冻结开始时间
     */
    @TableField("freeze_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime freezeTime;

    /**
     * 冻结结束时间
     */
    @TableField("unfreeze_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime unfreezeTime;

    /**
     * 登录错误次数（最大5次）
     */
    @TableField("login_error_count")
    private Integer loginErrorCount;

    /**
     * 注册时间
     */
    @TableField("register_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

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
     * 角色名称（关联查询）
     */
    @TableField(exist = false)
    private String roleName;

    /**
     * 角色编码（关联查询）
     */
    @TableField(exist = false)
    private String roleCode;

    /**
     * 确认密码（用于注册/修改密码）
     */
    @TableField(exist = false)
    private String confirmPassword;

    /**
     * 验证码（用于登录/注册）
     */
    @TableField(exist = false)
    private String captcha;

    /**
     * 令牌（登录后返回）
     */
    @TableField(exist = false)
    private String token;

    // ============= 业务方法 =============

    /**
     * 判断账号是否被冻结
     */
    public boolean isFrozen() {
        return accountStatus != null && accountStatus == 0;
    }

    /**
     * 判断账号是否正常
     */
    public boolean isNormal() {
        return accountStatus != null && accountStatus == 1;
    }

    /**
     * 判断是否需要解锁（登录错误次数过多）
     */
    public boolean needUnlock() {
        return loginErrorCount != null && loginErrorCount >= 5;
    }

    /**
     * 判断是否在冻结期内
     */
    public boolean isInFreezePeriod() {
        if (freezeTime == null || unfreezeTime == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(freezeTime) && now.isBefore(unfreezeTime);
    }

    /**
     * 获取显示状态文本
     */
    public String getStatusText() {
        if (accountStatus == null) {
            return "未知";
        }
        switch (accountStatus) {
            case 0:
                return "冻结";
            case 1:
                return "正常";
            default:
                return "未知";
        }
    }

    /**
     * 获取信誉等级
     */
    public String getCreditLevel() {
        if (creditScore == null) {
            return "未知";
        }
        if (creditScore >= 90) {
            return "优秀";
        } else if (creditScore >= 80) {
            return "良好";
        } else if (creditScore >= 70) {
            return "一般";
        } else if (creditScore >= 60) {
            return "较差";
        } else {
            return "极差";
        }
    }
}
