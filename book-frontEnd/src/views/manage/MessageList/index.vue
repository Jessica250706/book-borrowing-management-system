<template>
  <div class="message-list-page">
    <!-- 筛选和批量操作区域（仅保留后端支持的功能） -->
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
      
      <!-- 仅保留后端支持的“一键已读”按钮 -->
      <div class="batch-actions">
        <el-button 
          type="success" 
          @click="handleMarkAllRead"
        >
          一键已读
        </el-button>
      </div>
    </div>

    <!-- 表格组件（移除未支持的操作） -->
    <Table 
      :data="currentPageData"    
      :columns="columns" 
      :total="filteredList.length"  
      :current-page="currentPage"
      :page-size="pageSize"
      :show-index="true" 
      :loading="loading"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
    >
      <!-- 消息内容列 -->
      <template #column-content="{ row }">
        <div class="message-content">
          {{ row.content }}
        </div>
      </template>
      <!-- 状态列 -->
      <template #column-status="{ row }">
        <el-tag 
          :type="row.status === '未读' ? 'danger' : 'info'"
          size="small"
          effect="light"
        >
          {{ row.status }}
        </el-tag>
      </template>
      <!-- 操作列（仅保留“详情”） -->
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
          <span class="detail-value">{{ currentDetail.content }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态：</span>
          <span class="detail-value">
            <el-tag 
              :type="currentDetail.status === '未读' ? 'danger' : 'info'"
              size="small"
              effect="light"
            >
              {{ currentDetail.status }}
            </el-tag>
          </span>
        </div>
        <div class="detail-item">
          <span class="detail-label">时间：</span>
          <span class="detail-value">{{ currentDetail.time }}</span>
        </div>
      </div>
    </CustomDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import Table from '@/components/mytable/Table.vue';
import CustomDialog from '@/components/Dialog/customDialog/CustomDialog.vue';
import BookSearchInput from '@/components/BookScreen/BookSearchInput.vue';
// 仅保留后端已支持的接口
import { getMessageList, markAllRead } from '@/apis/Message';

// 分页相关
const currentPage = ref(1);
const pageSize = ref(20);
const loading = ref(false);

// 筛选表单
const filterForm = ref({
  status: '',
  keyword: ''
});

// 消息数据
const messageList = ref<any[]>([]);

// 详情对话框
const detailDialogVisible = ref(false);
const currentDetail = ref<any>({});

// 过滤后的消息列表
const filteredList = computed(() => {
  return messageList.value.filter(item => {
    if (filterForm.value.status && item.status !== filterForm.value.status) return false;
    if (filterForm.value.keyword && !item.content.includes(filterForm.value.keyword)) return false;
    return true;
  });
});

// 当前页数据
const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredList.value.slice(start, end);
});

// 表格列配置（简化）
const columns = ref([
  { prop: 'content', label: '消息通知', width: 400, align: 'left' },
  { prop: 'status', label: '状态', width: 120, align: 'center' },
  { prop: 'time', label: '通知时间', width: 180, align: 'center' },
]);

// 加载消息列表（仅保留后端支持的接口）
const loadData = async () => {
  try {
    loading.value = true;
    const params = {
      currentPage: currentPage.value,
      pageSize: pageSize.value,
      status: filterForm.value.status || undefined,
      keyword: filterForm.value.keyword || undefined
    };
    const response = await getMessageList(params);
    if (response.code === 200) {
      messageList.value = response.data.records || [];
    } else {
      ElMessage.error(response.message || '获取消息列表失败');
    }
  } catch (error: any) {
    console.error('加载消息失败:', error);
    ElMessage.error(error.message || '加载消息失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 筛选事件
const handleSearchInput = (val: string) => {
  filterForm.value.keyword = val;
  currentPage.value = 1;
  loadData();
};

const handleFilterChange = () => {
  currentPage.value = 1;
  loadData();
};

// 详情对话框
const handleDetail = (row: any) => {
  currentDetail.value = { ...row };
  detailDialogVisible.value = true;
};

// 一键已读（后端支持的功能）
const handleMarkAllRead = async () => {
  try {
    await markAllRead();
    // 刷新消息列表，更新状态
    loadData();
    ElMessage.success('所有消息已标记为已读');
  } catch (error: any) {
    ElMessage.error(error.message || '一键已读失败');
  }
};

// 分页事件
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadData();
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  loadData();
};

// 初始化加载
onMounted(() => {
  loadData();
});
</script>

<style scoped>
/* 筛选和批量操作区域 */
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

/* 批量操作按钮区域（靠右） */
.batch-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
}

/* 详情对话框样式 */
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

/* 消息列表基础样式 */
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

/* 表格样式 */
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

/* 响应式适配 */
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