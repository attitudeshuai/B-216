<template>
  <div class="app-container">
    <el-card shadow="never" class="notice-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">公告管理</span>
            <span class="header-subtitle">发布和管理系统公告</span>
          </div>
          <el-button type="primary" icon="Plus" @click="dialogVisible = true" v-if="user?.role === 'admin'">发布公告</el-button>
        </div>
      </template>
      
      <div v-loading="loading" class="timeline-container">
        <el-timeline v-if="list.length > 0">
          <el-timeline-item 
            v-for="(item, index) in list" 
            :key="index" 
            :timestamp="formatTime(item.publishTime)" 
            placement="top"
            :type="index === 0 ? 'primary' : ''"
            :hollow="index === 0"
          >
            <el-card shadow="hover" class="timeline-item-card">
              <div class="notice-content">
                <div class="notice-header">
                  <h4>{{ item.title }}</h4>
                  <div class="notice-actions" v-if="user?.role === 'admin' || user?.role === 'staff'">
                    <el-button size="small" type="primary" link icon="Edit" @click="openDialog(item)">编辑</el-button>
                    <el-button v-if="user?.role === 'admin'" size="small" type="danger" link icon="Delete" @click="remove(item.id)">删除</el-button>
                  </div>
                </div>
                <p class="notice-body">{{ item.content }}</p>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无公告" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '发布公告'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="60px" class="dialog-form">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="form.content" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="form.publishTime"
            type="datetime"
            placeholder="选择发布时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit" :loading="submitLoading">发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNotices, createNotice, updateNotice, deleteNotice } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const list = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const form = ref({ title: '', content: '', publishTime: '' })
const user = JSON.parse(localStorage.getItem('user') || '{}')

const load = async () => {
  loading.value = true
  try {
    const res = await getNotices()
    // Sort by date descending if not already sorted
    list.value = (res || []).sort((a, b) => new Date(b.publishTime) - new Date(a.publishTime))
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

const openDialog = (row) => {
  if (row && row.id) {
    form.value = { 
      id: row.id, 
      title: row.title, 
      content: row.content,
      publishTime: row.publishTime
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
      
    form.value = { title: '', content: '', publishTime: timeStr }
  }
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (!form.value.publishTime) {
    ElMessage.warning('请选择发布时间')
    return
  }

  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateNotice(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createNotice(form.value)
      ElMessage.success('发布成功')
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
  ElMessageBox.confirm('确认删除该公告吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNotice(id)
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

.timeline-container {
  padding: 20px 0;
}

.timeline-item-card {
  border-radius: 8px;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.notice-header h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.notice-actions {
  display: flex;
  gap: 8px;
}

.notice-body {
  margin: 0;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>
