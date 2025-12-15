<template>
  <div class="user-dashboard-page">
    <!-- 用户信息区域（保留原有布局） -->
    <div class="user-info-bar">
      <div class="avatar-section">
        <el-avatar :size="60" class="user-avatar">
          <img :src="userInfo.avatar || 'https://picsum.photos/60/60?random=avatar'" alt="用户头像" />
        </el-avatar>
        <div class="user-basic-info">
          <div class="username">{{ userInfo.userName || '未知用户' }}</div>
          <div class="user-id">id: {{ userInfo.uid || '000000' }}</div>
        </div>
      </div>
      <div class="user-meta-info">
        <div>账号: {{ userInfo.account || '未知账号' }}</div>
        <div>注册时间: {{ formatDate(userInfo.registerTime) || '未知时间' }}</div>
        <div>信誉分: <span class="credit-score">{{ userInfo.creditScore || 100 }}分</span></div>
        <div>当前角色: {{ userInfo.role || '学生' }}</div>
      </div>
    </div>

    <!-- 数据统计+图表区域（加载状态+动态数据） -->
    <div class="stats-chart-row" v-loading="loading">
      <!-- 借阅数据（对接接口统计数据） -->
      <div class="stats-card">
        <div class="stats-title">借阅数据</div>
        <div class="stats-content">
          <div class="stats-item">本月借阅: {{ borrowStats.monthBorrowCount || 0 }}本</div>
          <div class="stats-item">累计借阅: {{ borrowStats.totalBorrowCount || 0 }}本</div>
          <div class="stats-item">借阅频率: {{ borrowStats.borrowFrequency?.toFixed(1) || 0.0 }}本/月</div>
          <div class="stats-item">平均阅读: {{ borrowStats.averageReadingDays?.toFixed(1) || 0.0 }}天/本</div>
        </div>
      </div>

      <!-- 书籍分类饼图（对接接口分类数据） -->
      <div class="chart-card">
        <div class="chart-title">书籍分类占比</div>
        <div class="chart-container">
          <div id="category-pie-chart" style="width: 220px; height: 200px;"></div>
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

      <!-- 信誉分折线图（保留原有样式，后续可对接信誉分接口） -->
      <div class="chart-card">
        <div class="chart-title">信誉分趋势</div>
        <div class="chart-container">
          <div id="credit-line-chart" style="width: 280px; height: 200px;"></div>
        </div>
      </div>
    </div>

    <!-- 当前借阅+预约区域（保留原有跳转逻辑） -->
    <div class="book-list-row">
      <div class="book-list-card" @click="goToPage('/borrow/current')">
        <div class="list-title">当前借阅</div>
        <div class="book-grid">
          <div class="book-item" v-for="(book, index) in currentBorrowBooks" :key="index">
            <img :src="book.coverUrl || 'https://picsum.photos/100/140?random=book1'" alt="书籍封面" class="book-cover" />
            <div class="book-name">{{ book.bookName || '未知书籍' }}</div>
          </div>
          <!-- 不足4本时显示占位 -->
          <div class="book-item" v-for="i in Math.max(0, 4 - currentBorrowBooks.length)" :key="`borrow-placeholder-${i}`">
            <img src="https://picsum.photos/100/140?random=empty" alt="占位" class="book-cover empty-cover" />
            <div class="book-name">暂无书籍</div>
          </div>
        </div>
      </div>
      <div class="book-list-card" @click="goToPage('/reserve/current')">
        <div class="list-title">当前预约</div>
        <div class="book-grid">
          <div class="book-item" v-for="(book, index) in currentReserveBooks" :key="index">
            <img :src="book.coverUrl || 'https://picsum.photos/100/140?random=book2'" alt="书籍封面" class="book-cover" />
            <div class="book-name">{{ book.bookName || '未知书籍' }}</div>
          </div>
          <!-- 不足4本时显示占位 -->
          <div class="book-item" v-for="i in Math.max(0, 4 - currentReserveBooks.length)" :key="`reserve-placeholder-${i}`">
            <img src="https://picsum.photos/100/140?random=empty" alt="占位" class="book-cover empty-cover" />
            <div class="book-name">暂无预约</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';
// 导入新增的接口函数和类型
import { 
  getUserBorrowStatistics, 
  getCategoryBorrowStatistics,
  getCurrentBorrowList // 复用原有接口获取当前借阅书籍
} from '@/apis/Borrowv/index';
import type {
  UserBorrowStatisticsVO,
  CategoryBorrowCountVO,
  CurrentBorrowDTO
} from '@/apis/Borrowv/type';

const router = useRouter();
const loading = ref(true); // 全局加载状态

// 1. 借阅统计数据（对接 /api/borrow/statistics）
const borrowStats = ref<UserBorrowStatisticsVO>({});

// 2. 分类统计数据（对接 /api/borrow/category-statistics）
const categoryStats = ref<CategoryBorrowCountVO[]>([]);
const pieColors = ['#89CFF0', '#B5EAD7', '#FFD6A5', '#C8B6FF', '#FDFFB6', '#FFADAD']; // 清新色系
const otherCount = ref(0); // 其他分类总次数

// 3. 用户信息（可后续对接用户信息接口，这里先模拟）
const userInfo = reactive({
  userName: '超人不会飞',
  uid: '000001',
  account: '123456789',
  registerTime: '2025-10-11',
  creditScore: 100,
  role: '学生',
  avatar: ''
});

// 4. 当前借阅/预约书籍（对接现有接口，简化展示）
const currentBorrowBooks = ref<any[]>([]);
const currentReserveBooks = ref<any[]>([]);

