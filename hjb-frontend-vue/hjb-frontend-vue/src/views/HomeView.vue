<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Homes</h1>
      <el-button type="primary" @click="openAdd">+ Add Home</el-button>
    </div>

    <div style="margin-bottom:16px">
      <el-input v-model="search" placeholder="Search by Policy ID or type..." clearable style="width:320px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <el-table :data="filteredData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="homeId" label="Home ID" width="90" />
      <el-table-column prop="hjbHomepolicyHpId" label="Policy ID" width="90" />
      <el-table-column prop="pdate" label="Purchase Date" width="120" />
      <el-table-column prop="pvalue" label="Value" width="110">
        <template #default="{ row }">${{ Number(row.pvalue).toLocaleString() }}</template>
      </el-table-column>
      <el-table-column prop="area" label="Area (sqft)" width="100" />
      <el-table-column prop="homeType" label="Type" width="80">
        <template #default="{ row }">{{ { S: 'Single', M: 'Multi', C: 'Condo', T: 'Town' }[row.homeType] }}</template>
      </el-table-column>
      <el-table-column prop="afn" label="AFN" width="60">
        <template #default="{ row }">{{ row.afn ? 'Yes' : 'No' }}</template>
      </el-table-column>
      <el-table-column prop="hss" label="HSS" width="60">
        <template #default="{ row }">{{ row.hss ? 'Yes' : 'No' }}</template>
      </el-table-column>
      <el-table-column prop="sp" label="Pool" width="70">
        <template #default="{ row }">{{ row.sp ? { U: 'Under', O: 'Over', I: 'Indoor', M: 'Mixed' }[row.sp] : 'None' }}</template>
      </el-table-column>
      <el-table-column prop="basement" label="Basement" width="80">
        <template #default="{ row }">{{ row.basement ? 'Yes' : 'No' }}</template>
      </el-table-column>
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.homeId)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Home' : 'Add Home'" width="600">
      <el-form :model="form" label-width="130px">
        <el-form-item label="Home Policy ID">
          <el-input-number v-model="form.hjbHomepolicyHpId" :min="1" />
        </el-form-item>
        <el-form-item label="Purchase Date">
          <el-date-picker v-model="form.pdate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Purchase Value">
          <el-input-number v-model="form.pvalue" :precision="2" :min="0.01" :step="10000" />
        </el-form-item>
        <el-form-item label="Area (sqft)">
          <el-input-number v-model="form.area" :min="1" />
        </el-form-item>
        <el-form-item label="Home Type">
          <el-select v-model="form.homeType">
            <el-option label="Single Family" value="S" />
            <el-option label="Multi Family" value="M" />
            <el-option label="Condo" value="C" />
            <el-option label="Townhouse" value="T" />
          </el-select>
        </el-form-item>
        <el-form-item label="Auto Fire (AFN)">
          <el-select v-model="form.afn">
            <el-option label="Yes" :value="1" />
            <el-option label="No" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="Security (HSS)">
          <el-select v-model="form.hss">
            <el-option label="Yes" :value="1" />
            <el-option label="No" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="Swimming Pool">
          <el-select v-model="form.sp" clearable>
            <el-option label="Underground" value="U" />
            <el-option label="Overground" value="O" />
            <el-option label="Indoor" value="I" />
            <el-option label="Mixed" value="M" />
          </el-select>
        </el-form-item>
        <el-form-item label="Basement">
          <el-select v-model="form.basement">
            <el-option label="Yes" :value="1" />
            <el-option label="No" :value="0" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { homeApi } from '../api'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const search = ref('')

const HOME_TYPES = { S: 'single', M: 'multi', C: 'condo', T: 'town' }
const filteredData = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return tableData.value
  return tableData.value.filter(r =>
    String(r.hjbHomepolicyHpId).includes(q) ||
    (HOME_TYPES[r.homeType] || '').includes(q)
  )
})

const defaultForm = { homeId: null, pdate: '', pvalue: 0, area: 0, homeType: 'S', afn: 0, hss: 0, sp: null, basement: 0, hjbHomepolicyHpId: null }
const form = ref({ ...defaultForm })

const loadData = async () => {
  loading.value = true
  const res = await homeApi.findAll()
  tableData.value = res.data
  loading.value = false
}

const openAdd = () => { isEdit.value = false; form.value = { ...defaultForm }; dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

const handleSubmit = async () => {
  if (isEdit.value) { await homeApi.update(form.value); ElMessage.success('Updated') }
  else { await homeApi.add(form.value); ElMessage.success('Added') }
  dialogVisible.value = false; loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('Delete this home?', 'Warning', { type: 'warning' })
  await homeApi.delete(id); ElMessage.success('Deleted'); loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; }
</style>
