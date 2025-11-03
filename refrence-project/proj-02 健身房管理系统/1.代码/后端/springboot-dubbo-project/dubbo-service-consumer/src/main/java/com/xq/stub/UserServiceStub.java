package com.xq.stub;

import com.xq.entity.ResultVO;
import com.xq.entity.User;
import com.xq.service.UserService;
import com.xq.utils.ResultVOUtil;


/**
 * 本地存根的实现
 */
public class UserServiceStub implements UserService {

    private final UserService userService;

    // 构造函数传入真正的远程代理对象
    public UserServiceStub(UserService userService){
        this.userService = userService;
    }

    public ResultVO getUserById(Long id) {
        System.out.println("本地存根的代码被调用......");
        if(id > 0){
            return userService.getUserById(id);
        }
        return ResultVOUtil.error(500,"参数非法");
    }

    public ResultVO saveUser(User user) {
        return null;
    }
}
