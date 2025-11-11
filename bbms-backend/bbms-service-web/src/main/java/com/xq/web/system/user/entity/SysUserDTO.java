package com.xq.web.system.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserDTO extends SysUser {

    /**
     * 邮箱（当account为邮箱时）
     */
    @Email(message = "邮箱格式不正确")
    @TableField(exist = false)
    private String email;

    /**
     * 手机号（当account为手机号时）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @TableField(exist = false)
    private String phone;

    /**
     * 新密码（用于修改密码）
     */
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @TableField(exist = false)
    private String newPassword;

    /**
     * 角色名称列表（用于显示）
     */
    @TableField(exist = false)
    private String roleNames;

    /**
     * 创建时间范围查询-开始
     */
    @TableField(exist = false)
    private String createTimeStart;

    /**
     * 创建时间范围查询-结束
     */
    @TableField(exist = false)
    private String createTimeEnd;
}