package com.xq.web.system.auth.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 登录参数
 */
@Data
public class LoginParam {

    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$|^1[3-9]\\d{9}$",
            message = "账号格式错误，请输入手机号或邮箱")
    private String account;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\S]{8,20}$",
            message = "密码格式错误，8-20个字符，可包含字母、数字、特殊字符")
    private String password;
}