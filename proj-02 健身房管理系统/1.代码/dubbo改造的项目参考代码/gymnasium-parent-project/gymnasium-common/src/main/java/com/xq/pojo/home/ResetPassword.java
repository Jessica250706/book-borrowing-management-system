package com.xq.pojo.home;

import lombok.Data;

@Data
public class ResetPassword {
    private Long userId;
    private String userType;
    private String password;
    private String oldPassword;
}
