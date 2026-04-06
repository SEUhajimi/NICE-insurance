<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-logo" @click="$router.push('/')" style="cursor:pointer">
        <div class="logo-icon">HJB</div>
        <span class="logo-text">Insurance</span>
      </div>

      <el-tabs v-model="activeTab" class="auth-tabs">
        <!-- Login Tab -->
        <el-tab-pane label="Sign In" name="login">
          <el-form :model="loginForm" label-position="top" class="auth-form">
            <el-form-item label="Username">
              <el-input v-model="loginForm.username" placeholder="Your username" />
            </el-form-item>
            <el-form-item label="Password">
              <el-input v-model="loginForm.password" type="password" show-password placeholder="Your password" />
            </el-form-item>
            <div v-if="loginError" class="error-msg">{{ loginError }}</div>
            <el-button type="primary" class="submit-btn" :loading="loginLoading" @click="handleLogin">Sign In</el-button>
          </el-form>
        </el-tab-pane>

        <!-- Register Tab -->
        <el-tab-pane label="Sign Up" name="register">
          <el-form :model="regForm" label-position="top" class="auth-form">
            <div class="form-row">
              <el-form-item label="First Name">
                <el-input v-model="regForm.fname" placeholder="First name" />
              </el-form-item>
              <el-form-item label="Last Name">
                <el-input v-model="regForm.lname" placeholder="Last name" />
              </el-form-item>
            </div>
            <div class="form-row">
              <el-form-item label="Username">
                <el-input v-model="regForm.username" placeholder="Choose a username" />
              </el-form-item>
              <el-form-item label="Email">
                <el-input v-model="regForm.email" placeholder="your@email.com" />
              </el-form-item>
            </div>
            <div class="form-row">
              <el-form-item label="Password">
                <el-input v-model="regForm.password" type="password" show-password placeholder="Create password" />
              </el-form-item>
              <el-form-item label="Gender">
                <el-select v-model="regForm.gender" style="width:100%">
                  <el-option label="Male" value="M" />
                  <el-option label="Female" value="F" />
                </el-select>
              </el-form-item>
            </div>
            <div class="form-row">
              <el-form-item label="Marital Status">
                <el-select v-model="regForm.maritalStatus" style="width:100%">
                  <el-option label="Single" value="S" />
                  <el-option label="Married" value="M" />
                  <el-option label="Widowed" value="W" />
                </el-select>
              </el-form-item>
              <el-form-item label="Insurance Type">
                <el-select v-model="regForm.custType" style="width:100%">
                  <el-option label="Auto" value="A" />
                  <el-option label="Home" value="H" />
                  <el-option label="Both" value="B" />
                </el-select>
              </el-form-item>
            </div>
            <el-form-item label="Street Address">
              <el-input v-model="regForm.addrStreet" placeholder="123 Main St" />
            </el-form-item>
            <div class="form-row">
              <el-form-item label="City">
                <el-input v-model="regForm.addrCity" placeholder="City" />
              </el-form-item>
              <el-form-item label="State">
                <el-select v-model="regForm.addrState" filterable placeholder="State" style="width:100%">
                  <el-option v-for="s in US_STATES" :key="s" :label="s" :value="s" />
                </el-select>
              </el-form-item>
              <el-form-item label="Zipcode">
                <el-input v-model="regForm.zipcode" placeholder="Zip" />
              </el-form-item>
            </div>
            <div v-if="regError" class="error-msg">{{ regError }}</div>
            <div v-if="regSuccess" class="success-msg">Account created! You can now sign in.</div>
            <el-button type="primary" class="submit-btn" :loading="regLoading" @click="handleRegister">Create Account</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '../api/request.js'

const router = useRouter()
const route = useRoute()

const activeTab = ref('login')

onMounted(() => {
  if (route.query.tab === 'register') activeTab.value = 'register'
})

const US_STATES = [
  'AL','AK','AZ','AR','CA','CO','CT','DE','FL','GA',
  'HI','ID','IL','IN','IA','KS','KY','LA','ME','MD',
  'MA','MI','MN','MS','MO','MT','NE','NV','NH','NJ',
  'NM','NY','NC','ND','OH','OK','OR','PA','RI','SC',
  'SD','TN','TX','UT','VT','VA','WA','WV','WI','WY'
]

// Login
const loginForm = ref({ username: '', password: '' })
const loginError = ref('')
const loginLoading = ref(false)

async function handleLogin() {
  loginError.value = ''
  loginLoading.value = true
  try {
    const res = await request.post('/auth/customer/login', loginForm.value)
    if (res.code === 1 && res.data?.token) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('role', 'CUSTOMER')
      router.push('/portal')
    } else {
      loginError.value = res.msg || 'Login failed'
    }
  } catch {
    loginError.value = 'Invalid username or password'
  } finally {
    loginLoading.value = false
  }
}

// Register
const regForm = ref({
  username: '', password: '', email: '',
  fname: '', lname: '', gender: 'M',
  maritalStatus: 'S', custType: 'A',
  addrStreet: '', addrCity: '', addrState: '', zipcode: ''
})
const regError = ref('')
const regSuccess = ref(false)
const regLoading = ref(false)

async function handleRegister() {
  regError.value = ''
  regSuccess.value = false
  regLoading.value = true
  try {
    const res = await request.post('/auth/customer/register', regForm.value)
    if (res.code === 1) {
      regSuccess.value = true
      activeTab.value = 'login'
      loginForm.value.username = regForm.value.username
    } else {
      regError.value = res.msg || 'Registration failed'
    }
  } catch {
    regError.value = 'Registration failed. Please try again.'
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  background: var(--bg);
  padding: 40px 20px;
}

.auth-card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 40px;
  width: 100%; max-width: 560px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.3);
}

.auth-logo {
  display: flex; align-items: center; gap: 12px;
  margin-bottom: 28px; justify-content: center;
}

.logo-icon {
  width: 40px; height: 40px; background: var(--primary);
  border-radius: 10px; display: flex; align-items: center; justify-content: center;
  color: white; font-weight: 700; font-size: 14px;
}
.logo-text { color: white; font-size: 20px; font-weight: 700; }

.auth-form { display: flex; flex-direction: column; gap: 4px; margin-top: 8px; }

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.form-row .el-form-item { margin-bottom: 16px; }

.submit-btn { width: 100%; margin-top: 8px; }

.error-msg { color: #f87171; font-size: 13px; text-align: center; margin: 4px 0; }
.success-msg { color: #4ade80; font-size: 13px; text-align: center; margin: 4px 0; }
</style>
