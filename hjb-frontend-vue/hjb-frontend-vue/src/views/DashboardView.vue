<template>
  <div class="dashboard">
    <div class="page-header">
      <h1 class="page-title">Dashboard</h1>
      <span class="update-time">Data as of {{ today }}</span>
    </div>

    <!-- Summary Cards -->
    <div class="summary-cards">
      <div class="summary-card">
        <div class="summary-icon">📋</div>
        <div class="summary-body">
          <div class="summary-num">{{ totalPolicies }}</div>
          <div class="summary-label">Total Policies</div>
        </div>
      </div>
      <div class="summary-card">
        <div class="summary-icon">✅</div>
        <div class="summary-body">
          <div class="summary-num">{{ activePolicies }}</div>
          <div class="summary-label">Active Policies</div>
        </div>
      </div>
      <div class="summary-card">
        <div class="summary-icon">👥</div>
        <div class="summary-body">
          <div class="summary-num">{{ totalCustomers }}</div>
          <div class="summary-label">Insured Customers</div>
        </div>
      </div>
      <div class="summary-card">
        <div class="summary-icon">💰</div>
        <div class="summary-body">
          <div class="summary-num">${{ totalRevenue.toLocaleString() }}</div>
          <div class="summary-label">Total Revenue (6mo)</div>
        </div>
      </div>
    </div>

    <!-- Charts Row 1 -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="chart-title">Policy Type Distribution</div>
        <div ref="chartPolicyType" class="chart" />
      </div>
      <div class="chart-card">
        <div class="chart-title">Policy Status Overview</div>
        <div ref="chartPolicyStatus" class="chart" />
      </div>
    </div>

    <!-- Charts Row 2 -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="chart-title">Payment Method Distribution</div>
        <div ref="chartPaymentMethod" class="chart" />
      </div>
      <div class="chart-card chart-card--wide">
        <div class="chart-title">Monthly Revenue (Last 6 Months)</div>
        <div ref="chartRevenue" class="chart" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import * as echarts from 'echarts'
import { statsApi } from '../api'

const today = new Date().toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })

const chartPolicyType   = ref(null)
const chartPolicyStatus = ref(null)
const chartPaymentMethod = ref(null)
const chartRevenue      = ref(null)

const stats = ref(null)
const instances = []

const totalPolicies  = computed(() => {
  if (!stats.value?.policyByType) return 0
  return stats.value.policyByType.reduce((s, r) => s + Number(r.value), 0)
})
const activePolicies = computed(() => {
  if (!stats.value?.policyByStatus) return 0
  const active = stats.value.policyByStatus.find(r => r.label === 'C')
  return active ? Number(active.value) : 0
})
const totalCustomers = computed(() => {
  if (!stats.value?.customerByType) return 0
  return stats.value.customerByType.reduce((s, r) => s + Number(r.value), 0)
})
const totalRevenue = computed(() => {
  if (!stats.value?.monthlyRevenue) return 0
  return stats.value.monthlyRevenue.reduce((s, r) => s + Number(r.revenue), 0)
})

const COLORS = ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4']

function initChart(el, option) {
  const chart = echarts.init(el, null, { renderer: 'canvas' })
  chart.setOption(option)
  instances.push(chart)
  return chart
}

function buildPolicyTypeChart(data) {
  const seriesData = data.map(r => ({
    name: r.label === 'A' ? 'Auto' : 'Home',
    value: Number(r.value)
  }))
  initChart(chartPolicyType.value, {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, textStyle: { color: '#94a3b8' } },
    series: [{
      type: 'pie', radius: ['45%', '70%'], center: ['50%', '45%'],
      data: seriesData,
      itemStyle: { borderRadius: 6, borderWidth: 2, borderColor: '#1e293b' },
      label: { show: true, formatter: '{b}\n{c}', color: '#e2e8f0' },
      color: COLORS
    }]
  })
}

