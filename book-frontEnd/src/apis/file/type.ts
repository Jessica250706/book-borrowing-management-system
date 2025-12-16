/**
 * 文件信息DTO
 */
export interface FileInfoDTO {
    id?: number;
    originalFilename?: string;
    filename?: string;
    filePath?: string;
    size?: number;
    formattedSize?: string;
    contentType?: string;
    downloadUrl?: string;
    previewUrl?: string;
    uploadTime?: string;
    uploadUserId?: number;
    uploadUsername?: string;
    md5?: string;
    sha256?: string;
    description?: string;
    category?: string;
    tags?: string;
    isPublic?: number;
    downloadCount?: number;
    fileStatus?: number;
    deleted?: number;
    fileExtension?: string;
    fileIcon?: string;
    previewable?: boolean;
    statusText?: string;
    publicText?: string;
    uploadUserAvatar?: string;
}

/**
 * 上传文件响应
 */
export interface UploadResponse {
    code: number;
    data?: FileInfoDTO;
    message?: string;
}