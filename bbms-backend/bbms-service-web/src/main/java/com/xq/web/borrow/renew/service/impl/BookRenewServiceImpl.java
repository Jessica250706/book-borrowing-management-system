package com.xq.web.borrow.renew.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.service.BookBorrowService;
import com.xq.web.borrow.record.service.BookOperationLogService;
import com.xq.web.borrow.renew.dto.RemainingRenewDaysDTO;
import com.xq.web.borrow.renew.entity.BookRenew;
import com.xq.web.borrow.renew.mapper.BookRenewMapper;
import com.xq.web.borrow.renew.service.BookRenewService;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BookRenewServiceImpl extends ServiceImpl<BookRenewMapper, BookRenew> implements BookRenewService {

    @Autowired
    private BookBorrowService bookBorrowService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private BookOperationLogService bookOperationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean renewBooks(BatchOperateParam param, Long userId) {
        if (param == null || CollectionUtils.isEmpty(param.getIds())) {
            throw new RuntimeException("借阅ID列表不能为空");
        }

        try {
            List<Long> borrowIds = param.getIds();
            boolean allSuccess = true;

            for (Long borrowId : borrowIds) {
                boolean success = renewSingleBook(borrowId, userId);
                if (!success) {
                    allSuccess = false;
                    log.warn("续借失败，借阅记录ID: {}", borrowId);
                }
            }

            if (!allSuccess) {
                throw new RuntimeException("部分书籍续借失败，请检查借阅记录状态");
            }

            return true;

        } catch (Exception e) {
            throw new RuntimeException("续借失败: " + e.getMessage());
        }
    }

    /**
     * 续借单本书籍
     */
    private boolean renewSingleBook(Long borrowId, Long userId) {
        // 1. 查询借阅记录
        BookBorrow borrow = bookBorrowService.getById(borrowId);
        if (borrow == null) {
            throw new RuntimeException("借阅记录不存在，ID: " + borrowId);
        }

        // 2. 验证借阅记录属于当前用户
        if (!borrow.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该借阅记录");
        }

        // 3. 验证是否可以续借
        if (!borrow.canRenew()) {
            throw new RuntimeException("该借阅记录当前状态不可续借");
        }

        // 4. 获取用户信息，计算可续借天数
        SysUser user = sysUserService.getUserWithRoleInfo(userId);
        if (user == null) {
            throw new RuntimeException("用户信息不存在");
        }

        Integer maxRenewDays = user.getMaxRenewDays();
        if (maxRenewDays == null || maxRenewDays <= 0) {
            throw new RuntimeException("该用户角色不支持续借");
        }

        // 5. 计算本次可续借天数（使用内部方法获取天数）
        Integer remainingRenewDays = getRemainingRenewDaysInternal(borrowId);
        if (remainingRenewDays <= 0) {
            throw new RuntimeException("已达到最大续借天数限制");
        }

        // 本次续借天数（取剩余可续借天数或默认值中的较小值）
        Integer renewDays = Math.min(remainingRenewDays, 7); // 默认每次续借7天

        // 6. 更新借阅记录
        LocalDateTime beforeReturnTime = borrow.getExpectedReturnTime();
        borrow.doRenew(renewDays);
        boolean borrowUpdated = bookBorrowService.updateById(borrow);

        if (borrowUpdated) {
            // 7. 创建续借记录
            BookRenew renewRecord = createRenewRecord(borrow, userId, renewDays, beforeReturnTime);
            boolean renewSaved = this.save(renewRecord);

            // 8. 记录操作日志
            if (renewSaved) {
                bookOperationLogService.logRenew(
                        userId,
                        borrow.getBookId(),
                        String.format("用户续借书籍，续借%d天", renewDays)
                );
            }

            return renewSaved;
        }

        return false;
    }

    /**
     * 获取剩余可续借天数（仅返回天数，用于内部逻辑）
     */
    private Integer getRemainingRenewDaysInternal(Long borrowId) {
        RemainingRenewDaysDTO dto = getRemainingRenewDays(borrowId);
        return dto != null && dto.getCanRenew() ? dto.getRemainingRenewDays() : 0;
    }

    /**
     * 创建续借记录
     */
    private BookRenew createRenewRecord(BookBorrow borrow, Long userId, Integer renewDays, LocalDateTime beforeReturnTime) {
        BookRenew renew = new BookRenew();
        renew.setBorrowId(borrow.getBorrowId());
        renew.setUserId(userId);
        renew.setRenewTime(LocalDateTime.now());
        renew.setRenewDays(renewDays);
        renew.setBeforeReturnTime(beforeReturnTime);
        renew.setAfterReturnTime(borrow.getExpectedReturnTime());
        renew.setCreateTime(LocalDateTime.now());
        return renew;
    }

    @Override
    public RemainingRenewDaysDTO getRemainingRenewDays(Long borrowId) {
        if (borrowId == null) {
            throw new RuntimeException("借阅ID不能为空");
        }

        try {
            // 1. 查询借阅记录
            BookBorrow borrow = bookBorrowService.getById(borrowId);
            if (borrow == null) {
                return RemainingRenewDaysDTO.createCannotRenew(borrowId, "借阅记录不存在");
            }

            // 2. 获取用户信息
            SysUser user = sysUserService.getUserWithRoleInfo(borrow.getUserId());
            if (user == null) {
                return RemainingRenewDaysDTO.createCannotRenew(borrowId, "用户信息不存在");
            }

            // 3. 获取最大可续借天数
            Integer maxRenewDays = user.getMaxRenewDays();
            if (maxRenewDays == null || maxRenewDays <= 0) {
                return RemainingRenewDaysDTO.createCannotRenew(borrowId, "该用户角色不支持续借");
            }

            // 4. 检查借阅状态是否可以续借
            if (!borrow.canRenew()) {
                String reason = "当前借阅状态不可续借";
                if (borrow.isOverdue()) {
                    reason = "书籍已超时，不可续借";
                } else if (borrow.isReturned()) {
                    reason = "书籍已归还，不可续借";
                } else if (borrow.isReturnPending()) {
                    reason = "书籍归还待确认，不可续借";
                }
                return RemainingRenewDaysDTO.createCannotRenew(borrowId, reason);
            }

            // 5. 计算已续借天数
            Integer alreadyRenewedDays = borrow.getRenewDays() != null ? borrow.getRenewDays() : 0;

            // 6. 返回完整的续借信息
            return RemainingRenewDaysDTO.createCanRenew(
                    borrowId,
                    borrow.getBookId(),
                    borrow.getBookName(),
                    borrow.getUserId(),
                    maxRenewDays,
                    alreadyRenewedDays
            );

        } catch (Exception e) {
            return RemainingRenewDaysDTO.createCannotRenew(borrowId, "获取续借信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的续借记录
     */
    public List<BookRenew> getUserRenewRecords(Long userId) {
        LambdaQueryWrapper<BookRenew> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookRenew::getUserId, userId)
                .orderByDesc(BookRenew::getRenewTime);
        return this.list(queryWrapper);
    }

    /**
     * 检查是否可以续借
     */
    public boolean canRenew(Long borrowId, Long userId) {
        try {
            // 1. 查询借阅记录
            BookBorrow borrow = bookBorrowService.getById(borrowId);
            if (borrow == null) {
                return false;
            }

            // 2. 验证权限
            if (!borrow.getUserId().equals(userId)) {
                return false;
            }

            // 3. 检查借阅状态
            if (!borrow.canRenew()) {
                return false;
            }

            // 4. 检查剩余可续借天数
            Integer remainingDays = getRemainingRenewDaysInternal(borrowId);
            return remainingDays > 0;

        } catch (Exception e) {
            return false;
        }
    }
}