<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Payments</h1>
      <el-button type="primary" @click="openAdd">+ Add Payment</el-button>
    </div>

    <div style="margin-bottom:16px;display:flex;align-items:center;gap:12px">
      <el-input v-model="search" placeholder="Search by payment method..." clearable style="width:320px"
        @input="handleSearch" @clear="handleClear">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-tag type="danger" v-if="search">Server-side query active</el-tag>
    </div>

    <el-table :data="pagedData" stripe style="width: 100%" v-loading="loading">
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

    <div class="pagination-bar">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" :total="tableData.length"
        layout="total, sizes, prev, pager, next" background />
    </div>

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

const currentPage = ref(1)
const pageSize    = ref(10)
const pagedData   = computed(() =>
  tableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
)

// Server-side search — sends the raw input to the backend (vulnerable to SQL injection when backend uses ${})
async function handleSearch() {
  if (!search.value.trim()) { return loadData() }
  loading.value = true
  try {
    const res = await paymentApi.search(search.value)
    tableData.value = res.data ?? []
    currentPage.value = 1
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleClear() { loadData() }

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
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
