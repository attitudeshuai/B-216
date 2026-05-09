<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">卫生检查管理</span>
            <span class="header-subtitle">记录和管理宿舍卫生检查情况</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openDialog()">新增检查</el-button>
        </div>
      </template>
      
      <el-table :data="list" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="room.roomNumber" label="房间号" min-width="100">
          <template #default="scope">
            <span class="room-number">{{ scope.row.room?.roomNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="checkDate" label="检查日期" min-width="120">
           <template #default="scope">
             <div class="date-cell">
               <el-icon><Calendar /></el-icon>
               <span>{{ scope.row.checkDate ? scope.row.checkDate.split('T')[0] : '' }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" sortable min-width="100" align="center">
           <template #default="scope">
             <span :class="getScoreClass(scope.row.score)">{{ scope.row.score }}</span>
           </template>
        </el-table-column>
        <el-table-column prop="comments" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="inspector" label="检查人" min-width="100" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" plain icon="Edit" @click="openDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain icon="Delete" @click="handleDelete(scope.row.id)" v-if="user?.role === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑检查' : '新增检查'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px" class="dialog-form">
        <el-form-item label="房间">
          <el-select v-model="form.roomId" placeholder="请选择房间" style="width: 100%" filterable>
            <el-option
              v-for="room in rooms"
              :key="room.id"
              :label="room.roomNumber"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="检查日期">
          <el-date-picker
            v-model="form.checkDate"
            type="date"
            placeholder="选择检查日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="评分">
          <el-input-number v-model="form.score" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.comments" type="textarea" :rows="3" placeholder="请输入备注信息" />
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
import { getHygieneRecords, createHygieneRecord, updateHygieneRecord, deleteHygieneRecord, getRooms } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Calendar } from '@element-plus/icons-vue'

const list = ref([])
const rooms = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const form = ref({ roomId: null, score: 100, comments: '', checkDate: '' })
const user = JSON.parse(localStorage.getItem('user') || '{}')

const load = async () => {
  loading.value = true
  try {
    const results = await Promise.allSettled([getHygieneRecords(), getRooms()])
    
    if (results[0].status === 'fulfilled') {
      const res = results[0].value
      list.value = res.data || res
    }
    
    if (results[1].status === 'fulfilled') {
      const roomRes = results[1].value
      rooms.value = roomRes.data || roomRes
    }
  } finally {
    loading.value = false
  }
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const openDialog = (row) => {
  if (row) {
    form.value = {
      id: row.id,
      roomId: row.room?.id,
      score: row.score,
      comments: row.comments,
      checkDate: row.checkDate
    }
  } else {
    form.value = { 
      roomId: null, 
      score: 100, 
      comments: '',
      checkDate: new Date().toISOString().split('T')[0]
    }
  }
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.value.roomId) {
    ElMessage.warning('请选择房间')
    return
  }
  if (!form.value.checkDate) {
    ElMessage.warning('请选择检查日期')
    return
  }
  
  submitLoading.value = true
  const data = { 
    room: { id: form.value.roomId }, 
    score: form.value.score,
    comments: form.value.comments,
    inspector: user.name || 'Admin',
    checkDate: form.value.checkDate
  }

  try {
    if (form.value.id) {
      await updateHygieneRecord(form.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await createHygieneRecord(data)
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
  ElMessageBox.confirm('确认删除该卫生检查记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteHygieneRecord(id)
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

.room-number {
  font-weight: 600;
  color: #409EFF;
}

.date-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.score-excellent {
  color: #67C23A;
  font-weight: bold;
}
.score-good {
  color: #409EFF;
  font-weight: bold;
}
.score-pass {
  color: #E6A23C;
  font-weight: bold;
}
.score-fail {
  color: #F56C6C;
  font-weight: bold;
}
</style>
