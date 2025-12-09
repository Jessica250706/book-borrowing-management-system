package com.xq.web.borrow.renew.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.utils.DateUtil;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.mapper.BookBorrowMapper;
import com.xq.web.borrow.record.service.BookOperationLogService;
import com.xq.web.borrow.renew.dto.RemainingRenewDaysDTO;
import com.xq.web.borrow.renew.entity.BookRenew;
import com.xq.web.borrow.renew.mapper.BookRenewMapper;
import com.xq.web.borrow.renew.service.BookRenewService;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookRenewServiceImpl extends ServiceImpl<BookRenewMapper, BookRenew> implements BookRenewService {

    @Autowired
    private BookBorrowMapper bookBorrowMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BookOperationLogService bookOperationLogService;  // 这个没有循环依赖，可以保留

    // 默认每次续借天数
    private static final Integer DEFAULT_RENEW_DAYS = 7;

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
        // 1. 查询借阅记录 - 用Mapper
        BookBorrow borrow = bookBorrowMapper.selectById(borrowId);
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

        // 4. 获取用户信息，计算可续借天数 - 用Mapper
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户信息不存在");
        }

        // 注意：这里需要角色信息！如果maxRenewDays在角色表中，需要处理
        // 假设1：maxRenewDays在user表中（直接有）
        // 假设2：maxRenewDays在role表中（需要关联查询）
        Integer maxRenewDays = user.getMaxRenewDays(); // 假设1

        // 如果是假设2，需要额外查询角色
        if (maxRenewDays == null || maxRenewDays <= 0) {
            // 尝试从角色表获取
            // 这里需要实现getUserRoleMaxRenewDays方法
            maxRenewDays = getUserRoleMaxRenewDays(userId);
            if (maxRenewDays == null || maxRenewDays <= 0) {
                throw new RuntimeException("该用户角色不支持续借");
            }
        }

        // 5. 计算本次可续借天数
        Integer remainingRenewDays = calculateRenewableDaysInternal(borrow, user);
        if (remainingRenewDays <= 0) {
            throw new RuntimeException("已达到最大续借天数限制");
        }

        // 本次续借天数（取剩余可续借天数或默认值中的较小值）
        Integer renewDays = Math.min(remainingRenewDays, DEFAULT_RENEW_DAYS);

        // 6. 更新借阅记录 - 用Mapper
        Date beforeReturnTime = borrow.getExpectedReturnTime(); // 修改为Date类型
        borrow.doRenew(renewDays);
        boolean borrowUpdated = bookBorrowMapper.updateById(borrow) > 0;

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

    @Override
    public RemainingRenewDaysDTO getRemainingRenewDays(Long borrowId) {
        if (borrowId == null) {
            throw new RuntimeException("借阅ID不能为空");
        }

        try {
            // 1. 查询借阅记录 - 用Mapper
            BookBorrow borrow = bookBorrowMapper.selectById(borrowId);
            if (borrow == null) {
                return RemainingRenewDaysDTO.createCannotRenew(borrowId, "借阅记录不存在");
            }

            // 2. 获取用户信息 - 用Mapper
            SysUser user = sysUserMapper.selectById(borrow.getUserId());
            if (user == null) {
                return RemainingRenewDaysDTO.createCannotRenew(borrowId, "用户信息不存在");
            }

            // 3. 获取最大可续借天数
            Integer maxRenewDays = getMaxRenewDaysForUser(user);

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

    @Override
    public Map<Long, RemainingRenewDaysDTO> batchGetRemainingRenewDays(List<Long> borrowIds) {
        if (CollectionUtils.isEmpty(borrowIds)) {
            return Collections.emptyMap();
        }

        Map<Long, RemainingRenewDaysDTO> resultMap = new ConcurrentHashMap<>();

        try {
            // 1. 批量查询借阅记录 - 用Mapper
            List<BookBorrow> borrowList = bookBorrowMapper.selectBatchIds(borrowIds);
            if (CollectionUtils.isEmpty(borrowList)) {
                borrowIds.forEach(id -> resultMap.put(id,
                        RemainingRenewDaysDTO.createCannotRenew(id, "借阅记录不存在")));
                return resultMap;
            }

            Map<Long, BookBorrow> borrowMap = borrowList.stream()
                    .collect(Collectors.toMap(BookBorrow::getBorrowId, b -> b));

            // 2. 按用户分组，批量查询用户信息 - 用Mapper
            Set<Long> userIds = borrowList.stream()
                    .map(BookBorrow::getUserId)
                    .collect(Collectors.toSet());

            List<SysUser> userList = sysUserMapper.selectBatchIds(userIds);
            Map<Long, SysUser> userMap = userList.stream()
                    .collect(Collectors.toMap(SysUser::getUserId, u -> u));

            // 3. 批量处理每个借阅记录
            for (Long borrowId : borrowIds) {
                try {
                    BookBorrow borrow = borrowMap.get(borrowId);

                    if (borrow == null) {
                        resultMap.put(borrowId,
                                RemainingRenewDaysDTO.createCannotRenew(borrowId, "借阅记录不存在"));
                        continue;
                    }

                    SysUser user = userMap.get(borrow.getUserId());
                    if (user == null) {
                        resultMap.put(borrowId,
                                RemainingRenewDaysDTO.createCannotRenew(borrowId, "用户信息不存在"));
                        continue;
                    }

                    Integer maxRenewDays = getMaxRenewDaysForUser(user);
                    if (maxRenewDays == null || maxRenewDays <= 0) {
                        resultMap.put(borrowId,
                                RemainingRenewDaysDTO.createCannotRenew(borrowId, "该用户角色不支持续借"));
                        continue;
                    }

                    // 检查借阅状态
                    if (!borrow.canRenew()) {
                        String reason = "当前借阅状态不可续借";
                        if (borrow.isOverdue()) {
                            reason = "书籍已超时，不可续借";
                        } else if (borrow.isReturned()) {
                            reason = "书籍已归还，不可续借";
                        } else if (borrow.isReturnPending()) {
                            reason = "书籍归还待确认，不可续借";
                        }
                        resultMap.put(borrowId, RemainingRenewDaysDTO.createCannotRenew(borrowId, reason));
                        continue;
                    }

                    // 计算已续借天数
                    Integer alreadyRenewedDays = borrow.getRenewDays() != null ? borrow.getRenewDays() : 0;

                    // 创建可续借响应
                    resultMap.put(borrowId, RemainingRenewDaysDTO.createCanRenew(
                            borrowId,
                            borrow.getBookId(),
                            borrow.getBookName(),
                            borrow.getUserId(),
                            maxRenewDays,
                            alreadyRenewedDays
                    ));

                } catch (Exception e) {
                    log.error("处理借阅记录失败，借阅ID: {}", borrowId, e);
                    resultMap.put(borrowId,
                            RemainingRenewDaysDTO.createCannotRenew(borrowId, "获取续借信息失败: " + e.getMessage()));
                }
            }

            return resultMap;

        } catch (Exception e) {
            log.error("批量获取续借天数失败", e);
            // 返回所有记录为失败状态
            Map<Long, RemainingRenewDaysDTO> errorMap = new HashMap<>();
            borrowIds.forEach(id -> errorMap.put(id,
                    RemainingRenewDaysDTO.createCannotRenew(id, "批量查询失败: " + e.getMessage())));
            return errorMap;
        }
    }

    @Override
    public Integer calculateRenewableDays(Long borrowId, Long userId) {
        try {
            // 1. 查询借阅记录 - 用Mapper
            BookBorrow borrow = bookBorrowMapper.selectById(borrowId);
            if (borrow == null) {
                return 0;
            }

            // 2. 验证权限
            if (!borrow.getUserId().equals(userId)) {
                return 0;
            }

            // 3. 检查借阅状态
            if (!borrow.canRenew()) {
                return 0;
            }

            // 4. 获取用户信息 - 用Mapper
            SysUser user = sysUserMapper.selectById(userId);
            if (user == null) {
                return 0;
            }

            Integer maxRenewDays = getMaxRenewDaysForUser(user);
            if (maxRenewDays == null || maxRenewDays <= 0) {
                return 0;
            }

            // 5. 计算已续借天数
            Integer alreadyRenewedDays = borrow.getRenewDays() != null ? borrow.getRenewDays() : 0;

            // 6. 计算剩余可续借天数
            return Math.max(0, maxRenewDays - alreadyRenewedDays);

        } catch (Exception e) {
            log.error("计算可续借天数失败，借阅ID: {}, 用户ID: {}", borrowId, userId, e);
            return 0;
        }
    }

    @Override
    public boolean canRenewBook(Long borrowId, Long userId) {
        try {
            // 1. 查询借阅记录 - 用Mapper
            BookBorrow borrow = bookBorrowMapper.selectById(borrowId);
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
            Integer remainingDays = calculateRenewableDays(borrowId, userId);
            return remainingDays > 0;

        } catch (Exception e) {
            log.error("检查是否可以续借失败，借阅ID: {}, 用户ID: {}", borrowId, userId, e);
            return false;
        }
    }

    /**
     * 内部计算方法
     */
    private Integer calculateRenewableDaysInternal(BookBorrow borrow, SysUser user) {
        Integer maxRenewDays = getMaxRenewDaysForUser(user);
        if (maxRenewDays == null || maxRenewDays <= 0) {
            return 0;
        }

        Integer alreadyRenewedDays = borrow.getRenewDays() != null ? borrow.getRenewDays() : 0;
        return Math.max(0, maxRenewDays - alreadyRenewedDays);
    }

    /**
     * 获取用户的最大续借天数
     * 注意：需要根据你的数据结构调整
     */
    private Integer getMaxRenewDaysForUser(SysUser user) {
        // 情况1：maxRenewDays直接存储在user表中
        if (user.getMaxRenewDays() != null) {
            return user.getMaxRenewDays();
        }

        // 情况2：maxRenewDays存储在role表中
        if (user.getRoleId() != null) {
            // 需要查询角色表，这里需要你的SysRoleMapper
            // 暂时返回默认值，你需要根据实际情况实现
            return 14; // 默认14天
        }

        return 0;
    }

    /**
     * 从角色表获取最大续借天数
     * 需要你根据实际情况实现
     */
    private Integer getUserRoleMaxRenewDays(Long userId) {
        // 这里需要关联查询用户角色表
        // 示例SQL:
        // SELECT r.max_renew_days
        // FROM sys_user u
        // JOIN sys_role r ON u.role_id = r.role_id
        // WHERE u.user_id = #{userId}

        // 暂时返回null，你需要实现这个方法
        return null;
    }

    /**
     * 创建续借记录
     */
    private BookRenew createRenewRecord(BookBorrow borrow, Long userId, Integer renewDays, Date beforeReturnTime) {
        BookRenew renew = new BookRenew();
        Date now = DateUtil.now();
        renew.setBorrowId(borrow.getBorrowId());
        renew.setUserId(userId);
        renew.setRenewTime(now);
        renew.setRenewDays(renewDays);
        renew.setBeforeReturnTime(beforeReturnTime);
        renew.setAfterReturnTime(borrow.getExpectedReturnTime());
        renew.setCreateTime(now);
        return renew;
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
}
