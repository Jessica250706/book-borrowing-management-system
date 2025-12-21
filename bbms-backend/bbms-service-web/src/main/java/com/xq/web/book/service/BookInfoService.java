// BookInfoService.java
package com.xq.web.book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.book.dto.BookAdminDTO;
import com.xq.web.book.dto.BookDetailDTO;
import com.xq.web.book.dto.BookListDTO;
import com.xq.web.book.dto.BookInfoDTO;
import com.xq.web.book.dto.BorrowResultDTO;
import com.xq.web.book.dto.ReserveResultDTO;
import com.xq.web.book.dto.DeleteCheckDTO;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.entity.BookQueryParam;
import java.util.List;

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
     * @return 分页书籍列表
     */
    IPage<BookInfo> getBookListWithCategory(BookQueryParam param);
    
    /**
     * 获取新书推荐列表
     * 根据查询条件和分页参数获取新书推荐列表
     * 基础过滤：状态2全部显示，状态3、4仅显示近30天内的
     * 支持关键词搜索、分类过滤、状态过滤
     *
     * @param param 查询参数，包含筛选条件和分页信息
     * @return 新书推荐列表
     */
    IPage<BookInfo> getNewBooks(BookQueryParam param);

    /**
     * 借阅书籍
     * 处理用户借阅书籍的业务逻辑
     *
     * @param bookId 书籍ID
     * @param userId 用户ID
     * @param borrowDays 借阅天数
     * @return 借阅结果DTO，包含借阅详情
     */
    BorrowResultDTO borrowBook(Long bookId, Long userId, Integer borrowDays);

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
     * 发布时将书籍ID和 shelfTime 添加到 Redisson 延迟队列
     * shelfTime 在创建书籍时已填写，从数据库读取
     *
     * @param bookId 书籍ID
     * @return 更新后的书籍信息
     */
    BookInfo publishBook(Long bookId);

    /**
     * 批量发布书籍
     * @param bookIds 书籍ID列表
     * @return 更新后的书籍DTO列表
     */
    List<BookInfoDTO> publishBooks(List<Long> bookIds);

    /**
     * 上架书籍（从待上架状态2变为可借阅状态3）
     * 只有状态为2（待上架）的书籍才能上架
     *
     * @param bookId 书籍ID
     * @return 更新后的书籍信息
     */
    BookInfo shelveBook(Long bookId);

    /**
     * 批量上架书籍
     * @param bookIds 书籍ID列表
     * @return 更新后的书籍DTO列表
     */
    List<BookInfoDTO> shelveBooks(List<Long> bookIds);

    /**
     * 下架书籍（从待上架状态2或可借阅状态3变为未发布状态1）
     * 只有状态为2（待上架）或3（可借阅）的书籍才能下架
     *
     * @param bookId 书籍ID
     * @return 更新后的书籍信息
     */
    BookInfo unpublishBook(Long bookId);

    /**
     * 批量下架书籍
     * @param bookIds 书籍ID列表
     * @return 更新后的书籍DTO列表
     */
    List<BookInfoDTO> unpublishBooks(List<Long> bookIds);

    /**
     * 删除前检查 - 检查指定书籍是否有未归还的借阅记录
     * @param bookIds 书籍ID列表
     * @return 删除检查结果列表，包含是否存在借阅及借阅人数
     */
    List<DeleteCheckDTO> checkBooksBeforeDelete(List<Long> bookIds);

    /**
     * 批量删除书籍
     * @param bookIds 书籍ID列表
     * @return 被删除的书籍DTO列表
     */
    List<BookInfoDTO> deleteBooks(List<Long> bookIds);

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

    /**
     * 获取用户当前预约列表（条件+分页）
     * 仅返回用户自己已预约的记录，按预约时间升序排列
     *
     * @param param 查询参数
     * @param userId 用户ID
     * @return 分页预约列表 DTO
     */
    com.xq.dto.PageDTO<com.xq.web.book.dto.CurrentReservationDTO> getCurrentReservationList(com.xq.web.book.entity.CurrentReservationQueryParam param, Long userId);

    /**
     * 增加书籍的可借数量
     * @param bookIds 书籍ID列表
     * @return 是否成功
     */
    boolean increaseAvailableCount(List<Long> bookIds);
}