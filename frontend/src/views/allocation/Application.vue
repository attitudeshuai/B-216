<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">我的入住申请</span>
            <span class="header-subtitle">查看和管理入住申请记录</span>
          </div>
          <el-button type="primary" icon="Plus" @click="dialogVisible = true">提交申请</el-button>
        </div>
      </template>
      
      <el-table :data="list" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="createTime" label="申请时间" min-width="160">
           <template #default="scope">
             <span>{{ formatDateTime(scope.row.createTime) }}</span>
           </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请理由" min-width="200" show-overflow-tooltip />
        <el-table-column prop="preferredBuilding" label="意向楼宇" min-width="150">
           <template #default="scope">
             <div class="building-cell">
               <el-icon><OfficeBuilding /></el-icon>
               <span>{{ scope.row.preferredBuilding }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="preferredRoomType" label="意向房型" min-width="120">
           <template #default="scope">
             <div class="room-type-cell">
               <el-icon><House /></el-icon>
               <span>{{ scope.row.preferredRoomType }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'APPROVED' ? 'success' : (scope.row.status === 'REJECTED' ? 'danger' : 'warning')" effect="light" round>
              {{ {'PENDING': '待审核', 'APPROVED': '已通过', 'REJECTED': '已拒绝'}[scope.row.status] || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="提交申请" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px" class="dialog-form">
        <el-form-item label="意向楼宇">
          <el-select v-model="form.preferredBuilding" placeholder="请选择意向楼宇" style="width: 100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="意向房型">
          <el-select v-model="form.preferredRoomType" style="width: 100%">
            <el-option label="四人间" value="四人间" />
            <el-option label="六人间" value="六人间" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input type="textarea" v-model="form.reason" :rows="3" placeholder="请输入申请理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit" :loading="submitLoading">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyApplications, createApplication, getBuildings } from '../../api'
import { ElMessage } from 'element-plus'
import { Plus, House, OfficeBuilding } from '@element-plus/icons-vue'

const list = ref([])
const buildings = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const form = ref({ preferredBuilding: '', preferredRoomType: '', reason: '' })

// Get user from localStorage
const user = JSON.parse(localStorage.getItem('user') || '{}')
const studentId = user.id

const load = async () => {
  if (!studentId) return
  loading.value = true
  try {
      const results = await Promise.allSettled([getMyApplications(studentId), getBuildings()])
      
      if (results[0].status === 'fulfilled') {
        const res = results[0].value
        list.value = res.data || res
      }
      
      if (results[1].status === 'fulfilled') {
        const resBuildings = results[1].value
        buildings.value = resBuildings.data || resBuildings
      }
  } catch (e) {
      console.error(e)
  } finally {
    loading.value = false
  }
}

const formatDateTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const submit = async () => {
  if (!studentId) {
    ElMessage.error('无法获取用户信息')
    return
  }
  
  submitLoading.value = true
  try {
    await createApplication({ ...form.value, student: { id: studentId } })
    ElMessage.success('提交成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  flex-direction: column;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-subtitle {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.building-cell, .room-type-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
