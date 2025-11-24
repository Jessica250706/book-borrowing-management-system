package com.xq.web.borrow.renew.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "剩余可续借天数响应DTO")
public class RemainingRenewDaysDTO {

    @Schema(description = "借阅记录ID")
    private Long borrowId;

    @Schema(description = "书籍ID")
    private Long bookId;

    @Schema(description = "书籍名称")
    private String bookName;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "最大可续借天数")
    private Integer maxRenewDays;

    @Schema(description = "已续借天数")
    private Integer alreadyRenewedDays;

    @Schema(description = "剩余可续借天数")
    private Integer remainingRenewDays;

    @Schema(description = "是否可以续借")
    private Boolean canRenew;

    @Schema(description = "不可续借的原因")
    private String reason;

    @Schema(description = "建议续借天数")
    private Integer suggestedRenewDays;

    // ============= 构造方法 =============

    public RemainingRenewDaysDTO() {
    }

    public RemainingRenewDaysDTO(Long borrowId, Long bookId, String bookName, Long userId,
                                 Integer maxRenewDays, Integer alreadyRenewedDays) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.bookName = bookName;
        this.userId = userId;
        this.maxRenewDays = maxRenewDays;
        this.alreadyRenewedDays = alreadyRenewedDays;
        this.remainingRenewDays = calculateRemainingDays(maxRenewDays, alreadyRenewedDays);
        this.canRenew = calculateCanRenew();
        this.reason = generateReason();
        this.suggestedRenewDays = calculateSuggestedRenewDays();
    }

    // ============= 计算方法 =============

    private Integer calculateRemainingDays(Integer maxRenewDays, Integer alreadyRenewedDays) {
        if (maxRenewDays == null || alreadyRenewedDays == null) {
            return 0;
        }
        return Math.max(0, maxRenewDays - alreadyRenewedDays);
    }

    private Boolean calculateCanRenew() {
        return remainingRenewDays != null && remainingRenewDays > 0;
    }

    private String generateReason() {
        if (!canRenew) {
            if (maxRenewDays == null || maxRenewDays == 0) {
                return "该用户角色不支持续借";
            } else if (remainingRenewDays != null && remainingRenewDays <= 0) {
                return "已达到最大续借天数限制";
            } else {
                return "当前状态不可续借";
            }
        }
        return null;
    }

    private Integer calculateSuggestedRenewDays() {
        if (!canRenew) {
            return 0;
        }
        // 建议续借天数：取剩余天数和默认7天中的较小值
        return Math.min(remainingRenewDays, 7);
    }

    // ============= 便捷方法 =============

    /**
     * 创建不可续借的响应
     */
    public static RemainingRenewDaysDTO createCannotRenew(Long borrowId, String reason) {
        RemainingRenewDaysDTO dto = new RemainingRenewDaysDTO();
        dto.setBorrowId(borrowId);
        dto.setCanRenew(false);
        dto.setReason(reason);
        dto.setRemainingRenewDays(0);
        dto.setSuggestedRenewDays(0);
        return dto;
    }

    /**
     * 创建可续借的响应
     */
    public static RemainingRenewDaysDTO createCanRenew(Long borrowId, Long bookId, String bookName,
                                                       Long userId, Integer maxRenewDays, Integer alreadyRenewedDays) {
        return new RemainingRenewDaysDTO(borrowId, bookId, bookName, userId, maxRenewDays, alreadyRenewedDays);
    }
}
