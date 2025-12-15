package com.xq.web.system.file.controller;

import com.xq.dto.ResultVo;
import com.xq.utils.ResultUtils;
import com.xq.web.system.file.dto.FileInfoDTO;
import com.xq.web.system.file.entity.FileInfo;
import com.xq.web.system.file.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件管理
 * @module 文件管理
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${file.upload.path}")
    private String uploadPath;  // 直接注入配置

    /**
     * 上传单个文件
     * 文件上传接口，支持单个文件上传
     *
     * @param file 上传的文件
     * @return 上传结果，包含文件信息
     */
    @PostMapping("/upload")
    public ResultVo<FileInfoDTO> upload(@RequestParam("file") MultipartFile file) {
        try {
            FileInfo fileInfo = fileStorageService.uploadFile(file);
            FileInfoDTO dto = FileInfoDTO.fromEntity(fileInfo);
            return ResultUtils.success("上传成功", dto);
        } catch (IOException e) {
            return ResultUtils.errorMsg("文件上传失败: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        } catch (Exception e) {
            return ResultUtils.errorMsg("文件上传失败，请稍后重试");
        }
    }

    /**
     * 上传多个文件
     * 批量文件上传接口，支持多个文件同时上传
     *
     * @param files 上传的文件数组
     * @return 上传结果，包含所有文件信息列表
     */
    @PostMapping("/upload/multiple")
    public ResultVo<List<FileInfoDTO>> uploadMultiple(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return ResultUtils.errorMsg("请选择要上传的文件");
            }

            List<FileInfo> fileInfos = fileStorageService.uploadMultipleFiles(files);
            List<FileInfoDTO> dtos = fileInfos.stream()
                    .map(FileInfoDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResultUtils.success("上传成功", dtos);
        } catch (IOException e) {
            return ResultUtils.errorMsg("文件上传失败: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        } catch (Exception e) {
            return ResultUtils.errorMsg("文件上传失败，请稍后重试");
        }
    }

    /**
     * 文件下载
     * 下载文件接口，文件会以附件形式下载
     *
     * @param filename 文件名
     * @return 文件响应流
     * @throws IOException 文件操作异常
     */
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws IOException {
        // 1. 获取文件
        File file = fileStorageService.getFile(filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 2. 构建Resource对象
        Path filePath = Paths.get(file.getAbsolutePath());
        Resource resource = new UrlResource(filePath.toUri());

        // 3. 验证资源是否可读
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.status(500).build();
        }

        // 4. 确定Content-Type
        String contentType = fileStorageService.getContentType(filename);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 5. 设置响应头
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * 文件预览
     * 文件预览接口，支持在浏览器中直接预览（图片、PDF等）
     *
     * @param filename 文件名
     * @return 文件响应流
     * @throws IOException 文件操作异常
     */
    @GetMapping("/preview/{filename:.+}")
    public ResponseEntity<Resource> previewFile(@PathVariable String filename) throws IOException {
        // 1. 获取文件
        File file = fileStorageService.getFile(filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 2. 构建Resource对象
        Resource resource = new UrlResource(file.toURI());

        // 3. 验证资源是否可读
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.status(500).build();
        }

        // 4. 获取Content-Type
        String contentType = fileStorageService.getContentType(filename);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 5. 设置响应头（注意：没有attachment，浏览器会尝试显示而不是下载）
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    /**
     * 获取图片Base64编码
     * 获取图片文件的Base64编码，用于前端直接显示
     *
     * @param filename 文件名
     * @return Base64编码的图片数据
     * @throws IOException 文件操作异常
     */
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<String> getImageBase64(@PathVariable String filename) throws IOException {
        // 1. 验证文件是否存在
        File file = fileStorageService.getFile(filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 2. 验证是否为图片文件
        String contentType = fileStorageService.getContentType(filename);
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("非图片文件不支持Base64编码");
        }

        // 3. 将图片转换为Base64
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        // 4. 构建Data URL
        String base64Image = "data:" + contentType + ";base64," + encodedString;
        return ResponseEntity.ok(base64Image);
    }

    /**
     * 获取文件列表
     * 获取上传目录下的所有文件列表
     *
     * @return 文件列表结果
     */
    @GetMapping("/list")
    public ResultVo<List<Map<String, Object>>> getFileList() {
        try {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                return ResultUtils.success("目录不存在", Collections.emptyList());
            }

            // 验证是否为目录
            if (!uploadDir.isDirectory()) {
                return ResultUtils.errorMsg("上传路径不是有效的目录");
            }

            // 获取目录下的文件列表
            File[] files = uploadDir.listFiles();
            if (files == null) {
                return ResultUtils.success("目录为空或不可访问", Collections.emptyList());
            }

            List<Map<String, Object>> fileList = Arrays.stream(files)
                    .filter(File::isFile)
                    .map(file -> {
                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("filename", file.getName());
                        fileInfo.put("size", file.length());
                        fileInfo.put("formattedSize", formatFileSize(file.length()));
                        fileInfo.put("lastModified", new Date(file.lastModified()));
                        fileInfo.put("downloadUrl", "/api/file/download/" + file.getName());
                        fileInfo.put("previewUrl", "/api/file/preview/" + file.getName());
                        return fileInfo;
                    })
                    .sorted((f1, f2) -> {
                        // 按最后修改时间倒序排序
                        Date date1 = (Date) f1.get("lastModified");
                        Date date2 = (Date) f2.get("lastModified");
                        return date2.compareTo(date1);
                    })
                    .collect(Collectors.toList());

            return ResultUtils.success("获取成功", fileList);

        } catch (SecurityException e) {
            return ResultUtils.errorMsg("无权限访问文件目录");
        } catch (Exception e) {
            return ResultUtils.errorMsg("获取文件列表失败: " + e.getMessage());
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        else if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
        else return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }
}