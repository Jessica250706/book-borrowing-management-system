<template>
  <div class="message-list-page">
    <!-- 筛选和批量操作区域 -->
    <div class="search-section">
      <BookSearchInput 
        @search="handleSearchInput" 
        placeholder="搜索消息内容"
        style="width: 200px" 
      />
      <div class="filter-group">
        <span class="filter-label">消息状态:</span>
        <el-select 
          v-model="filterForm.status" 
          placeholder="所有状态"
          style="width: 150px"
          @change="handleFilterChange"
        >
          <el-option label="所有状态" value="" />
          <el-option label="未读" value="未读" />
          <el-option label="已读" value="已读" />
        </el-select>
      </div>
      
      <div class="batch-actions">
        <el-button 
          type="success" 
          @click="handleMarkAllRead"
        >
          一键已读
        </el-button>
      </div>
    </div>

    <!-- 表格组件 - 确保total传入数字类型 -->
     <Table 
      :data="messageList"          
      :columns="columns" 
      :total="Number(totalCount)"  <!-- 强制转为数字 -->
      :current-page="currentPage"
      :page-size="pageSize"
      :show-index="true" 
      :loading="loading"
      :row-key="(row) => row.messageId"  
      @size-change="handleSizeChange"  
      @current-change="handlePageChange"  
    >
      <!-- 消息内容列 -->
      <template #column-messageContent="{ row }">
        <div class="message-content">
          {{ row.messageContent || '' }}
        </div>
      </template>
      
      <!-- 状态列 -->
      <template #column-statusText="{ row }">
        <el-tag 
          :type="row.statusText === '未读' ? 'danger' : 'info'"
          size="small"
          effect="light"
        >
          {{ row.statusText || '' }}
        </el-tag>
      </template>
      
      <!-- 时间列 -->
      <template #column-sendTime="{ row }">
        <div class="message-time">
          {{ formatDate(row.sendTime) }}
        </div>
      </template>
      
      <!-- 操作列 -->
      <template #actions="{ row }">
        <div class="action-buttons">
          <span class="text-button" @click="handleDetail(row)">详情</span>
        </div>
      </template>
    </Table>

    <!-- 详情对话框 -->
    <CustomDialog 
      v-model="detailDialogVisible" 
      title="消息详情"
      :width="600"
    >
      <div class="detail-content">
        <div class="detail-item">
          <span class="detail-label">消息内容：</span>
          <span class="detail-value">{{ currentDetail.messageContent || '无内容' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态：</span>
          <span class="detail-value">
            <el-tag 
              :type="currentDetail.statusText === '未读' ? 'danger' : 'info'"
              size="small"
              effect="light"
            >
              {{ currentDetail.statusText || '' }}
            </el-tag>
          </span>
        </div>
        <div class="detail-item">
          <span class="detail-label">发送时间：</span>
          <span class="detail-value">{{ formatDate(currentDetail.sendTime) }}</span>
        </div>
      </div>
    </CustomDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import Table from '@/components/mytable/Table.vue';
import CustomDialog from '@/components/Dialog/customDialog/CustomDialog.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import { getMessageList, markAllRead } from '@/apis/Message/index';
import type { 
  GetMessageListParams, 
  SysMessageDTO,
  BaseApiResponse,
  MessagePageDTO
} from '@/apis/Message/type';

// 分页相关 - 确保初始值为数字
const currentPage = ref<number>(1);
const pageSize = ref<number>(20);
const totalCount = ref<number>(0); // 明确类型为数字
const loading = ref<boolean>(false);

// 筛选表单
const filterForm = ref({
  status: '',
  keyword: ''
});

// 消息数据
const messageList = ref<SysMessageDTO[]>([]);

// 详情对话框
const detailDialogVisible = ref<boolean>(false);
const currentDetail = ref<SysMessageDTO>({} as SysMessageDTO);

// 表格列配置 - 确保prop与数据字段完全匹配
const columns = ref([
  { prop: 'messageContent', label: '消息通知', width: 400, align: 'left' },
  { prop: 'statusText', label: '状态', width: 120, align: 'center' },
  { prop: 'sendTime', label: '发送时间', width: 180, align: 'center' },
]);

// 格式化时间 - 增加容错
const formatDate = (dateStr: string | undefined): string => {
  if (!dateStr) return '未知时间';
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString("zh-CN", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit"
    }).replace(/\//g, "-");
  } catch (error) {
    return dateStr || '未知时间';
  }
};

// 加载消息列表 - 修复total类型转换
const loadData = async () => {
  try {
    loading.value = true;
    const params: GetMessageListParams = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      status: filterForm.value.status || ''
    };
    
    console.log('消息列表请求参数:', params);
    
    const response: BaseApiResponse<MessagePageDTO> = await getMessageList(params);
    console.log('消息列表接口响应:', response);
    
    // 严格的类型判断和转换
    if ((response.code === 200 || response.code === 0) && response.data) {
      const { records = [], pageInfo = {} } = response.data;
      
      // 1. 确保列表数据正确赋值
      messageList.value = Array.isArray(records) ? records : [];
      
      // 2. 修复total类型（强制转为数字）
      const total = pageInfo.total || messageList.value.length;
      totalCount.value = Number(total) || 0; // 核心修复：转为数字
      
      if (messageList.value.length === 0) {
        ElMessage.info('暂无消息数据');
      }
    } else {
      ElMessage.error(response.message || '获取消息列表失败');
      messageList.value = [];
      totalCount.value = 0;
    }
  } catch (error: any) {
    console.error('加载消息列表失败:', error);
    ElMessage.error(error.message || '加载消息失败，请重试');
    messageList.value = [];
    totalCount.value = 0;
  } finally {
    loading.value = false;
  }
};

