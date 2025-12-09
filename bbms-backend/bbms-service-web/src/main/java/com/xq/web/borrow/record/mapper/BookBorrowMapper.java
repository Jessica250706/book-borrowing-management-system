package com.xq.web.borrow.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.web.borrow.record.dto.CurrentBorrowDTO;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.CurrentBorrowQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 批量更新借阅状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids,
                          @Param("status") Integer status,
                          @Param("actualReturnTime") Date actualReturnTime);
}
