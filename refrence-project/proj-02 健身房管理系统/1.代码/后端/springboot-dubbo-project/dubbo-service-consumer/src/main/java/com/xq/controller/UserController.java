package com.xq.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xq.entity.ResultVO;
import com.xq.service.UserService;
import com.xq.utils.ResultVOUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    // 这里用dubbo提供的@Reference注解实现服务的远程调用
    /*@Reference(parameters = {
            "getUserById.timeout","6000"
    })*/
    @Reference(version = "2.0.0",loadbalance = "roundrobin")
    UserService userService;

    @RequestMapping("/user/findById")
    public ResultVO findById(@RequestParam("id") Long id){
        return userService.getUserById(id);
    }

    public ResultVO fallBack(Long id){
        return ResultVOUtil.error(500,"使用hystrix触发了服务降级");
    }
}
// http://localhost:8082/user/findById?id=1