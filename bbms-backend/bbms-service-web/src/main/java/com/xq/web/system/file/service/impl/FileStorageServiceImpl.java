package com.xq.web.system.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.utils.DateUtil;
import com.xq.web.system.file.entity.FileInfo;
import com.xq.web.system.file.mapper.FileStorageMapper;
import com.xq.web.system.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl extends ServiceImpl<FileStorageMapper, FileInfo> implements FileStorageService {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.access.url-prefix}")
    private String urlPrefix;

    /**
     * 上传单个文件
     */
    public FileInfo uploadFile(MultipartFile file) throws IOException {
        // 1. 验证文件
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 2. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + fileExtension;

        // 3. 创建存储目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 4. 保存文件
        String filePath = uploadPath + originalFilename;
        File dest = new File(filePath);
        file.transferTo(dest);

        // 5. 构建文件信息
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalFilename(originalFilename);
        fileInfo.setFilename(uniqueFilename);
        fileInfo.setFilePath(filePath);
        fileInfo.setSize(file.getSize());
        fileInfo.setContentType(file.getContentType());
        fileInfo.setUploadTime(DateUtil.now());

        // 设置文件分类（根据扩展名自动判断）
        String category = getAutoCategory(fileExtension);
        fileInfo.setCategory(category);

        // 6. 生成访问URL
        String downloadUrl = "/api/file/download/" + originalFilename;
        String previewUrl = "/api/file/preview/" + originalFilename;
        fileInfo.setDownloadUrl(downloadUrl);
        fileInfo.setPreviewUrl(previewUrl);

        // 7. 保存到数据库
        this.save(fileInfo);

        log.info("文件上传成功: {}", fileInfo);
        return fileInfo;
    }

    /**
     * 根据文件扩展名自动分类
     */
    private String getAutoCategory(String fileExtension) {
        if (fileExtension == null || fileExtension.isEmpty()) {
            return "其他";
        }

        String ext = fileExtension.toLowerCase();
        if (ext.matches("\\.(jpg|jpeg|png|gif|bmp|webp|svg)$")) {
            return "图片";
        } else if (ext.matches("\\.(pdf|doc|docx|xls|xlsx|ppt|pptx|txt|md)$")) {
            return "文档";
        } else if (ext.matches("\\.(mp4|avi|mov|wmv|flv|mkv|rmvb)$")) {
            return "视频";
        } else if (ext.matches("\\.(mp3|wav|flac|aac|ogg|wma)$")) {
            return "音频";
        } else if (ext.matches("\\.(zip|rar|7z|tar|gz|bz2)$")) {
            return "压缩包";
        } else {
            return "其他";
        }
    }

    /**
     * 上传多个文件
     */
    public List<FileInfo> uploadMultipleFiles(MultipartFile[] files) throws IOException {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                fileInfos.add(uploadFile(file));
            }
        }
        return fileInfos;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String filename) {
        File file = new File(uploadPath + filename);
        return file.exists() && file.isFile();
    }

    /**
     * 根据文件名获取文件
     */
    public File getFile(String filename) {
        return new File(uploadPath + filename);
    }

    /**
     * 获取文件MIME类型
     */
    public String getContentType(String filename) {
        try {
            return Files.probeContentType(Paths.get(uploadPath + filename));
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}
