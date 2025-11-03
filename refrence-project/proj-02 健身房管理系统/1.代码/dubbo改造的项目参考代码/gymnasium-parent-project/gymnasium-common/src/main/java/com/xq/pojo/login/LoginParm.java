package com.xq.pojo.login;

import lombok.Data;

@Data
public class LoginParm {
    private String username;
    private String password;
    private String code;
    private String userType;
}