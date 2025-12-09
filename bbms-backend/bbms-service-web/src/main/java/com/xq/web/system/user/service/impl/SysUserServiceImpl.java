package com.xq.web.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.dto.PageDTO;
import com.xq.utils.PasswordUtils;
import com.xq.web.book.entity.BookInfo;
import com.xq.web.book.service.BookInfoService;
import com.xq.web.borrow.record.entity.BookBorrow;
import com.xq.web.borrow.record.entity.BookOperationLog;
import com.xq.web.borrow.record.service.BookBorrowService;
import com.xq.web.borrow.record.service.BookOperationLogService;
import com.xq.web.system.role.dto.RoleInfoDTO;
import com.xq.web.system.role.dto.SysRoleDetailDTO;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.role.mapper.SysRoleMapper;
import com.xq.web.system.role.service.SysRoleService;
import com.xq.web.system.user.dto.*;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.mapper.SysUserMapper;
import com.xq.web.system.user.service.SysUserRoleService;
import com.xq.web.system.user.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private BookBorrowService bookBorrowService;

    @Autowired
    private BookInfoService bookInfoService;

    @Autowired
    private BookOperationLogService bookOperationLogService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    // 最大登录错误次数
    private static final int MAX_LOGIN_ERROR_COUNT = 5;

    @Override
    public SysUser validateUser(String account, String password) {
        // 根据账号（用户名、邮箱、手机号）查询用户
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", account)
                .or().eq("account", account);
        SysUser user = this.getOne(queryWrapper);

        // 验证用户是否存在和密码是否正确
        if (user != null && PasswordUtils.matches(password, user.getPassword())) {
            // 查询角色信息并设置到用户对象中
            setRoleInfo(user);
            return user;
        }
        return null;
    }

    @Override
    public boolean validateRoleId(Long roleId) {
        if (roleId == null) {
            return false;
        }

        try {
            // 查询角色是否存在
            SysRole role = sysRoleService.getById(roleId);
            return role != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public SysUser login(String account, String password) {
        // 检查账号是否被锁定
        if (isAccountLocked(account)) {
            throw new RuntimeException("账号因登录错误次数过多已被锁定，请稍后重试或联系管理员");
        }

        // 验证用户
        SysUser user = validateUser(account, password);

        if (user != null) {
            // 登录成功，重置错误次数
            recordLoginSuccess(user.getUserId());
            return user;
        } else {
            // 登录失败，记录错误次数
            recordLoginFailure(account);
            return null;
        }
    }

    @Override
    @Transactional
    public void recordLoginSuccess(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLoginErrorCount(0); // 重置错误次数
        this.updateById(user);
    }

    @Override
    @Transactional
    public void recordLoginFailure(String account) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", account)
                .or().eq("account", account);
        SysUser user = this.getOne(queryWrapper);

        if (user != null) {
            // 更新错误次数
            Integer errorCount = user.getLoginErrorCount();
            if (errorCount == null) {
                errorCount = 0;
            }
            user.setLoginErrorCount(errorCount + 1);

            // 如果错误次数达到上限，锁定账号
            if (user.getLoginErrorCount() >= MAX_LOGIN_ERROR_COUNT) {
                user.setAccountStatus(0); // 锁定账号
            }

            this.updateById(user);
        }
    }

    @Override
    public boolean isAccountLocked(String account) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", account)
                .or().eq("account", account);
        SysUser user = this.getOne(queryWrapper);

        if (user != null) {
            // 检查错误次数是否达到上限
            if (user.getLoginErrorCount() != null && user.getLoginErrorCount() >= MAX_LOGIN_ERROR_COUNT) {
                return true;
            }
            // 检查账号状态
            return user.getAccountStatus() != null && user.getAccountStatus() == 0;
        }
        return false;
    }

    @Override
    @Transactional
    public void unlockAccount(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setAccountStatus(1); // 正常状态
        user.setLoginErrorCount(0); // 重置错误次数
        user.setFreezeTime(null);
        user.setUnfreezeTime(null);
        this.updateById(user);
    }

    @Override
    @Transactional
    public SysUser registerUser(RegisterRequestVO request) {
        // 检查账号是否已存在
        if (isAccountExists(request.getAccount())) {
            throw new RuntimeException("账号已存在");
        }

        // 检查用户名是否已存在
        if (isUsernameExists(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 获取默认角色（社会人员）
        Long roleId = request.getRoleId();
        SysRole role = sysRoleService.getById(roleId);
        if (role == null) {
            throw new RuntimeException("默认角色不存在，请检查数据库角色数据");
        }
        System.out.println("找到角色: " + role.getRoleName() + " (" + role.getRoleCode() + ")");

        // 创建用户实体
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);

        // 设置角色信息
        user.setRoleId(role.getRoleId());
        user.setRoleCode(role.getRoleCode());

        // 加密密码
        String encodedPassword = PasswordUtils.encode(request.getPassword());
        user.setPassword(encodedPassword);

        // 设置用户默认信息
        prepareUserForCreate(user);

        // 生成UID
        String uid = generateUid();
        user.setUid(uid);

        // 保存用户到数据库
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new RuntimeException("用户保存失败");
        }

        // 为用户分配角色（添加到sys_user_role表）
        System.out.println("开始为用户分配角色到sys_user_role表...");
        System.out.println("参数: userId=" + user.getUserId() + ", roleId=" + roleId);
        boolean roleAssigned = sysUserRoleService.assignRoleToUser(user.getUserId(), roleId);
        System.out.println("分配角色结果: " + roleAssigned);
        if (!roleAssigned) {
            throw new RuntimeException("用户角色分配失败");
        }

        // 验证关联是否真的存在
        System.out.println("验证用户角色关联是否创建成功...");
        boolean hasRole = sysUserRoleService.hasRole(user.getUserId(), roleId);
        System.out.println("验证结果: " + hasRole);

        if (!hasRole) {
            throw new RuntimeException("用户角色关联验证失败");
        }

        // 重新查询用户以获取完整信息（包括数据库生成的ID等）
        SysUser savedUser = this.getById(user.getUserId());
        setRoleInfo(savedUser);

        return savedUser;
    }

    /**
     * 注册用户（简化版）
     */
    @Override
    @Transactional
    public SysUser registerUser(String account, String password, String username) {
        RegisterRequestVO request = new RegisterRequestVO();
        request.setAccount(account);
        request.setPassword(password);
        request.setUsername(username);
        request.setConfirmPassword(password); // 简化版确认密码与密码相同

        return registerUser(request);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);
    }

    /**
     * 检查账号是否已存在
     */
    private boolean isAccountExists(String account) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 检查用户名是否已存在
     */
    private boolean isUsernameExists(String username) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 获取默认角色（社会人员）
     */
    private SysRoleDetailDTO getDefaultRole() {
        SysRoleDetailDTO role = sysRoleMapper.selectByRoleCode("READER_SOCIAL");
        if (role == null) {
            throw new RuntimeException("默认角色不存在，请检查数据库角色数据");
        }
        return role;
    }

    /**
     * 设置用户创建前的默认信息
     */
    private void prepareUserForCreate(SysUser user) {
        LocalDateTime now = LocalDateTime.now();

        if (user.getRegisterTime() == null) {
            user.setRegisterTime(now);
        }
        if (user.getCreditScore() == null) {
            user.setCreditScore(100);
        }
        if (user.getAccountStatus() == null) {
            user.setAccountStatus(1); // 正常状态
        }
        if (user.getLoginErrorCount() == null) {
            user.setLoginErrorCount(0);
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(now);
        }
        if (user.getUpdateTime() == null) {
            user.setUpdateTime(now);
        }
    }

    /**
     * 生成用户UID（不依赖角色身份，支持身份变更）
     */
    private String generateUid() {
        long timestamp = System.currentTimeMillis();
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "U" + timestamp + randomNum;
    }

    /**
     * 设置角色信息到用户对象
     */
    private void setRoleInfo(SysUser user) {
        if (user.getRoleId() != null) {
            SysRoleDetailDTO role = sysRoleMapper.selectByRoleId(user.getRoleId());
            if (role != null) {
                user.setRoleCode(role.getRoleCode());
                user.setRoleName(role.getRoleName());
            }
        }
    }

    /**
     * 根据用户ID获取完整用户信息（包含角色信息）
     */
    public SysUser getUserWithRoleInfo(Long userId) {
        SysUser user = this.getById(userId);
        if (user != null) {
            setRoleInfo(user);
        }
        return user;
    }

    /**
     * 修改密码
     */
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtils.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        String encodedNewPassword = PasswordUtils.encode(newPassword);
        user.setPassword(encodedNewPassword);
        return this.updateById(user);
    }

    /**
     * 重置密码（管理员操作）
     */
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String encodedPassword = PasswordUtils.encode(newPassword);
        user.setPassword(encodedPassword);
        return this.updateById(user);
    }

    /**
     * 更新用户角色（支持身份变更）
     */
    @Transactional
    public boolean updateUserRole(Long userId, Long newRoleId) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 查询新角色信息
        SysRoleDetailDTO newRole = sysRoleMapper.selectByRoleId(newRoleId);
        if (newRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 更新用户角色
        user.setRoleId(newRoleId);
        user.setRoleCode(newRole.getRoleCode());

        return this.updateById(user);
    }

    @Override
    public boolean updateUserRole(Long targetUserId, Long newRoleId, Long operatorId, String remark) {
        // 检查目标用户是否存在
        SysUser targetUser = this.getById(targetUserId);
        if (targetUser == null) {
            throw new RuntimeException("目标用户不存在");
        }

        // 检查新角色是否存在
        SysRole newRole = sysRoleService.getById(newRoleId);
        if (newRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 检查是否是系统管理员操作（只有系统管理员可以修改用户身份）
        SysUser operator = this.getById(operatorId);
        if (operator == null || !"SYS_ADMIN".equals(operator.getRoleCode())) {
            throw new RuntimeException("只有系统管理员可以修改用户身份");
        }

        // 检查目标用户是否是自己（不能修改自己的身份）
        if (targetUserId.equals(operatorId)) {
            throw new RuntimeException("不能修改自己的用户身份");
        }

        // 如果目标用户是读者角色且升级为管理员，检查是否有未归还的书籍
        if (isReaderRole(targetUser.getRoleCode()) && isAdminRole(newRole.getRoleCode())) {
            if (hasBorrowingBooks(targetUserId)) {
                // 自动归还所有借阅的书籍
                returnBorrowingBooks(targetUserId);
            }
        }

        // 记录原角色信息
        Long oldRoleId = targetUser.getRoleId();
        String oldRoleCode = targetUser.getRoleCode();
        String oldRoleName = targetUser.getRoleName();

        // 更新用户角色
        targetUser.setRoleId(newRoleId);
        targetUser.setRoleCode(newRole.getRoleCode());
        targetUser.setRoleName(newRole.getRoleName());

        // 同步更新用户角色关联表
        boolean roleUpdated = sysUserRoleService.updateUserRole(targetUserId, newRoleId);
        if (!roleUpdated) {
            throw new RuntimeException("更新用户角色关联失败");
        }

        boolean success = this.updateById(targetUser);

        if (success) {
            // 记录操作日志
            logUserRoleChange(targetUserId, oldRoleId, oldRoleCode, oldRoleName,
                    newRoleId, newRole.getRoleCode(), newRole.getRoleName(),
                    operatorId, remark);
        }

        return success;
    }

    @Override
    public boolean hasBorrowingBooks(Long userId) {
        // 查询用户当前借阅中的书籍数量
        LambdaQueryWrapper<BookBorrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookBorrow::getUserId, userId)
                .eq(BookBorrow::getBorrowStatus, 0); // 0-借阅中
        return bookBorrowService.count(queryWrapper) > 0;
    }

    private void returnBorrowingBooks(Long userId) {
        // 查找用户所有借阅中的书籍
        LambdaQueryWrapper<BookBorrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookBorrow::getUserId, userId)
                .eq(BookBorrow::getBorrowStatus, 0); // 0-借阅中

        List<BookBorrow> borrowingBooks = bookBorrowService.list(queryWrapper);

        for (BookBorrow borrow : borrowingBooks) {
            // 更新借阅状态为已归还
            borrow.setBorrowStatus(1); // 1-已归还
            borrow.setActualReturnTime(LocalDateTime.now());
            borrow.setReturnApplyTime(LocalDateTime.now());
            bookBorrowService.updateById(borrow);

            // 更新书籍可借数量
            BookInfo book = bookInfoService.getById(borrow.getBookId());
            if (book != null) {
                book.setAvailableCount(book.getAvailableCount() + 1);
                bookInfoService.updateById(book);
            }

            // 记录操作日志
            logBookOperation(userId, borrow.getBookId(), 5, "用户角色升级自动归还");
        }
    }

    private boolean isReaderRole(String roleCode) {
        return "READER_SOCIAL".equals(roleCode) ||
                "READER_STUDENT".equals(roleCode) ||
                "READER_TEACHER".equals(roleCode);
    }

    private boolean isAdminRole(String roleCode) {
        return "ADMIN".equals(roleCode) || "SYS_ADMIN".equals(roleCode);
    }

    private void logUserRoleChange(Long targetUserId, Long oldRoleId, String oldRoleCode, String oldRoleName,
                                   Long newRoleId, String newRoleCode, String newRoleName,
                                   Long operatorId, String remark) {
        // 实现用户角色变更日志记录
        // 可以记录到专门的日志表或操作日志表
    }

    private void logBookOperation(Long userId, Long bookId, int operationType, String desc) {
        // 记录书籍操作日志
        BookOperationLog operationLog = new BookOperationLog();
        operationLog.setUserId(userId);
        operationLog.setBookId(bookId);
        operationLog.setOperationType(operationType);
        operationLog.setOperationDesc(desc);
        bookOperationLogService.save(operationLog);
    }

    @Override
    public SysUser getUserDetail(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        try {
            // 1. 获取用户基本信息
            SysUser user = this.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            // 2. 获取角色信息并设置到用户对象中
            setRoleInfoToUser(user);

            // 3. 清除敏感信息
            user.clearSensitiveInfo();

            return user;

        } catch (Exception e) {
            throw new RuntimeException("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 设置角色信息到用户对象
     */
    private void setRoleInfoToUser(SysUser user) {
        if (user == null || user.getRoleId() == null) {
            return;
        }

        try {
            // 根据角色ID获取角色信息
            SysRole role = sysRoleService.getById(user.getRoleId());
            if (role != null) {
                // 设置角色编码和名称
                user.setRoleCode(role.getRoleCode());
                user.setRoleName(role.getRoleName());

                // 设置角色枚举（会自动设置角色名称）
                user.setRoleCode(role.getRoleCode()); // 这会触发RoleEnum的自动设置
            }
        } catch (Exception e) {
            throw new RuntimeException("设置用户角色信息失败: " + e.getMessage());
        }
    }

    @Override
    public PageDTO<UserListResponseDTO> getUserList(UserListRequestVO request) {
        System.out.println("开始查询用户列表，请求参数: {}" + request);

        // 1. 创建分页对象
        Page<SysUser> page = new Page<>(
                request.getPageNum() != null ? request.getPageNum() : 1,
                request.getPageSize() != null ? request.getPageSize() : 10
        );

        // 2. 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（用户名称/UID）
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = request.getKeyword().trim();
            queryWrapper.and(wrapper -> wrapper
                    .like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getUid, keyword)
            );
        }

        // 角色过滤
        if (StringUtils.hasText(request.getRoleFilter()) && !"ALL".equalsIgnoreCase(request.getRoleFilter())) {
            handleRoleFilter(queryWrapper, request.getRoleFilter());
        }

        // 按注册时间倒序排序
        queryWrapper.orderByDesc(SysUser::getRegisterTime);

        // 3. 执行查询
        System.out.println("执行用户列表查询，条件: {}" + queryWrapper.getCustomSqlSegment());
        Page<SysUser> resultPage = this.page(page, queryWrapper);

        if (resultPage.getRecords() == null || resultPage.getRecords().isEmpty()) {
            System.out.println("未查询到用户数据");
            return PageDTO.of(
                    resultPage.getCurrent(),
                    resultPage.getSize(),
                    resultPage.getTotal(),
                    List.of()
            );
        }

        System.out.println("查询到 {} 条用户记录" + resultPage.getRecords().size());

        // 4. 批量获取角色信息，减少数据库查询
        Map<Long, SysRole> roleMap = getRoleMap(resultPage.getRecords());

        // 5. 批量查询用户的借阅状态
        List<Long> userIds = resultPage.getRecords().stream()
                .map(SysUser::getUserId)
                .collect(Collectors.toList());
        Map<Long, Boolean> hasBorrowingBooksMap = checkHasBorrowingBooks(userIds);

        // 6. 转换为响应DTO
        List<UserListResponseDTO> userList = resultPage.getRecords().stream()
                .map(user -> {
                    UserListResponseDTO dto = convertToUserListResponse(user, roleMap.get(user.getRoleId()));
                    // 设置是否可以升级权限（读者可升级为管理员）
                    setCanUpgradeRole(dto, user);
                    // 设置是否有未归还书籍
                    dto.setHasBorrowingBooks(hasBorrowingBooksMap.getOrDefault(user.getUserId(), false));
                    return dto;
                })
                .collect(Collectors.toList());

        // 7. 构建分页响应
        PageDTO<UserListResponseDTO> pageDTO = PageDTO.of(
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getTotal(),
                userList
        );

        System.out.println("用户列表查询完成，当前页={}, 每页={}, 总数={}" +
                pageDTO.getPageInfo().getCurrentPage() +
                pageDTO.getPageInfo().getPageSize() +
                pageDTO.getPageInfo().getTotal());

        return pageDTO;
    }

    /**
     * 处理角色筛选条件
     */
    private void handleRoleFilter(LambdaQueryWrapper<SysUser> queryWrapper, String roleFilter) {
        // 根据角色代码查询对应的角色ID
        LambdaQueryWrapper<SysRole> roleQuery = new LambdaQueryWrapper<>();
        roleQuery.eq(SysRole::getRoleCode, roleFilter);
        SysRole role = sysRoleService.getOne(roleQuery, false);

        if (role != null) {
            // 根据角色ID筛选用户
            queryWrapper.eq(SysUser::getRoleId, role.getRoleId());
        } else {
            // 如果没有找到对应角色，使用一个不会返回结果的查询
            queryWrapper.eq(SysUser::getRoleId, -1L);
        }
    }

    /**
     * 批量获取角色信息映射
     */
    private Map<Long, SysRole> getRoleMap(List<SysUser> users) {
        // 提取所有角色ID
        Set<Long> roleIds = users.stream()
                .map(SysUser::getRoleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (roleIds.isEmpty()) {
            return new HashMap<>();
        }

        // 批量查询角色信息
        List<SysRole> roles = sysRoleService.listByIds(roleIds);
        return roles.stream()
                .collect(Collectors.toMap(SysRole::getRoleId, role -> role));
    }

    /**
     * 批量检查用户是否有未归还的书籍
     */
    private Map<Long, Boolean> checkHasBorrowingBooks(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        // 查询所有借阅中的书籍
        LambdaQueryWrapper<BookBorrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BookBorrow::getUserId, userIds)
                .eq(BookBorrow::getBorrowStatus, 0); // 0-借阅中

        List<BookBorrow> borrowingRecords = bookBorrowService.list(queryWrapper);

        // 构建用户ID到是否有借阅记录的映射
        Map<Long, Boolean> result = new HashMap<>();

        // 初始化所有用户为false
        userIds.forEach(userId -> result.put(userId, false));

        // 设置有借阅记录的用户为true
        borrowingRecords.forEach(record ->
                result.put(record.getUserId(), true)
        );

        return result;
    }

    /**
     * 转换用户实体为响应DTO
     */
    private UserListResponseDTO convertToUserListResponse(SysUser user, SysRole role) {
        UserListResponseDTO dto = new UserListResponseDTO();

        // 设置基本用户信息
        dto.setUserId(user.getUserId());
        dto.setAvatar(user.getAvatar());
        dto.setUsername(user.getUsername());
        dto.setUid(user.getUid());
        dto.setRegisterTime(formatRegisterTime(user.getRegisterTime()));

        // 设置角色信息（优先使用传入的role，如果没有则从user对象获取）
        if (role == null && user.getRole() != null) {
            role = user.getRole();
        }

        if (role != null) {
            RoleInfoDTO roleInfoDTO = new RoleInfoDTO();
            roleInfoDTO.setRoleId(role.getRoleId());
            roleInfoDTO.setRoleName(role.getRoleName());
            roleInfoDTO.setRoleCode(role.getRoleCode());

            // 如果角色有借阅相关限制信息，可以一并设置
            roleInfoDTO.setMaxBorrowNum(role.getMaxBorrowNum());
            roleInfoDTO.setMaxBorrowDays(role.getMaxBorrowDays());
            roleInfoDTO.setMaxRenewDays(role.getMaxRenewDays());

            dto.setRoleInfo(roleInfoDTO);
        } else if (user.getRoleId() != null) {
            // 如果role为空但roleId不为空，创建基础角色信息
            com.xq.web.system.role.dto.RoleInfoDTO roleInfoDTO = new com.xq.web.system.role.dto.RoleInfoDTO();
            roleInfoDTO.setRoleId(user.getRoleId());
            roleInfoDTO.setRoleName(user.getRoleName()); // 从user对象获取
            roleInfoDTO.setRoleCode(user.getRoleCode()); // 从user对象获取
            dto.setRoleInfo(roleInfoDTO);
        }

        // 获取角色编码（用于判断是否是读者）
        String roleCode = getRoleCodeForUser(user, role);

        // 设置借阅信用信息（仅读者显示）
        if (isReaderRole(roleCode)) {
            CreditInfoDTO creditInfoDTO = new CreditInfoDTO();
            creditInfoDTO.setCreditScore(user.getCreditScore() != null ? user.getCreditScore() : 100);
            creditInfoDTO.setCreditLevel(user.getCreditLevel()); // 使用SysUser的getCreditLevel方法
            creditInfoDTO.setCurrentBorrowCount(user.getCurrentBorrowCount() != null ? user.getCurrentBorrowCount() : 0);
            creditInfoDTO.setCurrentReserveCount(user.getCurrentReserveCount() != null ? user.getCurrentReserveCount() : 0);
            creditInfoDTO.setCanBorrowMore(user.canBorrowMore());
            creditInfoDTO.setCanReserveMore(user.canReserveMore());

            // 计算最大可借阅本数
            if (role != null && role.getMaxBorrowNum() != null) {
                creditInfoDTO.setMaxBorrowNum(role.getMaxBorrowNum());
                creditInfoDTO.setRemainingBorrowNum(Math.max(0,
                        role.getMaxBorrowNum() - (user.getCurrentBorrowCount() != null ? user.getCurrentBorrowCount() : 0)));
            }

            dto.setCreditInfo(creditInfoDTO);
        }

        // 设置账号状态
        AccountStatusDTO accountStatusDTO = new AccountStatusDTO();
        Integer accountStatus = user.getAccountStatus();
        accountStatusDTO.setStatus(accountStatus);

        // 根据用户状态判断是否在冻结期
        boolean inFreezePeriod = false;
        if (accountStatus != null && accountStatus == 0) {
            inFreezePeriod = user.isInFreezePeriod();
        }

        // 设置状态名称（使用SysUser的getStatusText方法）
        accountStatusDTO.setStatusName(user.getStatusText());
        accountStatusDTO.setInFreezePeriod(inFreezePeriod);

        // 设置冻结相关信息
        if (inFreezePeriod) {
            accountStatusDTO.setFreezeTime(user.getFreezeTime());
            accountStatusDTO.setUnfreezeTime(user.getUnfreezeTime());
        }

        // 设置登录错误次数相关信息
        accountStatusDTO.setLoginErrorCount(user.getLoginErrorCount() != null ? user.getLoginErrorCount() : 0);
        accountStatusDTO.setNeedUnlock(user.needUnlock());
        accountStatusDTO.setAccountAvailable(user.isAvailable());

        dto.setAccountStatus(accountStatusDTO);

        return dto;
    }

    /**
     * 获取用户的角色编码
     */
    private String getRoleCodeForUser(SysUser user, SysRole role) {
        if (role != null) {
            return role.getRoleCode();
        } else if (user.getRoleCode() != null) {
            return user.getRoleCode();
        } else if (user.getRole() != null) {
            return user.getRole().getRoleCode();
        }
        return null;
    }

    /**
     * 设置是否可以升级权限
     * 读者角色可以升级为管理员，但已有管理员权限的用户不能升级
     */
    private void setCanUpgradeRole(UserListResponseDTO dto, SysUser user) {
        if (dto.getRoleInfo() == null) {
            dto.setCanUpgradeRole(false);
            return;
        }

        String roleCode = dto.getRoleInfo().getRoleCode();

        // 只有读者角色可以升级为管理员
        if (isReaderRole(roleCode)) {
            dto.setCanUpgradeRole(true);
        } else {
            // 管理员角色不能升级
            dto.setCanUpgradeRole(false);
        }
    }

    /**
     * 格式化注册时间
     */
    private String formatRegisterTime(LocalDateTime registerTime) {
        if (registerTime == null) {
            return null;
        }

        // 根据需求自定义格式化
        // 示例：yyyy-MM-dd HH:mm:ss
        return registerTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取账号状态名称
     */
    private String getAccountStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }

        switch (status) {
            case 0:
                return "锁定";
            case 1:
                return "正常";
            case 2:
                return "冻结";
            default:
                return "未知";
        }
    }
}
