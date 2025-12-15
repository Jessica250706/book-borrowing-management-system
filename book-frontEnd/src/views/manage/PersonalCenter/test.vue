<template>
  <div class="user-dashboard-page">
    <!-- 用户信息区域（匹配新UI布局） -->
    <div class="user-info-bar">
      <div class="avatar-section">
        <el-avatar :size="60" class="user-avatar">
          <img src="https://picsum.photos/60/60?random=avatar" alt="用户头像" />
        </el-avatar>
        <div class="user-basic-info">
          <div class="username">超人不会飞</div>
          <div class="user-id">id:000001</div>
        </div>
      </div>
      <div class="user-meta-info">
        <div>账号: 123456789</div>
        <div>注册时间: 2025/10/11</div>
        <div>信誉分: <span class="credit-score">100分</span></div>
        <div>当前角色: 学生</div>
      </div>
    </div>

    <!-- 数据统计+图表区域（匹配新UI的三列布局） -->
    <div class="stats-chart-row">
      <!-- 借阅数据（居中显示） -->
      <div class="stats-card">
        <div class="stats-title">借阅数据</div>
        <div class="stats-content">
          <div class="stats-item">本月借阅: 10本</div>
          <div class="stats-item">累计借阅: 10本</div>
          <div class="stats-item">借阅频率: 10本/月</div>
          <div class="stats-item">平均阅读: 3天/本</div>
        </div>
      </div>

      <!-- 书籍分类饼图（清新颜色） -->
      <div class="chart-card">
        <div class="chart-title">书籍分类占比</div>
        <div class="chart-container">
          <div id="category-pie-chart" style="width: 220px; height: 200px;"></div>
        </div>
        <div class="chart-legend">
          <div class="legend-item"><span class="legend-dot dot-type1"></span>类别1</div>
          <div class="legend-item"><span class="legend-dot dot-type2"></span>类别2</div>
          <div class="legend-item"><span class="legend-dot dot-type3"></span>类别3</div>
          <div class="legend-item"><span class="legend-dot dot-type4"></span>类别4</div>
          <div class="legend-item"><span class="legend-dot dot-type5"></span>类别5</div>
          <div class="legend-item"><span class="legend-dot dot-other"></span>其他</div>
        </div>
      </div>

      <!-- 信誉分折线图 -->
      <div class="chart-card">
        <div class="chart-title">信誉分趋势</div>
        <div class="chart-container">
          <div id="credit-line-chart" style="width: 280px; height: 200px;"></div>
        </div>
      </div>
    </div>

    <!-- 当前借阅+预约区域（匹配新UI的两列布局，添加跳转） -->
    <div class="book-list-row">
      <!-- 当前借阅（点击跳转） -->
      <div class="book-list-card" @click="goToPage('/borrow/current')">
        <div class="list-title">当前借阅</div>
        <div class="book-grid">
          <div class="book-item" v-for="i in 4" :key="`borrow-${i}`">
            <img src="https://picsum.photos/100/140?random=book1" alt="书籍封面" class="book-cover" />
            <div class="book-name">望春风</div>
          </div>
        </div>
      </div>

      <!-- 当前预约（点击跳转） -->
      <div class="book-list-card" @click="goToPage('/reserve/current')">
        <div class="list-title">当前预约</div>
        <div class="book-grid">
          <div class="book-item" v-for="i in 4" :key="`reserve-${i}`">
            <img src="https://picsum.photos/100/140?random=book2" alt="书籍封面" class="book-cover" />
            <div class="book-name">望春风</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';

const router = useRouter();

// 点击跳转对应页面
const goToPage = (path: string) => {
  router.push(path);
};

// 初始化图表（更新为清新颜色）
onMounted(() => {
  // 书籍分类饼图（清新色系）
  const pieChart = echarts.init(document.getElementById('category-pie-chart'));
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: 10, name: '类别1', itemStyle: { color: '#89CFF0' } }, // 浅蓝
          { value: 15, name: '类别2', itemStyle: { color: '#B5EAD7' } }, // 浅绿
          { value: 20, name: '类别3', itemStyle: { color: '#FFD6A5' } }, // 浅橙
          { value: 25, name: '类别4', itemStyle: { color: '#C8B6FF' } }, // 浅紫
          { value: 30, name: '类别5', itemStyle: { color: '#FDFFB6' } }, // 浅黄
          { value: 24, name: '其他', itemStyle: { color: '#FFADAD' } }  // 浅粉
        ],
        label: { show: true, fontSize: 12 }
      }
    ]
  });

  // 信誉分折线图（匹配UI走势）
  const lineChart = echarts.init(document.getElementById('credit-line-chart'));
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
        lineStyle: { color: '#89CFF0', width: 2 }, // 清新浅蓝
        itemStyle: { color: '#89CFF0' }
      }
    ]
  });

  // 窗口resize自适应
  window.addEventListener('resize', () => {
    pieChart.resize();
    lineChart.resize();
  });
});
</script>

<style scoped>
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
</style>