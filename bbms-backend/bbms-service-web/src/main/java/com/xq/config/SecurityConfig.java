package com.xq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护（REST API 不需要）
                .csrf(csrf -> csrf.disable())
                // 使用无状态 session（适合 JWT）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 配置请求授权
                .authorizeHttpRequests(authz -> authz
                        // 公开接口（无需认证）
                        .requestMatchers(
                                "/api/user/login",
                                "/api/user/register",
                                "/api/user/refresh-token",
                                "/api/public/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/doc.html"
                        ).permitAll()
                        // 其他 API 接口需要认证（由你的拦截器处理）
                        .requestMatchers("/api/**").authenticated()
                        // 其他请求
                        .anyRequest().permitAll()
                )
                // 禁用表单登录
                .formLogin(form -> form.disable())
                // 禁用 HTTP Basic 认证
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
