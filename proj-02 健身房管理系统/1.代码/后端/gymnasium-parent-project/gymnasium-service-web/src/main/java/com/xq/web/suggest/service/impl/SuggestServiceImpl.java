package com.xq.web.suggest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.suggest.entity.Suggest;
import com.xq.web.suggest.mapper.SuggestMapper;
import com.xq.web.suggest.service.SuggestService;
import org.springframework.stereotype.Service;

@Service
public class SuggestServiceImpl extends ServiceImpl<SuggestMapper, Suggest> implements SuggestService {
}