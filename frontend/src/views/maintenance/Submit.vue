<template>
  <div class="app-container">
    <el-card shadow="never" class="form-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">维修申报</span>
            <span class="header-subtitle">提交宿舍维修申请</span>
          </div>
        </div>
      </template>

      <el-form :model="form" label-width="100px" ref="formRef" :rules="rules">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入维修标题" />
        </el-form-item>
        <el-form-item label="故障类型" prop="faultType">
          <el-select v-model="form.faultType" placeholder="请选择故障类型" style="width: 100%;">
            <el-option label="水电故障" value="水电故障" />
            <el-option label="电器故障" value="电器故障" />
            <el-option label="门窗损坏" value="门窗损坏" />
            <el-option label="家具损坏" value="家具损坏" />
            <el-option label="网络问题" value="网络问题" />
            <el-option label="其他问题" value="其他问题" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度" prop="emergencyLevel">
          <el-radio-group v-model="form.emergencyLevel">
            <el-radio label="一般">一般</el-radio>
            <el-radio label="紧急">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="5" placeholder="请详细描述维修问题" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交申请</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="list-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">我的申请</span>
            <span class="header-subtitle">查看提交的维修申请</span>
          </div>
          <el-select v-model="filterStatus" placeholder="按状态筛选" clearable @change="loadMyMaintenances" style="width: 140px;">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="DONE" />
          </el-select>
        </div>
      </template>

      <el-table :data="myList" style="width: 100%" v-loading="listLoading" stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="faultType" label="故障类型" width="120">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.faultType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="emergencyLevel" label="紧急程度" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.emergencyLevel === '紧急' ? 'danger' : 'info'" effect="plain" size="small">
              {{ scope.row.emergencyLevel || '一般' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportTime" label="申报时间" width="170">
          <template #default="scope">
            <span>{{ formatTime(scope.row.reportTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="completeTime" label="完成时间" width="170">
          <template #default="scope">
            <span>{{ formatTime(scope.row.completeTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="light" round size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理结果" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.result || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { createMaintenance, getMyMaintenances } from '../../api'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const loading = ref(false)
const listLoading = ref(false)
const filterStatus = ref('')
const myList = ref([])
const user = JSON.parse(localStorage.getItem('user') || '{}')

const form = reactive({
  title: '',
  faultType: '',
  emergencyLevel: '一般',
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  faultType: [{ required: true, message: '请选择故障类型', trigger: 'change' }],
  emergencyLevel: [{ required: true, message: '请选择紧急程度', trigger: 'change' }],
  description: [{ required: true, message: '请输入问题描述', trigger: 'blur' }]
}

const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

const getStatusText = (status) => {
  const map = { 'PENDING': '待处理', 'PROCESSING': '处理中', 'DONE': '已完成' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { 'PENDING': 'warning', 'PROCESSING': 'primary', 'DONE': 'success' }
  return map[status] || 'info'
}

const loadMyMaintenances = async () => {
  listLoading.value = true
  try {
    const res = await getMyMaintenances(user.id, filterStatus.value || undefined)
    myList.value = res
  } finally {
    listLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await createMaintenance({ ...form }, user.id)
      ElMessage.success('提交成功')
      handleReset()
      loadMyMaintenances()
    } finally {
      loading.value = false
    }
  })
}

const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.faultType = ''
  form.emergencyLevel = '一般'
}

onMounted(() => {
  loadMyMaintenances()
})
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

.form-card {
  margin-bottom: 20px;
}
</style>