function buildPolicyStatusChart(data) {
  const seriesData = data.map(r => ({
    name: r.label === 'C' ? 'Active' : 'Expired',
    value: Number(r.value)
  }))
  initChart(chartPolicyStatus.value, {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, textStyle: { color: '#94a3b8' } },
    series: [{
      type: 'pie', radius: ['45%', '70%'], center: ['50%', '45%'],
      data: seriesData,
      itemStyle: { borderRadius: 6, borderWidth: 2, borderColor: '#1e293b' },
      label: { show: true, formatter: '{b}\n{c}', color: '#e2e8f0' },
      color: ['#10b981', '#64748b']
    }]
  })
}

function buildPaymentMethodChart(data) {
  const seriesData = data.map((r, i) => ({
    name: r.label,
    value: Number(r.value),
    itemStyle: { color: COLORS[i % COLORS.length] }
  }))
  initChart(chartPaymentMethod.value, {
    tooltip: { trigger: 'item', formatter: '{b}: {c} payments ({d}%)' },
    legend: { bottom: 0, textStyle: { color: '#94a3b8' } },
    series: [{
      type: 'pie', radius: '65%', center: ['50%', '45%'],
      data: seriesData,
      label: { show: true, formatter: '{b}\n{c}', color: '#e2e8f0' }
    }]
  })
}

function buildRevenueChart(data) {
  const months  = data.map(r => r.month)
  const amounts = data.map(r => Number(r.revenue))
  initChart(chartRevenue.value, {
    tooltip: { trigger: 'axis', formatter: params => `${params[0].axisValue}<br/>Revenue: $${params[0].value.toLocaleString()}` },
    grid: { left: 60, right: 20, top: 20, bottom: 40 },
    xAxis: {
      type: 'category', data: months,
      axisLabel: { color: '#94a3b8' }, axisLine: { lineStyle: { color: '#334155' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#94a3b8', formatter: v => `$${v.toLocaleString()}` },
      splitLine: { lineStyle: { color: '#1e293b' } }
    },
    series: [{
      type: 'bar', data: amounts, barMaxWidth: 48,
      itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: '#3b82f6' },
        { offset: 1, color: '#1d4ed8' }
      ]), borderRadius: [4, 4, 0, 0] }
    }]
  })
}

onMounted(async () => {
  try {
    const res = await statsApi.get()
    stats.value = res.data

    buildPolicyTypeChart(res.data.policyByType || [])
    buildPolicyStatusChart(res.data.policyByStatus || [])
    buildPaymentMethodChart(res.data.paymentByMethod || [])
    buildRevenueChart(res.data.monthlyRevenue || [])
  } catch {
    // charts stay empty on error
  }

  window.addEventListener('resize', onResize)
})

function onResize() { instances.forEach(c => c.resize()) }

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  instances.forEach(c => c.dispose())
})
</script>

<style scoped>
.dashboard { max-width: 1200px; }

.page-header {
  display: flex; align-items: baseline; justify-content: space-between;
  margin-bottom: 24px;
}
.page-title { font-size: 28px; font-weight: 700; color: var(--text); }
.update-time { font-size: 13px; color: var(--text-secondary); }

/* Summary Cards */
.summary-cards {
  display: grid; grid-template-columns: repeat(4, 1fr);
  gap: 16px; margin-bottom: 24px;
}
.summary-card {
  background: var(--card-bg, #fff);
  border: 1px solid var(--border);
  border-radius: 14px; padding: 20px 24px;
  display: flex; align-items: center; gap: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.summary-icon { font-size: 28px; }
.summary-num { font-size: 26px; font-weight: 700; color: var(--text); line-height: 1.2; }
.summary-label { font-size: 12px; color: var(--text-secondary); margin-top: 2px; }

/* Charts */
.charts-row {
  display: grid; grid-template-columns: 1fr 1fr;
  gap: 16px; margin-bottom: 16px;
}
.chart-card {
  background: var(--card-bg, #fff);
  border: 1px solid var(--border);
  border-radius: 14px; padding: 20px 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.chart-card--wide { grid-column: span 1; }
.chart-title {
  font-size: 14px; font-weight: 600;
  color: var(--text-secondary); margin-bottom: 16px;
  text-transform: uppercase; letter-spacing: 0.5px;
}
.chart { height: 280px; width: 100%; }
</style>
