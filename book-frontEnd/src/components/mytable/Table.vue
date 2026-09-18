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
          <!-- 支持服务器端分页模式 -->
          <template v-if="serverPagination">
            {{ (parentCurrentPage - 1) * parentPageSize + $index + 1 }}
          </template>
          <!-- 默认前端分页模式 -->
          <template v-else>
            {{ (currentPage - 1) * pageSize + $index + 1 }}
          </template>
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
          <!-- 优先使用插槽 -->
          <slot
            v-if="$slots[`column-${column.prop}`]"
            :name="`column-${column.prop}`"
            :row="row"
            :index="$index"
          />
          <!-- 其次使用自定义渲染函数 -->
          <template v-else-if="column.render">
            <component :is="column.render(row)" />
          </template>
          <!-- 最后使用默认显示 -->
          <template v-else>
            {{ row[column.prop] }}
          </template>
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column
        v-if="showActions && showOperations"
        label="操作"
        :width="actionsWidth"
        align="center"
        fixed="right"
      >
        <template #default="{ row, $index }">
          <!-- 使用作用域插槽的方式传入操作按钮 -->
          <slot name="actions" :row="row" :index="$index">
            <!-- 默认操作按钮 -->
            <el-button
              v-for="action in effectiveOperationsActions"
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

    <!-- 分页 -->
    <div v-if="pagination && !serverPagination" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
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
import { ref, computed, watch } from 'vue';

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
  
  // 是否显示操作列，默认显示
  showOperations?: boolean;
  
  // 分页
  pagination?: boolean;
  total?: number;
  pageConfig?: PageConfig;
  
  // 操作
  actions?: ActionConfig[];
  operationsActions?: ActionConfig[]; 
  actionsWidth?: string;
  
  // 服务器端分页模式（新增）
  serverPagination?: boolean;
  parentCurrentPage?: number;
  parentPageSize?: number;
}

// Props
const props = withDefaults(defineProps<Props>(), {
  loading: false,
  rowKey: 'id',
  showSelection: true,
  showIndex: true,
  showActions: true,
  showOperations: true, 
  pagination: true,
  total: 0,
  pageConfig: () => ({
    pageSizes: [10, 20, 50, 100],
    layout: 'total, sizes, prev, pager, next, jumper'
  }),
  actions: () => [
    { name: 'detail', label: '详情', type: 'primary' },
    { name: 'edit', label: '编辑', type: 'success' },
    { name: 'delete', label: '删除', type: 'danger' }
  ],
  operationsActions: () => [], 
  actionsWidth: '240px',
  serverPagination: false,
  parentCurrentPage: 1,
  parentPageSize: 10
});

// Emits
const emit = defineEmits<{
  'selection-change': [selection: any[]];
  'size-change': [size: number];
  'current-change': [page: number];
  'action-click': [action: string, row: any];
}>();

// 分页数据（仅用于前端分页模式）
const currentPage = ref(1);
const pageSize = ref(props.pageConfig.pageSizes?.[0] || 10);

// 计算属性
const tableData = computed(() => {
  // 服务器端分页模式：直接返回数据
  if (props.serverPagination) return props.data;
  
  // 前端分页模式：进行分页处理
  if (!props.pagination) return props.data;
  
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return props.data.slice(start, end);
});

const pageSizes = computed(() => props.pageConfig.pageSizes || [10, 20, 50, 100]);
const pageLayout = computed(() => props.pageConfig.layout || 'total, sizes, prev, pager, next, jumper');

const effectiveOperationsActions = computed(() => {
  return props.operationsActions && props.operationsActions.length > 0 
    ? props.operationsActions 
    : props.actions;
});

// 事件处理
const handleSelectionChange = (selection: any[]) => {
  emit('selection-change', selection);
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  emit('size-change', size);
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  emit('current-change', page);
};

const handleActionClick = (action: string, row: any) => {
  emit('action-click', action, row);
};

// 监听数据变化重置页码（仅前端分页模式）
watch(() => props.data, () => {
  if (!props.serverPagination) {
    currentPage.value = 1;
  }
});
</script>

<style scoped>
.book-table-container {
  width: 100%;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  border-collapse: collapse !important;
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
</style>
