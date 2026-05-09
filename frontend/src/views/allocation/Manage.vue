<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" class="custom-tabs">
      <el-tab-pane label="入住申请" name="applications">
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <span class="header-title">入住申请</span>
                <span class="header-subtitle">处理学生的入住申请请求</span>
              </div>
              <el-button type="primary" icon="Refresh" @click="load" circle plain></el-button>
            </div>
          </template>
          <el-table :data="applications" style="width: 100%" v-loading="loading" stripe highlight-current-row>
            <el-table-column prop="student.name" label="学生姓名" min-width="100">
               <template #default="scope">
                 <div class="user-cell">
                   <el-avatar :size="24" icon="UserFilled" />
                   <span>{{ scope.row.student?.name || '未知' }}</span>
                 </div>
               </template>
            </el-table-column>
            <el-table-column prop="reason" label="理由" min-width="150" show-overflow-tooltip />
            <el-table-column prop="preferredBuilding" label="意向楼宇" min-width="120" />
            <el-table-column prop="status" label="状态" min-width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'APPROVED' ? 'success' : (scope.row.status === 'REJECTED' ? 'danger' : 'warning')" effect="light" round>
                  {{ {'PENDING': '待审核', 'APPROVED': '已通过', 'REJECTED': '已拒绝'}[scope.row.status] || scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="280" fixed="right" align="center" v-if="user?.role !== 'student'">
              <template #default="scope">
                <template v-if="scope.row.status === 'PENDING'">
                  <el-button size="small" type="success" plain icon="Check" @click="handleApprove(scope.row)">通过</el-button>
                  <el-button size="small" type="danger" plain icon="Close" @click="handleReject(scope.row)">驳回</el-button>
                </template>
                <el-button v-else-if="user?.role === 'admin'" size="small" type="danger" plain icon="Delete" @click="handleDeleteApplication(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="分配记录" name="allocations">
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <span class="header-title">分配记录</span>
                <span class="header-subtitle">管理宿舍床位分配情况</span>
              </div>
              <el-button type="primary" icon="Plus" @click="openAllocationDialog()">新增分配</el-button>
            </div>
          </template>
          <el-table :data="allocations" style="width: 100%" v-loading="loading" stripe highlight-current-row>
            <el-table-column prop="student.name" label="学生" min-width="120">
               <template #default="scope">
                 <div class="user-cell">
                   <el-avatar :size="24" icon="UserFilled" />
                   <span>{{ scope.row.student?.name || '未知' }}</span>
                 </div>
               </template>
            </el-table-column>
            <el-table-column prop="room.roomNumber" label="房间号" min-width="100">
              <template #default="scope">
                <span class="room-number">{{ scope.row.room?.roomNumber }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="bedNumber" label="床位" min-width="80" align="center">
              <template #default="scope">
                <el-tag effect="plain" type="info" round>{{ scope.row.bedNumber }}号床</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250" fixed="right" align="center">
              <template #default="scope">
                <el-button size="small" type="primary" plain icon="Edit" @click="openAllocationDialog(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" plain icon="Delete" @click="handleDeleteAllocation(scope.row.id)" v-if="user?.role === 'admin'">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="allocationDialogVisible" :title="allocationForm.id ? '编辑分配' : '新增分配'" width="500px" destroy-on-close>
      <el-form :model="allocationForm" label-width="80px" class="dialog-form">
        <el-form-item label="学生">
          <el-select v-model="allocationForm.studentId" placeholder="请选择学生" filterable :disabled="!!allocationForm.id" style="width: 100%">
            <el-option v-for="s in unallocatedStudents" :key="s.id" :label="s.name + ' (' + s.username + ')'" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼宇">
           <el-select v-model="selectedBuilding" placeholder="请选择楼宇" @change="handleBuildingChange" style="width: 100%">
              <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
           </el-select>
        </el-form-item>
        <el-form-item label="房间">
           <el-select v-model="allocationForm.roomId" placeholder="请选择房间" :disabled="!selectedBuilding" style="width: 100%">
              <el-option v-for="r in availableRooms" :key="r.id" :label="r.roomNumber + ' (空闲:' + r.availableBeds + ')'" :value="r.id" />
           </el-select>
        </el-form-item>
        <el-form-item label="床位号">
           <el-input v-model="allocationForm.bedNumber" placeholder="请输入床位号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="allocationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAllocation" :loading="submitLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getApplications, updateApplicationStatus, getAllocations, createAllocation, updateAllocation, deleteAllocation, deleteApplication, getUnallocatedStudents, getAvailableRooms, getBuildings } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Check, Close, Refresh, UserFilled } from '@element-plus/icons-vue'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const activeTab = ref('applications')
const loading = ref(false)
const submitLoading = ref(false)
const applications = ref([])
const allocations = ref([])

const allocationDialogVisible = ref(false)
const allocationForm = ref({ studentId: '', roomId: '', bedNumber: '' })

// New state variables for selectors
const unallocatedStudents = ref([])
const buildings = ref([])
const availableRooms = ref([])
const selectedBuilding = ref('')

const load = async () => {
  loading.value = true
  try {
    const results = await Promise.allSettled([getApplications(), getAllocations()])
    if (results[0].status === 'fulfilled') {
      applications.value = results[0].value
    }
    if (results[1].status === 'fulfilled') {
      allocations.value = results[1].value
    }
  } finally {
    loading.value = false
  }
}

const handleStatus = async (id, status) => {
  try {
    await updateApplicationStatus(id, status)
    ElMessage.success('操作成功')
    load()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleApprove = (row) => handleStatus(row.id, 'APPROVED')
const handleReject = (row) => handleStatus(row.id, 'REJECTED')

const handleDeleteApplication = (id) => {
  ElMessageBox.confirm('确认删除该申请记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteApplication(id)
      ElMessage.success('删除成功')
      load()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  })
}

const handleBuildingChange = async (val) => {
    allocationForm.value.roomId = '' // Reset room when building changes
    if (val) {
        try {
            const res = await getAvailableRooms(val)
            availableRooms.value = res
        } catch (e) {
            console.error(e)
            availableRooms.value = []
        }
    } else {
        availableRooms.value = []
    }
}

const openAllocationDialog = async (row) => {
  // Load initial data
  try {
      const resStudents = await getUnallocatedStudents()
      unallocatedStudents.value = resStudents
      
      const resBuildings = await getBuildings()
      buildings.value = resBuildings
  } catch(e) {
      console.error("Failed to load form data", e)
  }

  if (row) {
    allocationForm.value = {
      id: row.id,
      studentId: row.student?.id,
      roomId: row.room?.id,
      bedNumber: row.bedNumber
    }
    
    // Handle Edit Mode specific logic
    if (row.student && !unallocatedStudents.value.find(s => s.id === row.student.id)) {
        unallocatedStudents.value.push(row.student)
    }
    
    if (row.room && row.room.building) {
        selectedBuilding.value = row.room.building.id
        await handleBuildingChange(selectedBuilding.value)
        // Ensure current room is in available list (even if full) so it displays correctly
        if (row.room && !availableRooms.value.find(r => r.id === row.room.id)) {
             availableRooms.value.push(row.room)
        }
    }
  } else {
    allocationForm.value = { studentId: '', roomId: '', bedNumber: '' }
    selectedBuilding.value = ''
    availableRooms.value = []
  }
  allocationDialogVisible.value = true
}

const submitAllocation = async () => {
  submitLoading.value = true
  const data = {
    student: { id: allocationForm.value.studentId },
    room: { id: allocationForm.value.roomId },
    bedNumber: allocationForm.value.bedNumber
  }
  
  try {
    if (allocationForm.value.id) {
      await updateAllocation(allocationForm.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await createAllocation(data)
      ElMessage.success('分配成功')
    }
    allocationDialogVisible.value = false
    load()
  } catch (e) {
    // Error is handled by request interceptor
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

const handleDeleteAllocation = (id) => {
  ElMessageBox.confirm('确认删除该分配记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAllocation(id)
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

.room-number {
  font-weight: 600;
  color: #409EFF;
}
</style>
