<template>
  <div class="message-list-page">
    <!-- 使用 Table 组件 -->
    <Table 
      :data="currentPageData"    
      :columns="columns" 
      :total="messageList.length"  
      :actions="customActions"
      :current-page="currentPage"
      :page-size="pageSize"
      :show-selection="true" 
      :show-index="true" 
      @selection-change="handleSelectionChange"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
    >
      <!-- 自定义消息内容列 -->
      <template #column-content="{ row }">
        <div class="message-content">
          {{ row.content }}
        </div>
      </template>
      <!-- 自定义状态列 -->
      <template #column-status="{ row }">
        <el-tag 
          :type="row.status === '未读' ? 'danger' : 'info'"
          size="small"
          effect="light"
        >
          {{ row.status }}
        </el-tag>
      </template>
      <!-- 自定义操作列 -->
      <template #actions="{ row }">
        <div class="action-buttons">
          <span class="text-button" @click="handleDetail(row)">详情</span>
          <span class="text-button" @click="handleDelete(row)">删除</span>
          <span class="text-button" @click="handleMarkAsRead(row)" v-if="row.status === '未读'">标记已读</span>
        </div>
      </template>
    </Table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import Table from '@/components/mytable/Table.vue';
import { showConfirmDialog } from '@/components/Dialog/customDialog/CustomDialog.vue';

// 分页相关 - 默认显示20条
const currentPage = ref(1);
const pageSize = ref(20);

// 选中的消息（用于批量操作）
const selectedMessages = ref<any[]>([]);

// 模拟消息数据
const messageList = ref([
  {
    id: 1,
    content: '用户张明已归还《时间简史》，请前往当前归还界面进行二次确认',
    status: '已读',
    time: '2024/01/15 14:30:25'
  },
  {
    id: 2,
    content: '《百年孤独》在2024-01-15 10:20:15成功上架',
    status: '未读',
    time: '2024/01/15 10:20:15'
  },
  {
    id: 3,
    content: '用户李华已归还《设计心理学》，请前往当前归还界面进行二次确认',
    status: '已读',
    time: '2024/01/14 16:45:30'
  },
  {
    id: 4,
    content: '《人类简史》在2024-01-14 09:15:20成功上架',
    status: '未读',
    time: '2024/01/14 09:15:20'
  },
  {
    id: 5,
    content: '系统维护通知：本系统将于2024年1月20日凌晨2:00-4:00进行维护',
    status: '已读',
    time: '2024/01/13 18:00:00'
  },
  {
    id: 6,
    content: '新书《人工智能导论》已成功录入系统，等待上架',
    status: '未读',
    time: '2024/01/13 15:30:45'
  },
  {
    id: 7,
    content: '用户王五已预约《数据结构与算法》，请及时处理',
    status: '未读',
    time: '2024/01/12 11:20:30'
  },
  {
    id: 8,
    content: '《经济学原理》借阅即将到期，请提醒用户及时归还',
    status: '已读',
    time: '2024/01/12 09:15:20'
  },
  {
    id: 9,
    content: '系统备份完成，所有数据已安全存储',
    status: '已读',
    time: '2024/01/11 23:45:10'
  },
  {
    id: 10,
    content: '新用户注册成功，用户名为：user2024',
    status: '未读',
    time: '2024/01/11 16:30:45'
  },
  {
    id: 11,
    content: '图书馆将在本周六举办读书分享会',
    status: '已读',
    time: '2024/01/10 14:20:15'
  },
  {
    id: 12,
    content: '《小王子》库存不足，请及时补充',
    status: '未读',
    time: '2024/01/10 10:10:05'
  }
]);

// 计算当前页数据
const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return messageList.value.slice(start, end);
});

// 表格列配置
const columns = ref([
  { prop: 'content', label: '消息通知', width: 400, align: 'left' },
  { prop: 'status', label: '状态', width: 120, align: 'center' },
  { prop: 'time', label: '通知时间', width: 180, align: 'center' },
]);

// 自定义操作按钮配置
const customActions = ref([
  { name: 'detail', label: '详情', type: 'primary' },
  { name: 'delete', label: '删除', type: 'danger' },
  { name: 'markAsRead', label: '标记已读', type: 'success' }
]);

// 表格多选事件
const handleSelectionChange = (val: any[]) => {
  selectedMessages.value = val;
};

// 消息详情
const handleDetail = (row: any) => {
  ElMessage.info(`查看消息详情：${row.content}`);
};

// 删除消息
const handleDelete = async (row: any) => {
  const isConfirm = await showConfirmDialog({
    title: '删除消息',
    message: `确定要删除这条消息吗？<br>${row.content}`,
    confirmText: '确定删除',
    cancelText: '取消',
    dangerouslyUseHTMLString: true,
    onConfirm: async () => {
      messageList.value = messageList.value.filter(msg => msg.id !== row.id);
      ElMessage.success('消息删除成功');
    }
  });
  if (!isConfirm) return;
};

// 标记为已读
const handleMarkAsRead = (row: any) => {
  row.status = '已读';
  ElMessage.success('消息已标记为已读');
};

// 处理分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page;
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
};
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

/* 操作按钮容器 - 内容居中对齐 */
.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}

/* 蓝色文字按钮样式 */
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

/* 表格样式覆盖 */
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

/* 操作列完全居中 */
:deep(.el-table .el-table__cell:last-child) {
  text-align: center;
}

:deep(.el-table .el-table__cell:last-child .cell) {
  display: flex;
  justify-content: left;
  align-items: center;
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
  
  :deep(.el-table) {
    font-size: 13px;
  }
  
  .text-button {
    font-size: 13px;
  }
  
  .action-buttons {
    gap: 12px;
    flex-wrap: wrap;
  }
}
</style>