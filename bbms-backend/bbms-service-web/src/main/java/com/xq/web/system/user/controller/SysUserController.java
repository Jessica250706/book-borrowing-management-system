package com.xq.web.system.user.controller;

import com.xq.utils.JwtUtils;
import com.xq.utils.TokenExtractUtils;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.system.user.dto.RegisterRequestDTO;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    private JwtUtils jwtUtils;

    @Autowired
    private TokenExtractUtils tokenExtractUtils;

    /**
     * 用户注册
     * 新用户注册接口
     */
    @PostMapping("/register")
    public ResultVo<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        // 验证密码确认
        if (!request.isPasswordConfirmed()) {
            return ResultUtils.errorMsg("密码和确认密码不一致");
        }

        // 执行注册
        boolean success = sysUserService.registerUser(request.getAccount(), request.getPassword(), request.getUsername());

        if (success) {
            return ResultUtils.successMsg("注册成功");
        } else {
            return ResultUtils.errorMsg("注册失败");
        }
    }

    /**
     * 用户登录
     * 用户登录接口
     */
    @PostMapping("/login")
    public ResultVo<?> login(@RequestParam String account,
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

        // 清除敏感信息
        user.clearSensitiveInfo();
        user.setToken(token);

        return ResultUtils.success("登录成功", user);
    }

    /**
     * 获取当前用户信息
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public ResultVo<SysUser> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 从token中获取用户信息
        Long userId = jwtUtils.getUserId(token);
        SysUser user = sysUserService.getUserWithRoleInfo(userId);

        if (user != null) {
            user.clearSensitiveInfo();
            return ResultUtils.success("获取成功", user);
        } else {
            return ResultUtils.errorMsg("用户不存在");
        }
    }

    /**
     * 修改密码
     * 用户修改密码
     */
    @PutMapping("/password")
    public ResultVo<?> changePassword(@RequestParam String oldPassword,
                                      @RequestParam String newPassword,
                                      HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtils.getUserId(token);
        boolean success = sysUserService.changePassword(userId, oldPassword, newPassword);

        return success ? ResultUtils.successMsg("密码修改成功") : ResultUtils.errorMsg("密码修改失败");
    }

    /**
     * 刷新token
     * 刷新用户token，延长登录有效期
     */
    @PostMapping("/refresh-token")
    public ResultVo<?> refreshToken(HttpServletRequest request) {
        String token = tokenExtractUtils.extractTokenFromRequest(request);
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
     */
    @GetMapping("/token-status")
    public ResultVo<?> checkTokenStatus(HttpServletRequest request) {
        String token = tokenExtractUtils.extractTokenFromRequest(request);
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
}
