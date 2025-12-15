package com.xq.web.book.controller;

import com.xq.dto.PageDTO;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.book.dto.CurrentReservationDTO;
import com.xq.web.book.entity.CurrentReservationQueryParam;
import com.xq.web.book.service.BookInfoService;
import com.xq.common.context.UserContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取当前预约
 * 提供查询当前用户已预约书籍的接口，支持关键词搜索和分类筛选，按预约时间升序返回。
 * @module 获取当前预约
 */
@RestController
@RequestMapping("/api/reservation")
@Tag(name = "获取当前预约", description = "当前预约相关接口")
public class ReservationController {

    @Autowired
    private BookInfoService bookInfoService;

    /**
     * 获取当前用户的预约列表（条件+分页）
     * 默认根据预约时间升序排序，仅返回当前用户已预约且状态为等待中/已确认的记录
     *
     * @param param 查询参数，支持：currentPage、pageSize、keyword（书名/作者）、categoryCode
     * @return 分页结果：PageDTO<CurrentReservationDTO>
     */
    @GetMapping("/current/list")
    public ResultVo<PageDTO<CurrentReservationDTO>> getCurrentReservationList(CurrentReservationQueryParam param) {
        Long userId = UserContext.getUserId();
        PageDTO<CurrentReservationDTO> result = bookInfoService.getCurrentReservationList(param, userId);
        return ResultUtils.success("查询成功", result);
    }
}
