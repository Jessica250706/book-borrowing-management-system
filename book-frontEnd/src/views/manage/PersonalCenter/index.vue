<template>
  <div class="user-dashboard-page">
    <!-- 加载遮罩 -->
    <div class="loading-mask" v-if="loading">
      <el-icon size="24"><Loading /></el-icon>
      <span>数据加载中...</span>
    </div>

    <!-- 用户信息区域 -->
    <div class="user-info-bar">
      <div class="avatar-section">
        <el-avatar :size="60" class="user-avatar">
          <img 
            v-if="userInfo.avatar" 
            :src="userInfo.avatar" 
            alt="用户头像" 
          />
          <span v-else class="avatar-text">
            {{ getAvatarText(userInfo.username) }}
          </span>
        </el-avatar>
        <div class="user-basic-info">
          <div class="username">{{ userInfo.username || '未知用户' }}</div>
          <div class="user-id">id: {{ userInfo.uid || '000000' }}</div>
        </div>
      </div>
      <div class="user-meta-info">
        <div>账号: {{ userInfo.account || '未知账号' }}</div>
        <div>注册时间: {{ userInfo.registerTime ? formatDate(userInfo.registerTime) : '--' }}</div>
        <div>信誉分: <span class="credit-score">{{ userInfo.creditScore || 100 }}分</span></div>
        <div>当前角色: {{ userInfo.roleName || '学生' }}</div>
      </div>
    </div>

    <!-- 数据统计+图表区域（占比 1:3:2 / 7） -->
    <div class="stats-chart-row" v-loading="loading">
      <!-- 借阅数据（还原字体样式，取消加粗放大） -->
      <div class="stats-card">
        <div class="stats-title">借阅数据</div>
        <div class="stats-content">
          <div class="stats-item">本月借阅: {{ borrowStats.monthBorrowCount || 0 }}本</div>
          <div class="stats-item">累计借阅: {{ borrowStats.totalBorrowCount || 0 }}本</div>
          <div class="stats-item">借阅频率: {{ borrowStats.borrowFrequency?.toFixed(1) || 0.0 }}本/月</div>
          <div class="stats-item">平均阅读: {{ borrowStats.averageReadingDays?.toFixed(1) || 0.0 }}天/本</div>
        </div>
      </div>

      <!-- 书籍分类饼图（修复图标显示，保留白框） -->
      <div class="chart-card pie-chart-card">
        <div class="chart-title">书籍分类占比</div>
        <div class="chart-container">
          <div id="category-pie-chart" style="width: 100%; height: 220px;"></div>
          <div v-if="categoryStats.length === 0 && !loading" class="empty-chart">
            暂无借阅分类数据
          </div>
        </div>
        <div class="chart-legend">
          <div class="legend-item" v-for="(item, index) in categoryStats" :key="index">
            <span class="legend-dot" :style="{ backgroundColor: pieColors[index % pieColors.length] }"></span>
            {{ item.categoryName || '未知分类' }}({{ item.borrowCount || 0 }}次)
          </div>
          <div class="legend-item" v-if="categoryStats.length > 0">
            <span class="legend-dot" :style="{ backgroundColor: pieColors[5] }"></span>
            其他({{ otherCount }}次)
          </div>
        </div>
      </div>

      <!-- 信誉分趋势图（保留现有合适布局） -->
      <div class="chart-card line-chart-card">
        <div class="chart-title">信誉分趋势</div>
        <div class="chart-container">
          <div id="credit-line-chart" style="width: 100%; height: 220px;"></div>
          <div v-if="creditTrend.length === 0 && !loading" class="empty-chart">
            暂无信誉分数据
          </div>
        </div>
      </div>
    </div>

    <!-- 当前借阅+预约区域 -->
    <div class="book-list-row">
      <!-- 当前借阅 -->
      <div class="book-list-card" @click="goToCurrentBorrow">
        <div class="book-list-card-header">
          <span class="list-title">当前借阅</span>
          <el-button type="text" size="small" @click.stop="viewAllBorrow">查看全部</el-button>
        </div>
        <div class="book-grid">
          <div class="book-item" v-for="(book, index) in currentBorrowBooks" :key="index">
            <!-- 有封面显示图片，无封面显示占位符 -->
            <img
              v-if="book.bookCover && book.bookCover.trim()"
              :src="book.bookCover"
              alt="书籍封面"
              class="book-img"
              @error="() => handleCoverError(index, 'borrow')"
            />
            <BookCoverPlaceholder v-else />
            <div class="book-name">{{ book.bookName || '未知书籍' }}</div>
            <div class="book-author">作者：{{ book.author || '未知作者' }}</div>
          </div>
          <div v-if="currentBorrowBooks.length === 0 && !loading" class="no-book-tip">
            暂无当前借阅
          </div>
        </div>
      </div>

      <!-- 当前预约 -->
      <div class="book-list-card" @click="goToCurrentReserve">
        <div class="book-list-card-header">
          <span class="list-title">当前预约</span>
          <el-button type="text" size="small" @click.stop="viewAllReserve">查看全部</el-button>
        </div>
        <div class="book-grid">
          <div class="book-item" v-for="(book, index) in currentReserveBooks" :key="index">
            <!-- 有封面显示图片，无封面显示占位符 -->
            <<img
              v-if="book.bookCover && book.bookCover.trim()"
              :src="book.bookCover"
              alt="书籍封面"
              class="book-img"
              @error="() => handleCoverError(index, 'reserve')" 
            />
            <BookCoverPlaceholder v-else />
            <div class="book-name">{{ book.bookName || '未知书籍' }}</div>
            <div class="book-author">作者：{{ book.author || '未知作者' }}</div>
          </div>
          <div v-if="currentReserveBooks.length === 0 && !loading" class="no-book-tip">
            暂无当前预约
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import { ElMessage, ElButton } from 'element-plus';
import { Loading } from '@element-plus/icons-vue';
import BookCoverPlaceholder from '@/components/BookCoverPlaceholder/BookCoverPlaceholder.vue';
// 导入接口函数和类型
import { 
  getUserBorrowStatistics, 
  getCategoryBorrowStatistics,
  getCurrentBorrowList,
  getCurrentUserInfo,
  getCreditScoreTrend
} from '@/apis/Borrowv/index';
import type {
  UserBorrowStatisticsVO,
  CategoryBorrowCountVO
} from '@/apis/Borrowv/type';
import { getCurrentReserveList 
} from '@/apis/Reserve';

