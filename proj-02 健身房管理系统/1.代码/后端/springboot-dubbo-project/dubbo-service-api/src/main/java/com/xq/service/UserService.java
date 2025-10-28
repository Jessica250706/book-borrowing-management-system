package com.xq.service;

import com.xq.entity.ResultVO;
import com.xq.entity.User;


public interface UserService {

    public ResultVO getUserById(Long id);

    public ResultVO saveUser(User user);
}
