<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">维修处理</span>
            <span class="header-subtitle">管理学生维修申请</span>
          </div>
          <el-select v-model="filterStatus" placeholder="按状态筛选" clearable @change="load" style="width: 140px;">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="DONE" />
          </el-select>
        </div>
      </template>
      
      <el-table :data="list" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="faultType" label="故障类型" width="120">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.faultType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="问题描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="emergencyLevel" label="紧急程度" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.emergencyLevel === '紧急' ? 'danger' : 'info'" effect="plain" size="small">
              {{ scope.row.emergencyLevel || '一般' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="学生" width="120">
          <template #default="scope">
            <span>{{ scope.row.student?.name || '-' }}</span>
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
        <el-table-column label="操作" width="380" fixed="right" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.status === 'PENDING'" size="small" type="primary" plain icon="VideoPlay" @click="startProcess(scope.row)">开始处理</el-button>
            <el-button v-if="scope.row.status === 'PROCESSING'" size="small" type="success" plain icon="Check" @click="openCompleteDialog(scope.row)">完成</el-button>
            <el-button size="small" type="primary" plain icon="Edit" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain icon="Delete" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editDialogVisible" :title="editForm.id ? '编辑维修申请' : '新建维修申请'" width="550px" destroy-on-close>
      <el-form :model="editForm" label-width="100px" ref="editFormRef" :rules="editRules">
        <el-form-item label="标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入维修标题" />
        </el-form-item>
        <el-form-item label="故障类型" prop="faultType">
          <el-select v-model="editForm.faultType" placeholder="请选择故障类型" style="width: 100%;">
            <el-option label="水电故障" value="水电故障" />
            <el-option label="电器故障" value="电器故障" />
            <el-option label="门窗损坏" value="门窗损坏" />
            <el-option label="家具损坏" value="家具损坏" />
            <el-option label="网络问题" value="网络问题" />
            <el-option label="其他问题" value="其他问题" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度" prop="emergencyLevel">
          <el-radio-group v-model="editForm.emergencyLevel">
            <el-radio label="一般">一般</el-radio>
            <el-radio label="紧急">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input type="textarea" v-model="editForm.description" :rows="3" placeholder="请描述维修问题" />
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="DONE" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果">
          <el-input type="textarea" v-model="editForm.result" :rows="3" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleEditSubmit" :loading="submitLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="completeDialogVisible" title="完成维修" width="500px" destroy-on-close>
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-input type="textarea" v-model="completeForm.result" :rows="4" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="completeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleComplete" :loading="completeLoading">确认完成</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getMaintenances, createMaintenance, updateMaintenance, updateMaintenanceStatus, deleteMaintenance } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { VideoPlay, Check, Edit, Delete } from '@element-plus/icons-vue'

const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const completeLoading = ref(false)
const filterStatus = ref('')
const editDialogVisible = ref(false)
const completeDialogVisible = ref(false)
const editFormRef = ref(null)
const completeForm = reactive({ id: null, result: '' })
const user = JSON.parse(localStorage.getItem('user') || '{}')

const editForm = reactive({
  id: null,
  title: '',
  faultType: '',
  emergencyLevel: '一般',
  description: '',
  status: '',
  result: ''
})

const editRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  faultType: [{ required: true, message: '请选择故障类型', trigger: 'change' }],
  emergencyLevel: [{ required: true, message: '请选择紧急程度', trigger: 'change' }],
  description: [{ required: true, message: '请输入问题描述', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const load = async () => {
  loading.value = true
  try {
    const res = await getMaintenances(filterStatus.value || undefined)
    list.value = res
  } finally {
    loading.value = false
  }
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

const openEditDialog = (row) => {
  if (row) {
    Object.assign(editForm, row)
  } else {
    editForm.id = null
    editForm.title = ''
    editForm.faultType = ''
    editForm.emergencyLevel = '一般'
    editForm.description = ''
    editForm.status = 'PENDING'
    editForm.result = ''
  }
  editDialogVisible.value = true
}

const handleEditSubmit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (editForm.id) {
        await updateMaintenance(editForm.id, { ...editForm })
        ElMessage.success('更新成功')
      } else {
        await createMaintenance({ ...editForm }, user.id)
        ElMessage.success('创建成功')
      }
      editDialogVisible.value = false
      load()
    } finally {
      submitLoading.value = false
    }
  })
}

const openCompleteDialog = (row) => {
  completeForm.id = row.id
  completeForm.result = row.result || ''
  completeDialogVisible.value = true
}

const handleComplete = async () => {
  completeLoading.value = true
  try {
    await updateMaintenance(completeForm.id, { status: 'DONE', result: completeForm.result })
    ElMessage.success('已标记完成')
    completeDialogVisible.value = false
    load()
  } finally {
    completeLoading.value = false
  }
}

const startProcess = async (row) => {
  try {
    await updateMaintenanceStatus(row.id, 'PROCESSING')
    ElMessage.success('已开始处理')
    load()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该维修申请记录吗?', '提示', {
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
</style>
