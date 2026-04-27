import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router/index.js'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 5000
})

// Request interceptor: attach JWT token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

// Response interceptor
request.interceptors.response.use(
  response => {
    const data = response.data
    // 业务层错误（code: 0）统一弹出提示
    if (data && data.code === 0 && data.msg) {
      ElMessage.error(data.msg)
      return Promise.reject(new Error(data.msg))
    }
    return data
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      router.push('/login')
      ElMessage.warning('登录已过期，请重新登录')
      return Promise.reject(error)
    }
    if (error.response?.status === 403) {
      ElMessage.error('无权限执行该操作')
      return Promise.reject(error)
    }
    const msg = error.response?.data?.msg || '网络请求失败，请稍后重试'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
