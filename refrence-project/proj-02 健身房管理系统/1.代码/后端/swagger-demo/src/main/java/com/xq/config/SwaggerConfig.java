package com.xq.config;


import com.xq.annotation.NotIncludeSwagger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.RequestHandlerSelectors.withMethodAnnotation;
@Configuration
public class SwaggerConfig {
    /**
     * 创建Docket类型的对象,并使用Spring容器管理
     * Docket是Swagger中的全局配置对象
     * @return
     */
    @Bean
    public Docket getDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swaggerDemoApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xq.controller"))
                .apis(not(withMethodAnnotation(NotIncludeSwagger.class)))
                // 只有路径是/user或者/person开头的控制器方法生成接口文档
                //.paths(or(PathSelectors.regex("/user/.*"),PathSelectors.regex("/people/.*")))
                .build();
    }

    private ApiInfo swaggerDemoApiInfo(){
        return new ApiInfoBuilder()
                .contact(new Contact("JAVA学习社区", "https://www.qq.com",
                        "693419787@qq.com")) //配置文档主体内容
                //文档标题
                .title("这里是Swagger的标题")
                //文档描述
                .description("这里是Swagger的描述")
                //文档版本
                .version("1.0.0")
                .build();
    }

}