package com.xq.web.equipment.serivce.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.equipment.entity.Material;
import com.xq.web.equipment.mapper.MaterialMapper;
import com.xq.web.equipment.serivce.MaterialService;
import org.springframework.stereotype.Service;

@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {
}
