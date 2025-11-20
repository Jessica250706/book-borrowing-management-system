package com.xq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xq.common.converter.LongToStringSerializer;
import com.xq.common.converter.StringToLongDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson全局配置 - 放在公共模块
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonConfig {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 注册自定义序列化/反序列化器
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, new LongToStringSerializer());
        module.addSerializer(long.class, new LongToStringSerializer());
        module.addDeserializer(Long.class, new StringToLongDeserializer());
        module.addDeserializer(long.class, new StringToLongDeserializer());

        objectMapper.registerModule(module);
        return objectMapper;
    }
}
