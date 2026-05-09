<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- Left Hero Section (Same as Login for consistency) -->
      <div class="login-hero">
        <div class="hero-content">
          <div class="logo-area">
            <el-icon :size="40" color="#fff"><School /></el-icon>
            <h1 class="app-name">智慧宿管</h1>
          </div>
          <div class="slogan">
            <h2>加入我们</h2>
            <p>开启便捷的校园生活体验</p>
          </div>
          <ul class="features">
            <li><el-icon><Check /></el-icon> 快速注册账号</li>
            <li><el-icon><Check /></el-icon> 完善个人信息</li>
            <li><el-icon><Check /></el-icon> 即刻申请入住</li>
          </ul>
        </div>
        <div class="circle c1"></div>
        <div class="circle c2"></div>
      </div>

      <!-- Right Form Section -->
      <div class="login-form-section">
        <div class="form-content">
          <h2 class="welcome-text">创建账号</h2>
          <p class="sub-text">请填写以下信息完成注册</p>
          
          <el-form :model="form" :rules="rules" ref="formRef" size="large" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="设置用户名" 
                prefix-icon="User" 
              />
            </el-form-item>
            <el-form-item prop="name">
              <el-input 
                v-model="form.name" 
                placeholder="真实姓名" 
                prefix-icon="Avatar" 
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="设置密码" 
                prefix-icon="Lock" 
                show-password 
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input 
                v-model="form.phone" 
                placeholder="手机号码" 
                prefix-icon="Iphone" 
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="submit-btn" @click="handleRegister" :loading="loading">
                立即注册
              </el-button>
            </el-form-item>
            <div class="form-footer">
              <span class="no-account">已有账号？</span>
              <router-link to="/login" class="link-btn">直接登录</router-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api'
import { ElMessage } from 'element-plus'
import { User, Lock, Avatar, Iphone, School, Check } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  name: '',
  password: '',
  phone: ''
})

const validatePhone = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register(form.value)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        // Error handled in interceptor
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-wrapper {
  width: 1000px;
  height: 600px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0,0,0,0.1);
  display: flex;
  overflow: hidden;
}

/* Left Side */
.login-hero {
  flex: 1;
  background: linear-gradient(135deg, #3a7bd5 0%, #00d2ff 100%);
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;
  color: #fff;
  overflow: hidden;
}

.hero-content {
  z-index: 2;
  position: relative;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
}

.app-name {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
  letter-spacing: 2px;
}

.slogan h2 {
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: 600;
}

.slogan p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.features {
  list-style: none;
  padding: 0;
  margin: 0;
}

.features li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  margin-bottom: 15px;
  opacity: 0.9;
}

/* Decorative Circles */
.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255,255,255,0.1);
}

.c1 {
  width: 300px;
  height: 300px;
  top: -50px;
  left: -50px;
}

.c2 {
  width: 200px;
  height: 200px;
  bottom: -30px;
  right: -30px;
}

/* Right Side */
.login-form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  background-color: #fff;
}

.form-content {
  width: 320px;
}

.welcome-text {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
  font-weight: bold;
}

.sub-text {
  color: #999;
  margin-bottom: 30px;
  font-size: 14px;
}

.login-form {
  width: 100%;
}

.submit-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 10px;
  font-weight: bold;
  letter-spacing: 1px;
}

.form-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
}

.no-account {
  color: #666;
}

.link-btn {
  color: #409eff;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: color 0.3s;
}

.link-btn:hover {
  color: #66b1ff;
}
</style>