package com.xq.web.borrow.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.web.borrow.record.entity.BookOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 书籍操作日志Mapper
 */
@Mapper
public interface BookOperationLogMapper extends BaseMapper<BookOperationLog> {

    /**
     * 根据用户ID和操作类型查询日志
     */
    @Select("SELECT * FROM book_operation_log WHERE user_id = #{userId} AND operation_type = #{operationType} ORDER BY operation_time DESC")
    List<BookOperationLog> selectByUserAndType(@Param("userId") Long userId, @Param("operationType") Integer operationType);

    /**
     * 查询用户最近的操作日志
     */
    @Select("SELECT * FROM book_operation_log WHERE user_id = #{userId} ORDER BY operation_time DESC LIMIT #{limit}")
    List<BookOperationLog> selectRecentLogsByUser(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 统计书籍的各种操作次数
     */
    @Select("SELECT operation_type, COUNT(*) as count FROM book_operation_log WHERE book_id = #{bookId} GROUP BY operation_type")
    List<BookOperationLog> countOperationsByBook(@Param("bookId") Long bookId);
}