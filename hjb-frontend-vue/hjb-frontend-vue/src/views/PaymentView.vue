<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Payments</h1>
      <el-button type="primary" @click="openAdd">+ Add Payment</el-button>
    </div>

    <div style="margin-bottom:16px">
      <el-input v-model="search" placeholder="Search by Invoice ID or method..." clearable style="width:320px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <el-table :data="filteredData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="pId" label="Payment ID" width="110" />
      <el-table-column prop="hjbInvoiceIId" label="Invoice ID" width="100" />
      <el-table-column prop="method" label="Method" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ row.method }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="payAmount" label="Amount" width="120">
        <template #default="{ row }">${{ row.payAmount }}</template>
      </el-table-column>
      <el-table-column prop="payDate" label="Pay Date" />
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.pId)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Payment' : 'Add Payment'" width="500">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Invoice ID">
          <el-input-number v-model="form.hjbInvoiceIId" :min="1" />
        </el-form-item>
        <el-form-item label="Method">
          <el-select v-model="form.method">
            <el-option label="Credit" value="Credit" />
            <el-option label="Debit" value="Debit" />
            <el-option label="Check" value="Check" />
            <el-option label="PayPal" value="PayPal" />
          </el-select>
        </el-form-item>
        <el-form-item label="Amount">
          <el-input-number v-model="form.payAmount" :precision="2" :min="0.01" />
        </el-form-item>
        <el-form-item label="Pay Date">
          <el-date-picker v-model="form.payDate" type="date" value-format="YYYY-MM-DD" />
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
import { paymentApi } from '../api'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const search = ref('')

const filteredData = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return tableData.value
  return tableData.value.filter(r =>
    String(r.hjbInvoiceIId || '').includes(q) ||
    (r.method || '').toLowerCase().includes(q)
  )
})

const defaultForm = { pId: null, hjbInvoiceIId: null, method: 'Credit', payAmount: 0, payDate: '' }
const form = ref({ ...defaultForm })

const loadData = async () => {
  loading.value = true
  const res = await paymentApi.findAll()
  tableData.value = res.data
  loading.value = false
}

const openAdd = () => { isEdit.value = false; form.value = { ...defaultForm }; dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

const handleSubmit = async () => {
  if (isEdit.value) { await paymentApi.update(form.value); ElMessage.success('Updated') }
  else { await paymentApi.add(form.value); ElMessage.success('Added') }
  dialogVisible.value = false; loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('Delete this payment?', 'Warning', { type: 'warning' })
  await paymentApi.delete(id); ElMessage.success('Deleted'); loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; }
</style>
