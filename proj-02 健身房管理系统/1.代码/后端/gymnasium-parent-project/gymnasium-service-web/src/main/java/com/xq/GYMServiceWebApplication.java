package com.xq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2 //开启对所有控制器扫描
public class GYMServiceWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(GYMServiceWebApplication.class,args);
    }
}
