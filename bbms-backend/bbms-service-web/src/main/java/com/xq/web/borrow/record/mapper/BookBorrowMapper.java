package com.xq.web.borrow.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.web.borrow.record.entity.BookBorrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BookBorrowMapper extends BaseMapper<BookBorrow> {
    // 批量更新借阅状态（用于归还操作）
    int batchUpdateStatus(@Param("ids") List<Long> ids,  // 改为Long类型
                          @Param("status") Integer status,
                          @Param("actualReturnTime") Date actualReturnTime);
}