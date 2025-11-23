package com.xq.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Token提取工具类
 */
@Component
public class TokenExtractUtils {

    /**
     * 从请求中提取token
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
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
     * 从请求头中提取token（严格模式，只从Authorization头获取）
     */
    public String extractTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    /**
     * 从请求参数中提取token
     */
    public String extractTokenFromParameter(HttpServletRequest request) {
        return request.getParameter("token");
    }

    /**
     * 从Authorization头中提取token（便捷方法）
     */
    public String extractTokenFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}