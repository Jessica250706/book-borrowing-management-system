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
import com.xq.web.book.util.DtoConvertUtil;
import com.xq.web.book.mapper.BookInfoMapper;
import com.xq.web.book.mapper.BookReservationMapper;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.mapper.BookBorrowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import org.springframework.beans.BeanUtils;

@Service
public class BookInfoServiceImpl extends ServiceImpl<BookInfoMapper, BookInfo> implements BookInfoService {

    @Autowired
    private BookReservationMapper bookReservationMapper;
    
    @Autowired
    private BookBorrowMapper bookBorrowMapper;

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
        queryWrapper.eq("book_status", 2) // 可借阅状态
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
        if (book.getBookStatus() != 2) {
            throw new RuntimeException("图书不可借阅");
        }
        if (book.getAvailableCount() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        // 更新图书信息
        book.setAvailableCount(book.getAvailableCount() - 1);
        book.setBorrowCount(book.getBorrowCount() + 1);
        if (book.getAvailableCount() == 0) {
            book.setBookStatus(3); // 已借光
        }
        book.setUpdateTime(new Date());

        boolean updateResult = this.updateById(book);
        
        // 插入借阅记录
        if (updateResult) {
            BookBorrow borrow = new BookBorrow();
            borrow.setUserId(userId);
            borrow.setBookId(bookId);
            borrow.setBorrowTime(new Date());
            borrow.setExpectedReturnTime(new Date(System.currentTimeMillis() + borrowDays * 24L * 60 * 60 * 1000));
            borrow.setActualReturnTime(null); // 实际归还时间初始为空
            borrow.setRenewCount(0); // 续借次数初始为0
            borrow.setRenewDays(0); // 累计续借天数初始为0
            borrow.setBorrowStatus(0); // 借阅状态：0-借阅中
            borrow.setReturnConfirmStatus(0); // 归还确认状态初始为0
            borrow.setConfirmAdminId(null); // 确认管理员ID初始为空
            borrow.setConfirmTime(null); // 确认时间初始为空
            borrow.setCreateTime(new Date());
            borrow.setUpdateTime(new Date());
            
            bookBorrowMapper.insert(borrow);
        }

        return updateResult;
    }

    @Override
    @Transactional
    public boolean reserveBook(Long bookId, Long userId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        if (book.getBookStatus() == 3) { // 已借光
            // 可以预约，插入预约记录
            BookReservation reservation = new BookReservation();
            reservation.setUserId(userId);
            reservation.setBookId(bookId);
            reservation.setReservationTime(new Date());
            reservation.setReservationStatus(0); // 等待中
            reservation.setInvalidTime(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)); // 7天后过期
            reservation.setQueueNumber(1); // 简化处理，实际应该查询当前排队人数
            reservation.setNotifyStatus(0); // 未通知
            reservation.setCreateTime(new Date());
            reservation.setUpdateTime(new Date());
            
            bookReservationMapper.insert(reservation);
            return true;
        } else if (book.getBookStatus() == 2) { // 可借阅
            // 直接借阅，不需要预约
            return borrowBook(bookId, userId, 30);
        } else {
            throw new RuntimeException("图书不可预约");
        }
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
        
        return bookReservationMapper.updateById(reservation) > 0;
    }

    @Override
    @Transactional
    public boolean publishBook(Long bookId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 将图书状态改为可借阅
        book.setBookStatus(2);
        book.setUpdateTime(new Date());

        return this.updateById(book);
    }

    @Override
    @Transactional
    public boolean unpublishBook(Long bookId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 将图书状态改为未发布（下架状态）
        book.setBookStatus(0);
        book.setUpdateTime(new Date());

        return this.updateById(book);
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

    /**
     * 根据分类ID获取分类名称
     * @param categoryId 分类ID
     * @return 分类名称
     */
    private String getCategoryName(Long categoryId) {
        // TODO: 这里应该查询分类表，暂时返回默认值
        if (categoryId == null) {
            return "未分类";
        }
        // 后续实现：categoryService.getById(categoryId).getCategoryName();
        return "分类" + categoryId;
    }
}