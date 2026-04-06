<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Employees</h1>
      <el-button type="primary" @click="openAdd">+ Add Employee</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="search" placeholder="Search by name or username..." clearable style="width: 320px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <el-table :data="filteredData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="empId" label="ID" width="70" />
      <el-table-column prop="username" label="Username" />
      <el-table-column prop="fname" label="First Name" />
      <el-table-column prop="lname" label="Last Name" />
      <el-table-column prop="email" label="Email" />
      <el-table-column prop="role" label="Role" width="110">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="100" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row.empId)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="Add Employee" width="500">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="First Name">
          <el-input v-model="form.fname" />
        </el-form-item>
        <el-form-item label="Last Name">
          <el-input v-model="form.lname" />
        </el-form-item>
        <el-form-item label="Email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="Role">
          <el-select v-model="form.role">
            <el-option label="Employee" value="EMPLOYEE" />
            <el-option label="Admin" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit">Add</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { employeeApi } from '../api'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const search = ref('')

const filteredData = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return tableData.value
  return tableData.value.filter(r =>
    (r.fname + ' ' + r.lname).toLowerCase().includes(q) ||
    (r.username || '').toLowerCase().includes(q)
  )
})

const defaultForm = { username: '', password: '', fname: '', lname: '', email: '', role: 'EMPLOYEE' }
const form = ref({ ...defaultForm })

const loadData = async () => {
  loading.value = true
  const res = await employeeApi.findAll()
  tableData.value = res.data
  loading.value = false
}

const openAdd = () => {
  form.value = { ...defaultForm }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await employeeApi.add(form.value)
  ElMessage.success('Employee added successfully')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('Delete this employee?', 'Warning', { type: 'warning' })
  await employeeApi.delete(id)
  ElMessage.success('Deleted successfully')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { max-width: 1200px; }
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-title { font-size: 28px; font-weight: 700; color: var(--text); }
.search-bar { margin-bottom: 16px; }
</style>
