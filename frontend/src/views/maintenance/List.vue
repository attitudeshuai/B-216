<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">报修管理</span>
            <span class="header-subtitle">处理学生宿舍报修请求</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openDialog()" v-if="user?.role === 'student'">提交报修</el-button>
        </div>
      </template>
      
      <el-table :data="list" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="description" label="报修内容" min-width="200" show-overflow-tooltip>
           <template #default="scope">
             <div class="desc-cell">
               <el-icon><Tools /></el-icon>
               <span>{{ scope.row.description }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="emergencyLevel" label="紧急程度" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.emergencyLevel === '紧急' ? 'danger' : 'info'" effect="plain">
              {{ scope.row.emergencyLevel || '一般' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportTime" label="报修时间" min-width="150">
           <template #default="scope">
             <div class="date-cell">
               <el-icon><Calendar /></el-icon>
               <span>{{ formatTime(scope.row.reportTime) }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'DONE' ? 'success' : (scope.row.status === 'PROCESSING' ? 'primary' : 'warning')" effect="light" round>
              {{ {'PENDING': '待处理', 'DONE': '已完成', 'PROCESSING': '处理中'}[scope.row.status] || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.status !== 'DONE'" size="small" type="success" plain icon="Check" @click="complete(scope.row)">标记完成</el-button>
            <el-button size="small" type="primary" plain icon="Edit" @click="openDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain icon="Delete" @click="handleDelete(scope.row.id)" v-if="user?.role === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑报修' : '提交报修'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px" class="dialog-form">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="紧急程度" required>
          <el-select v-model="form.emergencyLevel" placeholder="请选择紧急程度">
            <el-option label="一般" value="一般" />
            <el-option label="紧急" value="紧急" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修内容">
          <el-input type="textarea" v-model="form.description" :rows="4" placeholder="请详细描述报修内容" />
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
import { getMaintenances, getMyMaintenances, createMaintenance, updateMaintenance, deleteMaintenance } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Check, Calendar, Tools } from '@element-plus/icons-vue'

const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const form = ref({ title: '', description: '', emergencyLevel: '一般' })
const user = JSON.parse(localStorage.getItem('user') || '{}')

const load = async () => {
  loading.value = true
  try {
    if (user.role === 'student') {
      const res = await getMyMaintenances(user.id)
      list.value = res
    } else {
      const res = await getMaintenances()
      list.value = res
    }
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const openDialog = (row) => {
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { title: '', description: '', emergencyLevel: '一般' }
  }
  dialogVisible.value = true
}

const submit = async () => {
  submitLoading.value = true
  try {
    const formatDate = (date) => {
      const d = new Date(date)
      const pad = (n) => n < 10 ? '0' + n : n
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    }

    if (form.value.id) {
      await updateMaintenance(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createMaintenance({ 
        ...form.value, 
        student: { id: user.id }, 
        status: 'PENDING', 
        reportTime: formatDate(new Date()) 
      })
      ElMessage.success('提交成功')
    }
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const complete = async (row) => {
  try {
    await updateMaintenance(row.id, { ...row, status: 'DONE', finishTime: new Date() })
    ElMessage.success('已标记完成')
    load()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该报修记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMaintenance(id)
      ElMessage.success('删除成功')
      load()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  })
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

.desc-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}
</style>
