package com.xq.web.lost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.lost.entity.Lost;
import com.xq.web.lost.mapper.LostMapper;
import com.xq.web.lost.service.LostService;
import org.springframework.stereotype.Service;


@Service
public class LostServiceImpl extends ServiceImpl<LostMapper, Lost> implements LostService {
}