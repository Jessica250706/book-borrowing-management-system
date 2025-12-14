package com.xq.web.book.controller;

import com.xq.common.annotation.RequireAdmin;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.book.dto.DeleteCheckDTO;
import com.xq.web.book.service.BookInfoService;
import com.xq.web.borrow.record.entity.BatchOperateParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书籍删除管理
 * 提供书籍删除前检查及删除操作，分离删除逻辑避免修改现有 API
 * @module 图书删除
 */
@RestController
@RequestMapping("/api/book-delete")
@Tag(name = "书籍删除", description = "书籍删除前检查及删除操作")
public class BookDeleteController {

    @Autowired
    private BookInfoService bookInfoService;

    /**
     * 删除前检查 - 检查书籍是否有未归还的借阅记录
     * 在删除书籍前调用此接口，返回每本书的借阅情况
     *
     * @param param 批量操作参数，包含 ids 书籍ID列表
     * @return 删除检查结果列表，包含每本书的 bookId、bookName、borrowCount、hasBorrowRecord
     */
    @PostMapping("/check")
    @RequireAdmin
    public ResultVo<List<DeleteCheckDTO>> checkBooksBeforeDelete(@RequestBody BatchOperateParam param) {
        try {
            if (param == null || param.getIds() == null || param.getIds().isEmpty()) {
                return ResultUtils.errorMsg("书籍ID列表不能为空");
            }
            
            List<DeleteCheckDTO> checkResults = bookInfoService.checkBooksBeforeDelete(param.getIds());
            return ResultUtils.success("检查完成", checkResults);
        } catch (Exception e) {
            return ResultUtils.errorMsg("检查失败: " + e.getMessage());
        }
    }

    /**
     * 确认删除书籍
     * 前端在显示确认对话框后，用户点击确定调用此接口
     * 后端将执行实际的删除操作（软删除，标记为已删除）
     *
     * @param param 批量操作参数，包含 ids 书籍ID列表
     * @return 被删除的书籍DTO列表
     */
    @PutMapping("/delete")
    @RequireAdmin
    public ResultVo<List<?>> confirmAndDeleteBooks(@RequestBody BatchOperateParam param) {
        try {
            if (param == null || param.getIds() == null || param.getIds().isEmpty()) {
                return ResultUtils.errorMsg("书籍ID列表不能为空");
            }
            
            // 调用原有的批量删除方法
            var deletedBooks = bookInfoService.deleteBooks(param.getIds());
            return ResultUtils.success("删除书籍成功", deletedBooks);
        } catch (Exception e) {
            return ResultUtils.errorMsg("删除失败: " + e.getMessage());
        }
    }
}
