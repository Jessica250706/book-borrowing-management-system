package com.xq.web.equipment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.equipment.entity.ListParam;
import com.xq.web.equipment.entity.Material;
import com.xq.web.equipment.serivce.MaterialService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    MaterialService materialService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Material material){
        if(materialService.save(material)){
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Material material){
        if(materialService.updateById(material)){
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    //删除
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id){
        if(materialService.removeById(id)){
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    //查询列表
    @GetMapping("/list")
    public ResultVo list(ListParam param){
        //构造分页对象
        IPage<Material> page = new Page<>(param.getCurrentPage(),param.getPageSize());
        //构造查询条件
        QueryWrapper<Material> query = new QueryWrapper();
        if(StringUtils.isNotEmpty(param.getName())){
            query.lambda().like(Material::getName,param.getName());
        }
        IPage<Material> list = materialService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }
}