// 格式化日期（注册时间显示）
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  return dateStr.split('T')[0].replace(/-/g, '/');
};

// 页面跳转
const goToPage = (path: string) => {
  router.push(path);
};

// 初始化图表（动态加载分类数据）
const initPieChart = () => {
  const pieChart = echarts.init(document.getElementById('category-pie-chart')!);
  // 处理图表数据（前五分类+其他）
  const chartData = categoryStats.value.slice(0, 5).map((item, index) => ({
    name: item.categoryName || '未知分类',
    value: item.borrowCount || 0,
    itemStyle: { color: pieColors[index] }
  }));
  // 添加"其他"分类
  if (otherCount.value > 0) {
    chartData.push({
      name: '其他',
      value: otherCount.value,
      itemStyle: { color: pieColors[5] }
    });
  }

  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}次 ({d}%)' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        data: chartData,
        label: { show: true, fontSize: 12, formatter: '{b}' }
      }
    ]
  });

  // 窗口自适应
  window.addEventListener('resize', () => pieChart.resize());
  return pieChart;
};

// 初始化信誉分折线图（保留原有逻辑）
const initLineChart = () => {
  const lineChart = echarts.init(document.getElementById('credit-line-chart')!);
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['一月', '二月', '三月', '四月', '五月']
    },
    yAxis: {
      type: 'value',
      max: 100,
      min: 0,
      splitLine: { lineStyle: { type: 'dashed' } }
    },
    series: [
      {
        data: [100, 100, 80, 90, 100],
        type: 'line',
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#89CFF0', width: 2 },
        itemStyle: { color: '#89CFF0' }
      }
    ]
  });
  window.addEventListener('resize', () => lineChart.resize());
  return lineChart;
};

// 加载所有数据（统计+分类+当前借阅）
const loadAllData = async () => {
  try {
    loading.value = true;

    // 并行请求多个接口，提升性能
    const [statsRes, categoryRes, borrowRes] = await Promise.all([
      getUserBorrowStatistics(),
      getCategoryBorrowStatistics(),
      getCurrentBorrowList({ currentPage: 1, pageSize: 4 }) // 获取前4本当前借阅书籍
    ]);

    // 1. 处理借阅统计数据
    if (statsRes.code === 200 && statsRes.data) {
      borrowStats.value = statsRes.data;
    } else {
      ElMessage.error('获取借阅统计失败');
    }

    // 2. 处理分类统计数据（计算"其他"分类总次数）
    if (categoryRes.code === 200 && categoryRes.data) {
      categoryStats.value = categoryRes.data.slice(0, 5); // 只取前五分类
      // 计算所有分类总次数
      const totalCount = categoryRes.data.reduce((sum, item) => sum + (item.borrowCount || 0), 0);
      // 计算前五分类总次数
      const top5Count = categoryStats.value.reduce((sum, item) => sum + (item.borrowCount || 0), 0);
      // 其他分类次数 = 总次数 - 前五分类次数
      otherCount.value = totalCount - top5Count;
    } else {
      ElMessage.error('获取分类统计失败');
    }

    // 3. 处理当前借阅书籍数据
    if (borrowRes.code === 200 && borrowRes.data?.records) {
      currentBorrowBooks.value = borrowRes.data.records;
    }

    // 初始化图表
    initPieChart();
    initLineChart();
  } catch (error: any) {
    console.error('数据加载失败:', error);
    ElMessage.error('数据加载失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 页面挂载时加载数据
onMounted(() => {
  loadAllData();
});
</script>

<style scoped>
/* 原有样式保留，新增以下样式 */

.user-dashboard-page {
  padding-bottom: 20px;
    max-width: 1400px;
    margin: 0 auto;
    min-height: 80vh;
}

/* 用户信息栏（匹配新UI的横向布局） */
.user-info-bar {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  border: 2px solid #FFD6A5; /* 匹配清新色系 */
}

.user-basic-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
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
}

.credit-score {
  color: #FF6B6B;
  font-weight: 500;
}

/* 数据统计+图表行（三列布局） */
.stats-chart-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

/* 借阅数据卡片（内容居中） */
.stats-card {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 16px;
  flex: 1;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
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
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #666;
}

/* 图表卡片 */
.chart-card {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 16px;
  flex: 1.5;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  align-self: flex-start;
}

.chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
  font-size: 12px;
  color: #666;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

/* 清新色系 */
.dot-type1 { background: #89CFF0; }
.dot-type2 { background: #B5EAD7; }
.dot-type3 { background: #FFD6A5; }
.dot-type4 { background: #C8B6FF; }
.dot-type5 { background: #FDFFB6; }
.dot-other { background: #FFADAD; }

/* 书籍列表行（两列布局，添加点击光标） */
.book-list-row {
  display: flex;
  gap: 20px;
}

.book-list-card {
  background: #FFFFFF;
  border-radius: 8px;
  padding: 16px;
  flex: 1;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  cursor: pointer; /* 点击提示 */
  transition: box-shadow 0.2s;
}

.book-list-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.list-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  text-align: center;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.book-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.book-cover {
  width: 80px;
  height: 110px;
  object-fit: cover;
  border-radius: 4px;
}

.book-name {
  font-size: 14px;
  color: #333;
  text-align: center;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .stats-chart-row {
    flex-direction: column;
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
  .book-list-row {
    flex-direction: column;
  }
}

.empty-cover {
  opacity: 0.3;
  background: #f5f5f5;
}

/* 加载状态样式优化 */
:deep(.el-loading-mask) {
  background: rgba(255, 255, 255, 0.8);
}

:deep(.el-loading-spinner) {
  top: 40%;
}
</style>