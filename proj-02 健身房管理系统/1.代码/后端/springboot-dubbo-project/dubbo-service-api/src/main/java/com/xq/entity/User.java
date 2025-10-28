package com.xq.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_user")
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long uid;
    private String name;
    private Integer age;
    private String email;
    private Integer isDeleted;
    private Integer sex;
}
