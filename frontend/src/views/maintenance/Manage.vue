<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">维修申请处理</span>
            <span class="header-subtitle">查看和处理学生宿舍维修申请</span>
          </div>
          <div class="header-right">
            <el-select v-model="filterStatus" placeholder="按状态筛选" clearable style="width: 140px" @change="load">
              <el-option label="待处理" value="待处理" />
              <el-option label="处理中" value="处理中" />
              <el-option label="已完成" value="已完成" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="filteredList" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="title" label="标题" min-width="130" show-overflow-tooltip />
        <el-table-column prop="faultType" label="故障类型" min-width="100" align="center">
          <template #default="scope">
            <el-tag effect="plain">{{ scope.row.faultType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="问题描述" min-width="180" show-overflow-tooltip />
        <el-table-column label="申报人" min-width="100">
          <template #default="scope">
            <span>{{ scope.row.student?.name || '-' }}</span>
          </template>
        </el-table-column>
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
        <el-table-column prop="result" label="处理结果" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.result || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="scope">
            <template v-if="scope.row.status === '待处理'">
              <el-button size="small" type="primary" plain @click="changeStatus(scope.row, '处理中')">开始处理</el-button>
            </template>
            <template v-if="scope.row.status === '处理中'">
              <el-button size="small" type="success" plain @click="openCompleteDialog(scope.row)">标记完成</el-button>
            </template>
            <el-button size="small" type="danger" plain icon="Delete" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="completeDialogVisible" title="标记维修完成" width="480px" destroy-on-close>
      <el-form :model="completeForm" label-width="90px" class="dialog-form">
        <el-form-item label="处理结果">
          <el-input type="textarea" v-model="completeForm.result" :rows="3" placeholder="请填写处理结果说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="completeDialogVisible = false">取消</el-button>
          <el-button type="success" @click="markComplete" :loading="submitLoading">确认完成</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMaintenances, updateMaintenanceStatus, updateMaintenance, deleteMaintenance } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const filterStatus = ref('')
const completeDialogVisible = ref(false)
const completeForm = ref({ id: null, result: '' })

const filteredList = computed(() => {
  if (!filterStatus.value) return list.value
  return list.value.filter(item => item.status === filterStatus.value)
})

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
    const res = await getMaintenances()
    list.value = res
  } finally {
    loading.value = false
  }
}

const changeStatus = async (row, status) => {
  try {
    await updateMaintenanceStatus(row.id, status)
    ElMessage.success('状态已更新')
    load()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const openCompleteDialog = (row) => {
  completeForm.value = { id: row.id, result: '' }
  completeDialogVisible.value = true
}

const markComplete = async () => {
  submitLoading.value = true
  try {
    await updateMaintenance(completeForm.value.id, {
      status: '已完成',
      result: completeForm.value.result
    })
    ElMessage.success('已标记完成')
    completeDialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
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

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dialog-form {
  padding: 10px 20px 0;
}
</style>
