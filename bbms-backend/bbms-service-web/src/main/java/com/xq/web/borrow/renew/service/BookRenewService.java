package com.xq.web.borrow.renew.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.renew.entity.BookRenew;

import java.util.List;
import java.util.Map;

public interface BookRenewService extends IService<BookRenew> {
    // 续借书籍（批量）- 简化参数
    boolean renewBooks(BatchOperateParam param, Long userId);

    // 获取剩余可续借天数 - 改为单个借阅ID
    Integer getRemainingRenewDays(Long borrowId);
}