<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Vehicle-Driver Links</h1>
      <el-button type="primary" @click="openAdd">+ Add Link</el-button>
    </div>

    <div style="margin-bottom:16px">
      <el-input v-model="search" placeholder="Search by VIN or license..." clearable style="width:320px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <el-table :data="filteredData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="hjbVehicleVin" label="Vehicle VIN" />
      <el-table-column prop="hjbDriverDriverLicense" label="Driver License" />
      <el-table-column label="Actions" width="100" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="Add Vehicle-Driver Link" width="500">
      <el-form :model="form" label-width="130px">
        <el-form-item label="Vehicle VIN">
          <el-input v-model="form.hjbVehicleVin" />
        </el-form-item>
        <el-form-item label="Driver License">
          <el-input v-model="form.hjbDriverDriverLicense" />
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
import { vehicleDriverApi } from '../api'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const search = ref('')

const filteredData = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return tableData.value
  return tableData.value.filter(r =>
    (r.hjbVehicleVin || '').toLowerCase().includes(q) ||
    (r.hjbDriverDriverLicense || '').toLowerCase().includes(q)
  )
})

const form = ref({ hjbVehicleVin: '', hjbDriverDriverLicense: '' })

const loadData = async () => {
  loading.value = true
  const res = await vehicleDriverApi.findAll()
  tableData.value = res.data
  loading.value = false
}

const openAdd = () => {
  form.value = { hjbVehicleVin: '', hjbDriverDriverLicense: '' }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await vehicleDriverApi.add(form.value)
  ElMessage.success('Added')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('Delete this link?', 'Warning', { type: 'warning' })
  await vehicleDriverApi.delete(row.hjbVehicleVin, row.hjbDriverDriverLicense)
  ElMessage.success('Deleted')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; }
</style>
