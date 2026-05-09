<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">考勤管理</span>
            <span class="header-subtitle">记录和管理学生考勤情况</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openDialog()">记录考勤</el-button>
        </div>
      </template>
      
      <el-table :data="list" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="student.name" label="学生" min-width="120">
           <template #default="scope">
             <div class="user-cell">
               <el-avatar :size="24" class="user-avatar">{{ scope.row.student?.name?.charAt(0) }}</el-avatar>
               <span>{{ scope.row.student?.name }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="time" label="日期" min-width="160">
           <template #default="scope">
             <div class="date-cell">
               <el-icon><Calendar /></el-icon>
               <span>{{ formatDateTime(scope.row.time) }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="type" label="状态" min-width="100" align="center">
           <template #default="scope">
             <el-tag :type="scope.row.type === 'NORMAL' ? 'success' : (scope.row.type === 'LATE' ? 'warning' : 'danger')" effect="light" round>
               {{ {'NORMAL': '正常', 'LATE': '晚归', 'ABSENT': '缺勤'}[scope.row.type] || scope.row.type }}
             </el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right" align="center">
           <template #default="scope">
             <el-button size="small" type="primary" plain icon="Edit" @click="openDialog(scope.row)">编辑</el-button>
             <el-button size="small" type="danger" plain icon="Delete" @click="handleDelete(scope.row.id)" v-if="user?.role === 'admin'">删除</el-button>
           </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑考勤' : '记录考勤'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px" class="dialog-form">
        <el-form-item label="学生">
          <el-select v-model="form.studentId" placeholder="请选择学生" filterable style="width: 100%">
            <el-option v-for="s in students" :key="s.id" :label="s.name + ' (' + s.username + ')'" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="正常" value="NORMAL" />
            <el-option label="晚归" value="LATE" />
            <el-option label="缺勤" value="ABSENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
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
import { getAttendances, createAttendance, updateAttendance, deleteAttendance, getUsers } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Calendar, User, Clock } from '@element-plus/icons-vue'

const list = ref([])
const students = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const form = ref({ studentId: '', type: 'NORMAL', remark: '' })
const user = JSON.parse(localStorage.getItem('user') || '{}')

const load = async () => {
  loading.value = true
  try {
    const results = await Promise.allSettled([getAttendances(), getUsers()])
    
    if (results[0].status === 'fulfilled') {
      const res = results[0].value
      list.value = res.data || res
    }
    
    if (results[1].status === 'fulfilled') {
      const resUsers = results[1].value
      students.value = (resUsers.data || resUsers).filter(u => u.role === 'student')
    }
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  if (row) {
    form.value = { 
      id: row.id,
      studentId: row.student?.id,
      type: row.type,
      remark: row.remark
    }
  } else {
    form.value = { studentId: '', type: 'NORMAL', remark: '' }
  }
  dialogVisible.value = true
}

// Helper to format date as yyyy-MM-dd HH:mm:ss
const formatDate = (date) => {
  const pad = (n) => n < 10 ? '0' + n : n
  return date.getFullYear() + '-' + 
         pad(date.getMonth() + 1) + '-' + 
         pad(date.getDate()) + ' ' + 
         pad(date.getHours()) + ':' + 
         pad(date.getMinutes()) + ':' + 
         pad(date.getSeconds())
}

const formatDateTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const submit = async () => {
  if (!form.value.studentId) {
    ElMessage.warning('请输入学生ID')
    return
  }
  
  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateAttendance(form.value.id, { ...form.value, student: { id: form.value.studentId } })
      ElMessage.success('更新成功')
    } else {
      await createAttendance({ 
        ...form.value, 
        student: { id: form.value.studentId }, 
        time: formatDate(new Date()) 
      })
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

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该考勤记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAttendance(id)
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

.date-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}
</style>
