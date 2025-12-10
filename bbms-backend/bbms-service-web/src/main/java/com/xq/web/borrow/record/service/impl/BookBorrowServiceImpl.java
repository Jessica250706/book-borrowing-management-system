package com.xq.web.borrow.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.dto.PageDTO;
import com.xq.utils.DateUtil;
import com.xq.utils.RenewDaysCalculator;
import com.xq.web.book.dto.BookInfoDTO;
import com.xq.web.borrow.record.dto.*;
import com.xq.web.system.user.dto.UserInfo;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;
import com.xq.web.borrow.record.mapper.BookBorrowMapper;
import com.xq.web.borrow.record.service.BookBorrowService;
import com.xq.web.borrow.record.service.BookOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

    @Override
    public PageDTO<BaseBorrowRecordDTO> getUserBorrowRecordList(BorrowParam param, Long userId) {
        // 构建分页对象
        IPage<BookBorrow> page = new Page<>(param.getPageNum(), param.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<BookBorrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookBorrow::getUserId, userId)
                .orderByDesc(BookBorrow::getBorrowTime);

        // 添加搜索条件
        if (StringUtils.hasText(param.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(BookBorrow::getBookName, param.getKeyword())
                    .or()
                    .like(BookBorrow::getAuthor, param.getKeyword()));
        }

        // 添加分类筛选
        if (param.getCategoryCode() != null) {
            queryWrapper.eq(BookBorrow::getCategoryId, param.getCategoryCode());
        }

        // 添加操作类型筛选
        if (param.getOperationType() != null) {
            queryWrapper.eq(BookBorrow::getOperationType, param.getOperationType());
        }

        // 执行查询
        IPage<BookBorrow> resultPage = this.page(page, queryWrapper);

        // 转换为DTO
        List<BaseBorrowRecordDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToBaseBorrowRecordDTO)
                .collect(Collectors.toList());

        // 构建分页响应
        return PageDTO.<BaseBorrowRecordDTO>builder()
                .list(dtoList)
                .total(resultPage.getTotal())
                .pageNum(param.getPageNum())
                .pageSize(param.getPageSize())
                .build();
    }

    @Override
    public PageDTO<BaseBorrowRecordDTO> getAdminBorrowRecordList(BorrowParam param) {
        // 构建分页对象
        IPage<BookBorrow> page = new Page<>(param.getPageNum(), param.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<BookBorrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(BookBorrow::getBorrowTime);

        // 添加搜索条件
        if (StringUtils.hasText(param.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(BookBorrow::getBookName, param.getKeyword())
                    .or()
                    .like(BookBorrow::getAuthor, param.getKeyword())
                    .or()
                    .like(BookBorrow::getUserName, param.getKeyword()));
        }

        // 添加分类筛选
        if (param.getCategoryCode() != null) {
            queryWrapper.eq(BookBorrow::getCategoryId, param.getCategoryCode());
        }

        // 添加操作类型筛选
        if (param.getOperationType() != null) {
            queryWrapper.eq(BookBorrow::getOperationType, param.getOperationType());
        }

        // 执行查询
        IPage<BookBorrow> resultPage = this.page(page, queryWrapper);

        // 转换为DTO
        List<BaseBorrowRecordDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToBaseBorrowRecordDTO)
                .collect(Collectors.toList());

        // 构建分页响应
        return PageDTO.<BaseBorrowRecordDTO>builder()
                .list(dtoList)
                .total(resultPage.getTotal())
                .pageNum(param.getPageNum())
                .pageSize(param.getPageSize())
                .build();
    }

    /**
     * 转换为当前借阅DTO
     */
    private CurrentBorrowDTO convertToCurrentBorrowDTO(BookBorrow borrow) {
        CurrentBorrowDTO dto = new CurrentBorrowDTO();

        // 设置基本属性
        dto.setId(borrow.getBorrowId());
        dto.setBookName(borrow.getBookName());
        dto.setBookCover(borrow.getCoverUrl());
        dto.setAuthor(borrow.getAuthor());
        dto.setCategoryCode(borrow.getCategoryCode());
        dto.setLatestReturnTime(borrow.getExpectedReturnTime());

        // 计算剩余借阅天数（正数表示剩余天数，负数表示超期天数）
        Integer remainingDays = borrow.getRemainingDays();
        if (borrow.isActuallyOverdue()) {
            // 如果已超时，返回负数的超期天数
            Long overdueDays = borrow.getOverdueDays();
            dto.setRemainingDays(-overdueDays.intValue());
        } else {
            // 如果未超时，返回正数的剩余天数
            dto.setRemainingDays(remainingDays != null ? remainingDays : 0);
        }

        // 设置可续借天数
        dto.setRenewableDays(RenewDaysCalculator.calculate(borrow));

        // 设置可操作列表
        dto.setOperations(determineAvailableOperations(borrow));

        return dto;
    }

    /**
     * 获取最大续借天数（可以根据用户角色或系统配置）
     */
    private Integer getMaxRenewDays(BookBorrow borrow) {
        // 从用户角色配置获取（假设borrow对象中有roleMaxRenewDays字段）
        if (borrow.getRoleMaxRenewDays() != null) {
            return borrow.getRoleMaxRenewDays();
        }

        // 默认值
        return 5;
    }

    /**
     * 确定可用的操作列表
     * 规则：
     * 1. 当 renew_count == 0 且可以续借时：包括 "renew", "return", "detail"
     * 2. 当 renew_count >= 1 或不能续借时：包括 "return", "detail"
     * 3. 如果已经超时：只能 "return", "detail"
     */
    private List<String> determineAvailableOperations(BookBorrow borrow) {
        List<String> operations = new java.util.ArrayList<>();

        // 如果可以查看详情（总是可以）
        operations.add("detail");

        // 如果可以归还
        if (borrow.canReturn()) {
            operations.add("return");
        }

        // 获取续借次数
        Integer renewCount = borrow.getRenewCount();
        if (renewCount == null) {
            renewCount = 0;
        }

        // 判断是否可以续借
        boolean canRenew = renewCount == 0 &&
                borrow.canRenew() &&
                RenewDaysCalculator.calculate(borrow) > 0;

        // 如果可以续借，添加续借操作
        if (canRenew) {
            operations.add("renew");
        }

        return operations;
    }

    /**
     * 转换为基础借阅记录DTO
     */
    private BaseBorrowRecordDTO convertToBaseBorrowRecordDTO(BookBorrow borrow) {
        BaseBorrowRecordDTO dto = new BaseBorrowRecordDTO();

        // 设置书籍信息
        BookInfoDTO bookInfo = new BookInfoDTO();
        bookInfo.setBookId(borrow.getBookId());
        bookInfo.setBookName(borrow.getBookName());
        bookInfo.setAuthor(borrow.getAuthor());
        bookInfo.setCoverUrl(borrow.getCoverUrl());
        dto.setBookInfo(bookInfo);

        // 设置分类信息
        dto.setCategoryName(borrow.getCategoryName());

        // 设置用户信息（管理员可见）
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(borrow.getUserId());
        userInfo.setUserName(borrow.getUserName());
        userInfo.setUid(borrow.getUid());
        userInfo.setAvatar(borrow.getAvatar());
        dto.setUserInfo(userInfo);

        // 设置操作类型和日期
        dto.setOperationType(borrow.getOperationTypeText());
        dto.setOperationDate(formatOperationDate(borrow));

        return dto;
    }

    /**
     * 格式化操作日期
     */
    private String formatOperationDate(BookBorrow borrow) {
        // 根据操作类型决定使用哪个时间字段
        Date operationDate;

        if (borrow.isBorrowOperation()) {
            operationDate = borrow.getBorrowTime();
        } else if (borrow.isRenewOperation()) {
            operationDate = DateUtil.toDate(borrow.getUpdateTime()); // 续借时间用更新时间
        } else if (borrow.isReturnOperation()) {
            operationDate = borrow.getReturnApplyTime() != null ?
                    borrow.getReturnApplyTime() : DateUtil.toDate(borrow.getUpdateTime());
        } else {
            operationDate = DateUtil.toDate(borrow.getCreateTime());
        }

        // 使用DateUtil格式化日期
        return DateUtil.format(operationDate);
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

}
