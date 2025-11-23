package com.xq.config;

import com.xq.status.StatusCode;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * 统一处理所有异常，返回统一的响应格式和HTTP 200状态码
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有运行时异常
     * @param e 运行时异常
     * @return 统一的响应对象
     */
    @ExceptionHandler(RuntimeException.class)
    public ResultVo<String> handleRuntimeException(RuntimeException e) {
        return ResultUtils.errorMsg(e.getMessage());
    }

    /**
     * 处理所有异常
     * @param e 异常
     * @return 统一的响应对象
     */
    @ExceptionHandler(Exception.class)
    public ResultVo<String> handleException(Exception e) {
        return ResultUtils.errorMsg("服务器内部错误");
    }
}