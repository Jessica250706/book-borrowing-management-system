package com.xq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class BookServiceWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookServiceWebApplication.class, args);
    }
}