const router = useRouter();
const loading = ref(true);

// 图片加载失败：清空封面地址，触发占位符
const handleCoverError = (index: number, type: string) => {
  if (type === 'borrow' && currentBorrowBooks.value[index]) {
    currentBorrowBooks.value[index].bookCover = '';
  } else if (type === 'reserve' && currentReserveBooks.value[index]) {
    currentReserveBooks.value[index].bookCover = ''; // 补充预约书籍的错误处理
  }
};

// 1. 借阅统计数据
const borrowStats = ref<UserBorrowStatisticsVO>({});

// 2. 分类统计数据
const categoryStats = ref<CategoryBorrowCountVO[]>([]);
const pieColors = ['#89CFF0', '#B5EAD7', '#FFD6A5', '#C8B6FF', '#FDFFB6', '#FFADAD'];
const otherCount = ref(0);

// 3. 用户信息
const userInfo = reactive({
  username: '未知用户',
  uid: '000000',
  account: '未知账号',
  registerTime: '',
  creditScore: 100,
  roleName: '学生',
  avatar: ''
});

// 4. 信誉分趋势数据
const creditTrend = ref<any[]>([]);

// 5. 当前借阅/预约书籍
const currentBorrowBooks = ref<any[]>([]);
const currentReserveBooks = ref<any[]>([]);

// 图表实例
const pieChart = ref<any>(null);
const lineChart = ref<any>(null);

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  return (dateStr.split('T')[0] || dateStr).replace(/-/g, '/');
};

// 获取头像文字（首字母）
const getAvatarText = (username: string): string => {
  if (!username) return '?';
  // 获取第一个字符的大写
  return username.charAt(0).toUpperCase();
};

// 页面跳转
const goToCurrentBorrow = () => {
  // 使用路由名称
  router.push({ name: 'currentBorrow' });
  // 或者使用路径
  // router.push('/borrow/currentBorrow');
};
const goToCurrentReserve = () => {
  // 使用路由名称
  router.push({ name: 'currentReserve' });
  // 或者使用路径
  // router.push('/borrow/currentReserve');
};

// 查看全部按钮的跳转也要修改
const viewAllBorrow = () => {
  router.push({ name: 'currentBorrow' });
};

const viewAllReserve = () => {
  router.push({ name: 'currentReserve' });
};


