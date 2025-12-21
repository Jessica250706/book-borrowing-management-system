<template>
  <div class="message-list-page">
    <!-- 页面标题 + 搜索筛选栏 + 批量操作按钮 -->
    <div class="page-header">
      <!-- 搜索和状态筛选组件 -->
      <div class="search-filter-group">
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
      </div>
      <!-- 批量操作按钮 -->
      <div class="batch-actions">
        <el-button 
          type="success" 
          @click="handleMarkAllRead"
        >
          一键已读
        </el-button>
      </div>
    </div>
    
    <!-- 核心表格组件：使用前端分页和筛选 -->
    <Table 
      :data="filteredMessageList"     
      :columns="columns"
      :loading="loading"
      :total="filteredMessageList.length"  
      :actions="customActions"
      :show-selection="false" 
      :show-index="true"
      :show-actions="true"
      :pagination="true"             
      row-key="messageId"  
      @selection-change="handleSelectionChange"
      @action-click="handleActionClick"
    >
      <!-- 消息内容列 -->
      <template #column-messageContent="{ row }">
        <div class="message-content">
          {{ row.messageContent || '' }}
        </div>
      </template>
      
      <!-- 状态列 - 修改为类似借阅页面的样式 -->
      <template #column-statusText="{ row }">
        <el-tag 
          :type="getStatusType(row.statusText)"
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
    
    <!-- 如果总条数为0，显示无数据提示 -->
    <div v-if="!loading && filteredMessageList.length === 0" class="empty-state">
      <el-empty :description="emptyDescription" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import Table from '@/components/mytable/Table.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';
import { getMessageList, markAllRead } from '@/apis/Message/index';
import type { 
  GetMessageListParams, 
  SysMessageDTO,
  BaseApiResponse,
  MessagePageDTO
} from '@/apis/Message/type';

// 分页相关（前端分页）
const loading = ref<boolean>(false);

// 筛选表单
const filterForm = ref({
  status: '',
  keyword: ''
});

// 消息数据（一次性加载所有数据）
const allMessageList = ref<SysMessageDTO[]>([]);

// 自定义操作按钮（Table组件需要）
const customActions = ref([]);

// 表格列配置
const columns = ref([
  { prop: 'messageContent', label: '消息通知', width: 400, align: 'left' },
  { prop: 'statusText', label: '状态', width: 120, align: 'center' },
  { prop: 'sendTime', label: '发送时间', width: 180, align: 'center' },
]);

// 状态标签类型映射 - 类似借阅页面的样式
const getStatusType = (statusText?: string) => {
  if (statusText === '未读') return 'danger';
  if (statusText === '已读') return 'success';
  return 'info';
};

// 计算空状态描述
const emptyDescription = computed(() => {
  if (filterForm.value.keyword || filterForm.value.status) {
    return '未找到符合条件的消息';
  }
  return '暂无消息记录';
});

// 计算未读消息数量
const unreadCount = computed(() => {
  return allMessageList.value.filter(item => item.statusText === '未读').length;
});

// 前端筛选逻辑（参考借阅页面）
const filteredMessageList = computed(() => {
  let filtered = allMessageList.value;
  
  // 按消息内容搜索
  if (filterForm.value.keyword) {
    const keyword = filterForm.value.keyword.toLowerCase();
    filtered = filtered.filter(message => 
      (message.messageContent?.toLowerCase().includes(keyword) || false)
    );
  }
  
  // 按状态筛选
  if (filterForm.value.status) {
    filtered = filtered.filter(message => 
      message.statusText === filterForm.value.status
    );
  }
  
  return filtered;
});

// 格式化时间
const formatDate = (dateStr: string | undefined): string => {
  if (!dateStr) return '未知时间';
  try {
    const date = new Date(dateStr);
    // 添加8小时解决时区问题（北京时间）
    date.setHours(date.getHours());
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  } catch (error) {
    console.error('日期格式化错误:', error);
    return dateStr || '未知时间';
  }
};

// 选择事件处理
const handleSelectionChange = (selection: SysMessageDTO[]) => {
  console.log('选中消息:', selection);
};

