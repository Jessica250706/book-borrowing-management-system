package com.xq.config.interceptor;

import com.xq.common.context.UserContext;
import com.xq.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器 - Spring Boot 3.x 版本
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取token
        String token = getTokenFromRequest(request);

        if (token != null && jwtUtils.verify(token)) {
            // 解析token获取用户信息
            Long userId = jwtUtils.getUserId(token);
            String roleCode = jwtUtils.getRoleCode(token);
            Boolean isAdmin = jwtUtils.isAdmin(token);

            // 设置到用户上下文
            UserContext.setUserId(userId);
            UserContext.setUserRole(roleCode);
            UserContext.setIsAdmin(isAdmin);

        } else {
            // token无效的处理 - 使用统一的响应格式和状态码
            response.setContentType("application/json;charset=UTF-8");
            // 使用ResultVo的NO_LOGIN状态码(600)代替HTTP 401，保持HTTP状态码为200
            response.getWriter().write("{\"code\": 600, \"message\": \"Token无效或已过期\"}");
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}