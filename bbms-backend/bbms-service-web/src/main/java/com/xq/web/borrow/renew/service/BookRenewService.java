package com.xq.web.borrow.renew.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.renew.dto.RemainingRenewDaysDTO;
import com.xq.web.borrow.renew.entity.BookRenew;

import java.util.List;
import java.util.Map;

public interface BookRenewService extends IService<BookRenew> {
    /**
     * 续借书籍（批量）
     */
    boolean renewBooks(BatchOperateParam param, Long userId);

    /**
     * 获取单条借阅记录的剩余可续借天数
     */
    RemainingRenewDaysDTO getRemainingRenewDays(Long borrowId);

    /**
     * 批量获取借阅记录的剩余可续借天数
     */
    Map<Long, RemainingRenewDaysDTO> batchGetRemainingRenewDays(List<Long> borrowIds);

    /**
     * 计算可续借天数（包含业务规则判断）
     */
    Integer calculateRenewableDays(Long borrowId, Long userId);

    /**
     * 判断是否可以续借
     */
    boolean canRenewBook(Long borrowId, Long userId);
}

