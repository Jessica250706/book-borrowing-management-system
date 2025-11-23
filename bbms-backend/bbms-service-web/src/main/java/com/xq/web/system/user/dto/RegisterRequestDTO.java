package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 用户注册请求
 */
@Data
@Schema(description = "用户注册请求")
public class RegisterRequestDTO {

    @NotBlank(message = "账号不能为空")
    @Schema(description = "登录账号", example = "user@example.com")
    private String account;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20位之间")
    @Schema(description = "用户昵称", example = "张三")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Schema(description = "密码", example = "123456")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码", example = "123456")
    private String confirmPassword;

    @Schema(description = "验证码", example = "123456")
    private String captcha;

    /**
     * 验证密码是否一致
     */
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}