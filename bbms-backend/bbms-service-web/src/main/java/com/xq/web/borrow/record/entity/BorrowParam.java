package com.xq.web.borrow.record.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class BorrowParam {
    private Long currentPage;         // 当前页码
    private Long pageSize;            // 页面容量
    private Long bookId;           // 书籍id（改为Long类型）
    private Integer borrowStatus;     // 借阅状态（可选条件）
    private Date startTime;           // 借阅开始时间（可选条件）
    private Date endTime;             // 借阅结束时间（可选条件）
    private List<Long> borrowIds;  // 批量操作时的借阅id列表（改为Long类型）

    // 新增字段 - 根据需求文档补充
    private String keyword;           // 搜索关键词（书名/作者/借阅用户）
    private Long categoryId;       // 书籍分类id（改为Long类型）
    private String categoryCode;      // 书籍分类编码
    private Integer operationType;    // 操作类别（0-所有类别，1-预约，2-取消预约，3-借阅，4-续借，5-归还）
    private String userName;          // 用户名（管理员端查询用）
    private String uid;               // 用户uid（管理员端查询用）

    // 排序相关字段
    private String sortField;         // 排序字段（borrowTime-借阅时间，expectedReturnTime-预计归还时间）
    private String sortOrder;         // 排序顺序（asc-升序，desc-降序）
}
