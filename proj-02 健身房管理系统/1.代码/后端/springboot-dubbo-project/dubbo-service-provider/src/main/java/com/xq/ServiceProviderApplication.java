package com.xq;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.xq.processor.ServiceParameterBeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDubbo
//@EnableHystrix
public class ServiceProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProviderApplication.class,args);
    }

   /* @Bean
    ServiceParameterBeanPostProcessor serviceParameterBeanPostProcessor() {
        return new ServiceParameterBeanPostProcessor();
    }*/
}
