<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Invoices</h1>
      <el-button type="primary" @click="openAdd">+ Add Invoice</el-button>
    </div>

    <div style="margin-bottom:16px">
      <el-input v-model="search" placeholder="Search by Invoice ID or Policy ID..." clearable style="width:320px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <el-table :data="pagedData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="iId" label="Invoice ID" width="100" />
      <el-table-column prop="iDate" label="Invoice Date" />
      <el-table-column prop="due" label="Due Date" />
      <el-table-column prop="amount" label="Amount" width="120">
        <template #default="{ row }">${{ row.amount }}</template>
      </el-table-column>
      <el-table-column label="Policy Type" width="120">
        <template #default="{ row }">
          <el-tag :type="row.hjbAutopolicyApId ? 'primary' : 'success'" size="small">
            {{ row.hjbAutopolicyApId ? 'Auto' : 'Home' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Policy ID" width="100">
        <template #default="{ row }">{{ row.hjbAutopolicyApId || row.hjbHomepolicyHpId }}</template>
      </el-table-column>
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.iid)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-bar">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" :total="filteredData.length"
        layout="total, sizes, prev, pager, next" background />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Invoice' : 'Add Invoice'" width="500">
      <el-form :model="form" label-width="140px">
        <el-form-item label="Invoice Date">
          <el-date-picker v-model="form.iDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Due Date">
          <el-date-picker v-model="form.due" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Amount">
          <el-input-number v-model="form.amount" :precision="2" :min="0.01" />
        </el-form-item>
        <el-form-item label="Policy Type">
          <el-select v-model="policyType" @change="onPolicyTypeChange">
            <el-option label="Auto Policy" value="auto" />
            <el-option label="Home Policy" value="home" />
          </el-select>
        </el-form-item>
        <el-form-item label="Policy ID">
          <el-input-number v-model="policyId" :min="1" />
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
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { invoiceApi } from '../api'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const policyType = ref('auto')
const policyId = ref(null)
const search = ref('')

const filteredData = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return tableData.value
  return tableData.value.filter(r =>
    String(r.iid || '').includes(q) ||
    String(r.hjbAutopolicyApId || r.hjbHomepolicyHpId || '').includes(q)
  )
})

const currentPage = ref(1)
const pageSize    = ref(10)
const pagedData   = computed(() => filteredData.value.slice((currentPage.value-1)*pageSize.value, currentPage.value*pageSize.value))
watch(search, () => { currentPage.value = 1 })

const defaultForm = { iId: null, iDate: '', due: '', amount: 0, hjbHomepolicyHpId: null, hjbAutopolicyApId: null }
const form = ref({ ...defaultForm })

const loadData = async () => {
  loading.value = true
  const res = await invoiceApi.findAll()
  tableData.value = res.data
  loading.value = false
}

const onPolicyTypeChange = () => { policyId.value = null }

const openAdd = () => {
  isEdit.value = false; form.value = { ...defaultForm }
  policyType.value = 'auto'; policyId.value = null
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true; form.value = { ...row }
  policyType.value = row.hjbAutopolicyApId ? 'auto' : 'home'
  policyId.value = row.hjbAutopolicyApId || row.hjbHomepolicyHpId
  dialogVisible.value = true
}

const handleSubmit = async () => {
  form.value.hjbAutopolicyApId = policyType.value === 'auto' ? policyId.value : null
  form.value.hjbHomepolicyHpId = policyType.value === 'home' ? policyId.value : null
  if (isEdit.value) { await invoiceApi.update(form.value); ElMessage.success('Updated') }
  else { await invoiceApi.add(form.value); ElMessage.success('Added') }
  dialogVisible.value = false; loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('Delete this invoice?', 'Warning', { type: 'warning' })
  await invoiceApi.delete(id); ElMessage.success('Deleted'); loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; }
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
