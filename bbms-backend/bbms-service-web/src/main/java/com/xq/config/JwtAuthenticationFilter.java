package com.xq.config;

import com.xq.utils.JwtUtils;
import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.service.SysUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SysUserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String token = extractToken(request);

        if (token != null) {
            try {
                boolean isValid = jwtUtils.verify(token);
                if (isValid) {
                    Long userId = jwtUtils.getUserId(token);
                    if (userId != null) {
                        SysUser user = userService.getUserWithRoleInfo(userId);

                        if (user != null) {
                            // 创建认证对象
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(user, null, getAuthorities(user));
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            // 设置到SecurityContext
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            // 验证SecurityContext是否设置成功
                            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(SysUser user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRoleCode() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRoleCode()));
        }
        return authorities;
    }
}
