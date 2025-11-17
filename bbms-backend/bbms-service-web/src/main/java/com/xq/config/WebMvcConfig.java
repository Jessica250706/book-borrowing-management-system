package com.xq.config;

import com.xq.common.converter.StringToLongConverter;
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

    /**
     * 字符串到Long类型的转换器
     * 用于自动将前端传入的字符串ID转换为后端需要的Long类型
     *
     * 注入原理：
     * 1. StringToLongConverter 已在bbms-common模块中使用@Component注解声明为Spring Bean
     * 2. 通过@Autowired自动注入到当前配置类中
     * 3. Spring会从应用上下文中查找匹配类型的Bean并自动注入
     */
    @Autowired
    private StringToLongConverter stringToLongConverter;

    /**
     * 添加自定义格式化器（转换器）到Spring MVC的格式化器注册表中
     *
     * 方法说明：
     * 1. 此方法在Spring MVC初始化时被调用
     * 2. 用于注册自定义的类型转换器，使其在参数绑定过程中生效
     * 3. 支持@RequestParam、@PathVariable、@ModelAttribute等注解的参数转换
     *
     * 工作流程：
     * 1. 当前端传入字符串格式的ID（如"123"）时
     * 2. Spring MVC在参数绑定过程中会使用注册的转换器
     * 3. StringToLongConverter将字符串"123"转换为Long类型的123
     * 4. 转换后的Long值被注入到Controller方法的参数中
     *
     * 支持场景：
     * - GET请求的查询参数：/api/books?id=123
     * - RESTful路径参数：/api/books/123
     * - POST表单参数：id=123
     * - JSON请求体中的字符串数字字段
     *
     * @param registry Spring格式化器注册表，用于管理类型转换器
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 注册字符串到Long类型的转换器
        // 此后，所有需要将String转换为Long的场景都会自动使用此转换器
        registry.addConverter(stringToLongConverter);

        // 如果需要注册其他转换器，可以继续添加：
        // registry.addConverter(otherConverter);
        // registry.addFormatter(formatter);
    }

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
