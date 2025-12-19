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

    <!-- 分页 - 强制显示（先测试能否显示） -->
    <div class="pagination-container" v-if="forceShowPagination">
      <div style="margin-bottom: 10px; color: red; font-weight: bold;">
        调试信息：total={{ totalValue }}, showPagination={{ props.showPagination }}, forceShowPagination={{ forceShowPagination }}
      </div>
      <el-pagination
        :current-page="currentPageValue"
        :page-size="pageSizeValue"
        :page-sizes="pageSizes"
        :layout="pageLayout"
        :total="totalValue"
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
  
  // 分页配置
  paginationMode?: 'frontend' | 'backend';
  currentPage?: number;
  pageSize?: number;
  total?: number;
  showPagination?: boolean;
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
  paginationMode: 'frontend',
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

// 计算属性
const totalValue = computed(() => {
  if (props.paginationMode === 'frontend') {
    return props.data.length;
  }
  return props.total || 0;
});

const currentPageValue = computed(() => {
  return props.paginationMode === 'backend' ? props.currentPage : internalCurrentPage.value;
});

const pageSizeValue = computed(() => {
  return props.paginationMode === 'backend' ? props.pageSize : internalPageSize.value;
});

// 关键修复：强制显示分页进行测试
const forceShowPagination = computed(() => {
  console.log('SimpleTable分页条件检查:', {
    showPagination: props.showPagination,
    totalValue: totalValue.value,
    dataLength: props.data.length,
    paginationMode: props.paginationMode
  });
  
  // 先强制显示，看看分页控件能否渲染出来
  return true; // 强制返回true测试
  
  // 如果上面能显示，再改为正常逻辑：
  // return props.showPagination && totalValue.value > 0;
});

// 计算表格数据
const tableData = computed(() => {
  if (!props.showPagination) {
    return props.data;
  }
  
  if (props.paginationMode === 'frontend') {
    const start = (internalCurrentPage.value - 1) * internalPageSize.value;
    const end = start + internalPageSize.value;
    return props.data.slice(start, end);
  }
  
  return props.data;
});

const pageSizes = computed(() => props.pageConfig.pageSizes || [10, 20, 50, 100]);
const pageLayout = computed(() => props.pageConfig.layout || 'total, sizes, prev, pager, next, jumper');

// 事件处理
const handleSelectionChange = (selection: any[]) => {
  emit('selection-change', selection);
};

const handleSizeChange = (size: number) => {
  console.log('SimpleTable: 每页条数变化', size);
  
  if (props.paginationMode === 'backend') {
    emit('size-change', size);
  } else {
    internalPageSize.value = size;
    internalCurrentPage.value = 1;
  }
};

const handleCurrentChange = (page: number) => {
  console.log('SimpleTable: 当前页变化', page);
  
  if (props.paginationMode === 'backend') {
    emit('current-change', page);
  } else {
    internalCurrentPage.value = page;
  }
};

const handleActionClick = (action: string, row: any) => {
  emit('action-click', action, row);
};

// 组件挂载时打印调试信息
onMounted(() => {
  console.log('SimpleTable组件已挂载', {
    props: {
      total: props.total,
      currentPage: props.currentPage,
      pageSize: props.pageSize,
      showPagination: props.showPagination,
      paginationMode: props.paginationMode
    },
    computed: {
      totalValue: totalValue.value,
      forceShowPagination: forceShowPagination.value
    }
  });
});

// 监听props变化
watch(() => props.total, (newVal) => {
  console.log('SimpleTable: total变化', newVal);
}, { immediate: true });

watch(() => props.showPagination, (newVal) => {
  console.log('SimpleTable: showPagination变化', newVal);
}, { immediate: true });
</script>

<style scoped>
.book-table-container {
  width: 100%;
  min-height: 400px; /* 确保有足够高度 */
}

.pagination-container {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 确保el-pagination显示 */
:deep(.el-pagination) {
  display: flex !important;
  visibility: visible !important;
  opacity: 1 !important;
}

/* 确保分页按钮显示 */
:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination .number) {
  display: inline-block !important;
  visibility: visible !important;
}

/* 确保选择器显示 */
:deep(.el-pagination .el-select) {
  display: inline-block !important;
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