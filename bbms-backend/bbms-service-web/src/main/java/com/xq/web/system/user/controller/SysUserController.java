package com.xq.web.system.user.controller;

import com.xq.utils.*;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.role.service.SysRoleService;
import com.xq.web.system.user.dto.RegisterRequestVO;
import com.xq.web.system.user.dto.RegisterResponseDTO;
import com.xq.web.system.user.dto.UserRoleUpdateRequestVO;
import com.xq.web.system.user.dto.UserRoleUpdateResponseDTO;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * @module 用户管理
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户注册、登录、信息管理")
@Validated
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TokenExtractUtils tokenExtractUtils;

    /**
     * 构建注册响应
     */
    private RegisterResponseDTO buildRegisterResponse(SysUser user, String token) {
        RegisterResponseDTO response = new RegisterResponseDTO();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setAccount(user.getAccount());
        response.setUid(user.getUid());
        response.setRoleCode(user.getRoleCode());
        response.setRoleName(user.getRoleName());
        response.setCreditScore(user.getCreditScore());
        response.setToken(token);
        response.setAvatar(user.getAvatar());
        return response;
    }

    /**
     * 用户注册
     * 新用户注册接口
     *
     * @param request 注册请求参数
     * @return 注册响应结果
     */
    @PostMapping("/register")
    public ResultVo<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestVO request) {
        // 验证密码确认
        if (!request.isPasswordConfirmed()) {
            return ResultUtils.errorMsg("密码和确认密码不一致");
        }

        try {
            // 执行注册，返回用户信息
            SysUser user = sysUserService.registerUser(request);

            // 生成登录token
            String token = jwtUtils.generateUserToken(
                    user.getUserId(),
                    user.getUsername(),
                    user.getRoleId(),
                    user.getRoleCode()
            );

            // 构建响应数据
            RegisterResponseDTO response = buildRegisterResponse(user, token);

            return ResultUtils.success("注册成功", response);

        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        } catch (Exception e) {
            return ResultUtils.errorMsg("注册失败，请稍后重试");
        }
    }

    /**
     * 用户登录
     * 用户登录接口
     *
     * @param account 登录账号
     * @param password 登录密码
     * @return 登录响应结果
     */
    @PostMapping("/login")
    public ResultVo<RegisterResponseDTO> login(@RequestParam String account,
                                               @RequestParam String password) {
        // 验证用户
        SysUser user = sysUserService.validateUser(account, password);
        if (user == null) {
            return ResultUtils.errorMsg("账号或密码错误");
        }

        // 检查账号状态
        if (!user.isAvailable()) {
            return ResultUtils.errorMsg("账号已被冻结，请联系管理员");
        }

        // 更新最后登录时间
        sysUserService.updateLastLoginTime(user.getUserId());

        // 生成token
        String token = jwtUtils.generateUserToken(
                user.getUserId(),
                user.getUsername(),
                user.getRoleId(),
                user.getRoleCode()
        );

        // 构建响应数据
        RegisterResponseDTO response = buildRegisterResponse(user, token);

        return ResultUtils.success("登录成功", response);
    }

    /**
     * 获取当前用户信息
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public ResultVo<RegisterResponseDTO> getCurrentUser() {
        // 直接从 SecurityContext 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResultUtils.errorMsg("用户未登录");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof SysUser) {
            SysUser user = (SysUser) principal;
            user.clearSensitiveInfo();

            // 构建 RegisterResponseDTO 响应
            RegisterResponseDTO response = buildRegisterResponse(user, null); // token 为 null，因为当前接口不返回新 token
            return ResultUtils.success("获取成功", response);
        } else {
            return ResultUtils.errorMsg("用户信息异常");
        }
    }

    /**
     * 修改密码
     * 用户修改密码接口
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 密码修改结果
     */
    @PutMapping("/password")
    public ResultVo<?> changePassword(@RequestParam String oldPassword,
                                      @RequestParam String newPassword) {
        Long userId = RequestUtils.getCurrentUserId(jwtUtils);
        boolean success = sysUserService.changePassword(userId, oldPassword, newPassword);

        return success ? ResultUtils.successMsg("密码修改成功") : ResultUtils.errorMsg("密码修改失败");
    }

    /**
     * 刷新token
     * 刷新用户token，延长登录有效期
     *
     * @param request HTTP请求对象
     * @return token刷新结果
     */
    @PostMapping("/refresh-token")
    public ResultVo<?> refreshToken(HttpServletRequest request) {
        String token = RequestUtils.extractTokenFromRequest();
        if (token == null) {
            return ResultUtils.errorMsg("Token不能为空");
        }

        try {
            // 使用安全方式获取用户信息（即使token过期也能获取）
            Long userId = jwtUtils.safeGetUserId(token);
            String username = jwtUtils.safeGetUsername(token);
            Long roleId = jwtUtils.safeGetRoleId(token);
            String roleCode = jwtUtils.safeGetRoleCode(token);

            // 检查必要信息是否完整
            if (userId == null || username == null || roleId == null || roleCode == null) {
                return ResultUtils.errorMsg("Token无效或已损坏");
            }

            // 查询用户最新信息（确保用户状态正常）
            SysUser user = sysUserService.getUserWithRoleInfo(userId);
            if (user == null) {
                return ResultUtils.errorMsg("用户不存在");
            }

            // 检查账号状态
            if (!user.isAvailable()) {
                return ResultUtils.errorMsg("账号已被冻结，请联系管理员");
            }

            // 检查token是否已过期（允许过期的token在一定时间内刷新）
            boolean isTokenExpired = jwtUtils.safeIsTokenExpired(token);
            if (isTokenExpired) {
                // 可以在这里添加逻辑：允许过期时间在一定范围内的token刷新
                // 比如允许过期30分钟内的token刷新
                long expiredMinutes = Math.abs(jwtUtils.getTokenRemainingMinutes(token));
                if (expiredMinutes > 30) { // 如果过期超过30分钟，不允许刷新
                    return ResultUtils.errorMsg("Token已过期太久，请重新登录");
                }
                // 过期30分钟内允许刷新
            }

            // 生成新的token
            String newToken = jwtUtils.generateUserToken(userId, username, roleId, roleCode);

            // 返回新的token
            user.clearSensitiveInfo();
            user.setToken(newToken);

            return ResultUtils.success("Token刷新成功", user);

        } catch (Exception e) {
            // 记录日志
            e.printStackTrace();
            return ResultUtils.errorMsg("Token刷新失败: " + e.getMessage());
        }
    }

    /**
     * 检查token状态
     * 检查token是否有效及剩余时间
     *
     * @return token状态信息
     */
    @GetMapping("/token-status")
    public ResultVo<?> checkTokenStatus() {
        String token = RequestUtils.extractTokenFromRequest();
        if (token == null) {
            return ResultUtils.errorMsg("Token不能为空");
        }

        try {
            Map<String, Object> status = new HashMap<>();

            // 检查token是否有效
            boolean isValid = jwtUtils.verify(token);
            status.put("valid", isValid);

            // 获取剩余时间
            long remainingMinutes = jwtUtils.getTokenRemainingMinutes(token);
            status.put("remainingMinutes", remainingMinutes);

            // 是否即将过期（30分钟内）
            boolean isExpiringSoon = jwtUtils.isTokenExpiringSoon(token, 30);
            status.put("expiringSoon", isExpiringSoon);

            // 是否已过期
            boolean isExpired = jwtUtils.safeIsTokenExpired(token);
            status.put("expired", isExpired);

            if (isValid && !isExpired) {
                // 获取用户信息
                Long userId = jwtUtils.safeGetUserId(token);
                SysUser user = sysUserService.getUserWithRoleInfo(userId);
                if (user != null) {
                    user.clearSensitiveInfo();
                    status.put("user", user);
                }
            }

            return ResultUtils.success("获取token状态成功", status);

        } catch (Exception e) {
            return ResultUtils.errorMsg("检查token状态失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户角色
     * 系统管理员修改用户身份接口
     *
     * @param request 角色更新请求
     * @return 更新结果
     */
    @PutMapping("/role")
    public ResultVo<UserRoleUpdateResponseDTO> updateUserRole(@Valid @RequestBody UserRoleUpdateRequestVO request) {
        try {
            Long operatorId = RequestUtils.getCurrentUserId(jwtUtils);

            // 执行角色更新
            boolean success = sysUserService.updateUserRole(
                    request.getUserId(),
                    request.getRoleId(),
                    operatorId,
                    request.getRemark()
            );

            if (success) {
                // 构建响应数据
                UserRoleUpdateResponseDTO response = buildUserRoleUpdateResponse(
                        request.getUserId(),
                        request.getRoleId(),
                        operatorId,
                        request.getRemark()
                );
                return ResultUtils.success("用户角色更新成功", response);
            } else {
                return ResultUtils.errorMsg("用户角色更新失败");
            }

        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        } catch (Exception e) {
            return ResultUtils.errorMsg("用户角色更新失败，请稍后重试");
        }
    }

    /**
     * 检查用户是否可以修改角色
     * 检查用户是否有未归还书籍等限制条件
     *
     * @param userId 用户ID
     * @param newRoleId 新角色ID
     * @return 检查结果
     */
    @GetMapping("/role/check")
    public ResultVo<Map<String, Object>> checkUserRoleUpdate(@RequestParam Long userId,
                                                             @RequestParam Long newRoleId) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 获取用户信息
            SysUser user = sysUserService.getUserDetail(userId);
            if (user == null) {
                return ResultUtils.errorMsg("用户不存在");
            }

            // 获取新角色信息
            SysRole newRole = sysRoleService.getById(newRoleId);
            if (newRole == null) {
                return ResultUtils.errorMsg("角色不存在");
            }

            result.put("user", user);
            result.put("newRole", newRole);

            // 检查是否有未归还书籍
            boolean hasBorrowingBooks = sysUserService.hasBorrowingBooks(userId);
            result.put("hasBorrowingBooks", hasBorrowingBooks);

            // 检查是否从读者升级到管理员
            boolean isReaderToAdmin = isReaderRole(user.getRoleCode()) && isAdminRole(newRole.getRoleCode());
            result.put("isReaderToAdmin", isReaderToAdmin);

            // 是否可以升级
            boolean canUpgrade = !hasBorrowingBooks || !isReaderToAdmin;
            result.put("canUpgrade", canUpgrade);

            if (isReaderToAdmin && hasBorrowingBooks) {
                result.put("warning", "当前用户有未归还书籍，升级为管理员将自动归还所有书籍");
            }

            return ResultUtils.success("检查完成", result);

        } catch (Exception e) {
            return ResultUtils.errorMsg("检查失败: " + e.getMessage());
        }
    }

    /**
     * 构建用户角色更新响应
     */
    private UserRoleUpdateResponseDTO buildUserRoleUpdateResponse(Long userId, Long newRoleId,
                                                                  Long operatorId, String remark) {
        UserRoleUpdateResponseDTO response = new UserRoleUpdateResponseDTO();

        // 获取用户信息
        SysUser user = sysUserService.getUserDetail(userId);
        SysRole newRole = sysRoleService.getById(newRoleId);
        SysUser operator = sysUserService.getUserDetail(operatorId);

        if (user != null) {
            response.setUserId(user.getUserId());
            response.setUsername(user.getUsername());
            response.setUid(user.getUid());
        }

        if (newRole != null) {
            response.setNewRoleId(newRole.getRoleId());
            response.setNewRoleCode(newRole.getRoleCode());
            response.setNewRoleName(newRole.getRoleName());
        }

        if (operator != null) {
            response.setOperatorId(operator.getUserId());
            response.setOperatorName(operator.getUsername());
        }

        response.setOperateTime(LocalDateTime.now());
        response.setRemark(remark);

        return response;
    }

    /**
     * 判断是否是读者角色
     */
    private boolean isReaderRole(String roleCode) {
        return "READER_SOCIAL".equals(roleCode) ||
                "READER_STUDENT".equals(roleCode) ||
                "READER_TEACHER".equals(roleCode);
    }

    /**
     * 判断是否是管理员角色
     */
    private boolean isAdminRole(String roleCode) {
        return "ADMIN".equals(roleCode) || "SYS_ADMIN".equals(roleCode);
    }
}
