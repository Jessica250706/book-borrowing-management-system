package com.xq.web.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 预约结果DTO
 * 用于返回预约操作的详细信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveResultDTO {
    
    /**
     * 预约ID（仅在预约成功时返回）
     */
    private Long reservationId;
    
    /**
     * 可用库存数量（仅在有货时返回）
     */
    private Integer availableCount;
    
    /**
     * 预约过期时间（仅在预约成功时返回）
     */
    private Date invalidTime;
    
    /**
     * 图书名称
     */
    private String bookName;
    
    /**
     * 图书ID
     */
    private Long bookId;
}
