<template>
  <div class="statistics" v-loading="loading">
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="时间范围">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计概览 -->
    <el-row :gutter="20" class="stat-overview">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <p class="label">总收入</p>
            <p class="value income">¥ {{ totalIncome.toFixed(2) }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <p class="label">总支出</p>
            <p class="value expense">¥ {{ totalExpense.toFixed(2) }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <p class="label">净收入</p>
            <p class="value" :class="netIncome >= 0 ? 'income' : 'expense'">
              ¥ {{ netIncome.toFixed(2) }}
            </p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <p class="label">储蓄率</p>
            <p class="value">{{ savingsRate.toFixed(1) }}%</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>收支趋势</template>
          <v-chart :option="trendOption" style="height: 350px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>支出分类占比</template>
          <v-chart :option="expensePieOption" style="height: 350px" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>收入分类占比</template>
          <v-chart :option="incomePieOption" style="height: 350px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>每日支出</template>
          <v-chart :option="dailyBarOption" style="height: 350px" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 分类明细 -->
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <span>分类明细</span>
          <el-radio-group v-model="detailType" size="small">
            <el-radio-button label="expense">支出</el-radio-button>
            <el-radio-button label="income">收入</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table :data="categoryDetails" style="width: 100%">
        <el-table-column prop="category" label="分类" width="150" />
        <el-table-column prop="amount" label="金额" width="150">
          <template #default="{ row }">
            ¥{{ row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="count" label="笔数" width="100" />
        <el-table-column prop="percentage" label="占比">
          <template #default="{ row }">
            <el-progress :percentage="row.percentage" :color="detailType === 'income' ? '#67c23a' : '#f56c6c'" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { billApi, type CategoryStatistics, type DailyTrend } from '@/api/bill'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 格式化日期为 yyyy-MM-dd
const formatDate = (date: Date): string => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取默认日期范围（本月）
const getDefaultDateRange = () => {
  const now = new Date()
  const startDate = formatDate(new Date(now.getFullYear(), now.getMonth(), 1))
  const endDate = formatDate(now)
  return [startDate, endDate]
}

const loading = ref(false)
const dateRange = ref<string[]>(getDefaultDateRange())
const detailType = ref('expense')

const totalIncome = ref(0)
const totalExpense = ref(0)
const netIncome = computed(() => totalIncome.value - totalExpense.value)
const savingsRate = computed(() => totalIncome.value > 0 ? (netIncome.value / totalIncome.value) * 100 : 0)

// 分类统计数据
const expenseCategoryData = ref<CategoryStatistics[]>([])
const incomeCategoryData = ref<CategoryStatistics[]>([])
// 趋势数据
const trendData = ref<DailyTrend[]>([])

// 加载统计数据
const loadStatistics = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return

  const startDate = dateRange.value[0]
  const endDate = dateRange.value[1]
  if (!startDate || !endDate) return

  loading.value = true

  try {
    // 并行加载所有数据
    const [expenseStats, incomeStats, trend] = await Promise.all([
      billApi.getCategoryStatistics('expense', startDate, endDate),
      billApi.getCategoryStatistics('income', startDate, endDate),
      billApi.getDailyTrend(startDate, endDate)
    ])

    expenseCategoryData.value = expenseStats || []
    incomeCategoryData.value = incomeStats || []
    trendData.value = trend || []

    // 计算总收入和总支出
    totalExpense.value = expenseCategoryData.value.reduce((sum, item) => sum + Number(item.amount), 0)
    totalIncome.value = incomeCategoryData.value.reduce((sum, item) => sum + Number(item.amount), 0)

  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  loadStatistics()
}

// 分类明细计算
const categoryDetails = computed(() => {
  const data = detailType.value === 'expense' ? expenseCategoryData.value : incomeCategoryData.value
  const total = data.reduce((sum, item) => sum + Number(item.amount), 0)

  return data.map(item => ({
    category: item.category,
    amount: Number(item.amount),
    count: 0, // API暂时没有返回笔数
    percentage: total > 0 ? Math.round((Number(item.amount) / total) * 100) : 0
  }))
})

onMounted(() => {
  loadStatistics()
})

const trendOption = computed(() => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    return { xAxis: { data: [] }, series: [] }
  }

  const startDateStr = dateRange.value[0]
  const endDateStr = dateRange.value[1]
  if (!startDateStr || !endDateStr) {
    return { xAxis: { data: [] }, series: [] }
  }
  const start = new Date(startDateStr)
  const end = new Date(endDateStr)
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
    return `${date.getMonth() + 1}/${date.getDate()}`
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

const expensePieOption = computed(() => {
  const data = expenseCategoryData.value.map((item, index) => ({
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
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        data: data.length > 0 ? data : [{ value: 0, name: '暂无数据', itemStyle: { color: '#e0e0e0' } }]
      }
    ]
  }
})

const incomeColors = ['#67c23a', '#409eff', '#e6a23c', '#909399', '#b37feb', '#36cfc9', '#ff85c0', '#f56c6c']

const incomePieOption = computed(() => {
  const data = incomeCategoryData.value.map((item, index) => ({
    value: Number(item.amount),
    name: item.category,
    itemStyle: { color: incomeColors[index % incomeColors.length] }
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
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        data: data.length > 0 ? data : [{ value: 0, name: '暂无数据', itemStyle: { color: '#e0e0e0' } }]
      }
    ]
  }
})

const dailyBarOption = computed(() => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    return { xAxis: { data: [] }, series: [] }
  }

  const startDateStr = dateRange.value[0]
  const endDateStr = dateRange.value[1]
  if (!startDateStr || !endDateStr) {
    return { xAxis: { data: [] }, series: [] }
  }
  const start = new Date(startDateStr)
  const end = new Date(endDateStr)
  const dates: string[] = []
  const expenseMap = new Map<string, number>()

  // 生成日期列表
  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const dateStr = formatDate(d)
    dates.push(dateStr)
    expenseMap.set(dateStr, 0)
  }

  // 填充支出数据
  trendData.value.forEach(item => {
    if (item.type === 'expense') {
      const rawDate = item.date || ''
      const dateStr = rawDate.includes('T') ? rawDate.split('T')[0] : rawDate
      expenseMap.set(dateStr, (expenseMap.get(dateStr) || 0) + Number(item.amount))
    }
  })

  // 格式化X轴标签
  const xLabels = dates.map(d => {
    const date = new Date(d)
    return `${date.getDate()}日`
  })

  return {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xLabels
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        type: 'bar',
        itemStyle: { color: '#f56c6c' },
        data: dates.map(d => expenseMap.get(d) || 0)
      }
    ]
  }
})
</script>

<style scoped>
.statistics {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.stat-overview {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content .label {
  color: #999;
  font-size: 14px;
  margin: 0 0 10px;
}

.stat-content .value {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.stat-content .value.income {
  color: #67c23a;
}

.stat-content .value.expense {
  color: #f56c6c;
}

.chart-row {
  margin-bottom: 20px;
}

.detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
