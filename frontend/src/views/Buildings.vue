<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" class="custom-tabs">
      <!-- Rooms Tab -->
      <el-tab-pane label="房间管理" name="rooms">
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <span class="header-title">房间列表</span>
                <span class="header-subtitle">管理所有宿舍房间信息及状态</span>
              </div>
              <el-button type="primary" icon="Plus" @click="openRoomDialog()">新增房间</el-button>
            </div>
          </template>
          <el-table :data="rooms" style="width: 100%" v-loading="loading" stripe highlight-current-row>
            <el-table-column prop="building.name" label="楼宇" min-width="120" />
            <el-table-column prop="roomNumber" label="房间号" min-width="100">
              <template #default="scope">
                <span class="room-number">{{ scope.row.roomNumber }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="type" label="类型" min-width="100">
               <template #default="scope">
                 <el-tag effect="plain" type="info" round>{{ scope.row.type }}</el-tag>
               </template>
            </el-table-column>
            <el-table-column prop="capacity" label="床位数" min-width="80" align="center" />
            <el-table-column prop="availableBeds" label="空闲床位" min-width="100" align="center">
              <template #default="scope">
                <span :class="scope.row.availableBeds > 0 ? 'text-success' : 'text-danger'">
                  {{ scope.row.availableBeds }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" min-width="100" align="center">
               <template #default="scope">
                 <el-tag :type="scope.row.status === '空闲' ? 'success' : (scope.row.status === '满员' ? 'danger' : 'warning')" effect="light" round>
                   {{ scope.row.status }}
                 </el-tag>
               </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right" align="center">
              <template #default="scope">
                <el-button size="small" type="primary" plain icon="Edit" @click="openRoomDialog(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" plain icon="Delete" @click="handleDeleteRoom(scope.row.id)" v-if="user?.role === 'admin'">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- Buildings Tab -->
      <el-tab-pane label="楼宇管理" name="buildings">
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <span class="header-title">楼宇列表</span>
                <span class="header-subtitle">管理学校所有宿舍楼宇信息</span>
              </div>
              <el-button type="primary" icon="Plus" @click="openBuildingDialog()">新增楼宇</el-button>
            </div>
          </template>
          <el-table :data="buildings" style="width: 100%" v-loading="loading" stripe highlight-current-row>
            <el-table-column prop="name" label="名称" min-width="120" />
            <el-table-column prop="location" label="位置" min-width="150" />
            <el-table-column prop="floorCount" label="层数" min-width="80" align="center" />
            <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="220" fixed="right" align="center">
              <template #default="scope">
                <el-button size="small" type="primary" plain icon="Edit" @click="openBuildingDialog(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" plain icon="Delete" @click="handleDeleteBuilding(scope.row.id)" v-if="user?.role === 'admin'">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- Room Dialog -->
    <el-dialog v-model="roomDialogVisible" :title="roomForm.id ? '编辑房间' : '新增房间'" width="500px" destroy-on-close>
      <el-form :model="roomForm" label-width="80px" class="dialog-form">
        <el-form-item label="楼宇">
          <el-select v-model="roomForm.building" value-key="id" placeholder="请选择楼宇" style="width: 100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="roomForm.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="roomForm.type" style="width: 100%">
            <el-option label="单人间" value="单人间" />
            <el-option label="双人间" value="双人间" />
            <el-option label="四人间" value="四人间" />
            <el-option label="六人间" value="六人间" />
          </el-select>
        </el-form-item>
        <el-form-item label="床位数">
          <el-input-number v-model="roomForm.capacity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="空闲床位">
          <el-input-number v-model="roomForm.availableBeds" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="roomForm.status" style="width: 100%">
            <el-option label="空闲" value="空闲" />
            <el-option label="满员" value="满员" />
            <el-option label="维修中" value="维修中" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roomDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveRoom" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Building Dialog -->
    <el-dialog v-model="buildingDialogVisible" :title="buildingForm.id ? '编辑楼宇' : '新增楼宇'" width="500px" destroy-on-close>
      <el-form :model="buildingForm" label-width="80px" class="dialog-form">
        <el-form-item label="名称">
          <el-input v-model="buildingForm.name" placeholder="请输入楼宇名称" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="buildingForm.location" placeholder="请输入楼宇位置" />
        </el-form-item>
        <el-form-item label="层数">
          <el-input-number v-model="buildingForm.floorCount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="buildingForm.description" type="textarea" :rows="3" placeholder="请输入描述信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="buildingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveBuilding" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRooms, getBuildings, createRoom, updateRoom, deleteRoom, createBuilding, updateBuilding, deleteBuilding } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const activeTab = ref('rooms')
const loading = ref(false)
const submitLoading = ref(false)
const rooms = ref([])
const buildings = ref([])
const user = JSON.parse(localStorage.getItem('user') || '{}')

// Room Dialog
const roomDialogVisible = ref(false)
const roomForm = ref({
  id: null,
  building: null,
  roomNumber: '',
  type: '四人间',
  capacity: 4,
  availableBeds: 4,
  status: '空闲'
})

// Building Dialog
const buildingDialogVisible = ref(false)
const buildingForm = ref({
  id: null,
  name: '',
  location: '',
  floorCount: 1,
  description: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const results = await Promise.allSettled([getRooms(), getBuildings()])
    if (results[0].status === 'fulfilled') {
      rooms.value = results[0].value
    } else {
      console.error('Failed to load rooms', results[0].reason)
      ElMessage.error('加载房间数据失败')
    }
    
    if (results[1].status === 'fulfilled') {
      buildings.value = results[1].value
    } else {
      console.error('Failed to load buildings', results[1].reason)
      ElMessage.error('加载楼宇数据失败')
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)

// Room Operations
const openRoomDialog = (row) => {
  if (row) {
    roomForm.value = { ...row }
    // Ensure building object is correctly referenced
  } else {
    roomForm.value = {
      id: null,
      building: null,
      roomNumber: '',
      type: '四人间',
      capacity: 4,
      availableBeds: 4,
      status: '空闲'
    }
  }
  roomDialogVisible.value = true
}

const saveRoom = async () => {
  submitLoading.value = true
  try {
    if (roomForm.value.id) {
      await updateRoom(roomForm.value.id, roomForm.value)
      ElMessage.success('更新成功')
    } else {
      await createRoom(roomForm.value)
      ElMessage.success('创建成功')
    }
    roomDialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDeleteRoom = (id) => {
  ElMessageBox.confirm('确认删除该房间吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRoom(id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  })
}

// Building Operations
const openBuildingDialog = (row) => {
  if (row) {
    buildingForm.value = { ...row }
  } else {
    buildingForm.value = {
      id: null,
      name: '',
      location: '',
      floorCount: 1,
      description: ''
    }
  }
  buildingDialogVisible.value = true
}

const saveBuilding = async () => {
  submitLoading.value = true
  try {
    if (buildingForm.value.id) {
      await updateBuilding(buildingForm.value.id, buildingForm.value)
      ElMessage.success('更新成功')
    } else {
      await createBuilding(buildingForm.value)
      ElMessage.success('创建成功')
    }
    buildingDialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDeleteBuilding = (id) => {
  ElMessageBox.confirm('确认删除该楼宇吗? 这可能会影响关联的房间。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBuilding(id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  })
}
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

.text-success {
  color: #67C23A;
  font-weight: bold;
}

.text-danger {
  color: #F56C6C;
  font-weight: bold;
}
</style>