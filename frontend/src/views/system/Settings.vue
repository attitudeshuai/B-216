<template>
  <div class="app-container">
    <el-card shadow="never" class="settings-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">系统设置</span>
            <span class="header-subtitle">管理系统参数和查看操作日志</span>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="demo-tabs">
        <el-tab-pane label="参数设置" name="params">
          <template #label>
            <span class="custom-tabs-label">
              <el-icon><Setting /></el-icon>
              <span>参数设置</span>
            </span>
          </template>
          <el-table :data="params" style="width: 100%" v-loading="loading" stripe highlight-current-row>
            <el-table-column prop="paramKey" label="参数键" min-width="150" />
            <el-table-column prop="paramValue" label="参数值" min-width="200">
              <template #default="scope">
                 <el-input 
                   v-if="scope.row.editing" 
                   v-model="scope.row.paramValue" 
                   size="small" 
                   style="width: 200px"
                 />
                 <span v-else class="param-value">{{ scope.row.paramValue }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="说明" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="150" fixed="right" align="center">
              <template #default="scope">
                <el-button v-if="!scope.row.editing" size="small" type="primary" plain icon="Edit" @click="scope.row.editing = true">编辑</el-button>
                <el-button v-else size="small" type="success" plain icon="Check" @click="saveParam(scope.row)">保存</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="操作日志" name="logs">
          <template #label>
            <span class="custom-tabs-label">
              <el-icon><Tickets /></el-icon>
              <span>操作日志</span>
            </span>
          </template>
          <el-table :data="logs" style="width: 100%" v-loading="loading" stripe highlight-current-row>
            <el-table-column prop="operatorUsername" label="操作人" min-width="120" />
            <el-table-column prop="operation" label="操作" min-width="150" />
            <el-table-column prop="details" label="详情" min-width="250" show-overflow-tooltip />
            <el-table-column prop="operateTime" label="时间" min-width="180">
              <template #default="scope">
                <span class="time-cell">{{ formatDateTime(scope.row.operateTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="ipAddress" label="IP" min-width="120" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSystemParams, updateSystemParam, getOperationLogs } from '../../api'
import { ElMessage } from 'element-plus'
import { Edit, Check, Setting, Tickets } from '@element-plus/icons-vue'

const activeTab = ref('params')
const params = ref([])
const logs = ref([])
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    const results = await Promise.allSettled([getSystemParams(), getOperationLogs()])
    
    if (results[0].status === 'fulfilled') {
      const resParams = results[0].value
      params.value = (resParams.data || resParams).map(p => ({ ...p, editing: false }))
    }
    
    if (results[1].status === 'fulfilled') {
      const resLogs = results[1].value
      logs.value = resLogs.data || resLogs
    }
  } finally {
    loading.value = false
  }
}

const formatDateTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const saveParam = async (row) => {
  try {
    await updateSystemParam(row.id, row)
    row.editing = false
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
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

.custom-tabs-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

.param-value {
  font-family: monospace;
  color: #409EFF;
}

.time-cell {
  color: #606266;
  font-size: 13px;
}
</style>
