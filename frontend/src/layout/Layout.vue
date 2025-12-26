<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon :size="28">
          <Wallet />
        </el-icon>
        <span v-show="!isCollapse" class="logo-text">Smart Ledger</span>
      </div>
      <el-menu :default-active="activeMenu" :collapse="isCollapse" :collapse-transition="false" router class="menu">
        <el-menu-item index="/dashboard">
          <el-icon>
            <DataAnalysis />
          </el-icon>
          <template #title>首页概览</template>
        </el-menu-item>
        <el-menu-item index="/bills">
          <el-icon>
            <List />
          </el-icon>
          <template #title>账单管理</template>
        </el-menu-item>
        <el-menu-item index="/diary">
          <el-icon>
            <Notebook />
          </el-icon>
          <template #title>记账日记</template>
        </el-menu-item>
        <el-menu-item index="/savings">
          <el-icon>
            <Coin />
          </el-icon>
          <template #title>存钱计划</template>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon>
            <PieChart />
          </el-icon>
          <template #title>统计分析</template>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon>
            <User />
          </el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Wallet,
  DataAnalysis,
  List,
  Notebook,
  Coin,
  PieChart,
  User,
  Fold,
  Expand,
  UserFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const isCollapse = ref(false)
const username = ref(localStorage.getItem('username') || '用户')

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title as string || '')

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-text {
  white-space: nowrap;
}

.menu {
  border-right: none;
  background: transparent;
}

.menu .el-menu-item {
  color: rgba(255, 255, 255, 0.7);
}

.menu .el-menu-item:hover,
.menu .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  color: #333;
}

.main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
