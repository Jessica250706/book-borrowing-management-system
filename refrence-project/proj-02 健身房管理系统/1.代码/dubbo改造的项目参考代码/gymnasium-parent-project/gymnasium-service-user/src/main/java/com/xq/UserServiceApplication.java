package com.xq;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author kriss
 * @version 1.0.0
 * @date 2024-07-20 22:05
 * @description TODO
 */
@SpringBootApplication
@EnableDubbo
@MapperScan(basePackages = "com.xq.mapper")
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class,args);
    }
}
