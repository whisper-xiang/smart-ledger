<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card income">
          <div class="stat-icon">
            <el-icon :size="32"><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">本月收入</p>
            <p class="stat-value">¥ {{ monthIncome.toFixed(2) }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card expense">
          <div class="stat-icon">
            <el-icon :size="32"><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">本月支出</p>
            <p class="stat-value">¥ {{ monthExpense.toFixed(2) }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card balance">
          <div class="stat-icon">
            <el-icon :size="32"><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">本月结余</p>
            <p class="stat-value">¥ {{ (monthIncome - monthExpense).toFixed(2) }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card savings">
          <div class="stat-icon">
            <el-icon :size="32"><Coin /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">存钱目标</p>
            <p class="stat-value">¥ {{ savingsGoal.toFixed(2) }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收支趋势</span>
              <el-radio-group v-model="trendType" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <v-chart :option="trendOption" style="height: 300px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span>支出分类</span>
          </template>
          <v-chart :option="pieOption" style="height: 300px" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近账单 -->
    <el-card class="recent-bills">
      <template #header>
        <div class="card-header">
          <span>最近账单</span>
          <el-button type="primary" text @click="$router.push('/bills')">
            查看全部
          </el-button>
        </div>
      </template>
      <el-table :data="recentBills" style="width: 100%">
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'income' ? 'success' : 'danger'" size="small">
              {{ row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="{ row }">
            <span :class="row.type === 'income' ? 'income-text' : 'expense-text'">
              {{ row.type === 'income' ? '+' : '-' }}¥{{ row.amount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { TrendCharts, Goods, Wallet, Coin } from '@element-plus/icons-vue'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const monthIncome = ref(12580.00)
const monthExpense = ref(8650.50)
const savingsGoal = ref(50000.00)
const trendType = ref('week')

const recentBills = ref([
  { date: '2024-12-25', category: '餐饮', type: 'expense', description: '午餐', amount: 35.00 },
  { date: '2024-12-25', category: '工资', type: 'income', description: '12月工资', amount: 8000.00 },
  { date: '2024-12-24', category: '交通', type: 'expense', description: '地铁充值', amount: 100.00 },
  { date: '2024-12-24', category: '购物', type: 'expense', description: '日用品', amount: 256.50 },
  { date: '2024-12-23', category: '娱乐', type: 'expense', description: '电影票', amount: 80.00 }
])

const trendOption = computed(() => ({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['收入', '支出']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: trendType.value === 'week'
      ? ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      : ['1日', '5日', '10日', '15日', '20日', '25日', '30日']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '收入',
      type: 'line',
      smooth: true,
      itemStyle: { color: '#67c23a' },
      areaStyle: { color: 'rgba(103, 194, 58, 0.1)' },
      data: [0, 0, 8000, 0, 500, 0, 0]
    },
    {
      name: '支出',
      type: 'line',
      smooth: true,
      itemStyle: { color: '#f56c6c' },
      areaStyle: { color: 'rgba(245, 108, 108, 0.1)' },
      data: [120, 85, 150, 200, 180, 350, 95]
    }
  ]
}))

const pieOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: ¥{c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      data: [
        { value: 2500, name: '餐饮', itemStyle: { color: '#f56c6c' } },
        { value: 1800, name: '购物', itemStyle: { color: '#e6a23c' } },
        { value: 1200, name: '交通', itemStyle: { color: '#409eff' } },
        { value: 800, name: '娱乐', itemStyle: { color: '#67c23a' } },
        { value: 600, name: '其他', itemStyle: { color: '#909399' } }
      ]
    }
  ]
}))
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card .stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-card.income .stat-icon {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.stat-card.expense .stat-icon {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.stat-card.balance .stat-icon {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.stat-card.savings .stat-icon {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-info .stat-label {
  color: #999;
  font-size: 14px;
  margin: 0 0 5px;
}

.stat-info .stat-value {
  color: #333;
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recent-bills {
  margin-bottom: 20px;
}

.income-text {
  color: #67c23a;
  font-weight: 500;
}

.expense-text {
  color: #f56c6c;
  font-weight: 500;
}
</style>
