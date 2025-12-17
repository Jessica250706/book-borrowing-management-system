import axios from 'axios';
import type {
  BaseApiResponse,
  GetMessageListParams,
  MessagePageDTO} from './type';
  
// 创建请求实例（复用项目基础配置，若已有全局请求实例可直接导入）
const request = axios.create({
  baseURL: 'http://localhost:8089', // 后端接口基础地址
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
  const response = await request.get<BaseApiResponse<MessagePageDTO>>(
    '/api/message/list', // 后端消息列表接口路径（需与后端确认）
    { params }
  );
  return response.data;
};

// 一键标记所有消息已读
export const markAllRead = () => {
  return request({
    url: '/message/markAllRead',
    method: 'put'
  });
};


export default {
  getMessageList,
  markAllRead
};
