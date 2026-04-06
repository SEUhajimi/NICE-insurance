<template>
  <div>
    <h1 class="portal-title">My Insurance Portal</h1>

    <el-tabs v-model="activeTab">
      <!-- Profile -->
      <el-tab-pane label="My Profile" name="profile">
        <div v-if="profile" class="profile-card">
          <div class="profile-row">
            <span class="profile-label">Name</span>
            <span class="profile-value">{{ profile.fname }} {{ profile.lname }}</span>
          </div>
          <div class="profile-row">
            <span class="profile-label">Address</span>
            <span class="profile-value">{{ profile.addrStreet }}, {{ profile.addrCity }}, {{ profile.addrState }} {{ profile.zipcode }}</span>
          </div>
          <div class="profile-row">
            <span class="profile-label">Gender</span>
            <span class="profile-value">{{ profile.gender === 'M' ? 'Male' : 'Female' }}</span>
          </div>
          <div class="profile-row">
            <span class="profile-label">Customer Type</span>
            <span class="profile-value">{{ { A: 'Auto', H: 'Home', B: 'Auto & Home' }[profile.custType] }}</span>
          </div>
        </div>
        <el-skeleton v-else :rows="4" animated />
      </el-tab-pane>

      <!-- Auto Policies -->
      <el-tab-pane label="Auto Policies" name="auto">
        <el-table :data="autoPolicies" stripe v-loading="loadingAuto">
          <el-table-column prop="apId" label="Policy ID" width="100" />
          <el-table-column prop="sdate" label="Start Date" />
          <el-table-column prop="edate" label="End Date" />
          <el-table-column prop="amount" label="Premium" width="120">
            <template #default="{ row }">${{ row.amount }}</template>
          </el-table-column>
          <el-table-column prop="status" label="Status" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'C' ? 'success' : 'info'" size="small">
                {{ row.status === 'C' ? 'Current' : 'Expired' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="!loadingAuto && autoPolicies.length === 0" class="empty-msg">No auto policies found.</div>
      </el-tab-pane>

      <!-- Home Policies -->
      <el-tab-pane label="Home Policies" name="home">
        <el-table :data="homePolicies" stripe v-loading="loadingHome">
          <el-table-column prop="hpId" label="Policy ID" width="100" />
          <el-table-column prop="sdate" label="Start Date" />
          <el-table-column prop="edate" label="End Date" />
          <el-table-column prop="amount" label="Premium" width="120">
            <template #default="{ row }">${{ row.amount }}</template>
          </el-table-column>
          <el-table-column prop="status" label="Status" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'C' ? 'success' : 'info'" size="small">
                {{ row.status === 'C' ? 'Current' : 'Expired' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="!loadingHome && homePolicies.length === 0" class="empty-msg">No home policies found.</div>
      </el-tab-pane>

      <!-- Invoices -->
      <el-tab-pane label="My Invoices" name="invoices">
        <el-table :data="invoices" stripe v-loading="loadingInvoices">
          <el-table-column prop="iid" label="Invoice ID" width="110" />
          <el-table-column prop="idate" label="Invoice Date" />
          <el-table-column prop="due" label="Due Date" />
          <el-table-column prop="amount" label="Amount" width="120">
            <template #default="{ row }">${{ row.amount }}</template>
          </el-table-column>
          <el-table-column label="Type" width="100">
            <template #default="{ row }">
              <el-tag :type="row.hjbAutopolicyApId ? 'primary' : 'success'" size="small">
                {{ row.hjbAutopolicyApId ? 'Auto' : 'Home' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Pay" width="120">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="openPayment(row)">Pay Now</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="!loadingInvoices && invoices.length === 0" class="empty-msg">No invoices found.</div>
      </el-tab-pane>
    </el-tabs>

    <!-- Payment Dialog -->
    <el-dialog v-model="payDialog" title="Make Payment" width="420">
      <el-form :model="payForm" label-width="120px">
        <el-form-item label="Invoice ID">
          <span>{{ payForm.invoiceId }}</span>
        </el-form-item>
        <el-form-item label="Amount">
          <el-input-number v-model="payForm.payAmount" :precision="2" :min="0.01" />
        </el-form-item>
        <el-form-item label="Method">
          <el-select v-model="payForm.method">
            <el-option label="Credit Card" value="Credit" />
            <el-option label="Debit Card" value="Debit" />
            <el-option label="Check" value="Check" />
            <el-option label="PayPal" value="PayPal" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payDialog = false">Cancel</el-button>
        <el-button type="primary" :loading="paying" @click="submitPayment">Confirm Payment</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { portalApi } from '../api'

const activeTab = ref('profile')
const profile = ref(null)
const autoPolicies = ref([])
const homePolicies = ref([])
const invoices = ref([])
const loadingAuto = ref(false)
const loadingHome = ref(false)
const loadingInvoices = ref(false)

const payDialog = ref(false)
const paying = ref(false)
const payForm = ref({ invoiceId: null, payAmount: 0, method: 'Credit' })

onMounted(async () => {
  const [profileRes, autoRes, homeRes, invoiceRes] = await Promise.all([
    portalApi.myProfile(),
    portalApi.myAutoPolicies(),
    portalApi.myHomePolicies(),
    portalApi.myInvoices(),
  ])
  profile.value = profileRes.data
  autoPolicies.value = autoRes.data || []
  homePolicies.value = homeRes.data || []
  invoices.value = invoiceRes.data || []
})

function openPayment(row) {
  payForm.value = { invoiceId: row.iid, payAmount: row.amount, method: 'Credit' }
  payDialog.value = true
}

async function submitPayment() {
  paying.value = true
  try {
    await portalApi.makePayment(payForm.value)
    ElMessage.success('Payment submitted successfully')
    payDialog.value = false
  } catch {
    ElMessage.error('Payment failed')
  } finally {
    paying.value = false
  }
}
</script>

<style scoped>
.portal-title { font-size: 28px; font-weight: 700; color: var(--text-primary); margin-bottom: 24px; }

.profile-card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 28px;
  max-width: 600px;
  display: flex; flex-direction: column; gap: 16px;
}

.profile-row { display: flex; gap: 16px; }
.profile-label { width: 130px; color: var(--text-secondary); font-size: 14px; flex-shrink: 0; }
.profile-value { color: var(--text-primary); font-size: 14px; font-weight: 500; }

.empty-msg { text-align: center; color: var(--text-secondary); padding: 40px; }
</style>
