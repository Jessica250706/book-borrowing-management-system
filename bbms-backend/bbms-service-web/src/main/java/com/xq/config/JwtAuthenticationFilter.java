package com.xq.config;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
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
import java.util.Map;

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
        logger.info("=== JWT过滤器开始 ===");
        logger.info("请求URI: " + requestUri);
        String token = extractToken(request);

        if (token != null) {
            logger.info("提取到Token，长度: " + token.length());
            logger.info("Token前50位: " + token.substring(0, Math.min(50, token.length())));

            try {
                // 1. 先安全解析（不验证过期）
                DecodedJWT decodedJWT = jwtUtils.safeJwtDecode(token);
                logger.info("Token安全解析成功");

                // 打印所有claims
                Map<String, Claim> claims = decodedJWT.getClaims();
                logger.info("Token Claims:");
                claims.forEach((key, claim) -> {
                    logger.info("  " + key + " = " + claim.asString());
                });

                // 检查userId字段
                Claim userIdClaim = claims.get("userId");
                if (userIdClaim != null) {
                    String userIdStr = userIdClaim.asString();
                    logger.info("Token中的userId字符串值: " + userIdStr);

                    try {
                        Long userId = Long.valueOf(userIdStr);
                        logger.info("转换后的userId: " + userId);
                    } catch (NumberFormatException e) {
                        logger.error("userId转换失败: " + userIdStr, e);
                    }
                }

                // 2. 正式验证
                boolean isValid = jwtUtils.verify(token);
                logger.info("Token验证结果: " + isValid);

                if (isValid) {
                    Long userId = jwtUtils.getUserId(token);
                    logger.info("getUserId() 返回: " + userId);

                    if (userId != null) {
                        logger.info("开始查询用户ID=" + userId + " 的信息");
                        SysUser user = userService.getUserWithRoleInfo(userId);
                        logger.info("查询结果: " + (user != null ? "用户存在，用户名: " + user.getUsername() : "用户不存在"));

                        if (user != null) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(user, null, getAuthorities(user));
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.info("SecurityContext设置成功");
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                logger.error("Token处理异常", e);
            }
        } else {
            logger.info("请求未包含Token");
        }

        logger.info("=== JWT过滤器结束 ===");
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
