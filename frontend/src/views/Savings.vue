<template>
  <div class="savings">
    <!-- 总览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="8">
        <el-card class="overview-card">
          <div class="overview-item">
            <p class="label">总存款目标</p>
            <p class="value">¥ {{ totalGoal.toFixed(2) }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="overview-card">
          <div class="overview-item">
            <p class="label">已存金额</p>
            <p class="value success">¥ {{ totalSaved.toFixed(2) }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="overview-card">
          <div class="overview-item">
            <p class="label">剩余目标</p>
            <p class="value warning">¥ {{ (totalGoal - totalSaved).toFixed(2) }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作栏 -->
    <el-card class="action-card">
      <el-row justify="end">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新建计划</el-button>
      </el-row>
    </el-card>

    <!-- 存钱计划列表 -->
    <div class="plan-list">
      <el-card v-for="plan in planList" :key="plan.id" class="plan-card">
        <div class="plan-header">
          <div class="plan-info">
            <el-icon :size="40" :style="{ color: plan.color }">
              <Coin />
            </el-icon>
            <div>
              <h3>{{ plan.name }}</h3>
              <p class="plan-date">{{ plan.startDate }} ~ {{ plan.endDate }}</p>
            </div>
          </div>
          <div class="plan-actions">
            <el-button type="success" size="small" @click="handleDeposit(plan)">存入</el-button>
            <el-button type="primary" text size="small" @click="handleEdit(plan)">编辑</el-button>
            <el-button type="danger" text size="small" @click="handleDelete(plan)">删除</el-button>
          </div>
        </div>

        <div class="plan-progress">
          <div class="progress-info">
            <span>已存: ¥{{ plan.savedAmount.toFixed(2) }}</span>
            <span>目标: ¥{{ plan.goalAmount.toFixed(2) }}</span>
          </div>
          <el-progress :percentage="Math.min((plan.savedAmount / plan.goalAmount) * 100, 100)" :color="plan.color"
            :stroke-width="12" />
        </div>

        <div class="plan-stats">
          <div class="stat-item">
            <span class="label">完成度</span>
            <span class="value">{{ ((plan.savedAmount / plan.goalAmount) * 100).toFixed(1) }}%</span>
          </div>
          <div class="stat-item">
            <span class="label">剩余天数</span>
            <span class="value">{{ plan.remainingDays }} 天</span>
          </div>
          <div class="stat-item">
            <span class="label">建议日存</span>
            <span class="value">¥{{ plan.dailySuggestion.toFixed(2) }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 新建/编辑计划对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑计划' : '新建存钱计划'" width="500px">
      <el-form ref="formRef" :model="planForm" :rules="rules" label-width="100px">
        <el-form-item label="计划名称" prop="name">
          <el-input v-model="planForm.name" placeholder="如：旅游基金、应急储备" />
        </el-form-item>
        <el-form-item label="目标金额" prop="goalAmount">
          <el-input-number v-model="planForm.goalAmount" :min="0" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="起止日期" prop="dateRange">
          <el-date-picker v-model="planForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="主题颜色">
          <el-color-picker v-model="planForm.color" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="planForm.remark" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 存入对话框 -->
    <el-dialog v-model="depositVisible" title="存入金额" width="400px">
      <el-form label-width="80px">
        <el-form-item label="计划">
          <el-input :value="currentPlan?.name" disabled />
        </el-form-item>
        <el-form-item label="存入金额">
          <el-input-number v-model="depositAmount" :min="0" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="depositVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDeposit">确认存入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, Coin } from '@element-plus/icons-vue'
import { savingsApi, type SavingPlan } from '@/api'

interface PlanDisplay extends SavingPlan {
  remainingDays: number
  dailySuggestion: number
}

const dialogVisible = ref(false)
const depositVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const depositAmount = ref(0)
const currentPlan = ref<PlanDisplay | null>(null)

const planForm = reactive({
  id: undefined as number | undefined,
  name: '',
  goalAmount: 0,
  dateRange: [] as string[],
  color: '#409eff',
  remark: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  goalAmount: [{ required: true, message: '请输入目标金额', trigger: 'blur' }],
  dateRange: [{ required: true, message: '请选择起止日期', trigger: 'change' }]
}

const planList = ref<PlanDisplay[]>([])

const calcPlanDisplay = (plan: SavingPlan): PlanDisplay => {
  const endDate = new Date(plan.endDate)
  const today = new Date()
  const remainingDays = Math.max(0, Math.ceil((endDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24)))
  const remaining = plan.goalAmount - plan.savedAmount
  const dailySuggestion = remainingDays > 0 ? remaining / remainingDays : 0
  return {
    ...plan,
    remainingDays,
    dailySuggestion: Math.max(0, dailySuggestion)
  }
}

const fetchPlanList = async () => {
  try {
    const res = await savingsApi.getPlanList()
    planList.value = res.map(calcPlanDisplay)
  } catch (error) {
    // 错误已在拦截器中处理
  }
}

onMounted(() => {
  fetchPlanList()
})

const totalGoal = computed(() => planList.value.reduce((sum, p) => sum + p.goalAmount, 0))
const totalSaved = computed(() => planList.value.reduce((sum, p) => sum + p.savedAmount, 0))

const handleAdd = () => {
  isEdit.value = false
  Object.assign(planForm, {
    id: undefined,
    name: '',
    goalAmount: 0,
    dateRange: [],
    color: '#409eff',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (plan: PlanDisplay) => {
  isEdit.value = true
  Object.assign(planForm, {
    id: plan.id,
    name: plan.name,
    goalAmount: plan.goalAmount,
    dateRange: [plan.startDate, plan.endDate],
    color: plan.color,
    remark: plan.remark || ''
  })
  dialogVisible.value = true
}

const handleDelete = (plan: PlanDisplay) => {
  ElMessageBox.confirm('确定要删除这个存钱计划吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await savingsApi.deletePlan(plan.id)
      ElMessage.success('删除成功')
      fetchPlanList()
    } catch (error) {
      // 错误已在拦截器中处理
    }
  }).catch(() => { })
}

const handleDeposit = (plan: PlanDisplay) => {
  currentPlan.value = plan
  depositAmount.value = 0
  depositVisible.value = true
}

const confirmDeposit = async () => {
  if (depositAmount.value <= 0) {
    ElMessage.warning('请输入有效金额')
    return
  }
  if (currentPlan.value) {
    try {
      await savingsApi.deposit(currentPlan.value.id, depositAmount.value)
      ElMessage.success(`成功存入 ¥${depositAmount.value.toFixed(2)}`)
      depositVisible.value = false
      fetchPlanList()
    } catch (error) {
      // 错误已在拦截器中处理
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (planForm.dateRange.length < 2) return
        const data = {
          name: planForm.name,
          goalAmount: planForm.goalAmount,
          startDate: planForm.dateRange[0],
          endDate: planForm.dateRange[1],
          color: planForm.color,
          remark: planForm.remark
        }
        if (isEdit.value && planForm.id) {
          await savingsApi.updatePlan(planForm.id, data)
          ElMessage.success('编辑成功')
        } else {
          await savingsApi.addPlan(data)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchPlanList()
      } catch (error) {
        // 错误已在拦截器中处理
      }
    }
  })
}
</script>

<style scoped>
.savings {
  padding: 0;
}

.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  text-align: center;
}

.overview-item .label {
  color: #999;
  font-size: 14px;
  margin: 0 0 10px;
}

.overview-item .value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.overview-item .value.success {
  color: #67c23a;
}

.overview-item .value.warning {
  color: #e6a23c;
}

.action-card {
  margin-bottom: 20px;
}

.plan-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.plan-card {
  transition: box-shadow 0.3s;
}

.plan-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.plan-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.plan-info h3 {
  margin: 0 0 5px;
  font-size: 18px;
  color: #333;
}

.plan-date {
  margin: 0;
  color: #999;
  font-size: 13px;
}

.plan-progress {
  margin-bottom: 20px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.plan-stats {
  display: flex;
  gap: 40px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.stat-item .label {
  color: #999;
  font-size: 12px;
}

.stat-item .value {
  color: #333;
  font-size: 16px;
  font-weight: 500;
}
</style>
