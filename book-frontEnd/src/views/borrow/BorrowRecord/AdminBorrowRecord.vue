<template>
  <div class="borrow-record-page">
    <!-- 搜索筛选栏 -->
    <div class="search-filter-group">
      <el-input
        placeholder="请输入书籍名称/作者/用户"
        v-model="searchParams.keyword"
        style="width: 200px"
        @keyup.enter="handleSearch"
      />
      <div class="filter-group">
        <span class="filter-label">书籍分类:</span>
        <el-select
          v-model="searchParams.categoryCode"
          placeholder="所有分类"
          style="width: 180px"
          @change="handleCategoryChange"
        >
          <el-option label="所有分类" value="" />
          <el-option
            v-for="item in categoryOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
      <el-select
        v-model="searchParams.operationType"
        placeholder="所有操作"
        style="width: 150px"
        @change="handleSearch"
      >
        <el-option label="所有操作" value="" />
        <el-option label="预约" value="1" />
        <el-option label="取消预约" value="2" />
        <el-option label="借阅" value="3" />
        <el-option label="续借" value="4" />
        <el-option label="归还" value="5" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- Table 组件：使用后端分页 -->
    <Table 
      v-if="loading || recordList.length > 0"  
      :data="recordList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :actions="customActions"
      :show-selection="false"
      :show-index="true"
      :show-actions="false"
      :pagination="false"
      row-key="logId"
      :server-pagination="true"
      :parent-current-page="currentPage"
      :parent-page-size="pageSize"
      @selection-change="handleSelectionChange"
      @action-click="handleActionClick"
    >
      <!-- 书籍信息插槽 -->
      <template #column-bookInfo="{ row }">
        <div class="book-info">
          <!-- 修改封面显示部分 -->
          <div v-if="row.bookInfo?.coverUrl" class="book-cover-container">
            <img 
              :src="row.bookInfo.coverUrl"
              class="book-cover" 
              :alt="row.bookInfo.bookName || '书籍封面'"
              @error="handleImageError($event, row)"
            />
          </div>
          <Cover v-else />
          <div class="book-detail">
            <div class="book-name">{{ row.bookInfo?.bookName || '未知书籍' }}</div>
            <div class="book-author">作者: {{ row.bookInfo?.author || '未知作者' }}</div>
          </div>
        </div>
      </template>

      <!-- 分类信息插槽 -->
      <template #column-category="{ row }">
        <div class="category-cell">
          {{ getCategoryName(row.bookCategory?.categoryCode) || row.bookCategory?.categoryName || '未知分类' }}
        </div>
      </template>

      <!-- 用户信息插槽 -->
      <template #column-userInfo="{ row }">
        <div class="user-info">
          <div class="user-avatar-container">
            <!-- 有头像时显示图片 -->
            <img 
              v-if="row.userInfo?.avatar"
              :src="row.userInfo.avatar" 
              class="user-avatar" 
              :alt="row.userInfo.userName || '用户头像'"
              @error="handleAvatarError"
            />
            <!-- 无头像时显示首字母 -->
            <div 
              v-else 
              class="default-avatar"
            >
              {{ getAvatarText(row.userInfo?.userName) }}
            </div>
          </div>
          <div class="user-detail">
            <div class="user-name">{{ row.userInfo?.userName || '未知用户' }}</div>
            <div class="user-id">ID: {{ row.userInfo?.uid || '未知ID' }}</div>
          </div>
        </div>
      </template>

      <!-- 操作类型插槽 -->
      <template #column-operationType="{ row }">
        <el-tag
          :type="getOperationTypeClass(row.operationType)"
          effect="light"
        >
          {{ row.operationTypeDesc || getOperationTypeName(row.operationType) }}
        </el-tag>
      </template>

      <!-- 操作时间插槽 -->
      <template #column-operationDate="{ row }">
        <div class="time-cell">
          {{ formatDateTime(row.operationDate) }}
        </div>
      </template>
    </Table>
    
    <!-- 独立分页控件 -->
    <div class="pagination-container" v-show="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="paginationConfig.pageSizes"
        :layout="paginationConfig.layout"
        :total="Number(total)"
        :hide-on-single-page="false"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 如果总条数为0，显示无数据提示 -->
    <div v-if="!loading && recordList.length === 0" class="empty-state">
      <el-empty description="暂无借阅记录" />
    </div>
  </div>
</template>

<script setup lang="ts">
import Table from '@/components/mytable/Table.vue';
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElEmpty } from 'element-plus';
import { getBorrowRecords } from '@/apis/Record/index';
import type { BaseBorrowRecordDTO, SearchParams } from '@/apis/Record/type';
import Cover from '@/components/BookCoverPlaceholder/cover.vue';

