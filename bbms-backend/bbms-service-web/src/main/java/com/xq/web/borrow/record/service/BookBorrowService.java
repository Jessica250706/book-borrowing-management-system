package com.xq.web.borrow.record.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.borrow.record.dto.AdminBorrowRecordDTO;
import com.xq.web.borrow.record.dto.BorrowRecordDTO;
import com.xq.web.borrow.record.dto.BorrowRecordListVO;
import com.xq.web.borrow.record.dto.CurrentBorrowListVO;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;

public interface BookBorrowService extends IService<BookBorrow> {
    /**
     * 获取当前借阅列表
     * @param param 查询参数
     * @param userId 用户ID（从Token获取）
     * @return 当前借阅列表VO
     */
    CurrentBorrowListVO getCurrentBorrowList(CurrentBorrowQueryParam param, Long userId);

    // 归还书籍（批量）- 简化参数
    boolean returnBooks(BatchOperateParam param);

    // 确认归还（管理员批量操作）- 简化参数
    boolean confirmReturn(BatchOperateParam param, Integer adminId);

    // 获取借阅记录（条件+分页）
    // 读者端借阅记录查询
    BorrowRecordListVO getUserBorrowRecordList(BorrowParam param, Long userId);
    // 管理员端借阅记录查询（不需要userId）
    BorrowRecordListVO getAdminBorrowRecordList(BorrowParam param);

    // 保留原有的getBorrowRecordList方法，用于兼容性
    IPage<BookBorrow> getBorrowRecordList(BorrowParam param);
}