<template>
  <div class="book-table-container">
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      :row-key="rowKey"
      table-layout="fixed"
      @selection-change="handleSelectionChange"
    >
      <!-- 选择列 -->
      <el-table-column
        v-if="showSelection"
        type="selection"
        width="55"
        align="center"
      />
      
      <!-- 序号列 -->
      <el-table-column
        v-if="showIndex"
        label="序号"
        width="80"
        align="center"
      >
        <template #default="{ $index }">
          {{ (internalCurrentPage - 1) * internalPageSize + $index + 1 }}
        </template>
      </el-table-column>
      
      <!-- 动态列 -->
      <el-table-column
        v-for="column in columns"
        :key="column.prop"
        :label="column.label"
        :width="column.width"
        :align="column.align || 'center'"
        :fixed="column.fixed"
      >
        <template #default="{ row, $index }">
          <slot
            v-if="$slots[`column-${column.prop}`]"
            :name="`column-${column.prop}`"
            :row="row"
            :index="$index"
          />
          <template v-else-if="column.render">
            <component :is="column.render(row)" />
          </template>
          <template v-else>
            {{ row[column.prop] }}
          </template>
        </template>
      </el-table-column>
      
      <!-- 操作列 -->
      <el-table-column
        v-if="showActions"
        label="操作"
        :width="actionsWidth"
        align="center"
        fixed="right"
      >
        <template #default="{ row, $index }">
          <slot name="actions" :row="row" :index="$index">
            <el-button
              v-for="action in actions"
              :key="action.name"
              :type="action.type || 'primary'"
              :size="action.size || 'small'"
              @click="handleActionClick(action.name, row)"
            >
              {{ action.label }}
            </el-button>
          </slot>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页：修复 v-model 报错，用属性绑定+事件 -->
    <div v-if="pagination && total > 0" class="pagination-container">
      <el-pagination
        :current-page="internalCurrentPage"
        :page-size="internalPageSize"
        :page-sizes="pageSizes"
        :layout="pageLayout"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';

// 类型定义
interface TableColumn {
  prop: string;
  label: string;
  width?: string | number;
  align?: 'left' | 'center' | 'right';
  fixed?: 'left' | 'right';
  render?: (row: any) => any;
}
interface ActionConfig {
  name: string;
  label: string;
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info';
  size?: 'large' | 'default' | 'small';
}
interface PageConfig {
  pageSizes?: number[];
  layout?: string;
}
interface Props {
  // 数据
  data: any[];
  loading?: boolean;
  rowKey?: string;
  
  // 列配置
  columns: TableColumn[];
  showSelection?: boolean;
  showIndex?: boolean;
  showActions?: boolean;
  
  // 分页（父组件传递的参数）
  pagination?: boolean;
  total?: number;
  pageConfig?: PageConfig;
  currentPage?: number;  // 可选，父组件传递初始页码
  pageSize?: number;     // 可选，父组件传递初始页大小
  
  // 操作
  actions?: ActionConfig[];
  actionsWidth?: string;
}

// Props 默认值配置
const props = withDefaults(defineProps<Props>(), {
  loading: false,
  rowKey: 'id',
  showSelection: true,
  showIndex: true,
  showActions: true,
  pagination: true,
  total: 0,
  currentPage: 1,
  pageSize: 10,
  pageConfig: () => ({
    pageSizes: [10, 20, 50, 100],
    layout: 'total, sizes, prev, pager, next, jumper'
  }),
  actions: () => [
    { name: 'detail', label: '详情', type: 'primary' },
    { name: 'edit', label: '编辑', type: 'success' },
    { name: 'delete', label: '删除', type: 'danger' }
  ],
  actionsWidth: '240px'
});

// Emits：向父组件传递分页变化事件
const emit = defineEmits<{
  'selection-change': [selection: any[]];
  'size-change': [size: number];    // 页大小变化
  'current-change': [page: number]; // 页码变化
  'action-click': [action: string, row: any];
}>();

// 组件内部维护分页状态（避免直接修改 props）
const internalCurrentPage = ref(props.currentPage);
const internalPageSize = ref(props.pageSize);

// 监听父组件传递的分页参数变化，同步到内部状态
onMounted(() => {
  internalCurrentPage.value = props.currentPage || 1;
  internalPageSize.value = props.pageSize || 10;
});

// 计算属性：分页切片数据
const tableData = computed(() => {
  if (!props.pagination) return props.data;
  const start = (internalCurrentPage.value - 1) * internalPageSize.value;
  const end = start + internalPageSize.value;
  return props.data.slice(start, end);
});

// 分页配置计算属性
const pageSizes = computed(() => props.pageConfig?.pageSizes || [10, 20, 50, 100]);
const pageLayout = computed(() => props.pageConfig?.layout || 'total, sizes, prev, pager, next, jumper');

// 事件处理
const handleSelectionChange = (selection: any[]) => {
  emit('selection-change', selection);
};

// 页大小变化：触发父组件事件
const handleSizeChange = (size: number) => {
  internalPageSize.value = size;
  emit('size-change', size);
};

// 页码变化：触发父组件事件
const handleCurrentChange = (page: number) => {
  internalCurrentPage.value = page;
  emit('current-change', page);
};

const handleActionClick = (action: string, row: any) => {
  emit('action-click', action, row);
};

</script>

<style scoped>
.book-table-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px; /* 确保表格和分页有间距 */
}

:deep(.el-table) {
  border-collapse: collapse !important;
  width: 100% !important;
}

:deep(.el-table__fixed-right),
:deep(.el-table__fixed-left) {
  height: 100% !important;
  box-shadow: none !important;
}

:deep(.el-table__header),
:deep(.el-table__body) {
  width: 100% !important;
}

:deep(.el-table__cell) {
  padding: 8px 0 !important;
}

/* 分页样式：确保显示在底部右侧 */
.pagination-container {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
  background: #fff; /* 避免被表格背景覆盖 */
}

:deep(.el-pagination) {
  display: inline-flex !important;
  opacity: 1 !important;
  z-index: 10;
  --el-pagination-text-color: #303133;
  --el-pagination-hover-color: #1890ff;
}
</style>