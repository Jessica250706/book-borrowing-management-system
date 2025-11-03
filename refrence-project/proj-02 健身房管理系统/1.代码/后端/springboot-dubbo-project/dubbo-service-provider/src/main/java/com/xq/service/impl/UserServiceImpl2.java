package com.xq.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xq.entity.ResultVO;
import com.xq.entity.User;
import com.xq.mapper.UserMapper;
import com.xq.service.UserService;
import com.xq.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

// @Service(parameters = {"getUserById.timeout","6000"}) // dubbo的service注解
@Service(version = "2.0.0")
public class UserServiceImpl2 implements UserService {

    @Autowired
    UserMapper userMapper;

   /* @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000") })*/

    public ResultVO getUserById(Long id) {
        int i = 10 / 0; //模拟服务调用异常
        System.out.println("服务提供方2被调用了....");
        /* try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        User user = userMapper.selectById(id);
        return ResultVOUtil.success(user);
    }

    public ResultVO saveUser(User user) {
        int i = userMapper.insert(user);
        if(i<=0){
            return ResultVOUtil.error(400,"添加商品失败");
        }else{
            Map<String,Object> res=new HashMap<>();
            res.put("msg","用户添加成功");
            res.put("id",user.getUid());
            return ResultVOUtil.success(res);
        }
    }
}
