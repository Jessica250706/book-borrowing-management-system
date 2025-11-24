package com.xq.web.book.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.book.entity.BookCategory;

import java.util.List;

/**
 * 书籍分类服务
 */
public interface BookCategoryService extends IService<BookCategory> {

    /**
     * 获取所有一级分类
     */
    List<BookCategory> getRootCategories();

    /**
     * 根据父分类ID获取子分类
     */
    List<BookCategory> getChildrenCategories(Long parentId);

    /**
     * 获取完整的分类树
     */
    List<BookCategory> getCategoryTree();

    /**
     * 根据分类编码获取分类
     */
    BookCategory getByCategoryCode(String categoryCode);

    /**
     * 验证分类编码是否唯一
     */
    boolean isCategoryCodeUnique(String categoryCode, Long excludeCategoryId);
}