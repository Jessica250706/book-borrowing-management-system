// BookInfoService.java
package com.xq.web.book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.book.dto.BookAdminDTO;
import com.xq.web.book.dto.BookDetailDTO;
import com.xq.web.book.dto.BookListDTO;
import com.xq.web.book.dto.ReserveResultDTO;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.entity.BookQueryParam;

/**
 * 书籍信息服务接口
 * 提供书籍的增删改查、借阅、预约等业务功能
 */
public interface BookInfoService extends IService<BookInfo> {

    /**
     * 获取书籍列表
     * 根据查询条件获取书籍列表，支持分页
     *
     * @param param 查询参数，包含筛选条件和分页信息
     * @return 分页书籍列表
     */
    IPage<BookInfo> getBookList(BookQueryParam param);
    
    /**
     * 获取书籍列表（关联查询分类信息）
     * 根据查询条件获取书籍列表，并关联查询分类信息，支持分页
     *
     * @param param 查询参数，包含筛选条件和分页信息
     * @return 分页书籍列表（含分类信息）
     */
    IPage<BookInfo> getBookListWithCategory(BookQueryParam param);

    /**
     * 获取新书推荐
     * 获取最新上架的书籍列表，按上架时间倒序排列
     *
     * @param currentPage 当前页码
     * @param pageSize 每页数量
     * @return 新书推荐列表
     */
    IPage<BookInfo> getNewBooks(Long currentPage, Long pageSize);

    /**
     * 借阅书籍
     * 处理用户借阅书籍的业务逻辑
     *
     * @param bookId 书籍ID
     * @param userId 用户ID
     * @param borrowDays 借阅天数
     * @return 借阅是否成功
     */
    boolean borrowBook(Long bookId, Long userId, Integer borrowDays);

    /**
     * 预约书籍
     * 如果有库存则返回预约结果（HTTP 200）
     * 如果无库存则创建预约记录（HTTP 201）
     *
     * @param bookId 书籍ID
     * @param userId 用户ID
     * @return 预约结果信息，包含HTTP状态码指示
     */
    ReserveResultDTO reserveBook(Long bookId, Long userId);

    /**
     * 取消预约
     * 处理用户取消预约书籍的业务逻辑
     *
     * @param bookId 书籍ID
     * @param userId 用户ID
     * @return 取消预约是否成功
     */
    boolean cancelReserve(Long bookId, Long userId);

    /**
     * 发布书籍（从未发布状态1变为待上架状态2）
     * 只有状态为1（未发布）的书籍才能发布
     *
     * @param bookId 书籍ID
     * @return 更新后的书籍信息
     */
    BookInfo publishBook(Long bookId);

    /**
     * 上架书籍（从待上架状态2变为可借阅状态3）
     * 只有状态为2（待上架）的书籍才能上架
     *
     * @param bookId 书籍ID
     * @return 更新后的书籍信息
     */
    BookInfo shelveBook(Long bookId);

    /**
     * 下架书籍（从待上架或可借阅状态变为未发布状态1）
     *
     * @param bookId 书籍ID
     * @return 更新后的书籍信息
     */
    BookInfo unpublishBook(Long bookId);

    /**
     * 将BookInfo实体转换为BookDetailDTO
     * 用于返回书籍详情信息
     *
     * @param bookInfo 书籍信息实体
     * @return 书籍详情DTO
     */
    BookDetailDTO convertToDetailDTO(BookInfo bookInfo);

    /**
     * 将BookInfo实体转换为BookListDTO
     * 用于返回书籍列表信息
     *
     * @param bookInfo 书籍信息实体
     * @return 书籍列表DTO
     */
    BookListDTO convertToListDTO(BookInfo bookInfo);

    /**
     * 将BookInfo实体转换为BookAdminDTO
     * 用于管理员页面展示的书籍信息
     *
     * @param bookInfo 书籍信息实体
     * @return 管理员用书籍DTO
     */
    BookAdminDTO convertToAdminDTO(BookInfo bookInfo);

    /**
     * 创建书籍（包含分类校验与默认值设置）
     * @param book 要创建的书籍
     * @return 创建并持久化后的 BookInfo（包含生成的 ID）
     */
    BookInfo createBook(BookInfo book);

    /**
     * 更新书籍信息（包含分类校验与字段填充）
     * @param book 要更新的书籍信息
     * @return 更新后的 BookInfo
     */
    BookInfo updateBookInfo(BookInfo book);
}