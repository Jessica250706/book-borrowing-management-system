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
          {{ paginationMode === 'frontend' 
            ? (internalCurrentPage - 1) * internalPageSize + $index + 1
            : (currentPage - 1) * pageSize + $index + 1
          }}
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
        v-if="showActions"
        label="操作"
        :width="actionsWidth"
        align="center"
        fixed="right"
      >
        <template #default="{ row, $index }">
          <slot name="actions" :row="row" :index="$index">
            <!-- 默认操作按钮 -->
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

    <!-- 分页 -->
    <div v-if="showPagination && total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="internalCurrentPage"
        v-model:page-size="internalPageSize"
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
  
  // 分页配置
  paginationMode?: 'frontend' | 'backend'; // 新增：分页模式
  currentPage?: number;   // 当前页码（后端分页时由父组件控制）
  pageSize?: number;      // 每页条数（后端分页时由父组件控制）
  total?: number;         // 总条数
  showPagination?: boolean; // 是否显示分页
  pageConfig?: PageConfig;
  
  // 操作
  actions?: ActionConfig[];
  actionsWidth?: string;
}

// Props
const props = withDefaults(defineProps<Props>(), {
  loading: false,
  rowKey: 'id',
  showSelection: true,
  showIndex: true,
  showActions: true,
  paginationMode: 'frontend', // 默认前端分页
  currentPage: 1,
  pageSize: 10,
  total: 0,
  showPagination: true,
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

// Emits
const emit = defineEmits<{
  'selection-change': [selection: any[]];
  'size-change': [size: number];
  'current-change': [page: number];
  'action-click': [action: string, row: any];
}>();

// 内部分页变量
const internalCurrentPage = ref(1);
const internalPageSize = ref(props.pageConfig.pageSizes?.[0] || 10);

// 计算属性：根据分页模式返回数据
const tableData = computed(() => {
  // 如果不显示分页，直接返回所有数据
  if (!props.showPagination) {
    return props.data;
  }
  
  // 前端分页模式：在组件内部进行数据切片
  if (props.paginationMode === 'frontend') {
    const start = (internalCurrentPage.value - 1) * internalPageSize.value;
    const end = start + internalPageSize.value;
    return props.data.slice(start, end);
  }
  
  // 后端分页模式：直接返回所有数据（由后端分页）
  return props.data;
});

// 计算总条数
const total = computed(() => {
  // 前端分页模式：使用数据长度
  if (props.paginationMode === 'frontend') {
    return props.data.length;
  }
  // 后端分页模式：使用传入的total
  return props.total || 0;
});

const pageSizes = computed(() => props.pageConfig.pageSizes || [10, 20, 50, 100]);
const pageLayout = computed(() => props.pageConfig.layout || 'total, sizes, prev, pager, next, jumper');

// 事件处理
const handleSelectionChange = (selection: any[]) => {
  emit('selection-change', selection);
};

const handleSizeChange = (size: number) => {
  internalPageSize.value = size;
  
  // 后端分页模式时，通知父组件
  if (props.paginationMode === 'backend') {
    emit('size-change', size);
  }
};

const handleCurrentChange = (page: number) => {
  internalCurrentPage.value = page;
  
  // 后端分页模式时，通知父组件
  if (props.paginationMode === 'backend') {
    emit('current-change', page);
  }
};

const handleActionClick = (action: string, row: any) => {
  emit('action-click', action, row);
};

// 监听数据变化重置页码（仅前端分页）
watch(() => props.data, () => {
  if (props.paginationMode === 'frontend') {
    internalCurrentPage.value = 1;
  }
});

// 监听外部传入的分页参数变化（后端分页模式）
watch(() => props.currentPage, (val) => {
  if (props.paginationMode === 'backend' && val) {
    internalCurrentPage.value = val;
  }
});

watch(() => props.pageSize, (val) => {
  if (props.paginationMode === 'backend' && val) {
    internalPageSize.value = val;
  }
});

// 初始化时设置分页参数
watch(() => props.paginationMode, () => {
  if (props.paginationMode === 'backend') {
    internalCurrentPage.value = props.currentPage;
    internalPageSize.value = props.pageSize;
  }
}, { immediate: true });
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