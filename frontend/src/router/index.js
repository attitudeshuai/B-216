import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import Users from '../views/Users.vue'
import Buildings from '../views/Buildings.vue'

// New Modules
import Application from '../views/allocation/Application.vue'
import AllocManage from '../views/allocation/Manage.vue'
import FeeStandards from '../views/fee/Standards.vue'
import FeeList from '../views/fee/List.vue'
import MaintenanceList from '../views/maintenance/List.vue'
import SafetyList from '../views/safety/List.vue'
import AttendanceList from '../views/attendance/List.vue'
import HygieneList from '../views/hygiene/List.vue'
import NoticeList from '../views/notice/List.vue'
import SystemSettings from '../views/system/Settings.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: Dashboard },
      { path: 'users', name: 'Users', component: Users },
      { path: 'dormitory', name: 'Dormitory', component: Buildings },
      
      // Allocation
      { path: 'allocation/application', name: 'Application', component: Application },
      { path: 'allocation/manage', name: 'AllocManage', component: AllocManage },
      
      // Fee
      { path: 'fee/standards', name: 'FeeStandards', component: FeeStandards },
      { path: 'fee/list', name: 'FeeList', component: FeeList },
      
      // Maintenance
      { path: 'maintenance', name: 'Maintenance', component: MaintenanceList },
      
      // Safety
      { path: 'safety', name: 'Safety', component: SafetyList },
      
      // Attendance
      { path: 'attendance', name: 'Attendance', component: AttendanceList },
      
      // Hygiene
      { path: 'hygiene', name: 'Hygiene', component: HygieneList },
      
      // Notice
      { path: 'notice', name: 'Notice', component: NoticeList },
      
      // System
      { path: 'system', name: 'System', component: SystemSettings }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
