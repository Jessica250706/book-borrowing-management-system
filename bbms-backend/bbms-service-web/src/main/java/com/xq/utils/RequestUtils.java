package com.xq.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class RequestUtils {

    private static JwtUtils jwtUtils;

    /**
     * 注入JwtUtils（静态工具类的注入方式）
     */
    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        RequestUtils.jwtUtils = jwtUtils;
    }

    /**
     * 获取当前请求的 HttpServletRequest
     */
    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    /**
     * 从请求中获取 token
     */
    public static String getTokenFromRequest() {
        HttpServletRequest request = getCurrentRequest();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }

    /**
     * 从请求中提取token（不严格）
     */
    public static String extractTokenFromRequest() {
        HttpServletRequest request = getCurrentRequest();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        // 也可以尝试从参数中获取（兼容性）
        String paramToken = request.getParameter("token");
        if (paramToken != null && !paramToken.trim().isEmpty()) {
            return paramToken;
        }

        return null;
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        if (jwtUtils == null) {
            log.error("JwtUtils未注入，请确保RequestUtils已被正确初始化");
            return null;
        }

        String token = getTokenFromRequest();
        if (token == null) {
            return null;
        }

        try {
            // 先验证token是否有效
            if (!jwtUtils.verify(token)) {
                return null;
            }
            return jwtUtils.getUserId(token);
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
            return null;
        }
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isCurrentUserAdmin() {
        if (jwtUtils == null) {
            log.error("JwtUtils未注入");
            return false;
        }

        String token = getTokenFromRequest();
        if (token == null) {
            return false;
        }

        try {
            // 验证token有效性
            if (!jwtUtils.verify(token)) {
                return false;
            }

            // 判断是否为管理员
            Boolean isAdmin = jwtUtils.isAdmin(token);
            return isAdmin != null && isAdmin;
        } catch (Exception e) {
            log.error("验证管理员权限失败", e);
            return false;
        }
    }

    /**
     * 判断当前用户是否为系统管理员
     */
    public static boolean isCurrentUserSysAdmin() {
        if (jwtUtils == null) {
            log.error("JwtUtils未注入");
            return false;
        }

        String token = getTokenFromRequest();
        if (token == null) {
            return false;
        }

        try {
            // 验证token有效性
            if (!jwtUtils.verify(token)) {
                return false;
            }

            String roleCode = jwtUtils.getRoleCode(token);
            return "SYS_ADMIN".equals(roleCode);
        } catch (Exception e) {
            log.error("验证系统管理员权限失败", e);
            return false;
        }
    }

    /**
     * 获取当前用户信息（从token中）
     */
    public static TokenUserInfo getCurrentUserInfo() {
        if (jwtUtils == null) {
            return null;
        }

        String token = getTokenFromRequest();
        if (token == null) {
            return null;
        }

        try {
            if (!jwtUtils.verify(token)) {
                return null;
            }

            TokenUserInfo userInfo = new TokenUserInfo();
            userInfo.setUserId(jwtUtils.getUserId(token));
            userInfo.setUsername(jwtUtils.getUsername(token));
            userInfo.setRoleId(jwtUtils.getRoleId(token));
            userInfo.setRoleCode(jwtUtils.getRoleCode(token));
            userInfo.setAdmin(jwtUtils.isAdmin(token));

            return userInfo;
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return null;
        }
    }

    /**
     * 权限验证辅助方法
     */
    public static void requireAdmin() {
        if (!isCurrentUserAdmin()) {
            throw new RuntimeException("无权限访问，需要管理员权限");
        }
    }

    public static void requireSysAdmin() {
        if (!isCurrentUserSysAdmin()) {
            throw new RuntimeException("无权限访问，需要系统管理员权限");
        }
    }

    /**
     * 权限验证辅助方法（带自定义错误信息）
     */
    public static void requireAdmin(String errorMessage) {
        if (!isCurrentUserAdmin()) {
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Token用户信息封装类
     */
    public static class TokenUserInfo {
        private Long userId;
        private String username;
        private Long roleId;
        private String roleCode;
        private Boolean admin;

        // getter和setter
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public String getRoleCode() {
            return roleCode;
        }

        public void setRoleCode(String roleCode) {
            this.roleCode = roleCode;
        }

        public Boolean getAdmin() {
            return admin;
        }

        public void setAdmin(Boolean admin) {
            this.admin = admin;
        }
    }
}