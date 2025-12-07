package com.xq.web.system.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xq.web.system.role.dto.RoleEnum;
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
    private Integer creditScore = 100;

    /**
     * 账号状态（0-冻结，1-正常，2-停用，3-注销）
     */
    @TableField("account_status")
    private Integer accountStatus = 1;

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
    private Integer loginErrorCount = 0;

    /**
     * 当前借阅数量
     */
    @TableField("current_borrow_count")
    private Integer currentBorrowCount = 0;

    /**
     * 当前预约数量
     */
    @TableField("current_reserve_count")
    private Integer currentReserveCount = 0;

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
     * 角色枚举（根据roleCode自动计算）
     */
    @TableField(exist = false)
    private RoleEnum role;

    /**
     * 角色名称（从token或关联查询获取）
     */
    @TableField(exist = false)
    private String roleName;

    /**
     * 角色编码（从token或关联查询获取）
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

    // ============= 业务方法 - 状态相关 =============

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
     * 判断账号是否停用
     */
    public boolean isDisabled() {
        return accountStatus != null && accountStatus == 2;
    }

    /**
     * 判断账号是否注销
     */
    public boolean isCancelled() {
        return accountStatus != null && accountStatus == 3;
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
     * 判断账号是否可用（正常状态且不在冻结期内）
     */
    public boolean isAvailable() {
        return isNormal() && !isInFreezePeriod();
    }

    /**
     * 获取显示状态文本
     */
    public String getStatusText() {
        if (accountStatus == null) {
            return "未知";
        }
        if (isInFreezePeriod()) {
            return "冻结中";
        }
        switch (accountStatus) {
            case 0:
                return "冻结";
            case 1:
                return "正常";
            case 2:
                return "停用";
            case 3:
                return "注销";
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

    /**
     * 判断信誉是否良好（可用于借阅权限判断）
     */
    public boolean isGoodCredit() {
        return creditScore != null && creditScore >= 60;
    }

    // ============= 业务方法 - 角色相关 =============

    /**
     * 获取角色枚举（基于roleCode）
     */
    public RoleEnum getRole() {
        if (this.role == null && this.roleCode != null) {
            this.role = RoleEnum.getByCode(this.roleCode);
        }
        return this.role;
    }

    /**
     * 设置角色编码并更新角色枚举
     */
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        this.role = RoleEnum.getByCode(roleCode);
        if (this.role != null) {
            this.roleName = this.role.getName();
        }
    }

    /**
     * 设置角色枚举并更新角色编码和名称
     */
    public void setRole(RoleEnum role) {
        this.role = role;
        if (role != null) {
            this.roleCode = role.getCode();
            this.roleName = role.getName();
        }
    }

    /**
     * 获取角色名称
     */
    public String getRoleName() {
        if (this.roleName == null && this.role != null) {
            this.roleName = this.role.getName();
        }
        return this.roleName;
    }

    /**
     * 判断是否为读者
     */
    public boolean isReader() {
        RoleEnum role = getRole();
        return role != null && role.isReader();
    }

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        RoleEnum role = getRole();
        return role != null && role.isAdmin();
    }

    /**
     * 判断是否为系统管理员
     */
    public boolean isSysAdmin() {
        RoleEnum role = getRole();
        return role != null && role.isSysAdmin();
    }

    /**
     * 判断是否为普通管理员（非系统管理员）
     */
    public boolean isNormalAdmin() {
        RoleEnum role = getRole();
        return role != null && role == RoleEnum.ADMIN;
    }

    /**
     * 获取最大可借阅本数
     */
    public Integer getMaxBorrowNum() {
        RoleEnum role = getRole();
        return role != null ? role.getMaxBorrowNum() : null;
    }

    /**
     * 获取最大可借阅天数
     */
    public Integer getMaxBorrowDays() {
        RoleEnum role = getRole();
        return role != null ? role.getMaxBorrowDays() : null;
    }

    /**
     * 获取最大可续借天数
     */
    public Integer getMaxRenewDays() {
        RoleEnum role = getRole();
        return role != null ? role.getMaxRenewDays() : null;
    }

    /**
     * 检查是否可以借阅更多书籍
     */
    public boolean canBorrowMore() {
        Integer maxBorrowNum = getMaxBorrowNum();
        if (maxBorrowNum == null) {
            return true; // 管理员无限制
        }
        return currentBorrowCount < maxBorrowNum;
    }

    /**
     * 检查是否可以预约更多书籍
     */
    public boolean canReserveMore() {
        // 预约数量限制可以根据业务需求调整，这里假设无限制
        return true;
    }

    /**
     * 检查是否可以续借
     */
    public boolean canRenew() {
        return getMaxRenewDays() != null && getMaxRenewDays() > 0;
    }

    // ============= 业务方法 - 借阅相关 =============

    /**
     * 增加借阅数量
     */
    public void incrementBorrowCount() {
        if (this.currentBorrowCount == null) {
            this.currentBorrowCount = 0;
        }
        this.currentBorrowCount++;
    }

    /**
     * 减少借阅数量
     */
    public void decrementBorrowCount() {
        if (this.currentBorrowCount != null && this.currentBorrowCount > 0) {
            this.currentBorrowCount--;
        }
    }

    /**
     * 增加预约数量
     */
    public void incrementReserveCount() {
        if (this.currentReserveCount == null) {
            this.currentReserveCount = 0;
        }
        this.currentReserveCount++;
    }

    /**
     * 减少预约数量
     */
    public void decrementReserveCount() {
        if (this.currentReserveCount != null && this.currentReserveCount > 0) {
            this.currentReserveCount--;
        }
    }

    /**
     * 重置借阅和预约数量（用于角色变更等情况）
     */
    public void resetBorrowStats() {
        this.currentBorrowCount = 0;
        this.currentReserveCount = 0;
    }

    // ============= 业务方法 - 密码相关 =============

    /**
     * 验证密码确认
     */
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * 清除敏感信息（用于返回前端）
     */
    public void clearSensitiveInfo() {
        this.password = null;
        this.confirmPassword = null;
        this.captcha = null;
        this.token = null;
    }

    /**
     * 准备创建用户（设置默认值）
     */
    public void prepareForCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.registerTime == null) {
            this.registerTime = now;
        }
        if (this.creditScore == null) {
            this.creditScore = 100;
        }
        if (this.accountStatus == null) {
            this.accountStatus = 1;
        }
        if (this.loginErrorCount == null) {
            this.loginErrorCount = 0;
        }
        if (this.currentBorrowCount == null) {
            this.currentBorrowCount = 0;
        }
        if (this.currentReserveCount == null) {
            this.currentReserveCount = 0;
        }
    }

    /**
     * 记录登录成功
     */
    public void recordLoginSuccess() {
        this.lastLoginTime = LocalDateTime.now();
        this.loginErrorCount = 0; // 重置错误次数
    }

    /**
     * 记录登录失败
     */
    public void recordLoginFailure() {
        if (this.loginErrorCount == null) {
            this.loginErrorCount = 0;
        }
        this.loginErrorCount++;
    }

    /**
     * 冻结账号
     */
    public void freezeAccount(LocalDateTime unfreezeTime) {
        this.accountStatus = 0;
        this.freezeTime = LocalDateTime.now();
        this.unfreezeTime = unfreezeTime;
    }

    /**
     * 解冻账号
     */
    public void unfreezeAccount() {
        this.accountStatus = 1;
        this.freezeTime = null;
        this.unfreezeTime = null;
        this.loginErrorCount = 0;
    }

    /**
     * 停用账号
     */
    public void disableAccount() {
        this.accountStatus = 2;
        this.freezeTime = null;
        this.unfreezeTime = null;
    }

    /**
     * 注销账号
     */
    public void cancelAccount() {
        this.accountStatus = 3;
        this.freezeTime = null;
        this.unfreezeTime = null;
        this.resetBorrowStats();
    }

    // ============= 静态方法 =============

    /**
     * 创建管理员用户（快速创建）
     */
    public static SysUser createAdminUser(String username, String account, String password) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setAccount(account);
        user.setPassword(password);
        user.setRole(RoleEnum.ADMIN);
        user.setUid(generateUid("ADM"));
        user.prepareForCreate();
        return user;
    }

    /**
     * 创建读者用户（快速创建）
     */
    public static SysUser createReaderUser(String username, String account, String password, RoleEnum readerRole) {
        if (!readerRole.isReader()) {
            throw new IllegalArgumentException("角色必须是读者类型");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setAccount(account);
        user.setPassword(password);
        user.setRole(readerRole);
        user.setUid(generateUid(readerRole.getCode().substring(7))); // 取READER_后面的部分
        user.prepareForCreate();
        return user;
    }

    /**
     * 从token信息创建用户对象（用于UserContext）
     */
    public static SysUser fromTokenInfo(Long userId, String username, Long roleId, String roleCode) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUsername(username);
        user.setRoleId(roleId);
        user.setRoleCode(roleCode); // 这会自动设置role枚举
        return user;
    }

    /**
     * 生成用户UID
     */
    private static String generateUid(String prefix) {
        return prefix + System.currentTimeMillis() % 100000;
    }

    // ============= toString方法（排除敏感信息） =============

    @Override
    public String toString() {
        return "SysUser{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", account='" + account + '\'' +
                ", roleId=" + roleId +
                ", roleCode='" + roleCode + '\'' +
                ", uid='" + uid + '\'' +
                ", creditScore=" + creditScore +
                ", accountStatus=" + accountStatus +
                ", currentBorrowCount=" + currentBorrowCount +
                ", currentReserveCount=" + currentReserveCount +
                ", registerTime=" + registerTime +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }
}