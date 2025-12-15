package com.xq.web.borrow.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.web.borrow.record.dto.*;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Mapper
public interface BookBorrowMapper extends BaseMapper<BookBorrow> {

    /**
     * 查询当前借阅列表（带书籍信息关联查询）
     * @param page 分页参数
     * @param userId 用户ID
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<CurrentBorrowDTO> selectCurrentBorrowList(
            @Param("page") Page<CurrentBorrowDTO> page,
            @Param("userId") Long userId,
            @Param("param") CurrentBorrowQueryParam param);

    /**
     * 查询当前归还列表（归还待确认的记录）
     */
    IPage<CurrentReturnDTO> selectCurrentReturnList(
            @Param("page") Page<CurrentReturnDTO> page,
            @Param("param") CurrentReturnQueryParam param
    );

    /**
     * 根据借阅记录ID列表查询详细信息（包含用户和书籍信息）
     * @param borrowIds 借阅记录ID列表
     * @return 借阅记录列表
     */
    List<BookBorrow> selectBorrowRecordsWithDetails(@Param("borrowIds") List<Long> borrowIds);

    /**
     * 查询借阅记录详情（包含用户名和书籍名）
     */
    BookBorrow selectBorrowRecordWithDetails(@Param("borrowId") Long borrowId);

    /**
     * 批量更新借阅状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids,
                          @Param("status") Integer status,
                          @Param("actualReturnTime") Date actualReturnTime);

    /**
     * 统计书籍的未归还借阅记录数
     * @param bookId 书籍ID
     * @return 未归还记录数
     */
    Long countBorrowRecordsByBookId(@Param("bookId") Long bookId);

    /**
     * 获取用户借阅统计信息
     * @param userId 用户ID
     * @return 借阅统计信息
     */
    UserBorrowStatisticsVO selectUserBorrowStatistics(@Param("userId") Long userId);

    /**
     * 获取用户本月借阅数量
     * @param userId 用户ID
     * @return 本月借阅数量
     */
    Integer selectMonthBorrowCount(@Param("userId") Long userId);

    /**
     * 获取用户累计借阅数量
     * @param userId 用户ID
     * @return 累计借阅数量
     */
    Integer selectTotalBorrowCount(@Param("userId") Long userId);

    /**
     * 获取用户借阅频率（本/月）
     * @param userId 用户ID
     * @return 借阅频率
     */
    BigDecimal selectBorrowFrequency(@Param("userId") Long userId);

    /**
     * 获取用户平均阅读时长（天/本）
     * @param userId 用户ID
     * @return 平均阅读时长
     */
    BigDecimal selectAverageReadingDays(@Param("userId") Long userId);

    /**
     * 获取用户借阅最多的五种书籍类别
     * @param userId 用户ID
     * @return 书籍类别借阅统计列表
     */
    List<CategoryBorrowCountVO> selectCategoryBorrowStatistics(@Param("userId") Long userId);
}
