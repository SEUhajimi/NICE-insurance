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
    // Business-layer error (code: 0): show error message
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
      ElMessage.warning('Session expired, please log in again')
      return Promise.reject(error)
    }
    if (error.response?.status === 403) {
      ElMessage.error('You do not have permission to perform this action')
      return Promise.reject(error)
    }
    const msg = error.response?.data?.msg || 'Network request failed, please try again later'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
