package com.xq.web.borrow.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;
import com.xq.web.borrow.record.mapper.BookBorrowMapper;
import com.xq.web.borrow.record.service.BookBorrowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class BookBorrowServiceImpl extends ServiceImpl<BookBorrowMapper, BookBorrow> implements BookBorrowService {

    @Override
    public IPage<BookBorrow> getCurrentBorrowList(CurrentBorrowQueryParam param) {
        Page<BookBorrow> page = new Page<>(param.getCurrentPage(), param.getPageSize());

        // 构建查询条件：查询当前用户的借阅中记录
        LambdaQueryWrapper<BookBorrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookBorrow::getUserId, param.getUserId())
                .eq(BookBorrow::getBorrowStatus, 0); // 0-借阅中

        // 添加其他查询条件
        if (StringUtils.hasText(param.getKeyword())) {
            // TODO:这里需要通过关联查询实现书名/作者搜索
            // 暂时先不实现，后续需要关联book_info表
        }

        // 按预计归还时间升序排序（先到期的在前）
        queryWrapper.orderByAsc(BookBorrow::getExpectedReturnTime);

        return baseMapper.selectPage(page, queryWrapper);
    }

    @Transactional
    @Override
    public boolean returnBooks(BatchOperateParam param) {
        if (param.getIds() == null || param.getIds().isEmpty()) {
            throw new RuntimeException("借阅ID列表不能为空");
        }

        // 批量更新借阅状态为"已归还"，并设置实际归还时间
        return baseMapper.batchUpdateStatus(
                param.getIds(),
                1, // 1-已归还
                new Date()
        ) > 0;
    }

    @Transactional
    @Override
    public boolean confirmReturn(BatchOperateParam param, Integer adminId) {
        if (param.getIds() == null || param.getIds().isEmpty()) {
            throw new RuntimeException("借阅ID列表不能为空");
        }

        // 管理员确认归还，更新确认状态
        for (Long borrowId : param.getIds()) {  // 改为Long类型
            BookBorrow borrow = new BookBorrow();
            borrow.setBorrowId(borrowId);
            borrow.setReturnConfirmStatus(1); // 1-已确认
            borrow.setConfirmAdminId(adminId.longValue());  // 转换为Long
            borrow.setConfirmTime(new Date());
            baseMapper.updateById(borrow);
        }
        return true;
    }

    @Override
    public IPage<BookBorrow> getBorrowRecordList(BorrowParam param) {
        Page<BookBorrow> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        // 构建多条件查询（根据param中的状态、时间等）
        LambdaQueryWrapper<BookBorrow> queryWrapper = new LambdaQueryWrapper<>();

        // 根据用户ID查询（读者端）
        if (param.getUserId() != null) {
            queryWrapper.eq(BookBorrow::getUserId, param.getUserId());
        }

        // 根据借阅状态查询
        if (param.getBorrowStatus() != null) {
            queryWrapper.eq(BookBorrow::getBorrowStatus, param.getBorrowStatus());
        }

        // 按借阅时间降序排序（最新的在前）
        queryWrapper.orderByDesc(BookBorrow::getBorrowTime);

        return baseMapper.selectPage(page, queryWrapper);
    }
}
