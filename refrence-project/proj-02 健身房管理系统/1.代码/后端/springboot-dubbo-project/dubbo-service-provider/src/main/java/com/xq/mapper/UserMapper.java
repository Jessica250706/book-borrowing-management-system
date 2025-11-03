package com.xq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
