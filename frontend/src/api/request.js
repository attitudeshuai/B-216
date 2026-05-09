import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

service.interceptors.request.use(
  config => {
    // Add token if needed
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    let message = error.message || '请求错误'
    if (error.response) {
      // Prioritize backend error message
      const backendMessage = error.response.data?.message || error.response.data?.error;
      if (backendMessage) {
        message = backendMessage;
      } else {
        switch (error.response.status) {
          case 400: message = '请求参数错误 (400)'; break;
          case 401: message = '未授权，请登录 (401)'; break;
          case 403: message = '拒绝访问 (403)'; break;
          case 404: message = '请求资源不存在 (404)'; break;
          case 500: message = '服务器内部错误 (500)'; break;
          case 502: message = '网关错误 (502)'; break;
          default: message = `连接错误 (${error.response.status})`;
        }
      }
    } else if (error.message.includes('timeout')) {
      message = '请求超时'
    } else if (error.message.includes('Network Error')) {
      message = '网络错误，请检查您的网络连接'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