// 初始化分类饼图（修复显示，保留白框）
const initPieChart = () => {
  if (pieChart.value) {
    pieChart.value.dispose();
  }
  
  const chartDom = document.getElementById('category-pie-chart');
  if (!chartDom) return;
  pieChart.value = echarts.init(chartDom);
  
  const chartData = categoryStats.value.slice(0, 5).map((item, index) => ({
    name: item.categoryName || '未知分类',
    value: item.borrowCount || 0,
    itemStyle: { color: pieColors[index] }
  }));
  if (otherCount.value > 0) {
    chartData.push({
      name: '其他',
      value: otherCount.value,
      itemStyle: { color: pieColors[5] }
    });
  }

  pieChart.value.setOption({
    tooltip: { 
      trigger: 'item', 
      formatter: '{b}: {c}次 ({d}%)',
      textStyle: { fontSize: 12 }
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'], // 调整半径，确保白框明显
        center: ['50%', '50%'],
        data: chartData,
        label: { 
          show: true, 
          fontSize: 11,
          formatter: '{b}',
          overflow: 'truncate', // 截断显示，避免遮挡
          textBorderColor: '#fff', // 文字描边，提升可读性
          textBorderWidth: 1
        },
        labelLine: { 
          show: true, 
          length: 10, 
          length2: 5,
          smooth: false, // 取消平滑，避免线条混乱
          lineStyle: {
            width: 1
          }
        },
        // 确保中间白框显示
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 2
        }
      }
    ],
    backgroundColor: 'transparent' // 透明背景，适配卡片样式
  });

  window.addEventListener('resize', () => pieChart.value?.resize());
};

// 初始化信誉分折线图（保留现有合适样式）
const initLineChart = () => {
  if (lineChart.value) {
    lineChart.value.dispose();
  }
  
  const chartDom = document.getElementById('credit-line-chart');
  if (!chartDom) return;
  lineChart.value = echarts.init(chartDom);
  
  const trendData = creditTrend.value.length > 0 
    ? creditTrend.value.map(item => item.averageScore)
    : [100, 95, 98, 100, 97, 100];
  const monthLabels = creditTrend.value.length > 0
    ? creditTrend.value.map(item => item.month)
    : ['7月', '8月', '9月', '10月', '11月', '12月'];

  lineChart.value.setOption({
    tooltip: { 
      trigger: 'axis',
      textStyle: { fontSize: 12 }
    },
    grid: { 
      left: '10%', 
      right: '8%', 
      bottom: '15%', 
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: monthLabels,
      axisLabel: { 
        fontSize: 12,
        interval: 0
      },
      axisLine: { lineStyle: { color: '#e8e8e8' } }
    },
    yAxis: {
      type: 'value',
      max: 100,
      min: 0,
      interval: 20,
      axisLabel: { fontSize: 12 },
      splitLine: { lineStyle: { type: 'dashed', color: '#f0f0f0' } },
      axisLine: { lineStyle: { color: '#e8e8e8' } }
    },
    series: [
      {
        data: trendData,
        type: 'line',
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#89CFF0', width: 2 },
        itemStyle: { 
          color: '#89CFF0', 
          borderWidth: 1, 
          borderColor: '#fff' 
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(137, 207, 240, 0.3)' },
            { offset: 1, color: 'rgba(137, 207, 240, 0.1)' }
          ])
        },
        smooth: true
      }
    ],
    backgroundColor: 'transparent'
  });
  
  window.addEventListener('resize', () => lineChart.value?.resize());
};

