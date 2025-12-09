package com.xq.web.borrow.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.dto.PageDTO;
import com.xq.web.borrow.record.dto.BaseBorrowRecordDTO;
import com.xq.web.borrow.record.dto.BookInfoVO;
import com.xq.web.borrow.record.dto.CurrentBorrowDTO;
import com.xq.web.borrow.renew.dto.RemainingRenewDaysDTO;
import com.xq.web.borrow.renew.service.BookRenewService;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.role.service.SysRoleService;
import com.xq.web.system.user.dto.UserInfoVO;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;
import com.xq.web.borrow.record.mapper.BookBorrowMapper;
import com.xq.web.borrow.record.service.BookBorrowService;
import com.xq.web.borrow.record.service.BookOperationLogService;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookBorrowServiceImpl extends ServiceImpl<BookBorrowMapper, BookBorrow> implements BookBorrowService {

    @Autowired
    private BookOperationLogService bookOperationLogService;

    @Autowired
    private BookRenewService bookRenewService;

    @Override
    public PageDTO<CurrentBorrowDTO> getCurrentBorrowList(CurrentBorrowQueryParam param, Long userId) {
        // 构建分页对象
        IPage<BookBorrow> page = new Page<>(param.getPageNum(), param.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<BookBorrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookBorrow::getUserId, userId)
                .in(BookBorrow::getBorrowStatus, 0, 2) // 借阅中或已超时
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

        // 执行查询
        IPage<BookBorrow> resultPage = this.page(page, queryWrapper);



        // 转换为DTO
        List<CurrentBorrowDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToCurrentBorrowDTO)
                .collect(Collectors.toList());

        // 构建分页响应
        return PageDTO.<CurrentBorrowDTO>builder()
                .list(dtoList)
                .total(resultPage.getTotal())
                .pageNum(param.getPageNum())
                .pageSize(param.getPageSize())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnBooks(BatchOperateParam param) {
        if (param == null || CollectionUtils.isEmpty(param.getIds())) {
            throw new RuntimeException("借阅记录ID列表不能为空");
        }

        try {
            List<Long> borrowIds = param.getIds();

            // 查询借阅记录
            List<BookBorrow> borrowRecords = this.listByIds(borrowIds);
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
            LocalDateTime now = LocalDateTime.now();
            for (BookBorrow borrow : borrowRecords) {
                borrow.doReturnApply();
                // 记录操作日志
                bookOperationLogService.logReturn(
                        borrow.getUserId(),
                        borrow.getBookId(),
                        "用户申请归还书籍"
                );
            }

            // 批量更新
            boolean success = this.updateBatchById(borrowRecords);

            log.info("用户归还书籍成功，借阅记录ID: {}", borrowIds);
            return success;

        } catch (Exception e) {
            log.error("归还书籍失败", e);
            throw new RuntimeException("归还书籍失败: " + e.getMessage());
        }
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

            // 查询借阅记录
            List<BookBorrow> borrowRecords = this.listByIds(borrowIds);
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
                // 更新用户当前借阅数量
                // 这里需要调用用户服务更新 current_borrow_count
                // 更新书籍可借数量
                // 这里需要调用书籍服务更新 available_count

                // 记录操作日志
                bookOperationLogService.logOperation(
                        adminId.longValue(),
                        borrow.getBookId(),
                        5, // 归还操作
                        "管理员确认归还书籍"
                );
            }

            // 批量更新
            boolean success = this.updateBatchById(borrowRecords);

            log.info("管理员确认归还成功，借阅记录ID: {}, 管理员ID: {}", borrowIds, adminId);
            return success;

        } catch (Exception e) {
            log.error("确认归还失败", e);
            throw new RuntimeException("确认归还失败: " + e.getMessage());
        }
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
        dto.setBookAuthor(borrow.getAuthor());
        dto.setCategory(borrow.getCategoryName());

        // 设置时间相关字段（格式化为字符串）
        if (borrow.getExpectedReturnTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dto.setLatestReturnTime(borrow.getExpectedReturnTime().format(formatter));
        }

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
        dto.setRenewableDays(calculateRenewableDays(borrow));

        // 设置可操作列表
        dto.setOperations(determineAvailableOperations(borrow));

        return dto;
    }

    /**
     * 计算可续借天数
     * 规则：只能续借一次，续借次数为0时才能续借
     */
    private Integer calculateRenewableDays(BookBorrow borrow) {
        if (!borrow.canRenew()) {
            return 0;
        }

        // 获取续借次数
        Integer renewCount = borrow.getRenewCount();
        if (renewCount == null) {
            renewCount = 0;
        }

        // 如果已经续借过，不能再次续借
        if (renewCount >= 1) {
            return 0;
        }

        // 获取系统配置的最大续借天数（可以从数据库或配置文件中读取）
        Integer maxRenewDays = getMaxRenewDays(borrow);

        // 已经续借的天数
        Integer alreadyRenewed = borrow.getRenewDays() != null ? borrow.getRenewDays() : 0;

        return Math.max(0, maxRenewDays - alreadyRenewed);
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
                calculateRenewableDays(borrow) > 0;

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

        // 设置序号（需要在外部设置，这里设为null）
        dto.setSerialNumber(null); // 需要在分页时设置

        // 设置书籍信息
        BookInfoVO bookInfo = new BookInfoVO();
        bookInfo.setBookId(borrow.getBookId());
        bookInfo.setBookName(borrow.getBookName());
        bookInfo.setAuthor(borrow.getAuthor());
        bookInfo.setCoverUrl(borrow.getCoverUrl());
        dto.setBookInfo(bookInfo);

        // 设置分类信息
        dto.setCategoryName(borrow.getCategoryName());

        // 设置用户信息（管理员可见）
        UserInfoVO userInfo = new UserInfoVO();
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
        LocalDateTime operationTime;

        if (borrow.isBorrowOperation()) {
            operationTime = borrow.getBorrowTime();
        } else if (borrow.isRenewOperation()) {
            operationTime = borrow.getUpdateTime(); // 续借时间用更新时间
        } else if (borrow.isReturnOperation()) {
            operationTime = borrow.getReturnApplyTime() != null ?
                    borrow.getReturnApplyTime() : borrow.getUpdateTime();
        } else {
            operationTime = borrow.getCreateTime();
        }

        // 格式化日期
        if (operationTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return operationTime.format(formatter);
        }

        return null;
    }
}
