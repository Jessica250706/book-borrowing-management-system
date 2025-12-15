package com.xq.dto;

import com.xq.status.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 封装返回值数据
 * @param <T>
 */
@Data
@AllArgsConstructor
public class ResultVo<T> {
    private Integer code;
    private T data;
    private String message;

    /**
     * 成功响应
     */
    public static <T> ResultVo<T> success(String message, T data) {
        return new ResultVo<>(StatusCode.SUCCESS_CODE, data, message);
    }

    public static <T> ResultVo<T> success(T data) {
        return new ResultVo<>(StatusCode.SUCCESS_CODE, data, "操作成功");
    }

    public static <T> ResultVo<T> success(String message) {
        return new ResultVo<>(StatusCode.SUCCESS_CODE, null, message);
    }

    public static <T> ResultVo<T> success() {
        return new ResultVo<>(StatusCode.SUCCESS_CODE, null, "操作成功");
    }

    /**
     * 错误响应
     */
    public static <T> ResultVo<T> error(String message) {
        return new ResultVo<>(StatusCode.ERROR_CODE, null, message);
    }

    public static <T> ResultVo<T> error(Integer code, String message) {
        return new ResultVo<>(code, null, message);
    }

    public static <T> ResultVo<T> error() {
        return new ResultVo<>(StatusCode.ERROR_CODE, null, "操作失败");
    }

    public static <T> ResultVo<T> error(String message, T data) {
        return new ResultVo<>(StatusCode.ERROR_CODE, data, message);
    }

    public static <T> ResultVo<T> error(Integer code, String message, T data) {
        return new ResultVo<>(code, data, message);
    }
}