// 加载所有数据
const loadAllData = async () => {
  try {
    loading.value = true;
    const [
      userInfoRes,
      statsRes,
      categoryRes,
      borrowRes,
      creditRes,
      reserveRes
    ] = await Promise.all([
      getCurrentUserInfo(),
      getUserBorrowStatistics(),
      getCategoryBorrowStatistics(),
      getCurrentBorrowList({ currentPage: 1, pageSize: 4 }),
      getCreditScoreTrend(),
      getCurrentReserveList({ currentPage: 1, pageSize: 4 })
    ]);

    // 处理用户信息
    if (userInfoRes.code === 200 && userInfoRes.data) {
      Object.assign(userInfo, userInfoRes.data);
    }

    // 处理借阅统计数据
    if (statsRes.code === 200 && statsRes.data) {
      borrowStats.value = statsRes.data;
    }

    // 处理分类统计数据
    if (categoryRes.code === 200 && categoryRes.data) {
      categoryStats.value = categoryRes.data.slice(0, 5);
      const totalCount = categoryRes.data.reduce((sum, item) => sum + (item.borrowCount || 0), 0);
      const top5Count = categoryStats.value.reduce((sum, item) => sum + (item.borrowCount || 0), 0);
      otherCount.value = totalCount - top5Count;
      initPieChart();
    }

    // 处理当前借阅书籍
    if (borrowRes.code === 200 && borrowRes.data?.records) {
      currentBorrowBooks.value = borrowRes.data.records;
    }

    // 处理当前预约书籍
    if (reserveRes.code === 200 && reserveRes.data?.records) {
      currentReserveBooks.value = reserveRes.data.records;
    }

    // 处理信誉分趋势
    if (creditRes.code === 200 && creditRes.data) {
      creditTrend.value = creditRes.data;
      initLineChart();
    } else {
      initLineChart();
    }

  } catch (error: any) {
    console.error('数据加载失败:', error);
    ElMessage.error('数据加载失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 挂载和卸载
onMounted(() => {
  loadAllData();
});

onUnmounted(() => {
  if (pieChart.value) pieChart.value.dispose();
  if (lineChart.value) lineChart.value.dispose();
});
</script>


<style scoped>
/* 页面基础样式 */
.user-dashboard-page {
  padding-bottom: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 80vh;
  background: none;
}

/* 用户信息栏样式 */
.user-info-bar {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  flex-wrap: wrap;
  gap: 16px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  border: 2px solid #409EFF;
  background-color: #409EFF;
}

.avatar-text {
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.username {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.user-id {
  font-size: 14px;
  color: #999;
}

.user-meta-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
  flex-wrap: wrap;
  row-gap: 8px;
}

.credit-score {
  color: #FF6B6B;
  font-weight: 500;
}

/* 数据统计+图表行样式 */
.stats-chart-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

/* 借阅数据卡片样式 */
.stats-card {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 16px;
  flex: 1;
  min-width: 200px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.stats-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  text-align: center;
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  justify-content: center;
  align-items: center;
}

.stats-item {
  font-size: 14px;
  color: #666;
  font-weight: normal;
  line-height: 1.5;
}

/* 图表卡片通用样式 */
.chart-card {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 300px;
}

.pie-chart-card {
  flex: 3;
  min-width: 350px;
}

.line-chart-card {
  flex: 2;
  min-width: 300px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  align-self: flex-start;
}

.chart-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  width: 100%;
  min-height: 220px;
}

.empty-chart {
  position: absolute;
  color: #8c939d;
  font-size: 14px;
  text-align: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 4px;
}

/* 图表图例样式 */
.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 10px;
  font-size: 12px;
  color: #666;
  justify-content: center;
  padding: 0 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  max-width: calc(100% / 3);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.legend-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

/* 书籍列表区域样式 */
.book-list-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.book-list-card {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 16px;
  flex: 1;
  min-width: 300px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.book-list-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.book-list-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.list-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  text-align: left;
  flex: 1;
  min-width: 150px;
}

/* 书籍网格布局 */
.book-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 100px;
  align-items: flex-start;
  padding: 8px 0;
}

.book-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.book-img {
  width: 80px;
  height: 110px;
  object-fit: cover;
  border-radius: 4px;
  background: #f5f5f5;
}

/* 占位符组件样式穿透 */
:deep(.book-cover-placeholder) {
  width: 80px !important;
  height: 110px !important;
  border-radius: 4px !important;
}

.book-name {
  font-size: 14px;
  color: #333;
  text-align: center;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  padding: 0 4px;
}

.book-author {
  font-size: 12px;
  color: #666;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  padding: 0 4px;
}

/* 空状态提示样式 */
.no-book-tip {
  grid-column: 1 / -1;
  text-align: center;
  font-size: 14px;
  color: #999;
  padding: 40px 0;
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 加载遮罩样式 */
.loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  gap: 10px;
  color: #666;
}

/* 加载状态优化 */
:deep(.el-loading-mask) {
  background: rgba(255, 255, 255, 0.8);
}

:deep(.el-loading-spinner) {
  top: 40%;
}

/* 响应式适配样式 */
@media (max-width: 1400px) {
  .pie-chart-card {
    flex: 2;
  }
  .line-chart-card {
    flex: 1;
  }
}

@media (max-width: 1200px) {
  .stats-chart-row {
    flex-direction: column;
  }
  .pie-chart-card, .line-chart-card, .stats-card {
    width: 100%;
    flex: none;
    min-width: auto;
  }
  .book-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .user-info-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .user-meta-info {
    gap: 12px;
  }
  .book-list-row {
    flex-direction: column;
    gap: 12px;
  }
  .book-grid {
    grid-template-columns: repeat(1, 1fr);
  }
  .chart-container {
    height: 200px !important;
  }
  .chart-legend {
    gap: 8px;
  }
  .legend-item {
    max-width: calc(100% / 2);
  }
}

@media (max-width: 480px) {
  .user-dashboard-page {
    padding: 10px;
  }
  .book-list-card {
    min-width: auto;
    padding: 12px;
  }
  .legend-item {
    max-width: 100%;
  }
}
</style>