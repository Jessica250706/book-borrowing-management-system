package com.xq.web.system.file.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xq.web.system.file.entity.FileInfo;
import lombok.Data;

import java.util.Date;

/**
 * 文件信息数据传输对象
 * 用于前端交互，排除数据库技术字段
 *
 * @author xq
 */
@Data
public class FileInfoDTO {

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 存储的文件名（唯一）
     */
    private String filename;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 格式化后的文件大小（如：2.5 MB）
     */
    private String formattedSize;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 下载URL
     */
    private String downloadUrl;

    /**
     * 预览URL
     */
    private String previewUrl;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;

    /**
     * 上传用户名
     */
    private String uploadUsername;

    /**
     * 文件MD5值（用于去重）
     */
    private String md5;

    /**
     * 文件SHA256值
     */
    private String sha256;

    /**
     * 文件描述
     */
    private String description;

    /**
     * 文件分类
     */
    private String category;

    /**
     * 文件标签
     */
    private String tags;

    /**
     * 是否公开（0=私有，1=公开）
     */
    private Integer isPublic = 1;

    /**
     * 下载次数
     */
    private Integer downloadCount = 0;

    /**
     * 文件状态（0=禁用，1=正常）
     */
    private Integer status = 1;

    /**
     * 是否删除（0=未删除，1=已删除）
     */
    private Integer deleted = 0;

    // ============= 业务计算字段 =============

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件类型图标
     */
    private String fileIcon;

    /**
     * 是否可预览
     */
    private Boolean previewable;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 公开状态文本
     */
    private String publicText;

    /**
     * 上传用户头像
     */
    private String uploadUserAvatar;

    // ============= 转换方法 =============

    /**
     * 从实体类转换为DTO
     */
    public static FileInfoDTO fromEntity(FileInfo entity) {
        if (entity == null) {
            return null;
        }

        FileInfoDTO dto = new FileInfoDTO();
        dto.setId(entity.getId());
        dto.setOriginalFilename(entity.getOriginalFilename());
        dto.setFilename(entity.getFilename());
        dto.setFilePath(entity.getFilePath());
        dto.setSize(entity.getSize());
        dto.setContentType(entity.getContentType());
        dto.setDownloadUrl(entity.getDownloadUrl());
        dto.setPreviewUrl(entity.getPreviewUrl());
        dto.setUploadTime(entity.getUploadTime());
        dto.setUploadUserId(entity.getUploadUserId());
        dto.setUploadUsername(entity.getUploadUsername());
        dto.setMd5(entity.getMd5());
        dto.setSha256(entity.getSha256());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setTags(entity.getTags());
        dto.setIsPublic(entity.getIsPublic());
        dto.setDownloadCount(entity.getDownloadCount());
        dto.setStatus(entity.getStatus());
        dto.setDeleted(entity.getDeleted());

        // 计算业务字段
        dto.calculateBusinessFields();

        return dto;
    }

    /**
     * 转换为实体类
     */
    public FileInfo toEntity() {
        FileInfo entity = new FileInfo();
        entity.setId(this.getId());
        entity.setOriginalFilename(this.getOriginalFilename());
        entity.setFilename(this.getFilename());
        entity.setFilePath(this.getFilePath());
        entity.setSize(this.getSize());
        entity.setContentType(this.getContentType());
        entity.setDownloadUrl(this.getDownloadUrl());
        entity.setPreviewUrl(this.getPreviewUrl());
        entity.setUploadTime(this.getUploadTime());
        entity.setUploadUserId(this.getUploadUserId());
        entity.setUploadUsername(this.getUploadUsername());
        entity.setMd5(this.getMd5());
        entity.setSha256(this.getSha256());
        entity.setDescription(this.getDescription());
        entity.setCategory(this.getCategory());
        entity.setTags(this.getTags());
        entity.setIsPublic(this.getIsPublic());
        entity.setDownloadCount(this.getDownloadCount());
        entity.setStatus(this.getStatus());
        entity.setDeleted(this.getDeleted());

        return entity;
    }

