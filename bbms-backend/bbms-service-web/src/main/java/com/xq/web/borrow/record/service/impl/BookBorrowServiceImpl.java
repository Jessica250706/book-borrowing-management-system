package com.xq.web.borrow.record.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.dto.PageDTO;
import com.xq.utils.RenewDaysCalculator;
import com.xq.web.book.service.BookInfoService;
import com.xq.web.borrow.record.dto.*;
import com.xq.web.borrow.record.entity.*;
import com.xq.web.borrow.record.mapper.BookBorrowMapper;
import com.xq.web.borrow.record.service.BookBorrowService;
import com.xq.web.operationLog.service.BookOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookBorrowServiceImpl extends ServiceImpl<BookBorrowMapper, BookBorrow> implements BookBorrowService {

    @Autowired
    private BookOperationLogService bookOperationLogService;

    @Autowired
    private BookInfoService bookInfoService;

    @Autowired
    private com.xq.web.message.service.SysMessageService sysMessageService;

    @Autowired
    private com.xq.web.book.mapper.BookReservationMapper bookReservationMapper;

    @Override
    public PageDTO<CurrentBorrowDTO> getCurrentBorrowList(CurrentBorrowQueryParam param, Long userId) {
        try {
            log.info("查询当前借阅列表，用户ID: {}, 参数: {}", userId, param);

            // 创建分页对象
            Page<CurrentBorrowDTO> page = new Page<>(param.getPageNum(), param.getPageSize());

            // 使用XML映射的关联查询
            IPage<CurrentBorrowDTO> resultPage = baseMapper.selectCurrentBorrowList(page, userId, param);
            log.info("查询成功，总记录数: {}", resultPage.getTotal());

            // 计算剩余天数和设置操作列表
            List<CurrentBorrowDTO> dtoList = resultPage.getRecords().stream()
                    .map(this::processCurrentBorrowDTO)
                    .collect(Collectors.toList());

            // 构建分页响应
            return PageDTO.<CurrentBorrowDTO>builder()
                    .list(dtoList)
                    .total(resultPage.getTotal())
                    .pageNum(param.getPageNum())
                    .pageSize(param.getPageSize())
                    .build();

        } catch (Exception e) {
            log.error("查询当前借阅列表失败", e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理当前借阅DTO，计算剩余天数和设置操作列表
     */
    private CurrentBorrowDTO processCurrentBorrowDTO(CurrentBorrowDTO dto) {
        // 计算剩余天数
        if (dto.getLatestReturnTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            Date returnDate = dto.getLatestReturnTime();
            LocalDateTime returnTime = returnDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            long daysBetween = ChronoUnit.DAYS.between(now, returnTime);
            if (daysBetween >= 0) {
                // 未超时
                dto.setRemainingDays((int) daysBetween);
            } else {
                // 已超时，设置负数表示超期天数
                dto.setRemainingDays(-(int) Math.abs(daysBetween));
            }
        } else {
            dto.setRemainingDays(0);
        }

        // 设置可续借天数
        dto.setRenewableDays(RenewDaysCalculator.calculate(dto));

        // 设置操作列表
        dto.setOperations(determineAvailableOperations(dto));

        return dto;
    }

    /**
     * 确定可用的操作列表
     */
    private List<String> determineAvailableOperations(CurrentBorrowDTO dto) {
        List<String> operations = new java.util.ArrayList<>();

        // 总是可以查看详情
        operations.add("detail");

        // 如果可以归还（借阅中或已超时）
        if (dto.getBorrowStatus() != null && (dto.getBorrowStatus() == 0 || dto.getBorrowStatus() == 2)) {
            operations.add("return");
        }

        // 如果可以续借
        if (dto.getRenewableDays() != null && dto.getRenewableDays() > 0) {
            operations.add("renew");
        }

        return operations;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnBooks(BatchOperateParam param) {
        if (param == null || CollectionUtils.isEmpty(param.getIds())) {
            throw new RuntimeException("借阅记录ID列表不能为空");
        }

        try {
            List<Long> borrowIds = param.getIds();
            log.info("批量归还书籍，借阅记录ID: {}", borrowIds);

            // 使用关联查询获取详细信息
            List<BookBorrow> borrowRecords = baseMapper.selectBorrowRecordsWithDetails(borrowIds);
            if (CollectionUtils.isEmpty(borrowRecords)) {
                throw new RuntimeException("未找到对应的借阅记录");
            }

            // 验证借阅记录状态
            for (BookBorrow borrow : borrowRecords) {
                if (!borrow.canReturn()) {
                    throw new RuntimeException("借阅记录【" + borrow.getBorrowId() + "】当前状态不可归还");
                }
            }

            // 批量更新状态为归还待确认
            for (BookBorrow borrow : borrowRecords) {
                borrow.doReturnApply();

                // 构建详细的日志描述
                String logDescription = buildReturnLogDescription(borrow);

                // 记录操作日志
                bookOperationLogService.logReturn(
                        borrow.getUserId(),
                        borrow.getBookId(),
                        logDescription
                );

                log.info("用户{}申请归还书籍《{}》",
                        borrow.getUserName(),
                        borrow.getBookName());
            }

            // 批量更新
            boolean success = this.updateBatchById(borrowRecords);

            // 发送消息给管理员：有待确认的还书
            if (success) {
                try {
                    for (BookBorrow borrow : borrowRecords) {
                        com.xq.web.message.entity.SysMessage message = new com.xq.web.message.entity.SysMessage();
                        message.setUserId(0L); // 管理员池
                        message.setMessageType(4); // 4-还书提醒
                        message.setMessageTitle("待确认还书");
                        message.setMessageContent(String.format("用户【%s】申请归还书籍《%s》，请及时确认。",
                            borrow.getUserName(), borrow.getBookName()));
                        message.setBookId(borrow.getBookId());
                        sysMessageService.sendMessage(message);
                    }
                } catch (Exception e) {
                    log.warn("发送还书提醒消息失败", e);
                }
            }

            log.info("批量归还申请成功，处理记录数: {}", borrowRecords.size());
            return success;

        } catch (Exception e) {
            log.error("归还书籍失败", e);
            throw new RuntimeException("归还书籍失败: " + e.getMessage());
        }
    }

    /**
     * 构建归还日志描述
     * 格式：用户名 + "申请归还书籍《" + 书籍名称 + "》"
     */
    private String buildReturnLogDescription(BookBorrow borrow) {
        StringBuilder description = new StringBuilder();

        // 添加用户名
        if (StringUtils.hasText(borrow.getUserName())) {
            description.append(borrow.getUserName());
        } else {
            description.append("用户");
        }

        description.append("申请归还书籍");

        // 添加书籍名称
        if (StringUtils.hasText(borrow.getBookName())) {
            description.append("《").append(borrow.getBookName()).append("》");
        }

        return description.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReturn(BatchOperateParam param, Integer adminId) {
        if (param == null || CollectionUtils.isEmpty(param.getIds())) {
            throw new RuntimeException("借阅记录ID列表不能为空");
        }

        if (adminId == null) {
            throw new RuntimeException("管理员ID不能为空");
        }

        try {
            List<Long> borrowIds = param.getIds();
            log.info("管理员{}批量确认归还，借阅记录ID: {}", adminId, borrowIds);

            // 使用关联查询获取详细信息
            List<BookBorrow> borrowRecords = baseMapper.selectBorrowRecordsWithDetails(borrowIds);
            if (CollectionUtils.isEmpty(borrowRecords)) {
                throw new RuntimeException("未找到对应的借阅记录");
            }

            // 验证借阅记录状态
            for (BookBorrow borrow : borrowRecords) {
                if (!borrow.validateReturnConfirm()) {
                    throw new RuntimeException("借阅记录【" + borrow.getBorrowId() + "】当前状态不可确认归还");
                }
            }

            // 批量确认归还
            for (BookBorrow borrow : borrowRecords) {
                borrow.doReturnConfirm(adminId.longValue());

                // 构建详细的日志描述
                String logDescription = buildConfirmReturnLogDescription(borrow, adminId);

                // 记录操作日志
                bookOperationLogService.logOperation(
                        adminId.longValue(),
                        borrow.getBookId(),
                        5, // 归还操作
                        logDescription
                );

                log.info("管理员{}确认归还用户{}的书籍《{}》",
                        adminId,
                        borrow.getUserName(),
                        borrow.getBookName());
            }

            // 批量更新
            boolean success = this.updateBatchById(borrowRecords);

            if (success) {
                // 提取书籍ID并去重
                List<Long> bookIds = borrowRecords.stream()
                        .map(BookBorrow::getBookId)
                        .distinct()
                        .collect(Collectors.toList());

                // 增加书籍的可借数量
                boolean stockUpdated = bookInfoService.increaseAvailableCount(bookIds);

                if (!stockUpdated) {
                    log.warn("借阅记录更新成功，但书籍库存更新失败，书籍ID: {}", bookIds);
                    // 可以选择抛出异常回滚事务，或者记录日志继续执行
                    // throw new RuntimeException("书籍库存更新失败");
                }

                log.info("更新了{}本书籍的库存数量", bookIds.size());
            }

            // 发送消息给预约用户：书籍已归还并可借阅
            if (success) {
                try {
                    for (BookBorrow borrow : borrowRecords) {
                        // 查询预约了该书的用户
                        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.xq.web.book.entity.BookReservation> qw
                            = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                        qw.eq("book_id", borrow.getBookId())
                            .in("reservation_status", 0, 1) // 预约中或可借阅
                            .eq("remind_status", 0); // 未提醒过的用户

                        java.util.List<com.xq.web.book.entity.BookReservation> reservations
                            = bookReservationMapper.selectList(qw);

                        if (reservations != null && !reservations.isEmpty()) {
                            for (com.xq.web.book.entity.BookReservation res : reservations) {
                                try {
                                    com.xq.web.message.entity.SysMessage message = new com.xq.web.message.entity.SysMessage();
                                    message.setUserId(res.getUserId());
                                    message.setMessageType(1); // 1-预约提醒
                                    message.setMessageTitle("预约归还");
                                    message.setMessageContent(String.format("您预约的《%s》已归还，请及时借阅。", borrow.getBookName()));
                                    message.setBookId(borrow.getBookId());
                                    sysMessageService.sendMessage(message);

                                    // 标记该预约为已提醒
                                    com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<com.xq.web.book.entity.BookReservation> uw
                                        = new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
                                    uw.eq("reservation_id", res.getReservationId())
                                        .set("remind_status", 1)
                                        .set("remind_time", new Date())
                                        .set("update_time", new Date());
                                    bookReservationMapper.update(null, uw);
                                } catch (Exception inner) {
                                    log.warn("给预约用户发送还书提醒失败 reservationId={} bookId={}", res.getReservationId(), borrow.getBookId(), inner);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("处理预约用户通知失败", e);
                }
            }

            log.info("管理员{}批量确认归还成功，处理记录数: {}", adminId, borrowRecords.size());
            return success;

        } catch (Exception e) {
            log.error("确认归还失败", e);
            throw new RuntimeException("确认归还失败: " + e.getMessage());
        }
    }

    /**
     * 构建确认归还日志描述
     * 格式：管理员 + "确认归还" + 用户名 + "的书籍《" + 书籍名称 + "》"
     */
    private String buildConfirmReturnLogDescription(BookBorrow borrow, Integer adminId) {
        StringBuilder description = new StringBuilder();

        // 添加管理员信息
        description.append("管理员(ID:").append(adminId).append(")");

        // 添加用户信息
        if (StringUtils.hasText(borrow.getUserName())) {
            description.append("确认归还用户【").append(borrow.getUserName()).append("】");
        } else {
            description.append("确认归还用户(ID:").append(borrow.getUserId()).append(")");
        }

        // 添加书籍信息
        description.append("的书籍");
        if (StringUtils.hasText(borrow.getBookName())) {
            description.append("《").append(borrow.getBookName()).append("》");
        }

        return description.toString();
    }

    /**
     * 获取当前归还书籍列表（条件+分页）
     * 管理员端获取当前所有归还但尚未进行二次确认的书籍列表
     */
    @Override
    public PageDTO<CurrentReturnDTO> getCurrentReturnList(CurrentReturnQueryParam param, Long userId) {
        try {
            log.info("查询当前归还列表，操作用户ID: {}, 参数: {}", userId, param);

            // 创建分页对象
            Page<CurrentReturnDTO> page = new Page<>(param.getPageNum(), param.getPageSize());

            // 使用XML映射的关联查询，查询归还待确认的记录
            IPage<CurrentReturnDTO> resultPage = baseMapper.selectCurrentReturnList(page, param);
            log.info("查询成功，总记录数: {}", resultPage.getTotal());

            // 处理DTO列表，设置操作列表
            List<CurrentReturnDTO> dtoList = resultPage.getRecords().stream()
                    .map(this::processCurrentReturnDTO)
                    .collect(Collectors.toList());

            // 构建分页响应
            return PageDTO.<CurrentReturnDTO>builder()
                    .list(dtoList)
                    .total(resultPage.getTotal())
                    .pageNum(param.getPageNum())
                    .pageSize(param.getPageSize())
                    .build();

        } catch (Exception e) {
            log.error("查询当前归还列表失败", e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 确定当前归还记录可进行的操作
     */
    private String[] determineCurrentReturnOperations(CurrentReturnDTO dto) {
        List<String> operations = new ArrayList<>();

        // 总是可以查看详情
        operations.add("detail");

        // 如果可以确认归还（状态为归还待确认）
        if (dto.getReturnConfirmStatus() != null && dto.getReturnConfirmStatus() == 0) {
            operations.add("confirmReturn");
        }

        return operations.toArray(new String[0]);
    }

    /**
     * 处理当前归还DTO
     * 设置操作列表和其他处理逻辑
     */
    private CurrentReturnDTO processCurrentReturnDTO(CurrentReturnDTO dto) {
        // 设置操作列表
        dto.setOperations(determineCurrentReturnOperations(dto));

        return dto;
    }

    @Override
    public UserBorrowStatisticsVO getUserBorrowStatistics(Long userId) {
        UserBorrowStatisticsVO statistics = new UserBorrowStatisticsVO();

        // 1. 本月借阅数量
        Integer monthBorrowCount = baseMapper.selectMonthBorrowCount(userId);
        statistics.setMonthBorrowCount(monthBorrowCount != null ? monthBorrowCount : 0);

        // 2. 累计借阅数量
        Integer totalBorrowCount = baseMapper.selectTotalBorrowCount(userId);
        statistics.setTotalBorrowCount(totalBorrowCount != null ? totalBorrowCount : 0);

        // 3. 借阅频率（本/月）
        BigDecimal borrowFrequency = baseMapper.selectBorrowFrequency(userId);
        statistics.setBorrowFrequency(borrowFrequency != null ? borrowFrequency : BigDecimal.ZERO);

        // 4. 平均阅读时长（天/本）
        BigDecimal averageReadingDays = baseMapper.selectAverageReadingDays(userId);
        statistics.setAverageReadingDays(averageReadingDays != null ? averageReadingDays : BigDecimal.ZERO);

        return statistics;
    }

    @Override
    public List<CategoryBorrowCountVO> getCategoryBorrowStatistics(Long userId) {
        return baseMapper.selectCategoryBorrowStatistics(userId);
    }
}
