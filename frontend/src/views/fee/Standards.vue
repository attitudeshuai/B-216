<template>
  <div class="app-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">费用标准管理</span>
            <span class="header-subtitle">设定和管理各类费用标准</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openDialog()" v-if="user?.role === 'admin'">新增标准</el-button>
        </div>
      </template>
      
      <el-table :data="list" style="width: 100%" v-loading="loading" stripe highlight-current-row>
        <el-table-column prop="name" label="费用名称" min-width="150">
           <template #default="scope">
             <div class="name-cell">
               <el-icon><Collection /></el-icon>
               <span>{{ scope.row.name }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" min-width="120">
           <template #default="scope">
             <span class="amount-text">¥ {{ scope.row.amount }}</span>
           </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" min-width="100" align="center">
          <template #default="scope">
            <el-tag effect="plain" type="info" round>
              {{ {'YEAR': '年', 'SEMESTER': '学期', 'MONTH': '月'}[scope.row.unit] || scope.row.unit }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right" align="center" v-if="user?.role === 'admin'">
          <template #default="scope">
            <el-button size="small" type="primary" plain icon="Edit" @click="openDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain icon="Delete" @click="remove(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑标准' : '新增标准'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px" class="dialog-form">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入费用名称" />
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="form.amount" :min="0" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单位">
          <el-select v-model="form.unit" style="width: 100%">
            <el-option label="年" value="YEAR" />
            <el-option label="学期" value="SEMESTER" />
            <el-option label="月" value="MONTH" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入费用说明" />
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
import { getFeeStandards, createFeeStandard, updateFeeStandard, deleteFeeStandard } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Money, Collection } from '@element-plus/icons-vue'

const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const form = ref({ name: '', amount: 0, unit: 'YEAR', description: '' })

const user = JSON.parse(localStorage.getItem('user') || '{}')

const load = async () => {
  loading.value = true
  try {
    const res = await getFeeStandards()
    list.value = res.data || res
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { name: '', amount: 0, unit: 'YEAR', description: '' }
  }
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入费用名称')
    return
  }

  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateFeeStandard(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createFeeStandard(form.value)
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

const remove = async (id) => {
  ElMessageBox.confirm('确认删除该费用标准吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteFeeStandard(id)
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

.name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.amount-text {
  font-family: 'DIN Alternate', sans-serif;
  color: #F56C6C;
  font-weight: bold;
}
</style>
