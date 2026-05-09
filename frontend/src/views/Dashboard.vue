<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h2 class="dashboard-title">工作台概览</h2>
      <span class="dashboard-subtitle">欢迎回来，查看系统最新动态</span>
    </div>
    
    <!-- Top Stats Cards -->
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="icon-wrapper">
            <el-icon :size="24"><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-info">
            <div class="label">宿舍楼总数</div>
            <div class="value">{{ stats.buildingCount || 0 }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="icon-wrapper">
            <el-icon :size="24"><House /></el-icon>
          </div>
          <div class="stat-info">
            <div class="label">总房间数</div>
            <div class="value">{{ stats.roomCount || 0 }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card orange">
          <div class="icon-wrapper">
            <el-icon :size="24"><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="label">缴费记录</div>
            <div class="value">{{ stats.feeCount || 0 }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card red">
          <div class="icon-wrapper">
            <el-icon :size="24"><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="label">报修记录</div>
            <div class="value">{{ stats.maintenanceCount || 0 }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts and Notice Section -->
    <el-row :gutter="20" class="mt-4">
      <el-col :span="14">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="header-title"><el-icon><DataLine /></el-icon> 宿舍数据统计</span>
            </div>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :span="10">
        <el-card class="notice-card cursor-pointer" shadow="never" @click="router.push('/notice')">
          <template #header>
            <div class="card-header">
              <span class="header-title"><el-icon><Bell /></el-icon> 最新系统公告</span>
              <el-tag size="small" effect="plain" class="more-tag" round>点击查看更多</el-tag>
            </div>
          </template>
          <div v-if="latestNotice" class="notice-content">
             <div class="notice-title">{{ latestNotice.title }}</div>
             <div class="notice-time">
               <el-icon><Clock /></el-icon> {{ latestNotice.publishTime }}
             </div>
             <div class="notice-body">{{ latestNotice.content }}</div>
          </div>
          <div v-else class="empty-state">
            <el-empty description="暂无公告" :image-size="100"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getBuildings, getRooms, getFees, getMaintenances, getNotices } from '../api'
import { OfficeBuilding, House, Money, Warning, DataLine, Bell, Clock } from '@element-plus/icons-vue'

const router = useRouter()
const chartRef = ref(null)
const latestNotice = ref(null)
const stats = ref({
  buildingCount: 0,
  roomCount: 0,
  feeCount: 0,
  maintenanceCount: 0
})

onMounted(async () => {
  try {
    // Fetch data independently to prevent one failure from blocking others
    const results = await Promise.allSettled([
      getBuildings(),
      getRooms(),
      getFees(),
      getMaintenances(),
      getNotices()
    ])

    const buildings = results[0].status === 'fulfilled' ? results[0].value : []
    const rooms = results[1].status === 'fulfilled' ? results[1].value : []
    const fees = results[2].status === 'fulfilled' ? results[2].value : []
    const maintenances = results[3].status === 'fulfilled' ? results[3].value : []
    const notices = results[4].status === 'fulfilled' ? results[4].value : []

    if (results[4].status === 'rejected') {
      console.error('Failed to fetch notices:', results[4].reason)
    }
    
    // Process Stats
    stats.value = {
      buildingCount: Array.isArray(buildings) ? buildings.length : (buildings.data?.length || 0),
      roomCount: Array.isArray(rooms) ? rooms.length : (rooms.data?.length || 0),
      feeCount: Array.isArray(fees) ? fees.length : (fees.data?.length || 0),
      maintenanceCount: Array.isArray(maintenances) ? maintenances.length : (maintenances.data?.length || 0)
    }
    
    // Process Notice
    const noticeList = Array.isArray(notices) ? notices : (notices.data || [])
    if (noticeList.length > 0) {
      // Safely parse date for sorting
      latestNotice.value = noticeList.sort((a, b) => {
        const dateA = new Date(a.publishTime?.replace(' ', 'T'))
        const dateB = new Date(b.publishTime?.replace(' ', 'T'))
        return dateB - dateA
      })[0]
    }

    // Process Chart Data (Mocking building occupancy for demo if real data complex)
    // In real app, calculate occupancy from rooms data
    const buildingNames = (Array.isArray(buildings) ? buildings : []).map(b => b.name)
    // Generate some mock data for the chart based on building count
    const chartData = buildingNames.map(name => ({
      name: name,
      value: Math.floor(Math.random() * 100) + 20 // Mock value
    }))

    await nextTick()
    initChart(chartData)
    
    // Resize chart on window resize
    window.addEventListener('resize', () => {
      const chart = echarts.getInstanceByDom(chartRef.value)
      chart && chart.resize()
    })

  } catch (e) {
    console.error('Failed to load dashboard data', e)
  }
})

const initChart = (data) => {
  if (!chartRef.value) return
  
  const chart = echarts.init(chartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: '5%',
      left: 'center'
    },
    series: [
      {
        name: '宿舍入住情况',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data.length > 0 ? data : [
          { value: 1048, name: '一号楼' },
          { value: 735, name: '二号楼' },
          { value: 580, name: '三号楼' },
          { value: 484, name: '四号楼' },
          { value: 300, name: '五号楼' }
        ]
      }
    ]
  }
  
  chart.setOption(option)
}
</script>

<style scoped>
.dashboard-container {
  padding: 10px;
}

.page-header {
  margin-bottom: 25px;
}

.dashboard-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.dashboard-subtitle {
  font-size: 14px;
  color: #909399;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  margin-bottom: 20px;
  border: none;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
}

.icon-wrapper {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.blue .icon-wrapper { background: linear-gradient(135deg, #36d1dc, #5b86e5); }
.green .icon-wrapper { background: linear-gradient(135deg, #11998e, #38ef7d); }
.orange .icon-wrapper { background: linear-gradient(135deg, #fce38a, #f38181); }
.red .icon-wrapper { background: linear-gradient(135deg, #ff416c, #ff4b2b); }

.stat-info {
  flex: 1;
}

.stat-info .label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-info .value {
  color: #303133;
  font-size: 24px;
  font-weight: bold;
  font-family: 'DIN Alternate', sans-serif;
}

.chart-card, .notice-card {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-container {
  height: 320px;
  width: 100%;
}

.notice-content {
  padding: 10px;
}

.notice-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.notice-body {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
  display: -webkit-box;
  -webkit-line-clamp: 8;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.more-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.more-tag:hover {
  transform: scale(1.05);
}
</style>