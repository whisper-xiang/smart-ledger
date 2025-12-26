<template>
  <div class="dashboard" v-loading="loading">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card income">
          <div class="stat-icon">
            <el-icon :size="32">
              <TrendCharts />
            </el-icon>
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
            <el-icon :size="32">
              <Goods />
            </el-icon>
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
            <el-icon :size="32">
              <Wallet />
            </el-icon>
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
            <el-icon :size="32">
              <Coin />
            </el-icon>
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
        <el-table-column prop="billDate" label="日期" width="120" />
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
import { ref, computed, onMounted, watch } from 'vue'
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
import { billApi, type Bill, type DailyTrend, type CategoryStatistics } from '@/api/bill'
import { savingsApi, type SavingPlan } from '@/api/savings'
import { ElMessage } from 'element-plus'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const loading = ref(false)
const monthIncome = ref(0)
const monthExpense = ref(0)
const savingsGoal = ref(0)
const trendType = ref('week')
const recentBills = ref<Bill[]>([])
const trendData = ref<DailyTrend[]>([])
const categoryData = ref<CategoryStatistics[]>([])

// 格式化日期为 yyyy-MM-dd
const formatDate = (date: Date): string => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取日期范围
const getDateRange = (type: 'week' | 'month') => {
  const now = new Date()
  const endDate = formatDate(now)
  let startDate: string

  if (type === 'week') {
    const weekStart = new Date(now)
    weekStart.setDate(now.getDate() - 6)
    startDate = formatDate(weekStart)
  } else {
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1)
    startDate = formatDate(monthStart)
  }

  return { startDate, endDate }
}

// 加载月度统计
const loadMonthStatistics = async () => {
  try {
    const now = new Date()
    const stats = await billApi.getMonthStatistics(now.getFullYear(), now.getMonth() + 1)
    monthIncome.value = stats.income || 0
    monthExpense.value = stats.expense || 0
  } catch (error) {
    console.error('加载月度统计失败:', error)
  }
}

// 加载存钱目标
const loadSavingsGoal = async () => {
  try {
    const plans = await savingsApi.getPlanList()
    if (plans && plans.length > 0) {
      savingsGoal.value = plans.reduce((sum: number, plan: SavingPlan) => sum + (plan.goalAmount || 0), 0)
    }
  } catch (error) {
    console.error('加载存钱目标失败:', error)
  }
}

// 加载最近账单
const loadRecentBills = async () => {
  try {
    const bills = await billApi.getRecentBills(5)
    recentBills.value = bills || []
  } catch (error) {
    console.error('加载最近账单失败:', error)
  }
}

// 加载趋势数据
const loadTrendData = async () => {
  try {
    const { startDate, endDate } = getDateRange(trendType.value as 'week' | 'month')
    const data = await billApi.getDailyTrend(startDate, endDate)
    trendData.value = data || []
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }
}

// 加载分类统计
const loadCategoryData = async () => {
  try {
    const now = new Date()
    const startDate = formatDate(new Date(now.getFullYear(), now.getMonth(), 1))
    const endDate = formatDate(now)
    const data = await billApi.getCategoryStatistics('expense', startDate, endDate)
    categoryData.value = data || []
  } catch (error) {
    console.error('加载分类统计失败:', error)
  }
}

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadMonthStatistics(),
      loadSavingsGoal(),
      loadRecentBills(),
      loadTrendData(),
      loadCategoryData()
    ])
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 监听趋势类型变化
watch(trendType, () => {
  loadTrendData()
})

onMounted(() => {
  loadAllData()
})

const trendOption = computed(() => {
  // 处理趋势数据
  const { startDate, endDate } = getDateRange(trendType.value as 'week' | 'month')
  const start = new Date(startDate)
  const end = new Date(endDate)
  const dates: string[] = []
  const incomeMap = new Map<string, number>()
  const expenseMap = new Map<string, number>()

  // 生成日期列表
  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const dateStr = formatDate(d)
    dates.push(dateStr)
    incomeMap.set(dateStr, 0)
    expenseMap.set(dateStr, 0)
  }

  // 填充数据
  trendData.value.forEach(item => {
    const rawDate = item.date || ''
    const dateStr = rawDate.includes('T') ? rawDate.split('T')[0] : rawDate
    if (item.type === 'income') {
      incomeMap.set(dateStr, (incomeMap.get(dateStr) || 0) + Number(item.amount))
    } else {
      expenseMap.set(dateStr, (expenseMap.get(dateStr) || 0) + Number(item.amount))
    }
  })

  // 格式化X轴标签
  const xLabels = dates.map(d => {
    const date = new Date(d)
    return trendType.value === 'week'
      ? ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()]
      : `${date.getMonth() + 1}/${date.getDate()}`
  })

  return {
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
      data: xLabels
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
        data: dates.map(d => incomeMap.get(d) || 0)
      },
      {
        name: '支出',
        type: 'line',
        smooth: true,
        itemStyle: { color: '#f56c6c' },
        areaStyle: { color: 'rgba(245, 108, 108, 0.1)' },
        data: dates.map(d => expenseMap.get(d) || 0)
      }
    ]
  }
})

const categoryColors = ['#f56c6c', '#e6a23c', '#409eff', '#67c23a', '#909399', '#b37feb', '#36cfc9', '#ff85c0']

const pieOption = computed(() => {
  const data = categoryData.value.map((item, index) => ({
    value: Number(item.amount),
    name: item.category,
    itemStyle: { color: categoryColors[index % categoryColors.length] }
  }))

  return {
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
        data: data.length > 0 ? data : [{ value: 0, name: '暂无数据', itemStyle: { color: '#e0e0e0' } }]
      }
    ]
  }
})
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
