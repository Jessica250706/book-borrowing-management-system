package com.xq.web.system.role.entity;

import lombok.Data;

/**
 * 封装Role相关的分页查询参数
 */
@Data
public class RoleParam {
    /**
     * 当前页码
     */
    private Long currentPage;
    /**
     * 页面数据容量
     */
    private Long pageSize;
    /**
     * 角色名称
     */
    private String roleName;
}
