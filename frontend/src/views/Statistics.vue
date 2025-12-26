<template>
  <div class="statistics">
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
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
            <el-progress
              :percentage="row.percentage"
              :color="detailType === 'income' ? '#67c23a' : '#f56c6c'"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
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

const dateRange = ref<string[]>([])
const detailType = ref('expense')

const totalIncome = ref(15800)
const totalExpense = ref(9650)
const netIncome = computed(() => totalIncome.value - totalExpense.value)
const savingsRate = computed(() => (netIncome.value / totalIncome.value) * 100)

const handleQuery = () => {
  ElMessage.success('查询成功')
}

const categoryDetails = computed(() => {
  if (detailType.value === 'expense') {
    return [
      { category: '餐饮', amount: 2800, count: 45, percentage: 29 },
      { category: '购物', amount: 2200, count: 12, percentage: 23 },
      { category: '交通', amount: 1500, count: 30, percentage: 16 },
      { category: '居住', amount: 1800, count: 3, percentage: 19 },
      { category: '娱乐', amount: 850, count: 8, percentage: 9 },
      { category: '其他', amount: 500, count: 5, percentage: 5 }
    ]
  } else {
    return [
      { category: '工资', amount: 12000, count: 1, percentage: 76 },
      { category: '兼职', amount: 2500, count: 4, percentage: 16 },
      { category: '投资收益', amount: 800, count: 2, percentage: 5 },
      { category: '其他', amount: 500, count: 3, percentage: 3 }
    ]
  }
})

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
    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
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
      data: [12000, 13500, 12800, 14200, 13000, 15800, 14500, 16000, 15200, 14800, 15500, 15800]
    },
    {
      name: '支出',
      type: 'line',
      smooth: true,
      itemStyle: { color: '#f56c6c' },
      areaStyle: { color: 'rgba(245, 108, 108, 0.1)' },
      data: [8500, 9200, 8800, 10500, 9800, 9650, 10200, 11000, 9500, 10800, 9200, 9650]
    }
  ]
}))

const expensePieOption = computed(() => ({
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
      data: [
        { value: 2800, name: '餐饮', itemStyle: { color: '#f56c6c' } },
        { value: 2200, name: '购物', itemStyle: { color: '#e6a23c' } },
        { value: 1800, name: '居住', itemStyle: { color: '#409eff' } },
        { value: 1500, name: '交通', itemStyle: { color: '#67c23a' } },
        { value: 850, name: '娱乐', itemStyle: { color: '#909399' } },
        { value: 500, name: '其他', itemStyle: { color: '#b88230' } }
      ]
    }
  ]
}))

const incomePieOption = computed(() => ({
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
      data: [
        { value: 12000, name: '工资', itemStyle: { color: '#67c23a' } },
        { value: 2500, name: '兼职', itemStyle: { color: '#409eff' } },
        { value: 800, name: '投资收益', itemStyle: { color: '#e6a23c' } },
        { value: 500, name: '其他', itemStyle: { color: '#909399' } }
      ]
    }
  ]
}))

const dailyBarOption = computed(() => ({
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
    data: Array.from({ length: 31 }, (_, i) => `${i + 1}日`)
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      type: 'bar',
      itemStyle: { color: '#f56c6c' },
      data: [
        120, 85, 150, 200, 0, 350, 95, 180, 220, 0,
        150, 280, 90, 175, 0, 420, 85, 130, 0, 250,
        180, 95, 310, 0, 150, 200, 85, 175, 0, 220, 150
      ]
    }
  ]
}))
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
