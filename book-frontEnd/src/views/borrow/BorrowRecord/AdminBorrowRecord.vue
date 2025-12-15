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

    <!-- SimpleTable 组件：传递分页参数+监听事件 -->
    <SimpleTable 
      :data="recordList"
      :columns="columns"
      :total="total"
      :current-page="currentPage"  
      :page-size="pageSize"        
      :loading="loading"
      :show-selection="false"      
      :show-actions="false"        
      :show-index="true"           
      @size-change="handleSizeChange"  
      @current-change="handleCurrentChange"  
    >
      <!-- 书籍信息插槽 -->
      <template #column-bookInfo="{ row }">
        <div class="book-info">
          <img 
            :src="row.bookInfo?.coverUrl || defaultBookCover"
            class="book-cover" 
            :alt="row.bookInfo?.bookName || '书籍封面'"
          />
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
          <img 
            :src="row.userInfo?.avatar || defaultAvatar" 
            class="user-avatar" 
            :alt="row.userInfo?.userName || '用户头像'"
          />
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
    </SimpleTable>
  </div>
</template>

<script setup lang="ts">
import SimpleTable from '@/components/mytable/SimpleTable.vue';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getBorrowRecords } from '@/apis/Record/index';
import type { BaseBorrowRecordDTO, SearchParams } from '@/apis/Record/type';

// 默认图片路径
const defaultBookCover = '@/assets/default.jpg';
const defaultAvatar = '@/assets/default-avatar.png';

// 加载状态
const loading = ref(false);

// 分页核心参数（父组件维护，传递给子组件）
const currentPage = ref(1);    // 当前页码
const pageSize = ref(10);      // 每页条数
const total = ref(0);          // 总记录数

// 搜索参数
const searchParams = ref<SearchParams>({ 
  keyword: '', 
  categoryCode: '',
  operationType: '',
  currentPage: 1,
  pageSize: 10
});

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

// 记录列表数据
const recordList = ref<BaseBorrowRecordDTO[]>([]);

// 表格列配置
const columns = ref([
  { prop: 'bookInfo', label: '书籍名称', width: 220, align: 'left' },
  { prop: 'category', label: '书籍分类', width: 180, align: 'left' },
  { prop: 'userInfo', label: '用户', width: 200, align: 'left' },
  { prop: 'operationType', label: '操作类别', width: 120, align: 'center' },
  { prop: 'operationDate', label: '操作时间', width: 180, align: 'center' },
]);

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

// 获取借阅记录数据
const fetchRecords = async () => {
  try {
    loading.value = true;
    searchParams.value.currentPage = currentPage.value;
    searchParams.value.pageSize = pageSize.value;
    
    const response = await getBorrowRecords(searchParams.value);
    console.log('接口返回数据：', response.data);
    console.log('总条数：', response.data.pageInfo?.total);
    
    if (response.code === 200 && response.data) {
      recordList.value = response.data.records || [];
      total.value = response.data.pageInfo?.total || 0; // 赋值总条数
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

// 分页大小变化事件（子组件触发）
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize;
  currentPage.value = 1; // 页码重置为1
  fetchRecords();
};

// 页码变化事件（子组件触发）
const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage;
  fetchRecords();
};

// 初始化加载数据
fetchRecords();
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

:deep(.el-table th) {
  text-align: left !important;
}

/* 确保分页组件不被父组件样式覆盖 */
:deep(.el-pagination) {
  margin-top: 16px !important;
  text-align: right !important;
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