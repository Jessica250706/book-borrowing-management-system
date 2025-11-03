package com.xq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO <T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;
    public ResultVO(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
