package com.xq.config.interceptor;

import com.xq.common.annotation.RequireAdmin;
import com.xq.common.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限拦截器 - Spring Boot 3.x 版本
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只拦截方法级别的处理器
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查方法上是否有@RequireAdmin注解
        if (handlerMethod.hasMethodAnnotation(RequireAdmin.class)) {
            if (!UserContext.getIsAdmin()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\": 403, \"message\": \"权限不足，需要管理员权限\"}");
                return false;
            }
        }

        return true;
    }
}