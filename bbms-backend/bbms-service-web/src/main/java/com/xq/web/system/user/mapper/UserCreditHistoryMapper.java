package com.xq.web.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.web.system.user.entity.UserCreditHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface UserCreditHistoryMapper extends BaseMapper<UserCreditHistory> {

    /**
     * 根据用户ID和时间范围查询信誉分历史
     */
    @Select("SELECT * FROM user_credit_history WHERE user_id = #{userId} " +
            "AND change_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY change_time ASC")
    List<UserCreditHistory> selectByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 查询指定时间之前的最新信誉分记录
     */
    @Select("SELECT * FROM user_credit_history WHERE user_id = #{userId} " +
            "AND change_time < #{beforeTime} " +
            "ORDER BY change_time DESC LIMIT 1")
    UserCreditHistory selectLatestBeforeTime(
            @Param("userId") Long userId,
            @Param("beforeTime") LocalDateTime beforeTime);
}
