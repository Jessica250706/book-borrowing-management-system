package com.xq.web.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.utils.DateUtil;
import com.xq.web.book.service.BookInfoService;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.entity.BookQueryParam;
import com.xq.web.book.entity.BookReservation;
import com.xq.web.book.entity.BookCategory;
import com.xq.web.book.dto.BookDetailDTO;
import com.xq.web.book.dto.BookListDTO;
import com.xq.web.book.dto.BookAdminDTO;
import com.xq.web.book.dto.ReserveResultDTO;
import com.xq.web.book.dto.BorrowResultDTO;
import com.xq.web.book.util.DtoConvertUtil;
import com.xq.web.book.mapper.BookInfoMapper;
import com.xq.web.book.mapper.BookReservationMapper;
import com.xq.web.book.mapper.BookCategoryMapper;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.mapper.BookBorrowMapper;
import com.xq.web.operationLog.service.BookOperationLogService;
import com.xq.common.context.UserContext;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.user.mapper.SysUserMapper;
import com.xq.web.system.role.mapper.SysRoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final SysUserMapper sysUserMapper;

    private final SysRoleMapper sysRoleMapper;

    private static final Logger logger = LoggerFactory.getLogger(BookInfoServiceImpl.class);

    public BookInfoServiceImpl(BookReservationMapper bookReservationMapper,
                               BookBorrowMapper bookBorrowMapper,
                               BookCategoryMapper bookCategoryMapper,
                               BookOperationLogService bookOperationLogService,
                               SysUserMapper sysUserMapper,
                               SysRoleMapper sysRoleMapper) {
        this.bookReservationMapper = bookReservationMapper;
        this.bookBorrowMapper = bookBorrowMapper;
        this.bookCategoryMapper = bookCategoryMapper;
        this.bookOperationLogService = bookOperationLogService;
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public IPage<BookInfo> getBookList(BookQueryParam param) {
        Page<BookInfo> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        
        // 【强制权限过滤】根据用户身份设置 isAdmin，传给 mapper
        // 读者：只能看状态 2(待上架)、3(可借阅)、4(已借光)
        // 管理员：能看所有状态 0,1,2,3,4
        param.setIsAdmin(UserContext.getIsAdmin());
        
        IPage<BookInfo> result = this.baseMapper.getBookList(page, param);
        
        // 为每本书填充分类名称
        result.getRecords().forEach(book -> {
            if (book.getCategoryId() != null) {
                book.setCategory(getCategoryName(book.getCategoryId()));
            }
        });
        
        return result;
    }
    
    @Override
    public IPage<BookInfo> getBookListWithCategory(BookQueryParam param) {
        Page<BookInfo> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        
        // 【强制权限过滤】
        param.setIsAdmin(UserContext.getIsAdmin());
        
        return this.baseMapper.getBookListWithCategory(page, param);
    }

    @Override
    public IPage<BookInfo> getNewBooks(BookQueryParam param) {
        Page<BookInfo> page = new Page<>(param.getCurrentPage(), param.getPageSize());
        
        // 【强制权限过滤】根据用户身份设置 isAdmin，传给 mapper
        // 读者：只能看状态 2(待上架)、3(可借阅)、4(已借光)
        // 管理员：可以看所有状态 0,1,2,3,4（但基础过滤仍然限制为 2,3,4）
        param.setIsAdmin(UserContext.getIsAdmin());
        
        IPage<BookInfo> result = this.baseMapper.getNewBooks(page, param);
        
        // 为每本书填充分类名称
        result.getRecords().forEach(book -> {
            if (book.getCategoryId() != null) {
                book.setCategory(getCategoryName(book.getCategoryId()));
            }
        });
        
        return result;
    }

    @Override
    @Transactional
    public BorrowResultDTO borrowBook(Long bookId, Long userId, Integer borrowDays) {
        // 管理员不能执行借阅操作
        if (UserContext.getIsAdmin()) {
            throw new RuntimeException("管理员不能进行借阅操作");
        }
        
        // 根据用户角色自动限制借阅天数
        borrowDays = getMaxBorrowDaysForUser(userId, borrowDays);
        
        // 检查用户是否已借阅该图书且未归还（防止重复借阅同一本书）
        QueryWrapper<BookBorrow> borrowCheckWrapper = new QueryWrapper<>();
        borrowCheckWrapper.eq("user_id", userId)
                         .eq("book_id", bookId)
                         .in("borrow_status", 0, 2, 3); // 借阅中、已超时、归还待确认
        if (bookBorrowMapper.selectCount(borrowCheckWrapper) > 0) {
            throw new RuntimeException("您已借阅该图书且未归还，请勿重复借阅");
        }
        
        // 检查用户当前借阅数量是否已达上限
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null && user.getRoleId() != null) {
            SysRole role = sysRoleMapper.selectById(user.getRoleId());
            if (role != null && role.getMaxBorrowNum() != null) {
                QueryWrapper<BookBorrow> countWrapper = new QueryWrapper<>();
                countWrapper.eq("user_id", userId)
                           .eq("borrow_status", 0); // 借阅中
                long currentBorrowCount = bookBorrowMapper.selectCount(countWrapper);
                if (currentBorrowCount >= role.getMaxBorrowNum()) {
                    throw new RuntimeException(String.format("您的借阅数量已达上限（%d本），请归还后再借阅", role.getMaxBorrowNum()));
                }
            }
        }
        
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        // 根据图书状态判断是否可借阅
        // 只有状态3（上架可借阅）才允许借阅
        if (book.getBookStatus() == null) {
            throw new RuntimeException("图书状态异常");
        }
        if (book.getBookStatus() != 3) {
            switch (book.getBookStatus()) {
                case 0, 1 -> throw new RuntimeException("图书未发布，无法借阅");
                case 2 -> throw new RuntimeException("图书尚未上架，请预约");
                case 4 -> throw new RuntimeException("图书已借光，请预约");
                default -> throw new RuntimeException("图书状态异常，无法借阅");
            }
        }
        // 检查库存是否充足
        if (book.getAvailableCount() == null || book.getAvailableCount() <= 0) {
            throw new RuntimeException("图书库存不足，请预约");
        }

        // 更新图书信息
        book.setAvailableCount(book.getAvailableCount() - 1);
        book.setBorrowCount(book.getBorrowCount() + 1);
        // 如果借光，改为状态4（已借光）
        if (book.getAvailableCount() == 0) {
            book.setBookStatus(4);
        }
        book.setUpdateTime(new Date());

        boolean updateResult = this.updateById(book);
        
        // 插入借阅记录
        if (updateResult) {
            BookBorrow borrow = createBorrowRecord(bookId, userId, borrowDays);
            bookBorrowMapper.insert(borrow);
            
            // 更新用户当前借阅数量
            if (user != null) {
                user.setCurrentBorrowCount((user.getCurrentBorrowCount() == null ? 0 : user.getCurrentBorrowCount()) + 1);
                sysUserMapper.updateById(user);
            }
            
            // 记录操作日志
            bookOperationLogService.logBorrow(userId, bookId, 
                String.format("借阅图书《%s》，借阅天数：%d天", book.getBookName(), borrowDays));
            
            // 构建借阅结果 DTO 并返回
            return BorrowResultDTO.builder()
                    .borrowId(borrow.getBorrowId())
                    .bookId(bookId)
                    .bookName(book.getBookName())
                    .author(book.getAuthor())
                    .coverUrl(book.getCoverUrl())
                    .category(getCategoryName(book.getCategoryId()))
                    .borrowTime(new Date())
                    .expectedReturnTime(java.sql.Timestamp.valueOf(LocalDateTime.now().plusDays(borrowDays)))
                    .borrowDays(borrowDays)
                    .borrowStatus(0)
                    .build();
        }
        
        throw new RuntimeException("借阅失败");
    }

    /**
     * 创建借阅记录对象
     */
    private BookBorrow createBorrowRecord(Long bookId, Long userId, Integer borrowDays) {
        Date now = DateUtil.now();
        BookBorrow borrow = new BookBorrow();
        borrow.setUserId(userId);
        borrow.setBookId(bookId);
        borrow.setOperationType(1); // 操作类型：1-借阅，2-续借，3-归还
        borrow.setBorrowTime(now);
        borrow.setExpectedReturnTime(DateUtil.plusDays(now, borrowDays));
        borrow.setBorrowStatus(0); // 借阅状态：0-借阅中
        borrow.setReturnConfirmStatus(0); // 归还确认状态初始为0
        return borrow;
    }

    @Override
    public BookInfo createBook(BookInfo book) {
        if (book == null) {
            throw new RuntimeException("图书信息不能为空");
        }

        // bookStatus 由 Controller 层强制赋值：0-草稿，1-未发布
        // 仅当 bookStatus != 0 时校验必填项
        if (book.getBookStatus() != null && book.getBookStatus() != 0) {
            validateRequiredFields(book);
        }
        
        // 草稿状态也必须提供书籍名称
        if (book.getBookName() == null || book.getBookName().trim().isEmpty()) {
            throw new RuntimeException("书籍名称不能为空");
        }

        // ✅ 新增：ISBN 唯一性校验，防止重复添加
        if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
            QueryWrapper<BookInfo> isbnQuery = new QueryWrapper<>();
            isbnQuery.eq("isbn", book.getIsbn());
            if (this.baseMapper.selectCount(isbnQuery) > 0) {
                throw new RuntimeException("ISBN 已存在，防止重复添加");
            }
        }

        // 设置默认值
        if (book.getAvailableCount() == null && book.getTotalCount() != null) {
            book.setAvailableCount(book.getTotalCount());
        }
        
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());

        // 处理分类逻辑
        handleBookCategory(book);

        boolean saved = this.save(book);
        if (!saved) {
            logger.error("创建图书失败，保存返回 false: {}", book);
            throw new RuntimeException("创建书籍失败");
        }
        logger.info("创建图书成功 id={} name={} status={}", book.getBookId(), book.getBookName(), book.getBookStatus());
        
        // 设置分类名称
        if (book.getCategoryId() != null) {
            book.setCategory(getCategoryName(book.getCategoryId()));
        }
        return book;
    }

    /**
     * 处理分类逻辑，根据bookStatus决定分类处理方式
     */
    private void handleBookCategory(BookInfo book) {
        if (book.getBookStatus() != null) {
            switch (book.getBookStatus()) {
                case 0 -> {
                    // 草稿状态：默认使用第一个分类，如果提供则查询
                    if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
                        book.setCategoryId(1L);
                    } else {
                        Long categoryId = queryCategoryId(book.getCategory());
                        book.setCategoryId(categoryId != null ? categoryId : 1L);
                    }
                }
                default -> {
                    // 非草稿状态：必须提供有效的分类
                    if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
                        logger.warn("创建图书失败，分类未提供");
                        throw new RuntimeException("书籍分类不能为空");
                    }
                    Long categoryId = queryCategoryId(book.getCategory());
                    if (categoryId == null) {
                        logger.warn("创建图书失败，分类不存在 name={}", book.getCategory());
                        throw new RuntimeException("分类不存在");
                    }
                    book.setCategoryId(categoryId);
                }
            }
        }
    }

    /**
     * 根据分类名称查询分类ID
     */
    private Long queryCategoryId(String categoryName) {
        QueryWrapper<BookCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_name", categoryName);
        BookCategory category = bookCategoryMapper.selectOne(queryWrapper);
        return category != null ? category.getCategoryId() : null;
    }

    /**
     * 校验必填项是否填写完整
     * 必填项：书籍名称、作者、分类、总数、简介、上架时间
     * 注：封面校验暂时注释，待文件上传接口完成后再启用
     */
    private void validateRequiredFields(BookInfo book) {
        validateStringField(book.getBookName(), "书籍名称", 1, 50);
        // TODO: 暂时注释掉封面校验，待文件上传接口完成后再启用
        // validateStringField(book.getCoverUrl(), "书籍封面", 1, Integer.MAX_VALUE);
        validateStringField(book.getAuthor(), "作者", 1, 30);
        validateStringField(book.getCategory(), "书籍分类", 1, Integer.MAX_VALUE);
        validateIntegerField(book.getTotalCount(), "书籍总数", 1, 999);
        validateStringField(book.getIntro(), "书籍简介", 1, 300);
        if (book.getShelfTime() == null) {
            throw new RuntimeException("上架时间不能为空");
        }
    }

    /**
     * 验证字符串字段
     */
    private void validateStringField(String value, String fieldName, int minLength, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(fieldName + "不能为空");
        }
        if (value.length() > maxLength) {
            throw new RuntimeException(fieldName + "长度不能超过" + maxLength + "字符");
        }
        if (value.length() < minLength) {
            throw new RuntimeException(fieldName + "长度不能少于" + minLength + "字符");
        }
    }

    /**
     * 验证整数字段
     */
    private void validateIntegerField(Integer value, String fieldName, int min, int max) {
        if (value == null || value < min || value > max) {
            throw new RuntimeException(fieldName + "必须为" + min + "~" + max + "的整数");
        }
    }

    @Override
    @Transactional
    public ReserveResultDTO reserveBook(Long bookId, Long userId) {
        // 管理员不能执行预约操作
        if (UserContext.getIsAdmin()) {
            throw new RuntimeException("管理员不能预约图书");
        }
        
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        // 检查图书状态是否允许预约
        if (book.getBookStatus() == null) {
            throw new RuntimeException("图书状态异常");
        }
        if (book.getBookStatus() == 0 || book.getBookStatus() == 1) {
            throw new RuntimeException("图书未发布，无法预约");
        }
        
        // 检查是否已经预约过该书籍（防止重复预约）
        QueryWrapper<BookReservation> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("user_id", userId)
                   .eq("book_id", bookId)
                   .in("reservation_status", 0, 1); // 预约中或可借阅
        if (bookReservationMapper.selectCount(checkWrapper) > 0) {
            throw new RuntimeException("您已预约过该图书，请勿重复预约");
        }
        
        // 状态3（可借阅）且有库存时，建议直接借阅而非预约
        if (book.getBookStatus() == 3 && book.getAvailableCount() > 0) {
            return ReserveResultDTO.builder()
                    .bookId(bookId)
                    .bookName(book.getBookName())
                    .availableCount(book.getAvailableCount())
                    .build();
        }
        
        // 无库存，创建预约记录（HTTP 201）
        BookReservation reservation = createReservation(bookId, userId);
        bookReservationMapper.insert(reservation);
        
        // 更新用户当前预约数量
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            user.setCurrentReserveCount((user.getCurrentReserveCount() == null ? 0 : user.getCurrentReserveCount()) + 1);
            sysUserMapper.updateById(user);
        }
        
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

    /**
     * 创建预约记录对象
     */
    private BookReservation createReservation(Long bookId, Long userId) {
        Date now = new Date();
        Date invalidTime = new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000); // 7天后过期
        
        BookReservation reservation = new BookReservation();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setReservationTime(now);
        reservation.setReservationStatus(0); // 等待中
        reservation.setInvalidTime(invalidTime);
        reservation.setRemindStatus(0); // 未提醒
        reservation.setCreateTime(now);
        reservation.setUpdateTime(now);
        return reservation;
    }

    @Override
    @Transactional
    public boolean cancelReserve(Long bookId, Long userId) {
        // 管理员不能执行取消预约操作
        if (UserContext.getIsAdmin()) {
            throw new RuntimeException("管理员不能取消预约");
        }
        
        // 查找用户的预约记录
        QueryWrapper<BookReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("book_id", bookId)
                   .in("reservation_status", 0, 1); // 等待中或已生效的预约
        
        BookReservation reservation = bookReservationMapper.selectOne(queryWrapper);
        if (reservation == null) {
            throw new RuntimeException("未找到有效的预约记录");
        }
        
        // 更新预约状态为已取消（状态 2）
        reservation.setReservationStatus(2);
        reservation.setUpdateTime(new Date());
        
        int updated = bookReservationMapper.updateById(reservation);
        boolean result = updated > 0;

        // 记录操作日志
        if (result) {
            // 更新用户当前预约数量
            SysUser user = sysUserMapper.selectById(userId);
            if (user != null && user.getCurrentReserveCount() != null && user.getCurrentReserveCount() > 0) {
                user.setCurrentReserveCount(user.getCurrentReserveCount() - 1);
                sysUserMapper.updateById(user);
            }
            
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

        // 只有作为草稿（状态 0）的书籍无法发布
        // 发布必须是介于未发布状态 1
        if (book.getBookStatus() == null) {
            throw new RuntimeException("图书状态异常");
        }
        if (book.getBookStatus() != 1) {
            throw new RuntimeException("只有未发布书籍才能发布，当前状态: " + book.getBookStatus());
        }

        // 将图书状态改为待上架（2）
        // 上架时间到达后，由定时任务自动改为可借阅（3）
        book.setBookStatus(2);
        book.setUpdateTime(new Date());

        this.updateById(book);
        book = this.getById(bookId);
        // 设置分类名称
        if (book.getCategoryId() != null) {
            book.setCategory(getCategoryName(book.getCategoryId()));
        }
        return book;
    }

    @Override
    @Transactional
    public BookInfo shelveBook(Long bookId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 只有待上架状态（状态 2）的书籍才能上架
        if (book.getBookStatus() == null) {
            throw new RuntimeException("图书状态异常");
        }
        if (book.getBookStatus() != 2) {
            throw new RuntimeException("只有待上架书籍才能上架，当前状态: " + book.getBookStatus());
        }

        // 将图书状态改为可借阅（3）
        book.setBookStatus(3);
        book.setShelfTime(new Date()); // 上架时设置上架时间为当前时间
        book.setUpdateTime(new Date());

        this.updateById(book);
        book = this.getById(bookId);
        // 设置分类名称
        if (book.getCategoryId() != null) {
            book.setCategory(getCategoryName(book.getCategoryId()));
        }
        return book;
    }

    @Override
    @Transactional
    public BookInfo unpublishBook(Long bookId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 只有待上架（状态 2）或可借阅（状态 3）状态的书籍才能下架
        if (book.getBookStatus() == null) {
            throw new RuntimeException("图书状态异常");
        }
        if (book.getBookStatus() != 2 && book.getBookStatus() != 3) {
            throw new RuntimeException("只有待上架或可借阅书籍才能下架，当前状态: " + book.getBookStatus());
        }

        // 将图书状态改为未发布（1）
        book.setBookStatus(1);
        book.setUpdateTime(new Date());

        this.updateById(book);
        book = this.getById(bookId);
        // 设置分类名称
        if (book.getCategoryId() != null) {
            book.setCategory(getCategoryName(book.getCategoryId()));
        }
        return book;
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
        // 处理分类：前端传的是 category（文字），需要转换为 categoryId
        if (book.getCategory() != null && !book.getCategory().trim().isEmpty()) {
            Long categoryId = queryCategoryId(book.getCategory());
            if (categoryId == null) {
                logger.warn("更新图书失败，分类不存在 name={}", book.getCategory());
                throw new RuntimeException("分类不存在");
            }
            book.setCategoryId(categoryId);
        } else if (book.getCategoryId() != null) {
            // 如果没传 category 但传了 categoryId，校验 categoryId 是否存在
            if (bookCategoryMapper.selectById(book.getCategoryId()) == null) {
                logger.warn("更新图书失败，分类不存在 id={}", book.getCategoryId());
                throw new RuntimeException("分类不存在");
            }
        }

        boolean updated = this.updateById(book);
        if (!updated) {
            logger.error("更新图书失败 id={}", book.getBookId());
            throw new RuntimeException("更新书籍失败");
        }
        
        // 重新查询完整数据并填充 category 字段
        BookInfo updatedBook = this.getById(book.getBookId());
        if (updatedBook != null && updatedBook.getCategoryId() != null) {
            updatedBook.setCategory(getCategoryName(updatedBook.getCategoryId()));
        }
        
        logger.info("更新图书成功 id={} name={}", book.getBookId(), book.getBookName());
        return updatedBook;
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

    /**
     * 根据用户角色自动限制借阅天数
     * 社会人员：15天，学生：30天，老师：60天
     */
    private Integer getMaxBorrowDaysForUser(Long userId, Integer requestBorrowDays) {
        try {
            SysUser user = sysUserMapper.selectById(userId);
            if (user == null || user.getRoleId() == null) {
                throw new RuntimeException("用户信息不完整");
            }

            SysRole role = sysRoleMapper.selectById(user.getRoleId());
            if (role == null || role.getMaxBorrowDays() == null) {
                throw new RuntimeException("用户角色信息不完整");
            }

            Integer maxDays = role.getMaxBorrowDays();
            if (requestBorrowDays == null || requestBorrowDays <= 0) {
                return maxDays;
            }
            // 取较小值：用户请求天数 vs 系统限制天数
            return Math.min(requestBorrowDays, maxDays);
        } catch (Exception e) {
            logger.error("获取借阅天数限制失败 userId={}", userId, e);
            throw new RuntimeException("获取用户借阅权限失败");
        }
    }
}