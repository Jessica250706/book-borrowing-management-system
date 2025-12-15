// BookReservationMapper.java
package com.xq.web.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.web.book.dto.CurrentReservationDTO;
import com.xq.web.book.entity.BookReservation;
import com.xq.web.book.entity.CurrentReservationQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书预约Mapper接口
 */
@Mapper
public interface BookReservationMapper extends BaseMapper<BookReservation> {

	/**
	 * 查询当前用户的预约列表（关联书籍与分类信息）
	 */
	IPage<CurrentReservationDTO> selectCurrentReservationList(
			@Param("page") Page<CurrentReservationDTO> page,
			@Param("userId") Long userId,
			@Param("param") CurrentReservationQueryParam param
	);

	/**
	 * 根据预约ID列表查询预约详情（包含书籍/用户/分类信息）
	 */
	List<BookReservation> selectReservationsWithDetails(@Param("reservationIds") List<Long> reservationIds);
}