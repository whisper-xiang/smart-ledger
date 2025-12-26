<template>
  <div class="bills">
    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="日期范围">
          <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 120px">
            <el-option label="收入" value="income" />
            <el-option label="支出" value="expense" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加账单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 账单列表 -->
    <el-card class="table-card">
      <el-table :data="billList" style="width: 100%" v-loading="loading">
        <el-table-column prop="billDate" label="日期" width="120" sortable />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'income' ? 'success' : 'danger'" size="small">
              {{ row.type === 'income' ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="amount" label="金额" width="120" align="right" sortable>
          <template #default="{ row }">
            <span :class="row.type === 'income' ? 'income-text' : 'expense-text'">
              {{ row.type === 'income' ? '+' : '-' }}¥{{ row.amount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handlePageChange" />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑账单' : '添加账单'" width="500px">
      <el-form ref="formRef" :model="billForm" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="billForm.type">
            <el-radio-button label="expense">支出</el-radio-button>
            <el-radio-button label="income">收入</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="billForm.amount" :min="0" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="billForm.category" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="cat in (billForm.type === 'income' ? incomeCategories : expenseCategories)" :key="cat"
              :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="billDate">
          <el-date-picker v-model="billForm.billDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="billForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { billApi, type Bill } from '@/api'

interface BillForm {
  id?: number
  type: string
  category: string
  amount: number
  billDate: string
  description: string
}

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const incomeCategories = ['工资', '奖金', '投资收益', '兼职', '红包', '其他收入']
const expenseCategories = ['餐饮', '交通', '购物', '娱乐', '居住', '医疗', '教育', '其他支出']
const categories = [...incomeCategories, ...expenseCategories]

const searchForm = reactive({
  dateRange: [] as string[],
  type: '',
  category: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const billForm = reactive<BillForm>({
  type: 'expense',
  amount: 0,
  category: '',
  billDate: '',
  description: ''
})

const rules: FormRules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  billDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const billList = ref<Bill[]>([])

const fetchBillList = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size
    }
    if (searchForm.type) params.type = searchForm.type
    if (searchForm.category) params.category = searchForm.category
    if (searchForm.dateRange?.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    const res = await billApi.getBillList(params)
    billList.value = res.records
    pagination.total = res.total
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchBillList()
})

const handleSearch = () => {
  pagination.page = 1
  fetchBillList()
}

const handleReset = () => {
  searchForm.dateRange = []
  searchForm.type = ''
  searchForm.category = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(billForm, {
    id: undefined,
    type: 'expense',
    amount: 0,
    category: '',
    billDate: new Date().toISOString().split('T')[0],
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: Bill) => {
  isEdit.value = true
  Object.assign(billForm, {
    id: row.id,
    type: row.type,
    category: row.category,
    amount: row.amount,
    billDate: row.billDate,
    description: row.description || ''
  })
  dialogVisible.value = true
}

const handleDelete = (row: Bill) => {
  ElMessageBox.confirm('确定要删除这条账单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await billApi.deleteBill(row.id)
      ElMessage.success('删除成功')
      fetchBillList()
    } catch (error) {
      // 错误已在拦截器中处理
    }
  }).catch(() => { })
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value && billForm.id) {
          await billApi.updateBill(billForm.id, billForm)
          ElMessage.success('编辑成功')
        } else {
          await billApi.addBill(billForm)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchBillList()
      } catch (error) {
        // 错误已在拦截器中处理
      }
    }
  })
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  fetchBillList()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchBillList()
}
</script>

<style scoped>
.bills {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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
