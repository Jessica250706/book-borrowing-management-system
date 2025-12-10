package com.xq.web.operationLog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.web.operationLog.dto.BaseBorrowRecordDTO;
import com.xq.web.operationLog.entity.BookOperationLog;
import com.xq.web.borrow.record.entity.BorrowParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 书籍操作日志Mapper接口
 */
@Mapper
public interface BookOperationLogMapper extends BaseMapper<BookOperationLog> {

    /**
     * 查询用户借阅记录（带关联信息）
     */
    IPage<BaseBorrowRecordDTO> selectUserBorrowRecordPage(
            Page<BaseBorrowRecordDTO> page,
            @Param("userId") Long userId,
            @Param("param") BorrowParam param);

    /**
     * 查询管理员借阅记录（带关联信息）
     */
    IPage<BaseBorrowRecordDTO> selectAdminBorrowRecordPage(
            Page<BaseBorrowRecordDTO> page,
            @Param("param") BorrowParam param);
}