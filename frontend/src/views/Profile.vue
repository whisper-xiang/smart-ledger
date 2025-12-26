<template>
  <div class="profile" v-loading="loading">
    <el-row :gutter="20">
      <!-- 用户信息卡片 -->
      <el-col :span="8">
        <el-card class="user-card">
          <div class="user-avatar">
            <el-avatar :size="100" :icon="UserFilled" />
            <el-button type="primary" text size="small" class="change-avatar">
              更换头像
            </el-button>
          </div>
          <div class="user-info">
            <h2>{{ userInfo.username }}</h2>
            <p class="email">{{ userInfo.email }}</p>
            <p class="join-date">注册时间：{{ userInfo.joinDate }}</p>
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <p class="value">{{ userInfo.billCount }}</p>
              <p class="label">账单数</p>
            </div>
            <div class="stat-item">
              <p class="value">{{ userInfo.diaryCount }}</p>
              <p class="label">日记数</p>
            </div>
            <div class="stat-item">
              <p class="value">{{ userInfo.savingPlans }}</p>
              <p class="label">存钱计划</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 设置区域 -->
      <el-col :span="16">
        <el-card class="settings-card">
          <el-tabs v-model="activeTab">
            <!-- 基本信息 -->
            <el-tab-pane label="基本信息" name="basic">
              <el-form ref="basicFormRef" :model="basicForm" :rules="basicRules" label-width="100px"
                style="max-width: 500px">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="basicForm.username" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="basicForm.email" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="basicForm.phone" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSaveBasic">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 修改密码 -->
            <el-tab-pane label="修改密码" name="password">
              <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px"
                style="max-width: 500px">
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 偏好设置 -->
            <el-tab-pane label="偏好设置" name="preferences">
              <el-form label-width="120px" style="max-width: 500px">
                <el-form-item label="默认货币">
                  <el-select v-model="preferences.currency" style="width: 200px">
                    <el-option label="人民币 (CNY)" value="CNY" />
                    <el-option label="美元 (USD)" value="USD" />
                    <el-option label="欧元 (EUR)" value="EUR" />
                  </el-select>
                </el-form-item>
                <el-form-item label="每月预算">
                  <el-input-number v-model="preferences.monthlyBudget" :min="0" :step="100" />
                </el-form-item>
                <el-form-item label="提醒通知">
                  <el-switch v-model="preferences.notifications" />
                </el-form-item>
                <el-form-item label="深色模式">
                  <el-switch v-model="preferences.darkMode" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSavePreferences">保存设置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 数据管理 -->
            <el-tab-pane label="数据管理" name="data">
              <div class="data-section">
                <h4>导出数据</h4>
                <p>将您的账单数据导出为 Excel 或 CSV 格式</p>
                <el-button type="primary" @click="handleExport('excel')">导出 Excel</el-button>
                <el-button @click="handleExport('csv')">导出 CSV</el-button>
              </div>
              <el-divider />
              <div class="data-section danger">
                <h4>危险操作</h4>
                <p>清空所有数据或注销账号，此操作不可恢复</p>
                <el-button type="danger" plain @click="handleClearData">清空数据</el-button>
                <el-button type="danger" @click="handleDeleteAccount">注销账号</el-button>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'

const loading = ref(false)
const activeTab = ref('basic')
const basicFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const userInfo = reactive({
  username: '',
  email: '',
  phone: '',
  joinDate: '',
  billCount: 0,
  diaryCount: 0,
  savingPlans: 0
})

const basicForm = reactive({
  username: '',
  email: '',
  phone: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const preferences = reactive({
  currency: 'CNY',
  monthlyBudget: 5000,
  notifications: true,
  darkMode: false
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const info = await userApi.getUserInfo()
    userInfo.username = info.username || ''
    userInfo.email = info.email || ''
    userInfo.phone = info.phone || ''
    userInfo.joinDate = info.createTime ? info.createTime.split('T')[0] : ''

    // 同步到表单
    basicForm.username = info.username || ''
    basicForm.email = info.email || ''
    basicForm.phone = info.phone || ''

    // 同步偏好设置
    preferences.currency = info.currency || 'CNY'
    preferences.monthlyBudget = info.monthlyBudget || 5000
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

// 加载用户统计
const loadUserStatistics = async () => {
  try {
    const stats = await userApi.getUserStatistics()
    userInfo.billCount = stats.billCount || 0
    userInfo.diaryCount = stats.diaryCount || 0
    userInfo.savingPlans = stats.savingPlanCount || 0
  } catch (error) {
    console.error('加载用户统计失败:', error)
  }
}

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    await Promise.all([loadUserInfo(), loadUserStatistics()])
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAllData()
})

const basicRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleSaveBasic = async () => {
  if (!basicFormRef.value) return
  await basicFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userApi.updateUserInfo({
          username: basicForm.username,
          email: basicForm.email,
          phone: basicForm.phone
        })
        userInfo.username = basicForm.username
        userInfo.email = basicForm.email
        userInfo.phone = basicForm.phone
        ElMessage.success('保存成功')
      } catch (error: any) {
        ElMessage.error(error.message || '保存失败')
      }
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userApi.updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功')
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error: any) {
        ElMessage.error(error.message || '密码修改失败')
      }
    }
  })
}

const handleSavePreferences = async () => {
  try {
    await userApi.updateUserInfo({
      currency: preferences.currency,
      monthlyBudget: preferences.monthlyBudget
    })
    ElMessage.success('设置已保存')
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

const handleExport = (format: string) => {
  ElMessage.success(`正在导出 ${format.toUpperCase()} 文件...`)
}

const handleClearData = () => {
  ElMessageBox.confirm('确定要清空所有数据吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('数据已清空')
  }).catch(() => { })
}

const handleDeleteAccount = () => {
  ElMessageBox.confirm('确定要注销账号吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('账号已注销')
  }).catch(() => { })
}
</script>

<style scoped>
.profile {
  padding: 0;
}

.user-card {
  text-align: center;
}

.user-avatar {
  margin-bottom: 20px;
}

.change-avatar {
  margin-top: 10px;
}

.user-info h2 {
  margin: 0 0 10px;
  color: #333;
}

.user-info .email {
  color: #666;
  margin: 0 0 5px;
}

.user-info .join-date {
  color: #999;
  font-size: 13px;
  margin: 0;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.user-stats .stat-item {
  text-align: center;
}

.user-stats .value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin: 0 0 5px;
}

.user-stats .label {
  color: #999;
  font-size: 13px;
  margin: 0;
}

.settings-card {
  min-height: 500px;
}

.data-section {
  margin-bottom: 20px;
}

.data-section h4 {
  margin: 0 0 10px;
  color: #333;
}

.data-section p {
  color: #666;
  margin: 0 0 15px;
}

.data-section.danger h4 {
  color: #f56c6c;
}
</style>
