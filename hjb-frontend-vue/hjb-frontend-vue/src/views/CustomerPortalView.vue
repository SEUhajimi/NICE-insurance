<template>
  <div>
    <!-- Welcome Banner -->
    <div class="welcome-banner">
      <div>
        <h1 class="welcome-title">Welcome back, {{ profile?.fname || username }}</h1>
        <p class="welcome-sub">Manage your insurance policies and payments</p>
      </div>
      <div class="stat-cards">
        <div class="stat-card">
          <div class="stat-num">{{ autoPolicies.length }}</div>
          <div class="stat-label">Auto Policies</div>
        </div>
        <div class="stat-card">
          <div class="stat-num">{{ homePolicies.length }}</div>
          <div class="stat-label">Home Policies</div>
        </div>
        <div class="stat-card" :class="{ 'has-unpaid': unpaidCount > 0 }">
          <div class="stat-num">{{ unpaidCount }}</div>
          <div class="stat-label">Unpaid Invoices</div>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="portal-tabs">

      <!-- Profile -->
      <el-tab-pane label="My Profile" name="profile">
        <div v-if="profile" class="profile-card">
          <div class="profile-section">
            <h3>Personal Information</h3>
            <div class="profile-grid">
              <div class="profile-item">
                <span class="pi-label">Full Name</span>
                <span class="pi-value">{{ profile.fname }} {{ profile.lname }}</span>
              </div>
              <div class="profile-item">
                <span class="pi-label">Gender</span>
                <span class="pi-value">{{ profile.gender === 'M' ? 'Male' : 'Female' }}</span>
              </div>
              <div class="profile-item">
                <span class="pi-label">Marital Status</span>
                <span class="pi-value">{{ { M: 'Married', S: 'Single', W: 'Widowed' }[profile.maritalStatus] }}</span>
              </div>
              <div class="profile-item">
                <span class="pi-label">Insurance Type</span>
                <span class="pi-value">
                  <el-tag v-if="profile.custType" size="small" type="primary">
                    {{ { A: 'Auto', H: 'Home', B: 'Auto + Home' }[profile.custType] }}
                  </el-tag>
                  <el-tag v-else size="small" type="info">Not yet insured</el-tag>
                </span>
              </div>
            </div>
          </div>
          <div class="profile-section">
            <h3>Address</h3>
            <div class="profile-grid">
              <div class="profile-item" style="grid-column: span 2">
                <span class="pi-label">Street</span>
                <span class="pi-value">{{ profile.addrStreet }}</span>
              </div>
              <div class="profile-item">
                <span class="pi-label">City</span>
                <span class="pi-value">{{ profile.addrCity }}</span>
              </div>
              <div class="profile-item">
                <span class="pi-label">State / Zip</span>
                <span class="pi-value">{{ profile.addrState }} {{ profile.zipcode }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-skeleton v-else :rows="5" animated style="max-width:600px" />
      </el-tab-pane>

      <!-- Get Insured (always visible) -->
      <el-tab-pane name="apply">
        <template #label>
          Get Insured
          <el-badge v-if="!profile?.custType" value="New" type="danger" class="tab-badge" />
        </template>
        <div class="apply-card">
          <h2 class="apply-title">Annual Insurance Plans</h2>
          <p class="apply-sub">All plans cover one full year from the date of purchase. An invoice will be issued upon confirmation.</p>

          <div class="plans-grid">
            <div v-for="plan in plans" :key="plan.id"
              class="plan-card" :class="{ selected: selectedPlan?.id === plan.id }"
              @click="selectedPlan = plan">
              <div class="plan-icon">{{ plan.icon }}</div>
              <div class="plan-type">{{ plan.typeLabel }}</div>
              <div class="plan-name">{{ plan.name }}</div>
              <div class="plan-price">${{ plan.amount }}<span>/yr</span></div>
              <ul class="plan-features">
                <li v-for="f in plan.features" :key="f">{{ f }}</li>
              </ul>
              <div class="plan-check" v-if="selectedPlan?.id === plan.id">✓ Selected</div>
            </div>
          </div>

          <el-button type="primary" size="large" :loading="purchasing"
            :disabled="!selectedPlan" @click="submitPurchase"
            style="margin-top:28px;min-width:200px">
            Confirm Purchase
          </el-button>
        </div>
      </el-tab-pane>

      <!-- My Policies -->
      <el-tab-pane name="policies">
        <template #label>
          Policies
          <el-badge v-if="autoPolicies.length + homePolicies.length > 0"
            :value="autoPolicies.length + homePolicies.length" class="tab-badge" />
        </template>

        <div v-if="autoPolicies.length > 0" class="policy-section">
          <h3 class="section-label">🚗 Auto Insurance</h3>
          <div class="policy-grid">
            <div v-for="p in autoPolicies" :key="p.apId" class="policy-card">
              <div class="policy-header">
                <span class="policy-id">Policy #{{ p.apId }}</span>
                <el-tag :type="p.status === 'C' ? 'success' : 'info'" size="small">
                  {{ p.status === 'C' ? 'Active' : 'Expired' }}
                </el-tag>
              </div>
              <div class="policy-details">
                <div><span class="pd-label">Start</span> {{ p.sdate }}</div>
                <div><span class="pd-label">End</span> {{ p.edate }}</div>
                <div class="policy-amount">${{ p.amount }}<span>/yr</span></div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="homePolicies.length > 0" class="policy-section">
          <h3 class="section-label">🏠 Home Insurance</h3>
          <div class="policy-grid">
            <div v-for="p in homePolicies" :key="p.hpId" class="policy-card">
              <div class="policy-header">
                <span class="policy-id">Policy #{{ p.hpId }}</span>
                <el-tag :type="p.status === 'C' ? 'success' : 'info'" size="small">
                  {{ p.status === 'C' ? 'Active' : 'Expired' }}
                </el-tag>
              </div>
              <div class="policy-details">
                <div><span class="pd-label">Start</span> {{ p.sdate }}</div>
                <div><span class="pd-label">End</span> {{ p.edate }}</div>
                <div class="policy-amount">${{ p.amount }}<span>/yr</span></div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="autoPolicies.length === 0 && homePolicies.length === 0" class="empty-state">
          <p>No active policies. Go to <strong>Get Insured</strong> to apply.</p>
        </div>
      </el-tab-pane>

      <!-- Invoices -->
      <el-tab-pane name="invoices">
        <template #label>
          Invoices
          <el-badge v-if="unpaidCount > 0" :value="unpaidCount" type="danger" class="tab-badge" />
        </template>

        <div class="invoice-filter">
          <el-radio-group v-model="invoiceFilter" size="small">
            <el-radio-button value="all">All</el-radio-button>
            <el-radio-button value="unpaid">Unpaid</el-radio-button>
            <el-radio-button value="paid">Paid</el-radio-button>
          </el-radio-group>
        </div>

        <el-table :data="filteredInvoices" stripe v-loading="loadingInvoices">
          <el-table-column prop="iId" label="Invoice #" width="100" />
          <el-table-column label="Type" width="90">
            <template #default="{ row }">
              <el-tag :type="row.policyType === 'Auto' ? 'primary' : 'success'" size="small">
                {{ row.policyType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="iDate" label="Issued" width="110" />
          <el-table-column prop="due" label="Due" width="110" />
          <el-table-column label="Amount" width="110">
            <template #default="{ row }">${{ row.amount }}</template>
          </el-table-column>
          <el-table-column label="Paid" width="110">
            <template #default="{ row }">
              <span :class="row.paid ? 'paid-amount' : 'unpaid-amount'">${{ row.paidAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Status" width="110">
            <template #default="{ row }">
              <el-tag :type="row.paid ? 'success' : 'danger'" size="small">
                {{ row.paid ? 'Paid' : 'Unpaid' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="" width="120">
            <template #default="{ row }">
              <el-button v-if="!row.paid" size="small" type="primary" @click="openPayment(row)">Pay Now</el-button>
              <el-button v-else size="small" plain @click="viewPayments(row)">View</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- Payment History -->
      <el-tab-pane label="Payment History" name="payments">
        <el-table :data="payments" stripe v-loading="loadingPayments">
          <el-table-column prop="pId" label="Payment #" width="110" />
          <el-table-column prop="hjbInvoiceIId" label="Invoice #" width="110" />
          <el-table-column prop="method" label="Method" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ row.method }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Amount" width="120">
            <template #default="{ row }"><span class="paid-amount">${{ row.payAmount }}</span></template>
          </el-table-column>
          <el-table-column prop="payDate" label="Date" />
        </el-table>
        <div v-if="!loadingPayments && payments.length === 0" class="empty-state">
          No payment history.
        </div>
      </el-tab-pane>

    </el-tabs>

    <!-- Pay Dialog -->
    <el-dialog v-model="payDialog" title="Make Payment" width="440">
      <div class="pay-invoice-info">
        <div class="pay-row">
          <span>Invoice #</span><strong>{{ payTarget?.iId }}</strong>
        </div>
        <div class="pay-row">
          <span>Total Amount</span><strong>${{ payTarget?.amount }}</strong>
        </div>
        <div class="pay-row">
          <span>Already Paid</span><strong>${{ payTarget?.paidAmount }}</strong>
        </div>
        <div class="pay-row outstanding">
          <span>Outstanding</span><strong>${{ outstanding }}</strong>
        </div>
      </div>
      <el-form :model="payForm" label-width="120px" style="margin-top:20px">
        <el-form-item label="Pay Amount">
          <el-input-number v-model="payForm.payAmount" :precision="2" :min="0.01" :max="outstanding" style="width:100%" />
        </el-form-item>
        <el-form-item label="Method">
          <el-select v-model="payForm.method" style="width:100%">
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

    <!-- Invoice Payments Detail -->
    <el-dialog v-model="detailDialog" :title="`Invoice #${detailInvoice?.iId} — Payment Records`" width="500">
      <el-table :data="detailInvoice?.payments || []">
        <el-table-column prop="pId" label="Payment #" width="100" />
        <el-table-column prop="method" label="Method" />
        <el-table-column label="Amount">
          <template #default="{ row }">${{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column prop="payDate" label="Date" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { portalApi, planApi } from '../api'

const username = localStorage.getItem('username') || ''
const activeTab = ref('profile')
const invoiceFilter = ref('all')

const profile = ref(null)
const autoPolicies = ref([])
const homePolicies = ref([])
const invoices = ref([])
const payments = ref([])
const loadingInvoices = ref(false)
const loadingPayments = ref(false)

const unpaidCount = computed(() => invoices.value.filter(i => !i.paid).length)
const filteredInvoices = computed(() => {
  if (invoiceFilter.value === 'paid') return invoices.value.filter(i => i.paid)
  if (invoiceFilter.value === 'unpaid') return invoices.value.filter(i => !i.paid)
  return invoices.value
})

// Plans (loaded from API)
const plans = ref([])
const selectedPlan = ref(null)
const purchasing = ref(false)

const loadPlans = async () => {
  try {
    const res = await planApi.findActive()
    plans.value = (res.data || []).map(p => ({
      id: `${p.planType}-${p.planId}`,
      type: p.planType,
      typeLabel: p.planType === 'AUTO' ? 'Auto Insurance' : 'Home Insurance',
      icon: p.planType === 'AUTO' ? '🚗' : '🏠',
      name: p.planName,
      amount: Number(p.amount),
      features: (p.features || '').split(',').map(f => f.trim()).filter(Boolean)
    }))
  } catch {
    // fallback: show empty
  }
}

async function submitPurchase() {
  purchasing.value = true
  try {
    await portalApi.purchasePolicy({ type: selectedPlan.value.type, amount: selectedPlan.value.amount })
    ElMessage.success('Policy purchased successfully!')
    selectedPlan.value = null
    activeTab.value = 'policies'
    const [profileRes, autoRes, homeRes, invRes, payRes] = await Promise.all([
      portalApi.myProfile(), portalApi.myAutoPolicies(), portalApi.myHomePolicies(),
      portalApi.myInvoices(), portalApi.myPayments()
    ])
    profile.value = profileRes.data
    autoPolicies.value = autoRes.data || []
    homePolicies.value = homeRes.data || []
    invoices.value = invRes.data || []
    payments.value = payRes.data || []
  } catch (e) {
    ElMessage.error(e?.response?.data?.msg || 'Purchase failed')
  } finally {
    purchasing.value = false
  }
}

// Payment dialog
const payDialog = ref(false)
const paying = ref(false)
const payTarget = ref(null)
const payForm = ref({ invoiceId: null, payAmount: 0, method: 'Credit' })

const outstanding = computed(() => {
  if (!payTarget.value) return 0
  return Number((payTarget.value.amount - payTarget.value.paidAmount).toFixed(2))
})

// Detail dialog
const detailDialog = ref(false)
const detailInvoice = ref(null)

onMounted(async () => {
  loadPlans()

  const [profileRes, autoRes, homeRes] = await Promise.all([
    portalApi.myProfile(),
    portalApi.myAutoPolicies(),
    portalApi.myHomePolicies(),
  ])
  profile.value = profileRes.data
  autoPolicies.value = autoRes.data || []
  homePolicies.value = homeRes.data || []

  loadingInvoices.value = true
  loadingPayments.value = true
  const [invRes, payRes] = await Promise.all([portalApi.myInvoices(), portalApi.myPayments()])
  invoices.value = invRes.data || []
  payments.value = payRes.data || []
  loadingInvoices.value = false
  loadingPayments.value = false
})

function openPayment(row) {
  payTarget.value = row
  payForm.value = { invoiceId: row.iId, payAmount: outstanding.value, method: 'Credit' }
  payDialog.value = true
}

function viewPayments(row) {
  detailInvoice.value = row
  detailDialog.value = true
}

async function submitPayment() {
  paying.value = true
  try {
    await portalApi.makePayment(payForm.value)
    ElMessage.success('Payment successful!')
    payDialog.value = false
    const [invRes, payRes] = await Promise.all([portalApi.myInvoices(), portalApi.myPayments()])
    invoices.value = invRes.data || []
    payments.value = payRes.data || []
  } catch (e) {
    ElMessage.error(e?.response?.data?.msg || 'Payment failed')
  } finally {
    paying.value = false
  }
}
</script>

<style scoped>
/* Welcome */
.welcome-banner {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; border: 1px solid var(--border);
  border-radius: 16px; padding: 28px 32px; margin-bottom: 28px;
  flex-wrap: wrap; gap: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.welcome-title { font-size: 24px; font-weight: 700; color: var(--text); margin: 0 0 4px; }
.welcome-sub { color: var(--text-secondary); font-size: 14px; margin: 0; }

.stat-cards { display: flex; gap: 16px; }
.stat-card {
  background: var(--bg); border: 1px solid var(--border);
  border-radius: 12px; padding: 16px 24px; text-align: center; min-width: 110px;
}
.stat-card.has-unpaid { border-color: var(--danger); background: #fff5f5; }
.stat-num { font-size: 28px; font-weight: 700; color: var(--text); }
.stat-label { font-size: 12px; color: var(--text-secondary); margin-top: 2px; }

/* Profile */
.profile-card {
  background: #fff; border: 1px solid var(--border);
  border-radius: 12px; padding: 28px; max-width: 680px;
  display: flex; flex-direction: column; gap: 24px;
}
.profile-section h3 {
  font-size: 12px; font-weight: 600; color: var(--text-secondary);
  margin: 0 0 16px; text-transform: uppercase; letter-spacing: 0.6px;
}
.profile-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.profile-item { display: flex; flex-direction: column; gap: 4px; }
.pi-label { font-size: 12px; color: var(--text-secondary); }
.pi-value { font-size: 15px; color: var(--text); font-weight: 500; }

/* Apply / Plans */
.apply-card {
  background: #fff; border: 1px solid var(--border);
  border-radius: 16px; padding: 40px;
  display: flex; flex-direction: column; align-items: center; text-align: center;
}
.apply-title { font-size: 22px; font-weight: 700; color: var(--text); margin: 0 0 8px; }
.apply-sub { color: var(--text-secondary); font-size: 14px; margin: 0 0 28px; max-width: 560px; line-height: 1.6; }

.plans-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px; width: 100%;
}
.plan-card {
  background: var(--bg); border: 2px solid var(--border);
  border-radius: 14px; padding: 24px 20px; cursor: pointer;
  transition: all 0.2s; text-align: center;
  display: flex; flex-direction: column; align-items: center; gap: 6px;
}
.plan-card:hover { border-color: var(--primary); background: #eff6ff; }
.plan-card.selected { border-color: var(--primary); background: #eff6ff; box-shadow: 0 0 0 3px rgba(37,99,235,0.15); }
.plan-icon { font-size: 28px; }
.plan-type { font-size: 11px; font-weight: 600; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.5px; }
.plan-name { font-size: 16px; font-weight: 700; color: var(--text); }
.plan-price { font-size: 26px; font-weight: 700; color: var(--primary); margin: 4px 0; }
.plan-price span { font-size: 13px; color: var(--text-secondary); font-weight: 400; }
.plan-features { list-style: none; padding: 0; margin: 8px 0 0; text-align: left; width: 100%; }
.plan-features li { font-size: 12px; color: var(--text-secondary); padding: 3px 0; }
.plan-features li::before { content: '✓ '; color: var(--success); font-weight: 600; }
.plan-check { font-size: 13px; font-weight: 600; color: var(--primary); margin-top: 8px; }

/* Policies */
.policy-section { margin-bottom: 28px; }
.section-label { font-size: 16px; font-weight: 600; color: var(--text); margin: 0 0 16px; }
.policy-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 16px; }
.policy-card {
  background: #fff; border: 1px solid var(--border);
  border-radius: 12px; padding: 20px;
}
.policy-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.policy-id { font-weight: 600; color: var(--text); font-size: 15px; }
.policy-details { display: flex; flex-direction: column; gap: 6px; font-size: 13px; color: var(--text-secondary); }
.pd-label { font-weight: 600; margin-right: 4px; }
.policy-amount { font-size: 22px; font-weight: 700; color: var(--primary); margin-top: 8px; }
.policy-amount span { font-size: 13px; color: var(--text-secondary); margin-left: 2px; }

/* Invoices */
.invoice-filter { margin-bottom: 16px; }
.paid-amount { color: #16a34a; font-weight: 600; }
.unpaid-amount { color: var(--danger); font-weight: 600; }

/* Payment dialog */
.pay-invoice-info {
  background: var(--bg); border-radius: 8px; border: 1px solid var(--border);
  padding: 16px 20px; display: flex; flex-direction: column; gap: 10px;
}
.pay-row { display: flex; justify-content: space-between; font-size: 14px; color: var(--text-secondary); }
.pay-row strong { color: var(--text); }
.pay-row.outstanding { border-top: 1px solid var(--border); padding-top: 10px; font-size: 15px; font-weight: 600; color: var(--text); }
.pay-row.outstanding strong { color: var(--danger); font-size: 18px; }

.empty-state { text-align: center; padding: 48px; color: var(--text-secondary); }
.tab-badge { margin-left: 6px; }
.portal-tabs { margin-top: 0; }
</style>
