<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">费用缴纳记录</span>
            <span class="header-subtitle">管理学生费用缴纳情况</span>
          </div>
          <el-button v-if="user?.role === 'admin'" type="primary" icon="Plus" @click="openDialog()">新增费用</el-button>
        </div>
      </template>

      <el-table :data="list" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="student.name" label="学生" min-width="120">
           <template #default="scope">
             <div class="user-cell">
               <el-avatar :size="24" icon="UserFilled" />
               <span>{{ scope.row.student?.name || '未知' }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" min-width="100">
           <template #default="scope">
             <span class="amount-text">¥ {{ scope.row.amount }}</span>
           </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" min-width="100">
          <template #default="scope">
             <el-tag effect="plain" type="info" round>{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="scope">
             <el-tag :type="scope.row.status === 'PAID' ? 'success' : 'danger'" effect="light" round>
               {{ scope.row.status === 'PAID' ? '已缴' : '未缴' }}
             </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center" v-if="user?.role === 'admin'">
          <template #default="scope">
            <el-button size="small" type="primary" plain icon="Edit" @click="openDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain icon="Delete" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑费用' : '新增费用'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px" class="dialog-form">
        <el-form-item label="学生">
          <el-select v-model="form.studentId" placeholder="请选择学生" filterable style="width: 100%">
            <el-option
              v-for="item in students"
              :key="item.id"
              :label="item.name + ' (' + item.username + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="form.amount" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="类型">
           <el-input v-model="form.type" placeholder="例如：住宿费" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="未缴" value="UNPAID" />
            <el-option label="已缴" value="PAID" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
           <el-date-picker v-model="form.dueDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
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
import { getFees, createFee, updateFee, deleteFee, getUsers } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, UserFilled } from '@element-plus/icons-vue'

const list = ref([])
const students = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const user = JSON.parse(localStorage.getItem('user') || '{}')
const dialogVisible = ref(false)
const form = ref({ studentId: '', amount: 0, type: '', status: 'UNPAID', dueDate: '' })

const loadStudents = async () => {
  try {
    const res = await getUsers()
    students.value = res.filter(u => u.role === 'student')
  } catch (e) {
    console.error('Failed to load students', e)
  }
}

const load = async () => {
  loading.value = true
  try {
    const res = await getFees()
    if (user.role === 'student') {
      list.value = res.filter(item => item.student?.id === user.id)
    } else {
      list.value = res
    }
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  loadStudents()
  if (row) {
    form.value = { 
      id: row.id,
      studentId: row.student?.id,
      amount: row.amount,
      type: row.type,
      status: row.status,
      dueDate: row.dueDate
    }
  } else {
    form.value = { studentId: '', amount: 0, type: '', status: 'UNPAID', dueDate: '' }
  }
  dialogVisible.value = true
}

const submit = async () => {
  submitLoading.value = true
  try {
    const data = { ...form.value, student: { id: form.value.studentId } }
    if (form.value.id) {
      await updateFee(form.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await createFee(data)
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
  ElMessageBox.confirm('确认删除该记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteFee(id)
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

.amount-text {
  font-weight: bold;
  color: #F56C6C;
}
</style>