    /**
     * 计算业务字段
     */
    public void calculateBusinessFields() {
        // 计算文件扩展名
        this.fileExtension = calculateFileExtension();

        // 计算格式化文件大小
        this.formattedSize = formatFileSize(this.size);

        // 计算文件图标
        this.fileIcon = calculateFileIcon();

        // 计算是否可预览
        this.previewable = calculatePreviewable();

        // 计算状态文本
        this.statusText = calculateStatusText();

        // 计算公开状态文本
        this.publicText = calculatePublicText();
    }

    /**
     * 计算文件扩展名
     */
    private String calculateFileExtension() {
        if (this.filename == null || !this.filename.contains(".")) {
            return "";
        }
        return this.filename.substring(this.filename.lastIndexOf("."));
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(Long size) {
        if (size == null) {
            return "0 B";
        }
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 计算文件图标
     */
    private String calculateFileIcon() {
        if (this.contentType == null && this.filename == null) {
            return "file";
        }

        String extension = this.fileExtension != null ?
                this.fileExtension.toLowerCase() : "";

        // 图片
        if (this.contentType != null && this.contentType.startsWith("image/")) {
            return "image";
        } else if (extension.matches("\\.(jpg|jpeg|png|gif|bmp|webp|svg|ico)$")) {
            return "image";
        }

        // 文档
        if (this.contentType != null && (
                this.contentType.contains("pdf") ||
                        this.contentType.contains("document") ||
                        this.contentType.contains("msword") ||
                        this.contentType.contains("excel") ||
                        this.contentType.contains("powerpoint") ||
                        this.contentType.contains("text"))) {
            return "document";
        } else if (extension.matches("\\.(pdf|doc|docx|xls|xlsx|ppt|pptx|txt|md|rtf)$")) {
            return "document";
        }

        // 压缩包
        if (extension.matches("\\.(zip|rar|7z|tar|gz|bz2)$")) {
            return "archive";
        }

        // 视频
        if (this.contentType != null && this.contentType.startsWith("video/")) {
            return "video";
        } else if (extension.matches("\\.(mp4|avi|mov|wmv|flv|mkv|rmvb|mpeg|mpg)$")) {
            return "video";
        }

        // 音频
        if (this.contentType != null && this.contentType.startsWith("audio/")) {
            return "audio";
        } else if (extension.matches("\\.(mp3|wav|flac|aac|ogg|wma)$")) {
            return "audio";
        }

        return "file";
    }

    /**
     * 计算是否可预览
     */
    private Boolean calculatePreviewable() {
        if (!isNormal() || isDeleted()) {
            return false;
        }

        String icon = this.fileIcon;
        return "image".equals(icon) || "document".equals(icon) || "pdf".equals(icon);
    }

    /**
     * 计算状态文本
     */
    private String calculateStatusText() {
        if (this.status == null) {
            return "未知";
        }
        switch (this.status) {
            case 0:
                return "禁用";
            case 1:
                return "正常";
            case 2:
                return "审核中";
            case 3:
                return "违规";
            default:
                return "未知";
        }
    }

    /**
     * 计算公开状态文本
     */
    private String calculatePublicText() {
        if (this.isPublic == null) {
            return "未知";
        }
        return this.isPublic == 1 ? "公开" : "私有";
    }

    // ============= 业务判断方法 =============

    /**
     * 判断文件是否正常
     */
    public boolean isNormal() {
        return this.status != null && this.status == 1;
    }

    /**
     * 判断文件是否禁用
     */
    public boolean isDisabled() {
        return this.status != null && this.status == 0;
    }

    /**
     * 判断文件是否删除
     */
    public boolean isDeleted() {
        return this.deleted != null && this.deleted == 1;
    }

    /**
     * 判断文件是否公开
     */
    public boolean isPublicFile() {
        return this.isPublic != null && this.isPublic == 1;
    }

    /**
     * 判断是否为图片文件
     */
    public boolean isImage() {
        return "image".equals(this.fileIcon);
    }

    /**
     * 判断是否为文档文件
     */
    public boolean isDocument() {
        return "document".equals(this.fileIcon);
    }

    /**
     * 判断是否为压缩文件
     */
    public boolean isArchive() {
        return "archive".equals(this.fileIcon);
    }

    /**
     * 判断是否为视频文件
     */
    public boolean isVideo() {
        return "video".equals(this.fileIcon);
    }

    /**
     * 判断是否为音频文件
     */
    public boolean isAudio() {
        return "audio".equals(this.fileIcon);
    }

    /**
     * 判断用户是否有权访问此文件
     */
    public boolean canAccess(Long userId) {
        if (isDeleted() || isDisabled()) {
            return false;
        }

        if (isPublicFile()) {
            return true;
        }

        // 私有文件只有上传者可以访问
        return userId != null && userId.equals(this.uploadUserId);
    }

    // ============= Builder 模式 =============

    /**
     * 创建 Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private final FileInfoDTO dto = new FileInfoDTO();

        public Builder id(Long id) {
            dto.setId(id);
            return this;
        }

        public Builder originalFilename(String originalFilename) {
            dto.setOriginalFilename(originalFilename);
            return this;
        }

        public Builder filename(String filename) {
            dto.setFilename(filename);
            return this;
        }

        public Builder filePath(String filePath) {
            dto.setFilePath(filePath);
            return this;
        }

        public Builder size(Long size) {
            dto.setSize(size);
            return this;
        }

        public Builder contentType(String contentType) {
            dto.setContentType(contentType);
            return this;
        }

        public Builder downloadUrl(String downloadUrl) {
            dto.setDownloadUrl(downloadUrl);
            return this;
        }

        public Builder previewUrl(String previewUrl) {
            dto.setPreviewUrl(previewUrl);
            return this;
        }

        public Builder uploadTime(Date uploadTime) {
            dto.setUploadTime(uploadTime);
            return this;
        }

        public Builder uploadUserId(Long uploadUserId) {
            dto.setUploadUserId(uploadUserId);
            return this;
        }

        public Builder uploadUsername(String uploadUsername) {
            dto.setUploadUsername(uploadUsername);
            return this;
        }

        public Builder md5(String md5) {
            dto.setMd5(md5);
            return this;
        }

        public Builder sha256(String sha256) {
            dto.setSha256(sha256);
            return this;
        }

        public Builder description(String description) {
            dto.setDescription(description);
            return this;
        }

        public Builder category(String category) {
            dto.setCategory(category);
            return this;
        }

        public Builder tags(String tags) {
            dto.setTags(tags);
            return this;
        }

        public Builder isPublic(Integer isPublic) {
            dto.setIsPublic(isPublic);
            return this;
        }

        public Builder downloadCount(Integer downloadCount) {
            dto.setDownloadCount(downloadCount);
            return this;
        }

        public Builder status(Integer status) {
            dto.setStatus(status);
            return this;
        }

        public Builder deleted(Integer deleted) {
            dto.setDeleted(deleted);
            return this;
        }

        public FileInfoDTO build() {
            dto.calculateBusinessFields();
            return dto;
        }
    }

    // ============= toString 方法（排除大字段） =============

    @Override
    public String toString() {
        return "FileInfoDTO{" +
                "id=" + id +
                ", originalFilename='" + originalFilename + '\'' +
                ", filename='" + filename + '\'' +
                ", size=" + formattedSize +
                ", contentType='" + contentType + '\'' +
                ", uploadUserId=" + uploadUserId +
                ", uploadUsername='" + uploadUsername + '\'' +
                ", status=" + statusText +
                ", isPublic=" + publicText +
                ", downloadCount=" + downloadCount +
                ", uploadTime=" + uploadTime +
                '}';
    }
}