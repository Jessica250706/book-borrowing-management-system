package com.xq.web.goods.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.goods.entity.Goods;
import com.xq.web.goods.entity.GoodsParam;
import com.xq.web.goods.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Goods goods){
        if(goodsService.save(goods)){
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Goods goods){
        if(goodsService.updateById(goods)){
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    //删除
    @DeleteMapping("/{goodsId}")
    public ResultVo delete(@PathVariable("goodsId") Long goodsId){
        if(goodsService.removeById(goodsId)) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    //查询
    @GetMapping("/list")
    public ResultVo list(GoodsParam param){
        //构造分页对象
        IPage<Goods> page = new Page<>(param.getCurrentPage(),param.getPageSize());
        //构造分页查询条件
        QueryWrapper<Goods> query = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(param.getName())){
            query.lambda().like(Goods::getName,param.getName());
        }
        IPage<Goods> list = goodsService.page(page,query);
        return ResultUtils.success("查询成功",list);
    }
}
