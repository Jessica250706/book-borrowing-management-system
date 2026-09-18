package com.xq.web.system.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.web.system.file.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileStorageService extends IService<FileInfo> {

    /**
     * 上传单个文件
     *
     * @param file 上传的文件
     * @return 文件信息
     * @throws IOException 文件操作异常
     */
    FileInfo uploadFile(MultipartFile file) throws IOException;

    /**
     * 上传多个文件
     *
     * @param files 上传的文件数组
     * @return 文件信息列表
     * @throws IOException 文件操作异常
     */
    List<FileInfo> uploadMultipleFiles(MultipartFile[] files) throws IOException;

    /**
     * 检查文件是否存在
     *
     * @param filename 文件名
     * @return 是否存在
     */
    boolean fileExists(String filename);

    /**
     * 根据文件名获取文件
     *
     * @param filename 文件名
     * @return 文件对象
     */
    File getFile(String filename);

    /**
     * 获取文件的MIME类型
     *
     * @param filename 文件名
     * @return MIME类型
     */
    String getContentType(String filename);
}