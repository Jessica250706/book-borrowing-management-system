import axios from 'axios';
import type {
  BaseApiResponse,
  GetMessageListParams,
  MessagePageDTO,
  SysMessageDTO
} from './type';

// 创建请求实例（复用项目基础配置）
const request = axios.create({
  baseURL: 'http://localhost:8089', // 与后端接口地址一致
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器：添加Token（与项目现有拦截器保持一致）
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

/**
 * 获取消息列表（分页）
 * @param params 分页和筛选参数
 */
export const getMessageList = async (
  params: GetMessageListParams
): Promise<BaseApiResponse<MessagePageDTO>> => {
  try {
    const response = await request.get<any>(
      '/api/message/list',
      { 
        params: {
          pageNum: params.pageNum,
          pageSize: params.pageSize,
          status: params.status,
          keyword: params.keyword // 添加关键词参数
        } 
      }
    );
    
    console.log('API原始响应:', response.data);
    
    // 适配不同后端响应格式
    const result = response.data;
    
    // 如果result已经有data字段，直接返回
    if (result.data !== undefined) {
      return result;
    } 
    // 否则，将整个响应包装在data字段中
    else {
      return {
        code: 200,
        data: result,
        message: 'success'
      };
    }
  } catch (error: any) {
    console.error('API调用错误:', error);
    return {
      code: 500,
      data: undefined,
      message: error.message || '请求失败'
    };
  }
};

/**
 * 一键标记所有消息已读
 */
export const markAllRead = async (): Promise<BaseApiResponse<number>> => {
  try {
    const response = await request.put<BaseApiResponse<number>>(
      '/api/message/markAllRead'
    );
    
    // 返回后端完整响应，包括code、data和message
    return {
      code: response.data?.code ?? 200,
      data: response.data?.data ?? 0,
      message: response.data?.message ?? '标记成功'
    };
  } catch (error: any) {
    console.error('标记全部已读失败:', error);
    return {
      code: 500,
      data: 0,
      message: error.message || '标记全部已读失败'
    };
  }
};

export default {
  getMessageList,
  markAllRead
};