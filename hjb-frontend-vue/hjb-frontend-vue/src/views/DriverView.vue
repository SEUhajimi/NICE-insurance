<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Drivers</h1>
      <el-button type="primary" @click="openAdd">+ Add Driver</el-button>
    </div>

    <div style="margin-bottom:16px">
      <el-input v-model="search" placeholder="Search by name or license..." clearable style="width:320px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <el-table :data="filteredData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="driverLicense" label="License" width="120" />
      <el-table-column prop="fname" label="First Name" />
      <el-table-column prop="lname" label="Last Name" />
      <el-table-column prop="birthday" label="Birthday" />
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.driverLicense)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Driver' : 'Add Driver'" width="500">
      <el-form :model="form" label-width="120px">
        <el-form-item label="License" v-if="!isEdit">
          <el-input v-model="form.driverLicense" />
        </el-form-item>
        <el-form-item label="First Name">
          <el-input v-model="form.fname" />
        </el-form-item>
        <el-form-item label="Last Name">
          <el-input v-model="form.lname" />
        </el-form-item>
        <el-form-item label="Birthday">
          <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" />
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
import { driverApi } from '../api'

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
    (r.driverLicense || '').toLowerCase().includes(q)
  )
})

const defaultForm = { driverLicense: '', fname: '', lname: '', birthday: '' }
const form = ref({ ...defaultForm })

const loadData = async () => {
  loading.value = true
  const res = await driverApi.findAll()
  tableData.value = res.data
  loading.value = false
}

const openAdd = () => { isEdit.value = false; form.value = { ...defaultForm }; dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

const handleSubmit = async () => {
  if (isEdit.value) { await driverApi.update(form.value); ElMessage.success('Updated') }
  else { await driverApi.add(form.value); ElMessage.success('Added') }
  dialogVisible.value = false; loadData()
}

const handleDelete = async (license) => {
  await ElMessageBox.confirm('Delete this driver?', 'Warning', { type: 'warning' })
  await driverApi.delete(license); ElMessage.success('Deleted'); loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; }
</style>
