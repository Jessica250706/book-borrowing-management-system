package com.xq.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xq.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    //远程服务接口的调用
    @Reference //一定要使用alibaba提供的注解
    UserService userService;

    @RequestMapping("showName")
    public String showName(){
        return userService.getName();
    }
}

// http://localhost:8083/user/showName.do