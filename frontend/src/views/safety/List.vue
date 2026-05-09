<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">安全管理</span>
            <span class="header-subtitle">监控和管理宿舍安全状况</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openDialog()" v-if="activeTab === 'safety'">新增记录</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="demo-tabs">
        <el-tab-pane label="安全检查记录" name="safety">
          <el-table :data="safetyList" style="width: 100%" v-loading="loading" stripe highlight-current-row>
            <el-table-column prop="location" label="地点" min-width="150">
               <template #default="scope">
                 <div class="location-cell">
                   <el-icon><Location /></el-icon>
                   <span>{{ scope.row.location }}</span>
                 </div>
               </template>
            </el-table-column>
            <el-table-column prop="type" label="类型" min-width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.type === 'ROUTINE' ? 'info' : 'danger'" effect="plain" round>
                  {{ {'ROUTINE': '定期检查', 'SURPRISE': '突击检查'}[scope.row.type] || scope.row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="recordTime" label="时间" min-width="120">
               <template #default="scope">
                 <div class="date-cell">
                   <el-icon><Calendar /></el-icon>
                   <span>{{ formatTime(scope.row.recordTime) }}</span>
                 </div>
               </template>
            </el-table-column>
            <el-table-column prop="result" label="结果" min-width="150" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" min-width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'NORMAL' ? 'success' : 'danger'" round>
                  {{ {'NORMAL': '正常', 'ABNORMAL': '异常'}[scope.row.status] || scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right" align="center">
              <template #default="scope">
                <el-button size="small" type="primary" plain icon="Edit" @click="openDialog(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" plain icon="Delete" @click="removeSafety(scope.row.id)" v-if="user?.role === 'admin'">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="门禁出入记录" name="access">
          <el-table :data="accessList" style="width: 100%" v-loading="loading" stripe highlight-current-row>
            <el-table-column prop="student.name" label="学生" min-width="120">
               <template #default="scope">
                 <div class="user-cell">
                   <el-avatar :size="24" class="user-avatar">{{ scope.row.student?.name?.charAt(0) }}</el-avatar>
                   <span>{{ scope.row.student?.name }}</span>
                 </div>
               </template>
            </el-table-column>
            <el-table-column prop="location" label="地点" min-width="150" />
            <el-table-column prop="direction" label="方向" min-width="100" align="center">
               <template #default="scope">
                 <el-tag :type="scope.row.direction === 'IN' ? 'success' : 'warning'" effect="light" round>
                   {{ scope.row.direction === 'IN' ? '进入' : '离开' }}
                 </el-tag>
               </template>
            </el-table-column>
            <el-table-column prop="accessTime" label="时间" min-width="160">
               <template #default="scope">
                 <div class="date-cell">
                   <el-icon><Calendar /></el-icon>
                   <span>{{ formatDateTime(scope.row.accessTime) }}</span>
                 </div>
               </template>
            </el-table-column>
            <el-table-column prop="method" label="方式" min-width="100" align="center">
              <template #default="scope">
                <el-tag effect="plain" type="info" round>
                  {{ {'FACE': '人脸', 'CARD': '刷卡', 'APP': '应用'}[scope.row.method] || scope.row.method }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right" align="center">
              <template #default="scope">
                <el-button size="small" type="danger" plain icon="Delete" @click="removeAccess(scope.row.id)" v-if="user?.role === 'admin'">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑安全记录' : '新增安全记录'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px" class="dialog-form">
        <el-form-item label="地点">
          <el-input v-model="form.location" placeholder="请输入检查地点" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="定期检查" value="ROUTINE" />
            <el-option label="突击检查" value="SURPRISE" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="form.recordTime"
            type="datetime"
            placeholder="选择检查时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结果">
          <el-input v-model="form.result" type="textarea" :rows="3" placeholder="请输入检查结果" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
             <el-option label="正常" value="NORMAL" />
             <el-option label="异常" value="ABNORMAL" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit" :loading="submitLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSafetyRecords, createSafetyRecord, updateSafetyRecord, deleteSafetyRecord, getAccessRecords, deleteAccessRecord } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Warning, Calendar, User, Location } from '@element-plus/icons-vue'

const activeTab = ref('safety')
const safetyList = ref([])
const accessList = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const form = ref({ location: '', type: 'ROUTINE', result: '', status: 'NORMAL', recordTime: '' })
const user = JSON.parse(localStorage.getItem('user') || '{}')

const load = async () => {
  loading.value = true
  try {
    const results = await Promise.allSettled([getSafetyRecords(), getAccessRecords()])
    
    if (results[0].status === 'fulfilled') {
      const resSafe = results[0].value
      safetyList.value = resSafe.data || resSafe
    }
    
    if (results[1].status === 'fulfilled') {
      const resAccess = results[1].value
      accessList.value = resAccess.data || resAccess
    }
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return time.split('T')[0]
}

const formatDateTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const openDialog = (row) => {
  if (row) {
    form.value = {
      id: row.id,
      location: row.location,
      type: row.type,
      result: row.result,
      status: row.status,
      recordTime: row.recordTime
    }
  } else {
    // Format current time as YYYY-MM-DD HH:mm:ss
    const now = new Date()
    const timeStr = now.getFullYear() + '-' +
      String(now.getMonth() + 1).padStart(2, '0') + '-' +
      String(now.getDate()).padStart(2, '0') + ' ' +
      String(now.getHours()).padStart(2, '0') + ':' +
      String(now.getMinutes()).padStart(2, '0') + ':' +
      String(now.getSeconds()).padStart(2, '0')
      
    form.value = { 
      location: '', 
      type: 'ROUTINE', 
      result: '', 
      status: 'NORMAL',
      recordTime: timeStr
    }
  }
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.value.location) {
    ElMessage.warning('请输入地点')
    return
  }
  if (!form.value.recordTime) {
    ElMessage.warning('请选择时间')
    return
  }

  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateSafetyRecord(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createSafetyRecord(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const removeSafety = (id) => {
  ElMessageBox.confirm('确认删除该安全记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSafetyRecord(id)
      ElMessage.success('删除成功')
      load()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  })
}

const removeAccess = (id) => {
  ElMessageBox.confirm('确认删除该门禁记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAccessRecord(id)
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

.location-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.date-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  background: linear-gradient(135deg, #a0cfff 0%, #409eff 100%);
  color: white;
  font-size: 12px;
}
</style>
