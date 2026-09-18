package com.xq.web.system.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 文件信息表实体类
 *
 * @author xq
 */
@Data
@TableName("file_info")
public class FileInfo {

    /**
     * 文件ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原始文件名
     */
    @TableField("original_filename")
    private String originalFilename;

    /**
     * 存储的文件名（唯一）
     */
    @TableField("filename")
    private String filename;

    /**
     * 文件存储路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @TableField("size")
    private Long size;

    /**
     * 文件类型
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 下载URL
     */
    @TableField("download_url")
    private String downloadUrl;

    /**
     * 预览URL
     */
    @TableField("preview_url")
    private String previewUrl;

    /**
     * 上传时间
     */
    @TableField("upload_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /**
     * 上传用户ID
     */
    @TableField("upload_user_id")
    private Long uploadUserId;

    /**
     * 上传用户名
     */
    @TableField("upload_username")
    private String uploadUsername;

    /**
     * 文件MD5值（用于去重）
     */
    @TableField("md5")
    private String md5;

    /**
     * 文件SHA256值
     */
    @TableField("sha256")
    private String sha256;

    /**
     * 文件描述
     */
    @TableField("description")
    private String description;

    /**
     * 文件分类
     */
    @TableField("category")
    private String category;

    /**
     * 文件标签
     */
    @TableField("tags")
    private String tags;

    /**
     * 是否公开（0=私有，1=公开）
     */
    @TableField("is_public")
    private Integer isPublic = 1;

    /**
     * 下载次数
     */
    @TableField("download_count")
    private Integer downloadCount = 0;

    /**
     * 文件状态（0=禁用，1=正常）
     * 注意：数据库字段名是 'status'，不是 'file_status'
     */
    @TableField("status")
    private Integer status = 1;

    /**
     * 是否删除（0=未删除，1=已删除）
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted = 0;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
