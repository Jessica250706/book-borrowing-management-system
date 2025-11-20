package com.xq.config;

import com.xq.config.interceptor.AuthInterceptor;
import com.xq.config.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 解决跨域问题
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 认证拦截器
    @Autowired
    private AuthInterceptor authInterceptor;

    // 权限拦截器
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    //配置跨域
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600)
                .allowCredentials(true);
    }

    //解决图片不能回显的问题
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //
       registry.addResourceHandler("/gym/**").addResourceLocations("http://localhost:9000/gym/");
    }

    // 拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先注册认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**")
                .excludePathPatterns("/api/public/**");

        // 再注册权限拦截器（在认证之后执行）
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**")
                .excludePathPatterns("/api/public/**");
    }
}
