package com.xq.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xq.common.converter.LongToStringSerializer;
import com.xq.common.converter.StringToLongDeserializer;

/**
 * 基础DTO，所有DTO继承此类
 */
public class BaseDTO {

    @JsonSerialize(using = LongToStringSerializer.class)
    @JsonDeserialize(using = StringToLongDeserializer.class)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
