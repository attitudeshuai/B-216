<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">维修申报</span>
            <span class="header-subtitle">提交宿舍维修申请，查看处理进度</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openDialog()">提交维修申请</el-button>
        </div>
      </template>

      <el-table :data="list" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
        <el-table-column prop="faultType" label="故障类型" min-width="100" align="center">
          <template #default="scope">
            <el-tag effect="plain">{{ scope.row.faultType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reportTime" label="申报时间" min-width="150">
          <template #default="scope">
            <span>{{ formatTime(scope.row.reportTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="处理状态" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="statusTagType(scope.row.status)" effect="light" round>
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="result" label="处理结果" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.result || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="提交维修申请" width="520px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px" class="dialog-form">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请简要描述维修问题" />
        </el-form-item>
        <el-form-item label="故障类型" prop="faultType">
          <el-select v-model="form.faultType" placeholder="请选择故障类型" style="width: 100%">
            <el-option v-for="ft in faultTypes" :key="ft" :label="ft" :value="ft" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="4" placeholder="请详细描述需要维修的问题" />
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
import { getMyMaintenances, createMaintenance } from '../../api'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const faultTypes = ['水电故障', '门窗损坏', '家具损坏', '网络故障', '墙面地面', '其他']

const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({ title: '', faultType: '', description: '' })
const user = JSON.parse(localStorage.getItem('user') || '{}')

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  faultType: [{ required: true, message: '请选择故障类型', trigger: 'change' }],
  description: [{ required: true, message: '请描述问题', trigger: 'blur' }]
}

const statusTagType = (status) => {
  const map = { '待处理': 'warning', '处理中': 'primary', '已完成': 'success' }
  return map[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const load = async () => {
  loading.value = true
  try {
    const res = await getMyMaintenances(user.id)
    list.value = res
  } finally {
    loading.value = false
  }
}

const openDialog = () => {
  form.value = { title: '', faultType: '', description: '' }
  dialogVisible.value = true
}

const submit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await createMaintenance({
      ...form.value,
      student: { id: user.id }
    })
    ElMessage.success('维修申请提交成功')
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

.dialog-form {
  padding: 10px 20px 0;
}
</style>
