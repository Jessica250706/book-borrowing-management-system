package com.xq.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xq.service.UserService;

@Service // 一定要注意，@Service注解使用的是dubbo下面的注解
public class UserServiceImpl implements UserService {

    public String getName() {
        return "hello,dubbo.....";
    }
}
