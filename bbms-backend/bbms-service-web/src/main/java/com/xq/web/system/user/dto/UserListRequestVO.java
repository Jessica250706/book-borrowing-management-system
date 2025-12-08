package com.xq.web.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 用户列表请求VO
 */
@Data
@Schema(description = "用户列表请求参数")
public class UserListRequestVO {

    @Schema(description = "当前页码", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", example = "10")
    @Min(value = 1, message = "每页数量不能小于1")
    private Integer pageSize = 10;

    @Schema(description = "搜索关键词（用户名称/UID）")
    private String keyword;

    @Schema(description = "权限角色筛选", example = "ALL",
            allowableValues = {"ALL", "READER_SOCIAL", "READER_STUDENT", "READER_TEACHER", "ADMIN", "SYS_ADMIN"})
    private String roleFilter = "ALL";

    @Schema(description = "排序字段", example = "register_time")
    private String orderBy = "register_time";

    @Schema(description = "排序方式：asc-升序，desc-降序", example = "desc")
    private String orderDirection = "desc";
}
