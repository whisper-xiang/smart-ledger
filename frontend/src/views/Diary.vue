<template>
  <div class="diary">
    <!-- 操作栏 -->
    <el-card class="action-card">
      <el-row justify="space-between" align="middle">
        <el-col :span="12">
          <el-date-picker v-model="selectedMonth" type="month" placeholder="选择月份" value-format="YYYY-MM"
            @change="handleMonthChange" />
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" :icon="Plus" @click="handleAdd">写日记</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 日记列表 -->
    <div class="diary-list">
      <el-empty v-if="diaryList.length === 0" description="暂无日记，快来记录吧~" />

      <el-card v-for="diary in diaryList" :key="diary.id" class="diary-card">
        <template #header>
          <div class="diary-header">
            <div class="diary-date">
              <el-icon>
                <Calendar />
              </el-icon>
              <span>{{ diary.diaryDate }}</span>
              <el-tag v-if="diary.mood" :type="getMoodType(diary.mood)" size="small">
                {{ diary.mood }}
              </el-tag>
            </div>
            <div class="diary-actions">
              <el-button type="primary" text size="small" @click="handleEdit(diary)">
                <el-icon>
                  <Edit />
                </el-icon>
              </el-button>
              <el-button type="danger" text size="small" @click="handleDelete(diary)">
                <el-icon>
                  <Delete />
                </el-icon>
              </el-button>
            </div>
          </div>
        </template>

        <div class="diary-content">
          <h3>{{ diary.title }}</h3>
          <p>{{ diary.content }}</p>
        </div>

      </el-card>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑日记' : '写日记'" width="600px">
      <el-form ref="formRef" :model="diaryForm" :rules="rules" label-width="80px">
        <el-form-item label="日期" prop="diaryDate">
          <el-date-picker v-model="diaryForm.diaryDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="diaryForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="心情" prop="mood">
          <el-radio-group v-model="diaryForm.mood">
            <el-radio-button label="开心">😊 开心</el-radio-button>
            <el-radio-button label="平静">😐 平静</el-radio-button>
            <el-radio-button label="难过">😢 难过</el-radio-button>
            <el-radio-button label="焦虑">😰 焦虑</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="diaryForm.content" type="textarea" :rows="6" placeholder="记录今天的财务心得..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, Calendar, Edit, Delete } from '@element-plus/icons-vue'
import { diaryApi, type Diary } from '@/api'

interface DiaryForm {
  id?: number
  diaryDate: string
  title: string
  content: string
  mood: string
}

const selectedMonth = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const diaryForm = reactive<DiaryForm>({
  diaryDate: '',
  title: '',
  content: '',
  mood: ''
})

const rules: FormRules = {
  diaryDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const diaryList = ref<Diary[]>([])

const fetchDiaryList = async () => {
  try {
    const params: any = { page: 1, size: 50 }
    if (selectedMonth.value) {
      const [year, month] = selectedMonth.value.split('-')
      const startDate = `${year}-${month}-01`
      const lastDay = new Date(parseInt(year), parseInt(month), 0).getDate()
      const endDate = `${year}-${month}-${lastDay}`
      params.startDate = startDate
      params.endDate = endDate
    }
    const res = await diaryApi.getDiaryList(params)
    diaryList.value = res.records
  } catch (error) {
    // 错误已在拦截器中处理
  }
}

onMounted(() => {
  fetchDiaryList()
})

const getMoodType = (mood: string) => {
  const moodMap: Record<string, string> = {
    '开心': 'success',
    '平静': 'info',
    '难过': 'warning',
    '焦虑': 'danger'
  }
  return moodMap[mood] || 'info'
}

const handleMonthChange = () => {
  fetchDiaryList()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(diaryForm, {
    id: undefined,
    diaryDate: new Date().toISOString().split('T')[0],
    title: '',
    content: '',
    mood: ''
  })
  dialogVisible.value = true
}

const handleEdit = (diary: Diary) => {
  isEdit.value = true
  Object.assign(diaryForm, {
    id: diary.id,
    diaryDate: diary.diaryDate,
    title: diary.title,
    content: diary.content,
    mood: diary.mood || ''
  })
  dialogVisible.value = true
}

const handleDelete = (diary: Diary) => {
  ElMessageBox.confirm('确定要删除这篇日记吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await diaryApi.deleteDiary(diary.id)
      ElMessage.success('删除成功')
      fetchDiaryList()
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
        if (isEdit.value && diaryForm.id) {
          await diaryApi.updateDiary(diaryForm.id, diaryForm)
          ElMessage.success('编辑成功')
        } else {
          await diaryApi.addDiary(diaryForm)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchDiaryList()
      } catch (error) {
        // 错误已在拦截器中处理
      }
    }
  })
}
</script>

<style scoped>
.diary {
  padding: 0;
}

.action-card {
  margin-bottom: 20px;
}

.diary-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.diary-card {
  transition: box-shadow 0.3s;
}

.diary-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.diary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.diary-date {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.diary-actions {
  display: flex;
  gap: 5px;
}

.diary-content h3 {
  margin: 0 0 10px;
  color: #333;
  font-size: 18px;
}

.diary-content p {
  margin: 0;
  color: #666;
  line-height: 1.8;
}

.diary-footer {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 20px;
}

.diary-footer .income {
  color: #67c23a;
}

.diary-footer .expense {
  color: #f56c6c;
}
</style>
