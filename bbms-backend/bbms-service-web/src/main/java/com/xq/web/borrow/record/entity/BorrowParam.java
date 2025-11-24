package com.xq.web.borrow.record.entity;

import com.xq.dto.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 借阅记录查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "借阅记录查询参数")
public class BorrowParam extends PageParam {

    @Schema(description = "书籍ID")
    private Long bookId;

    @Schema(description = "借阅状态（0-借阅中，1-已归还，2-已超时，3-归还待确认）")
    private Integer borrowStatus;

    @Schema(description = "借阅开始时间")
    private Date startTime;

    @Schema(description = "借阅结束时间")
    private Date endTime;

    @Schema(description = "批量操作时的借阅ID列表")
    private List<Long> borrowIds;

    @Schema(description = "搜索关键词（书名/作者/借阅用户）")
    private String keyword;

    @Schema(description = "书籍分类ID")
    private Long categoryId;

    @Schema(description = "书籍分类编码")
    private String categoryCode;

    @Schema(description = "操作类别（1-预约，2-取消预约，3-借阅，4-续借，5-归还）")
    private Integer operationType;

    @Schema(description = "用户名（管理员端查询用）")
    private String userName;

    @Schema(description = "用户UID（管理员端查询用）")
    private String uid;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "排序字段（borrow_time-借阅时间，expected_return_time-预计归还时间，create_time-创建时间）")
    private String sortField = "borrow_time";

    @Schema(description = "排序顺序（asc-升序，desc-降序）")
    private String sortOrder = "desc";

    // ============= 便捷方法 =============

    /**
     * 是否需要按用户查询（管理员查询特定用户时使用）
     */
    public boolean hasUserCondition() {
        return userName != null && !userName.trim().isEmpty() ||
                uid != null && !uid.trim().isEmpty();
    }

    /**
     * 是否需要时间范围查询
     */
    public boolean hasTimeRange() {
        return startTime != null || endTime != null;
    }

    /**
     * 是否需要分类查询
     */
    public boolean hasCategoryCondition() {
        return categoryId != null || (categoryCode != null && !categoryCode.trim().isEmpty());
    }

    /**
     * 获取有效的排序字段（防止SQL注入）
     */
    public String getValidSortField() {
        if (sortField == null) {
            return "borrow_time";
        }

        // 只允许特定的排序字段
        switch (sortField) {
            case "borrow_time":
            case "expected_return_time":
            case "create_time":
            case "update_time":
                return sortField;
            default:
                return "borrow_time";
        }
    }

    /**
     * 获取有效的排序顺序
     */
    public String getValidSortOrder() {
        if (sortOrder == null) {
            return "desc";
        }
        return "asc".equalsIgnoreCase(sortOrder) ? "asc" : "desc";
    }

    /**
     * 验证参数有效性（用于服务层验证）
     */
    public boolean validate() {
        // 验证分页参数
        if (getPageNum() == null || getPageNum() < 1) {
            return false;
        }
        if (getPageSize() == null || getPageSize() < 1 || getPageSize() > 100) {
            return false;
        }

        // 验证时间范围
        if (startTime != null && endTime != null && startTime.after(endTime)) {
            return false;
        }

        return true;
    }
}
