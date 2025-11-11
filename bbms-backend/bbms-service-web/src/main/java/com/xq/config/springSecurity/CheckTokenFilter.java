package com.xq.config.springSecurity;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xq.jwt.JwtUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Component("checkTokenFilter")
public class CheckTokenFilter extends OncePerRequestFilter {

    @Value("#{'${ignore.url}'.split(',')}")
    private List<String> ingoreUrl = Collections.emptyList();

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomizedUserDetailService customizedUserDetailService;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //获取请求的url
            String url = request.getRequestURI();
            //判断当前url是否放行
            if(!ingoreUrl.contains(url) && !url.startsWith("/images/") ){
                validateToken(request);
            }
        } catch (AuthenticationException e) {
            loginFailureHandler.commence(request,response,e);
            return;
        }
        //放行
        filterChain.doFilter(request,response);
    }

    //token校验的方法
    private void validateToken(HttpServletRequest request){
        //获取token数据
        String token = request.getHeader("token");
        //如果从头部获取token参数失败，从参数中获取
        if(StringUtils.isEmpty(token)){
            token = request.getParameter("token");
        }
        //如果没有获取到token
        if(StringUtils.isEmpty(token)){
            throw new CustomerAuthenticationException("token获取失败");
        }
        //token验证
        if(!jwtUtils.verify(token)){
            throw new CustomerAuthenticationException("token无效或已过期");
        }

        // 解析token获取用户信息
        DecodedJWT decodedJWT = jwtUtils.jwtDecode(token);
        Map<String, Claim> claims = decodedJWT.getClaims();

        // 从token中获取用户信息
        String username = claims.get("username").asString();
        String roleCode = claims.get("roleCode").asString();
        Long userId = Long.valueOf(claims.get("userId").asString());
        Boolean isAdmin = Boolean.valueOf(claims.get("isAdmin").asString());

        // 设置用户信息到request attribute，方便Controller层使用
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("roleCode", roleCode);
        request.setAttribute("isAdmin", isAdmin);

        //查询用户信息
        UserDetails details = customizedUserDetailService.loadUserByUsername(username + ":" + roleCode);
        //构造token
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //设置到上下文环境中
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}