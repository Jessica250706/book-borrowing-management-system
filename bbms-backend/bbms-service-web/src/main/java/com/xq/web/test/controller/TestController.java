package com.xq.web.test.controller;

import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 测试
 * @module 测试
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 测试字符串ID转换为Long
     */
    @GetMapping("/test-long-converter")
    public ResultVo<String> testLongConverter(@RequestParam Long id) {
        return ResultUtils.successMsg("转换成功，ID类型: " + id.getClass().getSimpleName() + ", 值: " + id);
    }

    /**
     * 测试列表ID转换
     */
    @PostMapping("/test-list-converter")
    public ResultVo<String> testListConverter(@RequestBody Map<String, Object> request) {
        // 这里可以接收包含Long列表的请求体进行测试
        return ResultUtils.successMsg("列表转换测试");
    }
}