// 搜索事件
const handleSearchInput = (val: string) => {
  filterForm.value.keyword = val;
  currentPage.value = 1;
  loadData();
};

// 状态筛选事件
const handleFilterChange = () => {
  currentPage.value = 1;
  loadData();
};

// 详情弹窗
const handleDetail = (row: SysMessageDTO) => {
  console.log('点击详情，行数据：', row);
  if (!row || !row.messageId) {
    ElMessage.warning('消息数据异常，无法查看详情');
    return;
  }
  // 深拷贝避免原数据污染
  currentDetail.value = JSON.parse(JSON.stringify(row));
  detailDialogVisible.value = true;
};

// 一键已读 - 适配后端返回数字1的情况
const handleMarkAllRead = async () => {
  try {
    loading.value = true;
    const response: BaseApiResponse<number> = await markAllRead();
    
    // 兼容后端返回1（字符串/数字）或标准code
    const isSuccess = [200, 0].includes(response.code || -1) 
      || response.data === 1 
      || response.data === '1';
    
    if (isSuccess) {
      ElMessage.success(`成功标记${response.data || 1}条消息为已读`);
      loadData(); // 刷新列表
    } else {
      ElMessage.error(response.message || '一键已读失败');
    }
  } catch (error: any) {
    console.error('一键已读失败:', error);
    ElMessage.error(error.message || '网络异常，一键已读失败');
  } finally {
    loading.value = false;
  }
};

// 分页事件 - 确保参数为数字
const handlePageChange = (page: number) => {
  currentPage.value = Number(page);
  loadData();
};

const handleSizeChange = (size: number) => {
  pageSize.value = Number(size);
  currentPage.value = 1;
  loadData();
};

// 初始化加载
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.message-list-page {
  padding-bottom: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 80vh;
}
.message-content {
  text-align: left;
  line-height: 1.5;
  color: #606266;
  padding: 4px 0;
}
.message-time {
  text-align: center;
  color: #606266;
  font-size: 14px;
}
.search-section {
  display: flex;
  align-items: center;
  gap: 25px;
  margin: 15px 0;
  padding: 0 5px;
}
.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
.filter-label {
  font-size: 14px;
  color: #333;
  white-space: nowrap;
}
.batch-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
}
.detail-content {
  padding: 10px 0;
}
.detail-item {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
}
.detail-label {
  width: 80px;
  font-weight: 500;
  color: #606266;
  flex-shrink: 0;
}
.detail-value {
  flex: 1;
  word-break: break-all;
  line-height: 1.5;
}
.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}
.text-button {
  color: #1890ff;
  cursor: pointer;
  font-size: 14px;
  padding: 2px 4px;
  white-space: nowrap;
}
.text-button:hover {
  text-decoration: underline;
  background-color: #f0f7ff;
  border-radius: 2px;
}
:deep(.el-table) {
  --el-table-header-text-color: #303133;
  --el-table-row-hover-bg-color: #F0F0F0;
  border-radius: 8px;
  overflow: hidden;
  background-color: #FFFFFF;
}
:deep(.el-table th) {
  background-color: #FAFAFA !important;
  font-weight: 600;
  border-bottom: 1px solid #EEEEEE;
}
:deep(.el-table td) {
  vertical-align: middle;
}
:deep(.el-tag) {
  font-size: 12px;
  font-weight: 500;
  border: none;
  border-radius: 4px;
}
:deep(.el-tag--danger) {
  background-color: #FEF0F0;
  color: #F56C6C;
}
:deep(.el-tag--info) {
  background-color: #F4F4F5;
  color: #909399;
}
@media (max-width: 768px) {
  .message-list-page {
    padding: 10px;
  }
  .search-section {
    flex-wrap: wrap;
    gap: 15px;
  }
  .batch-actions {
    margin-left: 0;
    margin-top: 10px;
    width: 100%;
    justify-content: flex-start;
  }
  :deep(.el-table) {
    font-size: 13px;
  }
  .text-button {
    font-size: 13px;
  }
}
</style>