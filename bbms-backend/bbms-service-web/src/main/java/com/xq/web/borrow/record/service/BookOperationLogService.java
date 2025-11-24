package com.xq.web.borrow.record.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.borrow.record.entity.BookOperationLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 书籍操作日志服务接口
 */
public interface BookOperationLogService extends IService<BookOperationLog> {

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
     * 根据用户ID查询操作日志
     */
    List<BookOperationLog> getLogsByUserId(Long userId);

    /**
     * 根据书籍ID查询操作日志
     */
    List<BookOperationLog> getLogsByBookId(Long bookId);

    /**
     * 根据操作类型查询操作日志
     */
    List<BookOperationLog> getLogsByOperationType(Integer operationType);

    /**
     * 查询用户的操作日志（分页）
     */
    IPage<BookOperationLog> getUserLogsPage(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询所有操作日志（分页）
     */
    IPage<BookOperationLog> getAllLogsPage(Integer pageNum, Integer pageSize);

    /**
     * 根据时间范围查询操作日志
     */
    List<BookOperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询用户的借阅记录日志
     */
    List<BookOperationLog> getUserBorrowLogs(Long userId);

    /**
     * 查询用户的预约记录日志
     */
    List<BookOperationLog> getUserReservationLogs(Long userId);

    /**
     * 统计用户的操作次数
     */
    Long countUserOperations(Long userId, Integer operationType);

    /**
     * 获取书籍的操作统计
     */
    List<BookOperationLog> getBookOperationStats(Long bookId);

    /**
     * 清理过期日志（保留指定天数内的日志）
     */
    boolean cleanExpiredLogs(Integer keepDays);
}