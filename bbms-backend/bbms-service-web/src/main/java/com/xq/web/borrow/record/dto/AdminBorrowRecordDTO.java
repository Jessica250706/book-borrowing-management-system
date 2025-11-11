package com.xq.web.borrow.record.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅记录返回DTO（管理员端）
 */
@Data
public class AdminBorrowRecordDTO {
    private Long id;                    // 借阅记录ID
    private String bookName;           // 书籍名称
    private String bookCover;          // 书籍封面URL
    private String bookAuthor;         // 书籍作者
    private String category;           // 分类
    private String operationType;      // 操作类别（借阅、归还、续借等）
    private LocalDateTime operationTime; // 操作时间
    private String userAvatar;         // 用户头像
    private String userNickname;       // 用户昵称
    private String userUid;            // 用户UID
}