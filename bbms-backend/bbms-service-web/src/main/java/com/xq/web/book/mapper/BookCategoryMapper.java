package com.xq.web.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xq.web.book.entity.BookCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 书籍分类Mapper
 */
@Mapper
public interface BookCategoryMapper extends BaseMapper<BookCategory> {

    /**
     * 根据父分类ID查询子分类
     */
    @Select("SELECT * FROM book_category WHERE parent_id = #{parentId} ORDER BY order_num ASC")
    List<BookCategory> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有一级分类
     */
    @Select("SELECT * FROM book_category WHERE parent_id = 0 ORDER BY order_num ASC")
    List<BookCategory> selectRootCategories();

    /**
     * 根据分类编码查询分类
     */
    @Select("SELECT * FROM book_category WHERE category_code = #{categoryCode}")
    BookCategory selectByCategoryCode(@Param("categoryCode") String categoryCode);

    /**
     * 查询分类树（包含子分类数量）
     */
    @Select("SELECT c.*, " +
            "(SELECT COUNT(*) FROM book_category sc WHERE sc.parent_id = c.category_id) as children_count " +
            "FROM book_category c WHERE c.parent_id = 0 ORDER BY c.order_num ASC")
    List<BookCategory> selectCategoryTree();
}