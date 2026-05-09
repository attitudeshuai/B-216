<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">用户管理</span>
            <span class="header-subtitle">管理系统所有用户及其角色权限</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openDialog()">新增用户</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="scope">
             <div class="user-cell">
               <el-avatar :size="24" :src="'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
               <span>{{ scope.row.username }}</span>
             </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="role" label="角色" min-width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 'admin'" type="danger" effect="light" round>管理员</el-tag>
            <el-tag v-else-if="scope.row.role === 'student'" effect="light" round>学生</el-tag>
            <el-tag v-else-if="scope.row.role === 'staff'" type="warning" effect="light" round>宿管</el-tag>
            <el-tag v-else type="info" effect="light" round>其他</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系方式" min-width="120" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" plain icon="Edit" @click="openDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain icon="Delete" @click="handleDelete(scope.row)" v-if="user?.role === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" class="dialog-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="如果不修改请留空" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
           <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
             <el-option label="管理员" value="admin" />
             <el-option label="宿管" value="staff" />
             <el-option label="学生" value="student" />
           </el-select>
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers, createUser, updateUser, deleteUser } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const user = JSON.parse(localStorage.getItem('user') || '{}')

const form = ref({
  username: '',
  password: '',
  name: '',
  role: 'student',
  phone: ''
})

const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }]
}

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUsers()
    tableData.value = res
  } catch (e) {
    // handled
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  if (row) {
    form.value = { ...row, password: '' } // Clear password field for editing
  } else {
    form.value = { username: '', password: '', name: '', role: 'student', phone: '' }
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.value.id) {
          await updateUser(form.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await createUser(form.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (e) {
        // handled
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该用户?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchData()
  })
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
