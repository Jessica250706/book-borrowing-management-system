package com.xq.web.lost.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.pojo.lost.Lost;
import com.xq.pojo.lost.LostParm;
import com.xq.service.lost.LostService;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lost")
public class LostController {
    @Reference
    private LostService lostService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Lost lost){
        if(lostService.save(lost)){
            return ResultUtils.success("插入成功!");
        }
        return ResultUtils.error("插入失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Lost lost){
        if(lostService.updateById(lost)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    //删除
    @DeleteMapping("/{lostId}")
    public ResultVo delete(@PathVariable("lostId") Long lostId){
        if(lostService.removeById(lostId)){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    //列表查询
    @GetMapping("/list")
    public ResultVo list(LostParm parm){
        //构造分页对象
        IPage<Lost> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
        QueryWrapper<Lost> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(parm.getLostName())){
            query.lambda().like(Lost::getLostName,parm.getLostName());
        }
        IPage<Lost> list = lostService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }
}
