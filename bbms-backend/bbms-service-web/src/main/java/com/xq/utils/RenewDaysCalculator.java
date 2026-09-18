package com.xq.utils;

import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.dto.CurrentBorrowDTO;

public class RenewDaysCalculator {

    /**
     * 基于BookBorrow计算可续借天数（原有逻辑）
     */
    public static Integer calculateFromBookBorrow(BookBorrow borrow) {
        if (!borrow.canRenew()) {
            return 0;
        }

        Integer renewCount = borrow.getRenewCount();
        if (renewCount == null) {
            renewCount = 0;
        }

        if (renewCount >= 1) {
            return 0;
        }

        Integer maxRenewDays = getMaxRenewDaysFromBookBorrow(borrow);
        Integer alreadyRenewed = borrow.getRenewDays() != null ? borrow.getRenewDays() : 0;

        return Math.max(0, maxRenewDays - alreadyRenewed);
    }

    /**
     * 基于CurrentBorrowDTO计算可续借天数（新逻辑）
     */
    public static Integer calculateFromDTO(CurrentBorrowDTO dto) {
        Integer remainingDays = dto.getRemainingDays();
        if (remainingDays == null || remainingDays < 0) {
            return 0;
        }

        if (dto.getRenewCount() != null && dto.getRenewCount() > 0) {
            return 0;
        }

        Integer maxRenewDays = 7;
        return Math.min(maxRenewDays, Math.max(0, remainingDays));
    }

    /**
     * 通用计算逻辑（支持两种类型）
     */
    public static <T> Integer calculate(T obj) {
        if (obj instanceof BookBorrow) {
            return calculateFromBookBorrow((BookBorrow) obj);
        } else if (obj instanceof CurrentBorrowDTO) {
            return calculateFromDTO((CurrentBorrowDTO) obj);
        }
        throw new IllegalArgumentException("不支持的参数类型");
    }

    private static Integer getMaxRenewDaysFromBookBorrow(BookBorrow borrow) {
        if (borrow.getRoleMaxRenewDays() != null) {
            return borrow.getRoleMaxRenewDays();
        }
        return 5;
    }
}