// 表格操作按钮点击事件（Table组件必需）
const handleActionClick = (action: string, row: SysMessageDTO) => {
  switch (action) {
    case 'detail':
      handleDetail(row);
      break;
  }
};

// 加载消息列表（一次性加载所有数据）
const loadData = async () => {
  try {
    loading.value = true;
    
    // 构建请求参数 - 不传递分页参数，获取所有数据
    const params: GetMessageListParams = {
      // 移除分页参数
      status: filterForm.value.status || undefined,
      keyword: filterForm.value.keyword || undefined
    };
    
    console.log('消息列表请求参数:', params);
    
    const response: BaseApiResponse<MessagePageDTO> = await getMessageList(params);
    console.log('消息列表接口响应:', response);
    
    // 同时处理code为0和200的情况，符合后端规范
    if ([200, 0].includes(response.code || -1) && response.data) {
      const { records = [] } = response.data;
      
      // 确保列表数据正确赋值
      allMessageList.value = Array.isArray(records) ? records : [];
      
      // 如果搜索无结果，给出提示
      if (allMessageList.value.length === 0 && (params.keyword || params.status)) {
        ElMessage.info('未找到符合条件的消息');
      }
    } else {
      ElMessage.error(response.message || '获取消息列表失败');
      allMessageList.value = [];
    }
  } catch (error: any) {
    console.error('加载消息列表失败:', error);
    ElMessage.error(error.message || '加载失败，请重试');
    allMessageList.value = [];
  } finally {
    loading.value = false;
  }
};

// 搜索事件 - 前端筛选
const handleSearchInput = (val: string) => {
  filterForm.value.keyword = val.trim();
};

// 状态筛选事件 - 前端筛选
const handleFilterChange = () => {
  // 无需额外操作，Table组件会自动处理
};

// 详情弹窗
const handleDetail = async (row: SysMessageDTO) => {
  console.log('查看消息详情，行数据：', row);
  if (!row || !row.messageId) {
    ElMessage.warning('消息数据异常，无法查看详情');
    return;
  }
  
  const messageContent = row.messageContent || '无内容';
  const statusText = row.statusText || '未知状态';
  const sendTime = formatDate(row.sendTime);
  
  // 构建消息详情内容
  const detailContent = `
    ${messageContent}
  `;
  
  await showConfirmDialog({
    title: '消息详情',
    message: detailContent,
    confirmText: '关闭',
    cancelText: '',
    showCancelButton: false,
    width: 500
  });
};

// 一键已读
const handleMarkAllRead = async () => {
  // 检查是否有未读消息
  if (unreadCount.value === 0) {
    ElMessage.info('所有消息已全部已读');
    return;
  }
  
  const isConfirm = await showConfirmDialog({
    title: '一键已读确认',
    message: `确定要将所有${unreadCount.value}条未读消息标记为已读吗？`,
    confirmText: '确定',
    cancelText: '取消'
  });
  
  if (!isConfirm) return;
  
  try {
    loading.value = true;
    const response: BaseApiResponse<number> = await markAllRead();
    
    // 兼容后端返回1（字符串/数字）或标准code
    const isSuccess = [200, 0].includes(response.code || -1) 
      || response.data === 1 
      || response.data === '1';
    
    if (isSuccess) {
      const count = response.data || unreadCount.value;
      ElMessage.success(`成功标记${count}条消息为已读`);
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

// 初始化加载
onMounted(() => {
  loadData();
});
</script>

<style scoped>
/* 保持样式不变 */
.message-list-page {
  padding-bottom: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 80vh;
  background: none;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

.search-filter-group {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #303133;
  white-space: nowrap;
}

/* 消息内容样式 */
.message-content {
  text-align: left;
  line-height: 1.5;
  color: #606266;
  padding: 8px 0;
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.message-time {
  text-align: center;
  color: #606266;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.text-button {
  color: #409EFF;
  cursor: pointer;
  font-size: 14px;
}

.text-button:hover {
  text-decoration: underline;
}

/* 移除独立分页容器的样式 */
</style>