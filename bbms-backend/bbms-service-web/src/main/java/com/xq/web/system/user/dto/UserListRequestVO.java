package com.xq.web.system.user.dto;

import com.xq.dto.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 用户列表请求VO
 */
@Data
@Schema(description = "用户列表请求参数")
public class UserListRequestVO extends PageParam {

    @Schema(description = "搜索关键词（用户名称/UID）")
    private String keyword;

    @Schema(description = "权限角色筛选：ALL、READER_SOCIAL、READER_STUDENT、READER_TEACHER、ADMIN、SYS_ADMIN",
            example = "ALL",
            allowableValues = {"ALL", "READER_SOCIAL", "READER_STUDENT", "READER_TEACHER", "ADMIN", "SYS_ADMIN"})
    private String roleFilter = "ALL";
}
