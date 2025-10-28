package com.xq.web.sys_role.entity;

import lombok.Data;

/**
 * 封装Role相关的分页查询参数
 */
@Data
public class RoleParam {

    private Long currentPage; //当前页码
    private Long pageSize; //页面容量
    private String roleName; //角色名称
}
