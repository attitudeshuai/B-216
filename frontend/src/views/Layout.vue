<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="aside">
      <div class="logo-container">
        <el-icon :size="24" class="logo-icon"><School /></el-icon>
        <span class="logo-text">智慧宿管系统</span>
      </div>
      
      <el-menu
        router
        :default-active="route.path"
        class="el-menu-vertical"
        background-color="transparent"
        text-color="#fff"
        active-text-color="#ffd04b"
        :unique-opened="true">
        
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页仪表盘</span>
        </el-menu-item>
        
        <el-menu-item index="/users" v-if="user?.role === 'admin'">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/dormitory" v-if="['admin', 'staff'].includes(user?.role)">
          <el-icon><House /></el-icon>
          <span>宿舍资源</span>
        </el-menu-item>
        
        <el-sub-menu index="/allocation">
          <template #title>
            <el-icon><Guide /></el-icon>
            <span>分配管理</span>
          </template>
          <el-menu-item index="/allocation/application" v-if="user?.role === 'student'">入住申请</el-menu-item>
          <el-menu-item index="/allocation/manage" v-if="['admin', 'staff'].includes(user?.role)">分配处理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/fee">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>费用管理</span>
          </template>
          <el-menu-item index="/fee/list">缴费记录</el-menu-item>
          <el-menu-item index="/fee/standards" v-if="['admin', 'staff'].includes(user?.role)">费用标准</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/maintenance">
          <el-icon><Tools /></el-icon>
          <span>维修管理</span>
        </el-menu-item>

        <el-menu-item index="/safety" v-if="['admin', 'staff'].includes(user?.role)">
          <el-icon><Lock /></el-icon>
          <span>安全管理</span>
        </el-menu-item>
        
        <el-menu-item index="/hygiene" v-if="['admin', 'staff'].includes(user?.role)">
          <el-icon><Brush /></el-icon>
          <span>卫生管理</span>
        </el-menu-item>
        
        <el-menu-item index="/notice" v-if="user?.role !== 'student'">
          <el-icon><Bell /></el-icon>
          <span>公告通知</span>
        </el-menu-item>
        
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
           <el-breadcrumb separator="/">
             <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
             <el-breadcrumb-item>{{ route.meta.title || route.name }}</el-breadcrumb-item>
           </el-breadcrumb>
        </div>
        <div class="header-right">
          <div class="user-info">
            <el-avatar :size="32" icon="UserFilled" class="user-avatar" />
            <span class="user-name">{{ user?.name || '用户' }}</span>
            <span class="user-role-tag">{{ formatRole(user?.role) }}</span>
          </div>
          <el-button type="danger" plain size="small" @click="logout" icon="SwitchButton">退出</el-button>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <transition name="fade-transform" mode="out-in">
          <router-view />
        </transition>
      </el-main>
      
      <el-footer class="footer">
        <p>&copy; 2026 智慧宿舍管理系统 | Dormitory Management System</p>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { School, SwitchButton, UserFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const user = ref(null)

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
  } else {
    router.push('/login')
  }
})

const formatRole = (role) => {
  const map = {
    'admin': '管理员',
    'student': '学生',
    'staff': '宿管'
  }
  return map[role] || role
}

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
}

.aside {
  background: linear-gradient(135deg, #66a6ff 0%, #89f7fe 100%);
  color: #fff;
  transition: width 0.3s;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
  display: flex;
  flex-direction: column;
}

.logo-container {
  height: 60px;
  line-height: 60px;
  background-color: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-weight: 600;
  font-size: 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  color: #fff;
}

.el-menu-vertical {
  border-right: none;
  flex: 1;
}

/* Header Styles */
.header {
  height: 60px;
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0,0,0,0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.05);
  z-index: 9;
}

.user-info {
  display: flex;
  align-items: center;
  margin-right: 20px;
  cursor: pointer;
}

.user-avatar {
  background-color: #409EFF;
  margin-right: 8px;
}

.user-name {
  font-size: 14px;
  color: #606266;
  margin-right: 8px;
  font-weight: 500;
}

.user-role-tag {
  font-size: 12px;
  background-color: #f0f9eb;
  color: #67c23a;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #e1f3d8;
}

.header-right {
  display: flex;
  align-items: center;
}

/* Main Content Styles */
.main-container {
  flex-direction: column;
  background-color: #f0f2f5;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  overflow: hidden;
}

.main-content {
  padding: 20px;
  overflow-y: auto;
}

.footer {
  height: 40px;
  line-height: 40px;
  text-align: center;
  font-size: 12px;
  color: #909399;
  background-color: transparent;
  border-top: 1px solid rgba(0,0,0,0.05);
}

/* Transition */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>