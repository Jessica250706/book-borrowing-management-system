package com.xq.web.operationLog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.dto.PageDTO;
import com.xq.web.operationLog.dto.BaseBorrowRecordDTO;
import com.xq.web.operationLog.entity.BookOperationLog;
import com.xq.web.borrow.record.entity.BorrowParam;

import java.util.List;

/**
 * 书籍操作日志服务接口
 */
public interface BookOperationLogService extends IService<BookOperationLog> {

    // 获取借阅记录（条件+分页）
    /**
     * 获取读者端借阅记录列表
     */
    PageDTO<BaseBorrowRecordDTO> getUserBorrowRecordList(BorrowParam param, Long userId);

    /**
     * 获取管理员端借阅记录列表
     */
    PageDTO<BaseBorrowRecordDTO> getAdminBorrowRecordList(BorrowParam param);

    /**
     * 记录预约操作日志
     */
    void logReservation(Long userId, Long bookId, String operationDesc);

    /**
     * 记录取消预约操作日志
     */
    void logCancelReservation(Long userId, Long bookId, String operationDesc);

    /**
     * 记录借阅操作日志
     */
    void logBorrow(Long userId, Long bookId, String operationDesc);

    /**
     * 记录续借操作日志
     */
    void logRenew(Long userId, Long bookId, String operationDesc);

    /**
     * 记录归还操作日志
     */
    void logReturn(Long userId, Long bookId, String operationDesc);

    /**
     * 记录通用操作日志
     */
    void logOperation(Long userId, Long bookId, Integer operationType, String operationDesc);

    /**
     * 根据书籍ID查询操作日志
     */
    List<BookOperationLog> getLogsByBookId(Long bookId);
}