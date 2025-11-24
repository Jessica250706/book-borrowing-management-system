package com.xq.web.system.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 角色枚举
 * 对应数据库 sys_role 表的角色编码
 */
@Getter
@Schema(description = "角色枚举")
public enum RoleEnum {

    READER_SOCIAL("READER_SOCIAL", "社会人员", 5, 15, 5),
    READER_STUDENT("READER_STUDENT", "学生", 20, 30, 20),
    READER_TEACHER("READER_TEACHER", "老师", 50, 60, 30),
    ADMIN("ADMIN", "管理员", null, null, null),
    SYS_ADMIN("SYS_ADMIN", "系统管理员", null, null, null);

    @Schema(description = "角色编码", example = "READER_STUDENT")
    private final String code;

    @Schema(description = "角色名称", example = "学生")
    private final String name;

    @Schema(description = "最大可借阅本数")
    private final Integer maxBorrowNum;

    @Schema(description = "最大可借阅天数")
    private final Integer maxBorrowDays;

    @Schema(description = "最大可续借天数")
    private final Integer maxRenewDays;

    RoleEnum(String code, String name, Integer maxBorrowNum, Integer maxBorrowDays, Integer maxRenewDays) {
        this.code = code;
        this.name = name;
        this.maxBorrowNum = maxBorrowNum;
        this.maxBorrowDays = maxBorrowDays;
        this.maxRenewDays = maxRenewDays;
    }

    /**
     * 根据角色编码获取枚举
     */
    public static RoleEnum getByCode(String code) {
        for (RoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 根据角色名称获取枚举
     */
    public static RoleEnum getByName(String name) {
        for (RoleEnum role : values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 判断是否为读者角色
     */
    public boolean isReader() {
        return this == READER_SOCIAL || this == READER_STUDENT || this == READER_TEACHER;
    }

    /**
     * 判断是否为管理员角色
     */
    public boolean isAdmin() {
        return this == ADMIN || this == SYS_ADMIN;
    }

    /**
     * 判断是否为系统管理员
     */
    public boolean isSysAdmin() {
        return this == SYS_ADMIN;
    }

    /**
     * 验证角色编码是否有效
     */
    public static boolean isValidCode(String code) {
        return getByCode(code) != null;
    }
}