// 默认图片路径
const defaultBookCover = '@/assets/default.jpg';

// 加载状态
const loading = ref(false);

// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 分页配置
const paginationConfig = reactive({
  pageSizes: [10, 20, 30, 50],
  layout: "total, sizes, prev, pager, next, jumper" as const
});

// 搜索参数
const searchParams = ref<SearchParams>({ 
  keyword: '', 
  categoryCode: '',
  operationType: '',
  currentPage: 1,
  pageSize: 10
});

// 自定义操作按钮（Table组件需要）
const customActions = ref([]);

// 记录列表数据
const recordList = ref<BaseBorrowRecordDTO[]>([]);

// 书籍分类选项
const categoryOptions = [
  { label: 'A、马克思主义、列宁主义、毛泽东思想、邓小平理论', value: 'A' },
  { label: 'B、哲学、宗教', value: 'B' },
  { label: 'C、社会科学总论', value: 'C' },
  { label: 'D、政治、法律', value: 'D' },
  { label: 'E、军事', value: 'E' },
  { label: 'F、经济', value: 'F' },
  { label: 'G、文化、科学、教育、体育', value: 'G' },
  { label: 'H、语言、文字', value: 'H' },
  { label: 'I、文学', value: 'I' },
  { label: 'J、艺术', value: 'J' },
  { label: 'K、历史、地理', value: 'K' },
  { label: 'N、自然科学总论', value: 'N' },
  { label: 'O、数理科学和化学', value: 'O' },
  { label: 'P、天文学、地球科学', value: 'P' },
  { label: 'Q、生物科学', value: 'Q' },
  { label: 'R、医药、卫生', value: 'R' },
  { label: 'S、农业科学', value: 'S' },
  { label: 'T、工业技术', value: 'T' },
  { label: 'U、交通运输', value: 'U' },
  { label: 'V、航空、航天', value: 'V' },
  { label: 'X、环境科学、安全科学', value: 'X' },
  { label: 'Z、综合性图书', value: 'Z' }
];

// 表格列配置
const columns = ref([
  { prop: 'bookInfo', label: '书籍名称', width: 220, align: 'left' as const },
  { prop: 'category', label: '书籍分类', width: 180, align: 'left' as const },
  { prop: 'userInfo', label: '用户', width: 200, align: 'left' as const },
  { prop: 'operationType', label: '操作类别', width: 120, align: 'center' as const },
  { prop: 'operationDate', label: '操作时间', width: 180, align: 'center' as const },
]);

// 获取头像文字（首字母）
const getAvatarText = (username?: string): string => {
  if (!username) return '?';
  return username.charAt(0).toUpperCase();
};

// 操作类型名称映射
const getOperationTypeName = (type?: number): string => {
  const typeMap: Record<number, string> = {
    1: '预约',
    2: '取消预约',
    3: '借阅',
    4: '续借',
    5: '归还'
  };
  return typeMap[type || 0] || '未知操作';
};

// 操作类型样式映射
const getOperationTypeClass = (type?: number): string => {
  const typeMap: Record<number, string> = {
    1: 'warning',
    2: 'danger',
    3: 'success',
    4: 'primary',
    5: 'success'
  };
  return typeMap[type || 0] || 'info';
};

// 根据分类代码获取分类名称
const getCategoryName = (code?: string): string => {
  const category = categoryOptions.find(item => item.value === code);
  return category?.label || '';
};

// 格式化日期时间
const formatDateTime = (dateTime?: string): string => {
  if (!dateTime) return '未知时间';
  
  try {
    const date = new Date(dateTime);
    date.setHours(date.getHours() + 8); // 添加8小时（北京时间）
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  } catch (error) {
    console.error('日期格式化错误:', error);
    return dateTime;
  }
};

// 处理图片加载失败
const handleImageError = (event: Event, row: BaseBorrowRecordDTO) => {
  const img = event.target as HTMLImageElement;
  // 移除图片元素，显示占位组件
  img.parentElement?.removeChild(img);
  const placeholder = document.createElement('div');
  placeholder.className = 'cover-placeholder';
  img.parentElement?.appendChild(placeholder);
  // 可以在这里设置一个标记，避免重复处理
  row.bookInfo = { ...row.bookInfo, coverUrl: undefined };
};

