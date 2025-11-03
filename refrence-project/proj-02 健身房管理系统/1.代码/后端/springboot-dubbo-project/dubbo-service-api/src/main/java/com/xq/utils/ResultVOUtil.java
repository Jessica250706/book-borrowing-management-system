package com.xq.utils;

import com.xq.entity.ResultVO;

public class ResultVOUtil {
    public static ResultVO success(){
        return new ResultVO<>(200,"操作成功");
    }
    public static ResultVO success(Object data){
        return new ResultVO<>(200,"操作成功",data);
    }
    public static ResultVO error(Integer code,String msg){
        return new ResultVO<>(code,msg);
    }
}
