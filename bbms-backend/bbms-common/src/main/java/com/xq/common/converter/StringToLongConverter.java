package com.xq.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 字符串到Long类型转换器
 * 用于处理前端传字符串ID，后端自动转换为Long类型
 */
@Component
public class StringToLongConverter implements Converter<String, Long> {

    @Override
    public Long convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        source = source.trim();
        try {
            return Long.valueOf(source);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID格式错误，无法转换为Long类型: " + source);
        }
    }
}