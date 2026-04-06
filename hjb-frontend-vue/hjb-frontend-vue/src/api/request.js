import axios from 'axios'
import router from '../router/index.js'

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
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
    return response.data
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      router.push('/login')
    }
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export default request
