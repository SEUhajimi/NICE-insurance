<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Vehicles</h1>
      <el-button type="primary" @click="openAdd">+ Add Vehicle</el-button>
    </div>

    <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="vin" label="VIN" width="200" />
      <el-table-column prop="mmy" label="Make-Model-Year" />
      <el-table-column prop="status" label="Status" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ { L: 'Leased', F: 'Financed', O: 'Owned' }[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="hjbAutopolicyApId" label="Policy ID" width="100" />
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.vin)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Vehicle' : 'Add Vehicle'" width="500">
      <el-form :model="form" label-width="120px">
        <el-form-item label="VIN" v-if="!isEdit">
          <el-input v-model="form.vin" maxlength="17" />
        </el-form-item>
        <el-form-item label="Make-Model-Year">
          <el-input v-model="form.mmy" />
        </el-form-item>
        <el-form-item label="Status">
          <el-select v-model="form.status">
            <el-option label="Leased" value="L" />
            <el-option label="Financed" value="F" />
            <el-option label="Owned" value="O" />
          </el-select>
        </el-form-item>
        <el-form-item label="Auto Policy ID">
          <el-input-number v-model="form.hjbAutopolicyApId" :min="1" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { vehicleApi } from '../api'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

const defaultForm = { vin: '', mmy: '', status: 'O', hjbAutopolicyApId: null }
const form = ref({ ...defaultForm })

const loadData = async () => {
  loading.value = true
  const res = await vehicleApi.findAll()
  tableData.value = res.data
  loading.value = false
}

const openAdd = () => { isEdit.value = false; form.value = { ...defaultForm }; dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

const handleSubmit = async () => {
  if (isEdit.value) { await vehicleApi.update(form.value); ElMessage.success('Updated') }
  else { await vehicleApi.add(form.value); ElMessage.success('Added') }
  dialogVisible.value = false; loadData()
}

const handleDelete = async (vin) => {
  await ElMessageBox.confirm('Delete this vehicle?', 'Warning', { type: 'warning' })
  await vehicleApi.delete(vin); ElMessage.success('Deleted'); loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; }
</style>
