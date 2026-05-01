<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Insurance Plans</h1>
      <el-button type="primary" @click="openAdd">+ Add Plan</el-button>
    </div>

    <el-table :data="pagedData" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="planId" label="ID" width="60" />
      <el-table-column label="Type" width="100">
        <template #default="{ row }">
          <el-tag :type="row.planType === 'AUTO' ? 'primary' : 'success'" size="small">
            {{ row.planType === 'AUTO' ? '🚗 Auto' : '🏠 Home' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="planName" label="Plan Name" width="110" />
      <el-table-column label="Annual Price" width="130">
        <template #default="{ row }">
          <span style="font-weight:600;color:#2563eb">${{ row.amount }}/yr</span>
        </template>
      </el-table-column>
      <el-table-column label="Features" min-width="300">
        <template #default="{ row }">
          <div class="feature-chips">
            <el-tag
              v-for="f in (row.features || '').split(',')"
              :key="f" size="small" type="info"
              style="margin:2px"
            >{{ f.trim() }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="Status" width="90">
        <template #default="{ row }">
          <el-tag :type="row.isActive ? 'success' : 'info'" size="small">
            {{ row.isActive ? 'Active' : 'Hidden' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.planId)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-bar">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" :total="tableData.length"
        layout="total, sizes, prev, pager, next" background />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Plan' : 'Add Plan'" width="540">
      <el-form :model="form" label-width="130px">
        <el-form-item label="Policy Type">
          <el-select v-model="form.planType" style="width:100%">
            <el-option label="🚗 Auto Insurance" value="AUTO" />
            <el-option label="🏠 Home Insurance" value="HOME" />
          </el-select>
        </el-form-item>
        <el-form-item label="Plan Name">
          <el-input v-model="form.planName" placeholder="e.g. Basic, Standard, Premium" />
        </el-form-item>
        <el-form-item label="Annual Price ($)">
          <el-input-number v-model="form.amount" :precision="2" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="Features">
          <el-input
            v-model="form.features"
            type="textarea" :rows="4"
            placeholder="One feature per line, will be stored comma-separated"
            @input="normalizeFeatures"
          />
          <div style="font-size:12px;color:#94a3b8;margin-top:4px">
            Enter each feature on a new line (or comma-separated)
          </div>
        </el-form-item>
        <el-form-item label="Visible to Users">
          <el-switch v-model="form.isActive" />
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
import { planApi } from '../api'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

const currentPage = ref(1)
const pageSize    = ref(10)
const pagedData   = computed(() => tableData.value.slice((currentPage.value-1)*pageSize.value, currentPage.value*pageSize.value))

const defaultForm = { planId: null, planType: 'AUTO', planName: '', amount: 800, features: '', isActive: true }
const form = ref({ ...defaultForm })

const loadData = async () => {
  loading.value = true
  const res = await planApi.findAll()
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
  // Display features with newlines for easier editing
  form.value = { ...row, features: (row.features || '').split(',').map(f => f.trim()).join('\n') }
  dialogVisible.value = true
}

// Convert newlines → commas on input
const normalizeFeatures = () => {
  // Allow editing with newlines; we'll normalize on save
}

const handleSubmit = async () => {
  // Normalize features: newlines or commas → comma-separated, trimmed
  const rawFeatures = form.value.features || ''
  form.value.features = rawFeatures
    .split(/[\n,]/)
    .map(f => f.trim())
    .filter(Boolean)
    .join(',')

  if (isEdit.value) {
    await planApi.update(form.value)
    ElMessage.success('Plan updated')
  } else {
    await planApi.add(form.value)
    ElMessage.success('Plan added')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('Delete this plan? This will not affect existing policies.', 'Confirm', { type: 'warning' })
  await planApi.delete(id)
  ElMessage.success('Deleted')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; }
.feature-chips { display: flex; flex-wrap: wrap; }
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
