package com.xq.web.borrow.record.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;

public interface BookBorrowService extends IService<BookBorrow> {
    // 读者端：获取当前借阅列表（条件+分页）
    IPage<BookBorrow> getCurrentBorrowList(CurrentBorrowQueryParam param);

    // 归还书籍（批量）- 简化参数
    boolean returnBooks(BatchOperateParam param);

    // 确认归还（管理员批量操作）- 简化参数
    boolean confirmReturn(BatchOperateParam param, Integer adminId);

    // 获取借阅记录（条件+分页）
    IPage<BookBorrow> getBorrowRecordList(BorrowParam param);
}