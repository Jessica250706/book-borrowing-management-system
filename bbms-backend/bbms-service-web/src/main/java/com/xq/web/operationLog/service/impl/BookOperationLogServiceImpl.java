package com.xq.web.operationLog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.dto.PageDTO;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.operationLog.dto.BaseBorrowRecordDTO;
import com.xq.web.operationLog.entity.BookOperationLog;
import com.xq.web.operationLog.mapper.BookOperationLogMapper;
import com.xq.web.operationLog.service.BookOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 书籍操作日志服务实现类
 */
@Slf4j
@Service
public class BookOperationLogServiceImpl extends ServiceImpl<BookOperationLogMapper, BookOperationLog>
        implements BookOperationLogService {

    @Override
    public PageDTO<BaseBorrowRecordDTO> getUserBorrowRecordList(BorrowParam param, Long userId) {
        try {
            log.info("查询用户借阅记录，用户ID: {}, 参数: {}", userId, param);

            // 创建分页对象 - 使用DTO类型的分页
            Page<BaseBorrowRecordDTO> page = new Page<>(param.getCurrentPage(), param.getPageSize());

            // 调用Mapper查询
            IPage<BaseBorrowRecordDTO> resultPage = baseMapper.selectUserBorrowRecordPage(page, userId, param);
            log.info("查询用户借阅记录成功，总记录数: {}", resultPage.getTotal());

            // 如果需要额外的数据处理，可以在这里进行
            List<BaseBorrowRecordDTO> processedList = resultPage.getRecords().stream()
                    .map(this::processBorrowRecordDTO)
                    .collect(Collectors.toList());

            // 转换为PageDTO - 使用builder模式
            return PageDTO.<BaseBorrowRecordDTO>builder()
                    .list(processedList)
                    .total(resultPage.getTotal())
                    .pageNum(param.getCurrentPage())
                    .pageSize(param.getPageSize())
                    .build();

        } catch (Exception e) {
            log.error("查询用户借阅记录失败，用户ID: {}, 参数: {}", userId, param, e);
            throw new RuntimeException("查询用户借阅记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    public PageDTO<BaseBorrowRecordDTO> getAdminBorrowRecordList(BorrowParam param) {
        try {
            log.info("查询管理员借阅记录，参数: {}", param);

            // 创建分页对象 - 使用DTO类型的分页
            Page<BaseBorrowRecordDTO> page = new Page<>(param.getCurrentPage(), param.getPageSize());

            // 调用Mapper查询
            IPage<BaseBorrowRecordDTO> resultPage = baseMapper.selectAdminBorrowRecordPage(page, param);
            log.info("查询管理员借阅记录成功，总记录数: {}", resultPage.getTotal());

            // 如果需要额外的数据处理，可以在这里进行
            List<BaseBorrowRecordDTO> processedList = resultPage.getRecords().stream()
                    .map(this::processBorrowRecordDTO)
                    .collect(Collectors.toList());

            // 转换为PageDTO - 使用builder模式
            return PageDTO.<BaseBorrowRecordDTO>builder()
                    .list(processedList)
                    .total(resultPage.getTotal())
                    .pageNum(param.getCurrentPage())
                    .pageSize(param.getPageSize())
                    .build();

        } catch (Exception e) {
            log.error("查询管理员借阅记录失败，参数: {}", param, e);
            throw new RuntimeException("查询管理员借阅记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理借阅记录DTO，添加额外信息
     */
    private BaseBorrowRecordDTO processBorrowRecordDTO(BaseBorrowRecordDTO dto) {
        try {
            // 可以根据operationType添加操作类型描述
            String operationTypeDesc = getOperationTypeDescription(dto.getOperationType());
            // 如果有需要，可以在这里设置到DTO中
            // dto.setOperationTypeDesc(operationTypeDesc);

            // 还可以进行其他处理，如格式化日期等
            return dto;
        } catch (Exception e) {
            log.warn("处理借阅记录DTO时出错", e);
            return dto;
        }
    }

    /**
     * 获取操作类型描述
     */
    private String getOperationTypeDescription(Integer operationType) {
        if (operationType == null) {
            return "未知操作";
        }
        switch (operationType) {
            case 1: return "预约";
            case 2: return "取消预约";
            case 3: return "借阅";
            case 4: return "续借";
            case 5: return "归还";
            default: return "其他操作";
        }
    }

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
    public List<BookOperationLog> getLogsByBookId(Long bookId) {
        LambdaQueryWrapper<BookOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookOperationLog::getBookId, bookId)
                .orderByDesc(BookOperationLog::getOperationTime);
        return this.list(queryWrapper);
    }
}