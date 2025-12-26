<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <el-icon :size="48" color="#409eff">
          <Wallet />
        </el-icon>
        <h1>创建账号</h1>
        <p>加入 Smart Ledger，开始智能记账</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" size="large" />
        </el-form-item>

        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" :prefix-icon="Message" size="large" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large"
            show-password />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" :prefix-icon="Lock" size="large"
            show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="register-btn" @click="handleRegister">
            注 册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Wallet, User, Lock, Message } from '@element-plus/icons-vue'
import { authApi } from '@/api'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await authApi.register({
          username: form.username,
          password: form.password,
          email: form.email
        })
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        // 错误已在拦截器中处理
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  margin: 15px 0 5px;
  color: #333;
  font-size: 28px;
}

.register-header p {
  color: #999;
  font-size: 14px;
}

.register-form {
  width: 100%;
}

.register-btn {
  width: 100%;
}

.register-footer {
  text-align: center;
  color: #999;
}

.register-footer a {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.register-footer a:hover {
  text-decoration: underline;
}
</style>
