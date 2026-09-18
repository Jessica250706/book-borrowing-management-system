import request from '@/apis/request';
import type { UploadResponse, FileInfoDTO } from './type';

/**
 * 文件上传接口
 */
export const uploadFile = async (file: File): Promise<UploadResponse> => {
    const formData = new FormData();
    formData.append('file', file);

    return request({
        url: '/api/file/upload',
        method: 'POST',
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        data: formData
    }) as Promise<UploadResponse>; // 添加类型断言
};

/**
 * 构建完整预览URL
 */
const buildFullPreviewUrl = (url: string | undefined): string => {
    if (!url) return '';

    // 如果已经是完整URL，直接返回
    if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
    }

    // 如果是相对路径，构建完整URL
    if (url.startsWith('/')) {
        // 获取当前协议和主机名
        const protocol = window.location.protocol; // http: 或 https:
        const hostname = window.location.hostname;

        // 构建带端口号8089的URL
        return `${protocol}//${hostname}:8089${url}`;
    }

    return url;
};

/**
 * 封面上传（封装上传接口）
 */
export const uploadCover = async (file: File): Promise<string> => {
    try {
        const response = await uploadFile(file);

        const code = response.code ?? 200;

        if (code === 200 || code === 0) {
            const fileData = response.data;
            if (fileData) {
                // 优先使用预览URL
                let url = fileData.previewUrl;

                if (url) {
                    // 构建带端口号的完整URL
                    url = buildFullPreviewUrl(url);
                    console.log('封面上传成功，完整预览URL:', url);
                    return url;
                }

                return '';
            }
        }
        throw new Error(response.message || '上传失败');
    } catch (error: any) {
        console.error('封面上传失败:', error);
        throw error;
    }
};

/**
 * 预览文件上传（封装上传接口）
 */
export const uploadPreviewFile = async (file: File): Promise<FileInfoDTO & { fullPreviewUrl?: string }> => {
    try {
        const response = await uploadFile(file);

        const code = response.code ?? 200;

        if (code === 200 || code === 0) {
            // 获取原始文件信息
            const fileData = response.data as FileInfoDTO;

            // 如果有预览URL，构建完整URL
            if (fileData.previewUrl) {
                const fullPreviewUrl = buildFullPreviewUrl(fileData.previewUrl);
                console.log('预览文件上传成功，完整预览URL:', fullPreviewUrl);

                // 返回包含完整预览URL的文件信息
                return {
                    ...fileData,
                    fullPreviewUrl
                };
            }

            return fileData;
        }
        throw new Error(response.message || '上传失败');
    } catch (error: any) {
        console.error('预览文件上传失败:', error);
        throw error;
    }
};