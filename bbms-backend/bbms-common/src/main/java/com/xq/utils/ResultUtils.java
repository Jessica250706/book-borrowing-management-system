package com.xq.utils;

import com.xq.status.StatusCode;

/**
 * 数据返回工具类 - 完全泛型版本
 */
public class ResultUtils {

    /**
     * 成功响应
     */
    public static <T> ResultVo<T> success() {
        return new ResultVo<>(StatusCode.SUCCESS_CODE, null, "操作成功");
    }

    public static <T> ResultVo<T> successMsg(String message) {
        return new ResultVo<>(StatusCode.SUCCESS_CODE, null, message);
    }

    public static <T> ResultVo<T> successData(T data) {
        return new ResultVo<>(StatusCode.SUCCESS_CODE, data, "操作成功");
    }

    public static <T> ResultVo<T> success(String message, T data) {
        return new ResultVo<>(StatusCode.SUCCESS_CODE, data, message);
    }

    /**
     * 错误响应
     */
    public static <T> ResultVo<T> error() {
        return new ResultVo<>(StatusCode.ERROR_CODE, null, "操作失败");
    }

    public static <T> ResultVo<T> errorMsg(String message) {
        return new ResultVo<>(StatusCode.ERROR_CODE, null, message);
    }

    public static <T> ResultVo<T> errorData(T data) {
        return new ResultVo<>(StatusCode.ERROR_CODE, data, "操作失败");
    }

    public static <T> ResultVo<T> error(String message, T data) {
        return new ResultVo<>(StatusCode.ERROR_CODE, data, message);
    }

    /**
     * 自定义响应
     */
    public static <T> ResultVo<T> result(int code, T data, String message) {
        return new ResultVo<>(code, data, message);
    }

    public static <T> ResultVo<T> result(int code, String message) {
        return new ResultVo<>(code, null, message);
    }

    /**
     * 新增：快速方法
     */
    public static <T> ResultVo<T> ok(T data) {
        return successData(data);
    }

    public static <T> ResultVo<T> fail(String message) {
        return errorMsg(message);
    }
}