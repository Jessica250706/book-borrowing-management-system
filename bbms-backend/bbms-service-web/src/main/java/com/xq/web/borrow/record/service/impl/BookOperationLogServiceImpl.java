package com.xq.web.borrow.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.borrow.record.entity.BookOperationLog;
import com.xq.web.borrow.record.mapper.BookOperationLogMapper;
import com.xq.web.borrow.record.service.BookOperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 书籍操作日志服务实现类
 */
@Service
public class BookOperationLogServiceImpl extends ServiceImpl<BookOperationLogMapper, BookOperationLog>
        implements BookOperationLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logReservation(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = BookOperationLog.createReservationLog(userId, bookId, operationDesc);
        log.prepareForSave();
        this.save(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logCancelReservation(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = BookOperationLog.createCancelReservationLog(userId, bookId, operationDesc);
        log.prepareForSave();
        this.save(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logBorrow(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = BookOperationLog.createBorrowLog(userId, bookId, operationDesc);
        log.prepareForSave();
        this.save(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logRenew(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = BookOperationLog.createRenewLog(userId, bookId, operationDesc);
        log.prepareForSave();
        this.save(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logReturn(Long userId, Long bookId, String operationDesc) {
        BookOperationLog log = BookOperationLog.createReturnLog(userId, bookId, operationDesc);
        log.prepareForSave();
        this.save(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logOperation(Long userId, Long bookId, Integer operationType, String operationDesc) {
        BookOperationLog log = BookOperationLog.createLog(userId, bookId, operationType, operationDesc);
        log.prepareForSave();
        this.save(log);
    }

    @Override
    public List<BookOperationLog> getLogsByUserId(Long userId) {
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookOperationLog::getUserId, userId)
                .orderByDesc(BookOperationLog::getOperationTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<BookOperationLog> getLogsByBookId(Long bookId) {
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookOperationLog::getBookId, bookId)
                .orderByDesc(BookOperationLog::getOperationTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<BookOperationLog> getLogsByOperationType(Integer operationType) {
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookOperationLog::getOperationType, operationType)
                .orderByDesc(BookOperationLog::getOperationTime);
        return this.list(queryWrapper);
    }

    @Override
    public IPage<BookOperationLog> getUserLogsPage(Long userId, Integer pageNum, Integer pageSize) {
        Page<BookOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookOperationLog::getUserId, userId)
                .orderByDesc(BookOperationLog::getOperationTime);
        return this.page(page, queryWrapper);
    }

    @Override
    public IPage<BookOperationLog> getAllLogsPage(Integer pageNum, Integer pageSize) {
        Page<BookOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(BookOperationLog::getOperationTime);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<BookOperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(BookOperationLog::getOperationTime, startTime, endTime)
                .orderByDesc(BookOperationLog::getOperationTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<BookOperationLog> getUserBorrowLogs(Long userId) {
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookOperationLog::getUserId, userId)
                .eq(BookOperationLog::getOperationType, 3) // 借阅操作
                .orderByDesc(BookOperationLog::getOperationTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<BookOperationLog> getUserReservationLogs(Long userId) {
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookOperationLog::getUserId, userId)
                .in(BookOperationLog::getOperationType, 1, 2) // 预约和取消预约
                .orderByDesc(BookOperationLog::getOperationTime);
        return this.list(queryWrapper);
    }

    @Override
    public Long countUserOperations(Long userId, Integer operationType) {
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookOperationLog::getUserId, userId);
        if (operationType != null) {
            queryWrapper.eq(BookOperationLog::getOperationType, operationType);
        }
        return this.count(queryWrapper);
    }

    @Override
    public List<BookOperationLog> getBookOperationStats(Long bookId) {
        // 这里可以扩展为更复杂的统计查询
        return getLogsByBookId(bookId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cleanExpiredLogs(Integer keepDays) {
        if (keepDays == null || keepDays <= 0) {
            return false;
        }

        LocalDateTime expireTime = LocalDateTime.now().minusDays(keepDays);
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(BookOperationLog::getCreateTime, expireTime);

        return this.remove(queryWrapper);
    }
}