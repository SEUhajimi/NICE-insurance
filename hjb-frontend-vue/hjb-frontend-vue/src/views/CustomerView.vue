<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Customers</h1>
      <el-button type="primary" @click="openAdd">+ Add Customer</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="search" placeholder="Search by name, city, or state..." clearable style="width: 320px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <el-table :data="filteredData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="custId" label="ID" width="70" />
      <el-table-column prop="fname" label="First Name" />
      <el-table-column prop="lname" label="Last Name" />
      <el-table-column prop="gender" label="Gender" width="80">
        <template #default="{ row }">{{ row.gender === 'M' ? 'Male' : 'Female' }}</template>
      </el-table-column>
      <el-table-column prop="maritalStatus" label="Marital" width="90">
        <template #default="{ row }">{{ { M: 'Married', S: 'Single', W: 'Widowed' }[row.maritalStatus] }}</template>
      </el-table-column>
      <el-table-column prop="custType" label="Type" width="80">
        <template #default="{ row }">
          <el-tag :type="{ A: 'primary', H: 'success', B: 'warning' }[row.custType]" size="small">
            {{ { A: 'Auto', H: 'Home', B: 'Both' }[row.custType] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="addrCity" label="City" />
      <el-table-column prop="addrState" label="State" width="70" />
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.custId)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Customer' : 'Add Customer'" width="600">
      <el-form :model="form" label-width="130px">
        <el-form-item label="First Name">
          <el-input v-model="form.fname" />
        </el-form-item>
        <el-form-item label="Last Name">
          <el-input v-model="form.lname" />
        </el-form-item>
        <el-form-item label="Gender">
          <el-select v-model="form.gender">
            <el-option label="Male" value="M" />
            <el-option label="Female" value="F" />
          </el-select>
        </el-form-item>
        <el-form-item label="Marital Status">
          <el-select v-model="form.maritalStatus">
            <el-option label="Married" value="M" />
            <el-option label="Single" value="S" />
            <el-option label="Widowed" value="W" />
          </el-select>
        </el-form-item>
        <el-form-item label="Customer Type">
          <el-select v-model="form.custType">
            <el-option label="Auto" value="A" />
            <el-option label="Home" value="H" />
            <el-option label="Both" value="B" />
          </el-select>
        </el-form-item>
        <el-form-item label="Street">
          <el-input v-model="form.addrStreet" />
        </el-form-item>
        <el-form-item label="City">
          <el-input v-model="form.addrCity" />
        </el-form-item>
        <el-form-item label="State">
          <el-select v-model="form.addrState" filterable placeholder="Select or type state">
            <el-option v-for="s in US_STATES" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="Zipcode">
          <el-input v-model="form.zipcode" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? 'Update' : 'Add' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { customerApi } from '../api'

const US_STATES = [
  'AL','AK','AZ','AR','CA','CO','CT','DE','FL','GA',
  'HI','ID','IL','IN','IA','KS','KY','LA','ME','MD',
  'MA','MI','MN','MS','MO','MT','NE','NV','NH','NJ',
  'NM','NY','NC','ND','OH','OK','OR','PA','RI','SC',
  'SD','TN','TX','UT','VT','VA','WA','WV','WI','WY'
]

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const search = ref('')

const filteredData = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return tableData.value
  return tableData.value.filter(r =>
    (r.fname + ' ' + r.lname).toLowerCase().includes(q) ||
    (r.addrCity || '').toLowerCase().includes(q) ||
    (r.addrState || '').toLowerCase().includes(q)
  )
})

const defaultForm = {
  fname: '', lname: '', gender: 'M',
  maritalStatus: 'S', custType: 'A', addrStreet: '',
  addrCity: '', addrState: '', zipcode: ''
}
const form = ref({ ...defaultForm })

const loadData = async () => {
  loading.value = true
  const res = await customerApi.findAll()
  tableData.value = res.data
  loading.value = false
}

const openAdd = () => {
  isEdit.value = false
  form.value = { ...defaultForm }
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await customerApi.update(form.value)
    ElMessage.success('Updated successfully')
  } else {
    await customerApi.add(form.value)
    ElMessage.success('Added successfully')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('Are you sure to delete this customer?', 'Warning', { type: 'warning' })
  await customerApi.delete(id)
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
