// DtoConvertUtil.java
package com.xq.web.book.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xq.web.book.entity.BookInfo;

/**
 * DTO转换工具类
 * 提供通用的DTO转换方法，用于图书相关DTO的转换
 */
public class DtoConvertUtil {
    
    private static final Logger log = LoggerFactory.getLogger(DtoConvertUtil.class);
    
    /**
     * 设置图书相关DTO的公共字段
     * 提取所有图书DTO转换中共享的逻辑，简化代码并提高可维护性
     * @param dto DTO对象
     * @param bookInfo 图书信息实体
     */
    public static <T> void setBookCommonFields(T dto, BookInfo bookInfo) {
        if (dto == null || bookInfo == null) {
            return;
        }
        
        // 使用反射设置公共字段，避免重复代码
        try {
            // 设置借阅状态描述
            String borrowStatusDesc = convertToBorrowStatusDesc(bookInfo.getBookStatus());
            java.lang.reflect.Method setBorrowStatusMethod = dto.getClass().getMethod("setBorrowStatus", String.class);
            setBorrowStatusMethod.invoke(dto, borrowStatusDesc);
            
            // 设置是否可借阅（只有 bookStatus=3 时才是可借阅状态）
            boolean canBorrow = bookInfo.getBookStatus() == 3 && bookInfo.getAvailableCount() > 0;
            java.lang.reflect.Method setCanBorrowMethod = dto.getClass().getMethod("setCanBorrow", boolean.class);
            setCanBorrowMethod.invoke(dto, canBorrow);
        } catch (NoSuchMethodException e) {
            // 如果DTO不包含这些方法，不报错，直接跳过
            log.debug("DTO {} 不包含需要的方法，跳过设置公共字段", dto.getClass().getSimpleName());
        } catch (Exception e) {
            // 如果其他反射调用失败，记录错误并继续，不影响主流程
            log.error("设置DTO公共字段失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据书籍状态获取状态描述
     * 书籍状态映射：0-草稿未发布，1-非草稿未发布，2-待上架，3-可借阅，4-已借光
     * @param bookStatus 书籍状态
     * @return 状态描述
     */
    public static String convertToBorrowStatusDesc(Integer bookStatus) {
        if (bookStatus == null) {
            return "未知状态";
        }
        switch (bookStatus) {
            case 0:
                return "草稿未发布";
            case 1:
                return "非草稿未发布";
            case 2:
                return "待上架";
            case 3:
                return "可借阅";
            case 4:
                return "已借光";
            default:
                return "未知状态";
        }
    }
}