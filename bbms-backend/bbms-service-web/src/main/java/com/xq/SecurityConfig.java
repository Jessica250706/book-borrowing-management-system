package com.xq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（使用新API）
                .csrf(csrf -> csrf.disable())

                // 配置会话管理为无状态
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 配置请求授权
                .authorizeHttpRequests(authorize -> authorize
                        // 允许公开访问的路径
                        .requestMatchers("/api/auth/**").permitAll()  // 登录注册接口
                        .requestMatchers("/api/public/**").permitAll() // 公共接口
                        .anyRequest().authenticated()  // 其他接口需要认证
                )

                // 禁用HTTP Basic认证
                .httpBasic(httpBasic -> httpBasic.disable())

                // 禁用表单登录
                .formLogin(formLogin -> formLogin.disable());

        return http.build();
    }
}