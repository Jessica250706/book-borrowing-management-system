package com.xq.web.book.dto;

import com.xq.dto.PageInfoDTO;
import lombok.Data;
import java.util.List;

/**
 * 书籍列表响应VO
 */
@Data
public class BookListVO {
    private PageInfoDTO pageInfo;      // 分页信息
    private List<BookListDTO> records; // 书籍列表
}