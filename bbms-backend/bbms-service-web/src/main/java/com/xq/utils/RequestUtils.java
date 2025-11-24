package com.xq.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestUtils {

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
    public static Long getCurrentUserId(JwtUtils jwtUtils) {
        String token = getTokenFromRequest();
        return jwtUtils.getUserId(token);
    }
}
