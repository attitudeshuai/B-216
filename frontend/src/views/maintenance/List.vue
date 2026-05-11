<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">报修管理</span>
            <span class="header-subtitle">处理学生宿舍报修请求</span>
          </div>
          <div class="header-right">
            <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 140px; margin-right: 12px" @change="load">
              <el-option label="待处理" value="PENDING" />
              <el-option label="处理中" value="PROCESSING" />
              <el-option label="已完成" value="DONE" />
            </el-select>
            <el-button type="primary" icon="Plus" @click="openDialog()" v-if="user?.role === 'student'">提交报修</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="list" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="title" label="标题" min-width="120" show-overflow-tooltip />
        <el-table-column prop="faultType" label="故障类型" min-width="100" align="center">
          <template #default="scope">
            <el-tag type="info" effect="plain">{{ scope.row.faultType || '其他' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="报修内容" min-width="180" show-overflow-tooltip>
           <template #default="scope">
             <div class="desc-cell">
               <el-icon><Tools /></el-icon>
               <span>{{ scope.row.description }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="emergencyLevel" label="紧急程度" min-width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.emergencyLevel === '紧急' ? 'danger' : 'info'" effect="plain">
              {{ scope.row.emergencyLevel || '一般' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请人" min-width="100" align="center" v-if="user?.role !== 'student'">
          <template #default="scope">
            <span>{{ scope.row.student?.name || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reportTime" label="报修时间" min-width="140">
           <template #default="scope">
             <div class="date-cell">
               <el-icon><Calendar /></el-icon>
               <span>{{ formatTime(scope.row.reportTime) }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="90" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="light" round>
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="scope">
            <template v-if="user?.role !== 'student' && scope.row.status !== 'DONE'">
              <el-button v-if="scope.row.status === 'PENDING'" size="small" type="primary" plain icon="VideoPlay" @click="updateStatus(scope.row, 'PROCESSING')">开始处理</el-button>
              <el-button v-if="scope.row.status === 'PROCESSING'" size="small" type="success" plain icon="Check" @click="updateStatus(scope.row, 'DONE')">标记完成</el-button>
            </template>
            <el-button size="small" type="primary" plain icon="Edit" @click="openDialog(scope.row)" v-if="user?.role === 'student' || scope.row.status === 'PENDING'">编辑</el-button>
            <el-button size="small" type="danger" plain icon="Delete" @click="handleDelete(scope.row.id)" v-if="user?.role === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑报修' : '提交报修'" width="520px" destroy-on-close>
      <el-form :model="form" label-width="90px" class="dialog-form">
        <el-form-item label="报修标题" required>
          <el-input v-model="form.title" placeholder="请输入报修标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="故障类型" required>
          <el-select v-model="form.faultType" placeholder="请选择故障类型" style="width: 100%">
            <el-option label="水电故障" value="水电故障" />
            <el-option label="家具损坏" value="家具损坏" />
            <el-option label="电器故障" value="电器故障" />
            <el-option label="门窗损坏" value="门窗损坏" />
            <el-option label="网络问题" value="网络问题" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度" required>
          <el-radio-group v-model="form.emergencyLevel">
            <el-radio label="一般">一般</el-radio>
            <el-radio label="紧急">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="问题描述" required>
          <el-input type="textarea" v-model="form.description" :rows="4" placeholder="请详细描述报修内容，包括具体位置、问题现象等" maxlength="500" show-word-limit />
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
import { getMaintenances, getMyMaintenances, createMaintenance, updateMaintenance, updateMaintenanceStatus, deleteMaintenance } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Check, Calendar, Tools, VideoPlay } from '@element-plus/icons-vue'

const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const filterStatus = ref('')
const form = ref({ title: '', description: '', faultType: '', emergencyLevel: '一般' })
const user = JSON.parse(localStorage.getItem('user') || '{}')

const load = async () => {
  loading.value = true
  try {
    const status = filterStatus.value || undefined
    if (user.role === 'student') {
      const res = await getMyMaintenances(user.id, status)
      list.value = res
    } else {
      const res = await getMaintenances(status)
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

const getStatusType = (status) => {
  const map = { PENDING: 'warning', PROCESSING: 'primary', DONE: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待处理', PROCESSING: '处理中', DONE: '已完成' }
  return map[status] || status
}

const openDialog = (row) => {
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { title: '', description: '', faultType: '', emergencyLevel: '一般' }
  }
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.value.title || !form.value.faultType || !form.value.description) {
    ElMessage.warning('请填写完整信息')
    return
  }
  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateMaintenance(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createMaintenance({ 
        ...form.value, 
        student: { id: user.id }
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

const updateStatus = async (row, status) => {
  const text = status === 'PROCESSING' ? '开始处理' : '标记完成'
  ElMessageBox.confirm(`确认${text}该报修申请吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateMaintenanceStatus(row.id, status)
      ElMessage.success('操作成功')
      load()
    } catch (e) {
      ElMessage.error('操作失败')
    }
  })
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

.header-right {
  display: flex;
  align-items: center;
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

.dialog-form {
  padding: 10px 0;
}
</style>