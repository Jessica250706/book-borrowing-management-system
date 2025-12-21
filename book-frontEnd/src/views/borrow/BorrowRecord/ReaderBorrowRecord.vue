<template>
  <div class="borrow-record-page">
    <!-- 搜索筛选栏 -->
    <div class="search-filter-group">
      <el-input
        placeholder="请输入书籍名称/作者/用户名"
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
          @change="handleSearch"
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
      <el-button @click="handleReset">清空</el-button>
    </div>

    <!-- Table 组件，使用前端分页 -->
    <Table 
      v-if="loading || filteredRecords.length > 0"  
      :data="filteredRecords"  
      :columns="columns"
      :total="filteredTotal"   
      :loading="loading"
      :show-selection="false"      
      :show-actions="false"        
      :show-index="true"           
      :pagination="true"  
      row-key="logId"  
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

      <!-- 操作类型插槽 -->
      <template #column-operationType="{ row }">
        <el-tag
          :type="getOperationTypeClass(row.operationType)"
          effect="light"
        >
          {{ row.operationTypeDesc || getOperationTypeName(row.operationType) }}
        </el-tag>
      </template>

      <!-- 操作时间插槽：添加时间格式化 -->
      <template #column-operationDate="{ row }">
        <div class="time-cell">
          {{ formatDateTime(row.operationDate) }}
        </div>
      </template>
    </Table>
    
    <!-- 无数据提示 -->
    <div v-if="!loading && allRecords.length === 0" class="empty-state">
      <el-empty description="暂无借阅记录" />
    </div>
    
    <!-- 搜索无结果提示 -->
    <div v-if="!loading && allRecords.length > 0 && filteredRecords.length === 0" class="empty-state">
      <el-empty description="未找到符合条件的记录" />
    </div>
  </div>
</template>

<script setup lang="ts">
import Table from '@/components/mytable/Table.vue';  
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getBorrowRecords } from '@/apis/Record/index';
import type { BaseBorrowRecordDTO, SearchParams } from '@/apis/Record/type';
import Cover from '@/components/BookCoverPlaceholder/cover.vue';

// 默认图片路径
const defaultBookCover = '@/assets/default.jpg';

// 加载状态
const loading = ref(false);

// 存储所有原始数据（一次性获取）
const allRecords = ref<BaseBorrowRecordDTO[]>([]);
// 筛选后的数据
const filteredRecords = computed(() => {
  let result = [...allRecords.value];
  
  // 关键词筛选
  if (searchParams.value.keyword?.trim()) {
    const keyword = searchParams.value.keyword.trim().toLowerCase();
    result = result.filter(record => {
      // 搜索书籍名称
      const bookName = record.bookInfo?.bookName?.toLowerCase() || '';
      // 搜索作者
      const author = record.bookInfo?.author?.toLowerCase() || '';
      // 搜索用户名
      const userName = record.userInfo?.userName?.toLowerCase() || '';
      
      return bookName.includes(keyword) || 
             author.includes(keyword) || 
             userName.includes(keyword);
    });
  }
  
  // 分类筛选
  if (searchParams.value.categoryCode) {
    result = result.filter(record => 
      record.bookCategory?.categoryCode === searchParams.value.categoryCode
    );
  }
  
  // 操作类型筛选
  if (searchParams.value.operationType) {
    const operationType = parseInt(searchParams.value.operationType);
    result = result.filter(record => 
      record.operationType === operationType
    );
  }
  
  return result;
});

// 筛选后的记录总数
const filteredTotal = computed(() => filteredRecords.value.length);

// 搜索参数
const searchParams = ref<SearchParams>({ 
  keyword: '', 
  categoryCode: '',
  operationType: '',
  currentPage: 1,  // 注意：现在这个currentPage是给Table内部用的
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

// 表格列配置
const columns = ref([
  { prop: 'bookInfo', label: '书籍名称', width: 220, align: 'left' },
  { prop: 'category', label: '书籍分类', width: 180, align: 'left' },
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

// 格式化日期时间（添加8小时解决时区问题）
const formatDateTime = (dateTime?: string): string => {
  if (!dateTime) return '未知时间';
  
  try {
    const date = new Date(dateTime);
    // 临时解决方案：添加8小时（北京时间）
    date.setHours(date.getHours() + 8);
    
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


// 获取所有借阅记录数据（一次性获取）
const fetchAllRecords = async () => {
  try {
    loading.value = true;
    
    // 不传搜索参数，获取所有数据
    const params: SearchParams = {
      currentPage: 1,
      pageSize: 1000,  // 获取足够多的数据，假设不会超过1000条
      keyword: undefined,
      categoryCode: undefined,
      operationType: undefined
    };
    
    console.log('获取所有借阅记录...');
    const response = await getBorrowRecords(params);
    
    if (response.code === 200 && response.data) {
      allRecords.value = response.data.records || [];
      console.log(`成功获取 ${allRecords.value.length} 条记录`);
      
      // 确保有用户信息
      allRecords.value.forEach(record => {
        if (!record.userInfo) {
          record.userInfo = {
            userId: 0,
            userName: '未知用户',
            uid: '未知ID'
          };
        }
      });
    } else {
      ElMessage.error(response.message || '获取借阅记录失败');
      allRecords.value = [];
    }
  } catch (error: any) {
    console.error('获取借阅记录失败:', error);
    ElMessage.error(error.message || '获取借阅记录失败，请重试');
    allRecords.value = [];
  } finally {
    loading.value = false;
  }
};

// 搜索事件（前端筛选）
const handleSearch = () => {
  // 前端筛选，不需要重新请求数据
  console.log('执行前端筛选:', searchParams.value);
  
  // 显示筛选结果信息
  if (searchParams.value.keyword?.trim() || 
      searchParams.value.categoryCode || 
      searchParams.value.operationType) {
    ElMessage.success({
      message: `找到 ${filteredTotal.value} 条记录`,
      duration: 2000
    });
  }
};

// 清空搜索
const handleReset = () => {
  searchParams.value = {
    keyword: '', 
    categoryCode: '',
    operationType: '',
    currentPage: 1,
    pageSize: 10
  };
  ElMessage.info('已清空筛选条件');
};

// 初始化加载所有数据
fetchAllRecords();
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

@media (max-width: 768px) {
  .search-filter-group {
    flex-direction: column;
    align-items: flex-start;
  }
  .search-filter-group > * {
    width: 100% !important;
    margin-bottom: 10px;
  }
}
</style>