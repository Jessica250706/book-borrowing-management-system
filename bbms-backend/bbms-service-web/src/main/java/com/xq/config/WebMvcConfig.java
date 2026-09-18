package com.xq.config;

import com.xq.config.interceptor.AuthInterceptor;
import com.xq.config.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

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

    @Value("${file.upload.path}")
    private String uploadPath;

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
        // registry.addResourceHandler("/gym/**").addResourceLocations("http://localhost:9000/gym/");
        // 映射本地文件到URL路径
        registry.addResourceHandler("/file/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        // 可选：添加缓存控制
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }

    // 拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先注册认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/**")
                .excludePathPatterns("/api/public/**");

        // 再注册权限拦截器（在认证之后执行）
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/**")
                .excludePathPatterns("/api/public/**");
    }
}
