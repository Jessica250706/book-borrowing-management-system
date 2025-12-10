package com.xq.web.system.role.dto;

import lombok.Data;

/**
 * 角色详情DTO
 */
@Data
public class SysRoleDetailDTO {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 最大可借阅本数
     */
    private Integer maxBorrowNum;

    /**
     * 最大可借阅天数
     */
    private Integer maxBorrowDays;

    /**
     * 最大可续借天数
     */
    private Integer maxRenewDays;

    /**
     * 备注
     */
    private String remark;
}
