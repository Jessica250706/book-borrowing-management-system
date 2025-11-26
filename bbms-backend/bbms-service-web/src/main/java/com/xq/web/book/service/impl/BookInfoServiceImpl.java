// BookInfoServiceImpl.java
package com.xq.web.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.book.service.BookInfoService;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.entity.BookQueryParam;
import com.xq.web.book.entity.BookReservation;
import com.xq.web.book.dto.BookDetailDTO;
import com.xq.web.book.dto.BookListDTO;
import com.xq.web.book.dto.BookAdminDTO;
import com.xq.web.book.dto.ReserveResultDTO;
import com.xq.web.book.util.DtoConvertUtil;
import com.xq.web.book.mapper.BookInfoMapper;
import com.xq.web.book.mapper.BookReservationMapper;
import com.xq.web.book.mapper.BookCategoryMapper;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.mapper.BookBorrowMapper;
import com.xq.web.borrow.record.service.BookOperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.beans.BeanUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class BookInfoServiceImpl extends ServiceImpl<BookInfoMapper, BookInfo> implements BookInfoService {

    private final BookReservationMapper bookReservationMapper;

    private final BookBorrowMapper bookBorrowMapper;

    private final BookCategoryMapper bookCategoryMapper;

    private final BookOperationLogService bookOperationLogService;

    private static final Logger logger = LoggerFactory.getLogger(BookInfoServiceImpl.class);

    public BookInfoServiceImpl(BookReservationMapper bookReservationMapper,
                               BookBorrowMapper bookBorrowMapper,
                               BookCategoryMapper bookCategoryMapper,
                               BookOperationLogService bookOperationLogService) {
        this.bookReservationMapper = bookReservationMapper;
        this.bookBorrowMapper = bookBorrowMapper;
        this.bookCategoryMapper = bookCategoryMapper;
        this.bookOperationLogService = bookOperationLogService;
    }

    @Override
    public IPage<BookInfo> getBookList(BookQueryParam param) {
        Page<BookInfo> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        return this.baseMapper.getBookList(page, param);
    }
    
    @Override
    public IPage<BookInfo> getBookListWithCategory(BookQueryParam param) {
        Page<BookInfo> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        return this.baseMapper.getBookListWithCategory(page, param);
    }

    @Override
    public IPage<BookInfo> getNewBooks(Long currentPage, Long pageSize) {
        Page<BookInfo> page = new Page<>(currentPage, pageSize);

        // 查询最近30天内上架且可借阅的图书
        QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_status", 1) // 可借阅状态
                .orderByDesc("shelf_time")
                .last("LIMIT 50"); // 限制数量，按上架时间排序

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean borrowBook(Long bookId, Long userId, Integer borrowDays) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        if (book.getBookStatus() != 1) {
            throw new RuntimeException("图书不可借阅");
        }
        if (book.getAvailableCount() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        // 更新图书信息
        book.setAvailableCount(book.getAvailableCount() - 1);
        book.setBorrowCount(book.getBorrowCount() + 1);
        if (book.getAvailableCount() == 0) {
            book.setBookStatus(2); // 已借光
        }
        book.setUpdateTime(new Date());

        boolean updateResult = this.updateById(book);
        
        // 插入借阅记录
        if (updateResult) {
            BookBorrow borrow = new BookBorrow();
            borrow.setUserId(userId);
            borrow.setBookId(bookId);
            borrow.setOperationType(1); // 操作类型：1-借阅，2-续借，3-归还
            borrow.setBorrowTime(LocalDateTime.now());
            borrow.setExpectedReturnTime(LocalDateTime.now().plusDays(borrowDays)); // 设置预计归还时间（当前时间 + 借阅天数）
            borrow.setActualReturnTime(null); // 实际归还时间初始为空
            borrow.setRenewCount(0); // 续借次数初始为0
            borrow.setRenewDays(0); // 累计续借天数初始为0
            borrow.setBorrowStatus(0); // 借阅状态：0-借阅中
            borrow.setReturnConfirmStatus(0); // 归还确认状态初始为0
            borrow.setConfirmAdminId(null); // 确认管理员ID初始为空
            borrow.setConfirmTime(null); // 确认时间初始为空
            
            bookBorrowMapper.insert(borrow);
            
            // 记录操作日志
            bookOperationLogService.logBorrow(userId, bookId, 
                String.format("借阅图书《%s》，借阅天数：%d天", book.getBookName(), borrowDays));
        }

        return updateResult;
    }

    @Override
    public BookInfo createBook(BookInfo book) {
        if (book == null) {
            throw new RuntimeException("图书信息不能为空");
        }

        // 设置默认值
        if (book.getBookStatus() == null) {
            book.setBookStatus(0); // 默认未发布
        }
        if (book.getAvailableCount() == null) {
            book.setAvailableCount(book.getTotalCount());
        }
        book.setShelfTime(new Date()); // 设置上架时间
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());

        // 校验分类是否存在（若提供了 categoryId）
        if (book.getCategoryId() != null) {
            if (bookCategoryMapper.selectById(book.getCategoryId()) == null) {
                logger.warn("创建图书失败，分类不存在 id={}", book.getCategoryId());
                throw new RuntimeException("分类不存在");
            }
        }

        boolean saved = this.save(book);
        if (!saved) {
            logger.error("创建图书失败，保存返回 false: {}", book);
            throw new RuntimeException("创建书籍失败");
        }
        logger.info("创建图书成功 id={} name={}", book.getBookId(), book.getBookName());
        return book;
    }

    @Override
    @Transactional
    public ReserveResultDTO reserveBook(Long bookId, Long userId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        // 如果有库存，返回库存信息（HTTP 200）
        if (book.getAvailableCount() > 0) {
            return ReserveResultDTO.builder()
                    .bookId(bookId)
                    .bookName(book.getBookName())
                    .availableCount(book.getAvailableCount())
                    .build();
        }
        
        // 无库存，创建预约记录（HTTP 201）
        BookReservation reservation = new BookReservation();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setReservationTime(new Date());
        reservation.setReservationStatus(0); // 等待中
        reservation.setInvalidTime(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)); // 7天后过期
        reservation.setRemindStatus(0); // 未提醒
        reservation.setCreateTime(new Date());
        reservation.setUpdateTime(new Date());
        
        bookReservationMapper.insert(reservation);
        
        // 记录操作日志
        bookOperationLogService.logReservation(userId, bookId, 
            String.format("预约图书《%s》", book.getBookName()));
        
        return ReserveResultDTO.builder()
                .reservationId(reservation.getReservationId())
                .bookId(bookId)
                .bookName(book.getBookName())
                .invalidTime(reservation.getInvalidTime())
                .build();
    }

    @Override
    @Transactional
    public boolean cancelReserve(Long bookId, Long userId) {
        // 查找用户的预约记录
        QueryWrapper<BookReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("book_id", bookId)
                   .in("reservation_status", 0, 1); // 等待中或已生效的预约
        
        BookReservation reservation = bookReservationMapper.selectOne(queryWrapper);
        if (reservation == null) {
            throw new RuntimeException("未找到有效的预约记录");
        }
        
        // 更新预约状态为已取消
        reservation.setReservationStatus(2); // 已取消
        reservation.setUpdateTime(new Date());
        
        boolean result = bookReservationMapper.updateById(reservation) > 0;
        
        // 记录操作日志
        if (result) {
            BookInfo book = this.getById(bookId);
            String bookName = book != null ? book.getBookName() : "未知图书";
            bookOperationLogService.logCancelReservation(userId, bookId, 
                String.format("取消预约图书《%s》", bookName));
        }
        
        return result;
    }

    @Override
    @Transactional
    public BookInfo publishBook(Long bookId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 将图书状态改为可借阅
        book.setBookStatus(1);
        book.setUpdateTime(new Date());

        this.updateById(book);
        return this.getById(bookId);
    }

    @Override
    @Transactional
    public BookInfo unpublishBook(Long bookId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 将图书状态改为未发布（下架状态）
        book.setBookStatus(0);
        book.setUpdateTime(new Date());

        this.updateById(book);
        return this.getById(bookId);
    }

    /**
     * 将BookInfo转换为BookDetailDTO
     * @param bookInfo 图书信息
     * @return BookDetailDTO
     */
    @Override
    public BookDetailDTO convertToDetailDTO(BookInfo bookInfo) {
        BookDetailDTO dto = new BookDetailDTO();
        // 使用BeanUtils进行属性拷贝，简化代码
        BeanUtils.copyProperties(bookInfo, dto);
        
        // 使用公共工具类
        DtoConvertUtil.setBookCommonFields(dto, bookInfo);
        dto.setCategory(getCategoryName(bookInfo.getCategoryId()));
        
        return dto;
    }

    /**
     * 将BookInfo转换为BookListDTO
     * @param bookInfo 图书信息
     * @return BookListDTO
     */
    @Override
    public BookListDTO convertToListDTO(BookInfo bookInfo) {
        BookListDTO dto = new BookListDTO();
        // 使用BeanUtils进行属性拷贝
        BeanUtils.copyProperties(bookInfo, dto);
        
        // 使用公共工具类
        DtoConvertUtil.setBookCommonFields(dto, bookInfo);
        
        return dto;
    }

    /**
     * 将BookInfo转换为BookAdminDTO
     * @param bookInfo 图书信息
     * @return BookAdminDTO
     */
    @Override
    public BookAdminDTO convertToAdminDTO(BookInfo bookInfo) {
        BookAdminDTO dto = new BookAdminDTO();
        // 使用BeanUtils进行属性拷贝
     BeanUtils.copyProperties(bookInfo, dto);
        
        // 使用公共工具类
        DtoConvertUtil.setBookCommonFields(dto, bookInfo);
        dto.setCategory(getCategoryName(bookInfo.getCategoryId()));
        
        return dto;
    }

    @Override
    public BookInfo updateBookInfo(BookInfo book) {
        // 校验分类是否存在（若提供了 categoryId）
        if (book.getCategoryId() != null) {
            if (bookCategoryMapper.selectById(book.getCategoryId()) == null) {
                logger.warn("更新图书失败，分类不存在 id={}", book.getCategoryId());
                throw new RuntimeException("分类不存在");
            }
            // 填充分类名称字段
            book.setCategory(getCategoryName(book.getCategoryId()));
        }

        boolean updated = this.updateById(book);
        if (!updated) {
            logger.error("更新图书失败 id={}", book.getBookId());
            throw new RuntimeException("更新书籍失败");
        }
        logger.info("更新图书成功 id={} name={}", book.getBookId(), book.getBookName());
        return book;
    }

    /**
     * 根据分类ID获取分类名称
     * @param categoryId 分类ID
     * @return 分类名称
     */
    private String getCategoryName(Long categoryId) {
        if (categoryId == null) {
            return "未分类";
        }
        try {
            var category = bookCategoryMapper.selectById(categoryId);
            if (category != null) {
                return category.getCategoryName();
            }
        } catch (Exception e) {
            logger.warn("获取分类名称失败 id={}", categoryId, e);
        }
        return "未分类";
    }
}