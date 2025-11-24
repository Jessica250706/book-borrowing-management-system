package com.xq.web.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.entity.BookQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookInfoMapper extends BaseMapper<BookInfo> {

    // 分页查询图书列表
    IPage<BookInfo> getBookList(Page<BookInfo> page, @Param("param") BookQueryParam param);

    // 获取新书推荐列表（最近上架的书）
    IPage<BookInfo> getNewBooks(Page<BookInfo> page);
    
    // 分页查询图书列表（关联查询分类信息）
    IPage<BookInfo> getBookListWithCategory(Page<BookInfo> page, @Param("param") BookQueryParam param);
}