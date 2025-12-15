package com.xq.web.borrow.record.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.dto.PageDTO;
import com.xq.web.borrow.record.dto.*;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.BorrowParam;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;

import java.util.List;

public interface BookBorrowService extends IService<BookBorrow> {

    /**
     * 获取当前借阅书籍列表（条件+分页）
     * @param param 查询参数
     * @param userId 用户ID（从Token获取）
     * @return 当前借阅列表DTO
     */
    PageDTO<CurrentBorrowDTO> getCurrentBorrowList(CurrentBorrowQueryParam param, Long userId);

    /**
     * 获取当前归还书籍列表（条件+分页）
     * @param param 查询参数
     * @param userId 用户ID（从Token获取）
     * @return 当前归还列表DTO
     */
    PageDTO<CurrentReturnDTO> getCurrentReturnList(CurrentReturnQueryParam param, Long userId);

    /**
     * 归还书籍（批量）
     */
    boolean returnBooks(BatchOperateParam param);

    /**
     * 确认归还（管理员批量操作）
     */
    boolean confirmReturn(BatchOperateParam param, Integer adminId);

    /**
     * 获取用户借阅统计信息
     * @param userId 用户ID
     * @return 借阅统计信息
     */
    UserBorrowStatisticsVO getUserBorrowStatistics(Long userId);

    /**
     * 获取用户借阅最多的五种书籍类别
     * @param userId 用户ID
     * @return 书籍类别借阅统计列表
     */
    List<CategoryBorrowCountVO> getCategoryBorrowStatistics(Long userId);
}
