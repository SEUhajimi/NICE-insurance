<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Auto Policies</h1>
      <el-button type="primary" @click="openAdd">+ Add Policy</el-button>
    </div>

    <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="apId" label="Policy ID" width="100" />
      <el-table-column prop="hjbCustomerCustId" label="Customer ID" width="110" />
      <el-table-column prop="sdate" label="Start Date" />
      <el-table-column prop="edate" label="End Date" />
      <el-table-column prop="amount" label="Amount" width="120">
        <template #default="{ row }">${{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="Status" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'C' ? 'success' : 'info'" size="small">
            {{ row.status === 'C' ? 'Current' : 'Expired' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.apId)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Auto Policy' : 'Add Auto Policy'" width="500">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Customer ID">
          <el-input-number v-model="form.hjbCustomerCustId" :min="1" />
        </el-form-item>
        <el-form-item label="Start Date">
          <el-date-picker v-model="form.sdate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="End Date">
          <el-date-picker v-model="form.edate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Amount">
          <el-input-number v-model="form.amount" :precision="2" :min="0.01" />
        </el-form-item>
        <el-form-item label="Status">
          <el-select v-model="form.status">
            <el-option label="Current" value="C" />
            <el-option label="Expired" value="E" />
          </el-select>
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
import { autoPolicyApi } from '../api'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

const defaultForm = { apId: null, sdate: '', edate: '', amount: 0, status: 'C', hjbCustomerCustId: null }
const form = ref({ ...defaultForm })

const loadData = async () => {
  loading.value = true
  const res = await autoPolicyApi.findAll()
  tableData.value = res.data
  loading.value = false
}

const openAdd = () => { isEdit.value = false; form.value = { ...defaultForm }; dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

const handleSubmit = async () => {
  if (isEdit.value) { await autoPolicyApi.update(form.value); ElMessage.success('Updated') }
  else { await autoPolicyApi.add(form.value); ElMessage.success('Added') }
  dialogVisible.value = false; loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('Delete this policy?', 'Warning', { type: 'warning' })
  await autoPolicyApi.delete(id); ElMessage.success('Deleted'); loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; }
</style>