// 处理头像加载失败
const handleAvatarError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  img.style.display = 'none';
  const parent = img.parentElement;
  if (parent) {
    const username = img.alt || '';
    const defaultAvatar = document.createElement('div');
    defaultAvatar.className = 'default-avatar';
    defaultAvatar.textContent = getAvatarText(username);
    parent.appendChild(defaultAvatar);
  }
};

// 分页事件处理
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchRecords();
};

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage;
  fetchRecords();
};

// 表格事件处理
const handleSelectionChange = (selection: BaseBorrowRecordDTO[]) => {
  console.log('已选择:', selection.length, '条记录');
};

const handleActionClick = (action: string, row: BaseBorrowRecordDTO) => {
  console.log('操作点击:', action, row);
};

// 获取借阅记录数据
const fetchRecords = async () => {
  try {
    loading.value = true;
    
    const requestParams: SearchParams = {
      keyword: searchParams.value.keyword.trim() || undefined,
      categoryCode: searchParams.value.categoryCode || undefined,
      operationType: searchParams.value.operationType || undefined,
      currentPage: currentPage.value,
      pageSize: pageSize.value
    };
    
    console.log('请求参数:', requestParams);
    const response = await getBorrowRecords(requestParams);
    console.log('接口返回数据：', response);
    
    if (response.code === 200 && response.data) {
      recordList.value = response.data.records || [];
      
      // 同步总条数
      const totalFromApi = response.data.pageInfo?.total;
      if (typeof totalFromApi === 'string') {
        total.value = parseInt(totalFromApi, 10) || 0;
      } else {
        total.value = totalFromApi || 0;
      }
      
      console.log('设置 total.value:', total.value);
      console.log('记录数量:', recordList.value.length);
    } else {
      ElMessage.error(response.message || '获取借阅记录失败');
      recordList.value = [];
      total.value = 0;
    }
  } catch (error: any) {
    console.error('获取借阅记录失败:', error);
    ElMessage.error(error.message || '获取借阅记录失败，请重试');
    recordList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 搜索事件
const handleSearch = () => {
  currentPage.value = 1;
  fetchRecords();
};

// 分类筛选事件
const handleCategoryChange = (val: string) => {
  searchParams.value.categoryCode = val;
  currentPage.value = 1;
  fetchRecords();
};

// 初始化加载数据
onMounted(() => {
  fetchRecords();
});
</script>

<style scoped>
.borrow-record-page {
  padding-bottom: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 80vh;
}

.search-filter-group {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
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

.book-info {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.book-cover {
  width: 50px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
  background-color: #f5f5f5;
}

.book-detail {
  flex: 1;
  min-width: 0;
}

.book-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.book-author {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-detail {
  flex: 1;
  min-width: 0;
}

.user-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.user-id {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.category-cell {
  padding: 8px 0;
  text-align: left;
}

.time-cell {
  white-space: nowrap;
}

/* 空状态样式，增加padding-top实现下移 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px; /* 保持最小高度，确保垂直居中 */
  padding-top: 100px; /* 关键：增加顶部间距，实现下移（数值可根据需求调整） */
  margin-top: 10px; /* 可选：额外增加与表格区域的间距 */
}

/* 分页容器样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 10px 0;
}

.pagination-container :deep(.el-pagination) {
  font-size: 14px;
}

.pagination-container :deep(.el-pagination__total),
.pagination-container :deep(.el-pagination__jump) {
  margin-right: 10px;
}

.pagination-container :deep(.el-pagination .btn-prev),
.pagination-container :deep(.el-pagination .btn-next) {
  border: 1px solid #dcdfe6;
  border-radius: 3px;
}

.pagination-container :deep(.el-pager li) {
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  margin: 0 4px;
}

.pagination-container :deep(.el-pager li.active) {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

/* 表格样式 */
:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
}

:deep(.el-table .cell) {
  padding: 0 12px;
}

:deep(.el-table__header .cell) {
  text-align: center;
}

:deep(.el-table th:last-child .cell) {
  text-align: left;
  padding-left: 22px;
}

/* 分类列左对齐 */
:deep(.el-table .el-table__cell:has(.category-cell)) {
  text-align: left;
}

/* 用户头像样式 */
.user-avatar-container {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
}

.default-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #409EFF;
  color: white;
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .search-filter-group {
    flex-direction: column;
    align-items: flex-start;
  }
  .search-filter-group > * {
    width: 100% !important;
    margin-bottom: 10px;
  }
  
  :deep(.el-table__column) {
    &[width="220"], &[width="200"] {
      width: 180px !important;
    }
    &[width="180"] {
      width: 150px !important;
    }
    &[width="120"] {
      width: 100px !important;
    }
  }
}
</style>