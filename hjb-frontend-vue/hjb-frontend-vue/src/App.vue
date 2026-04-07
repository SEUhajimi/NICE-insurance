<template>
  <!-- Public layout: landing, customer auth -->
  <router-view v-if="layout === 'public'" />

  <!-- Customer portal layout -->
  <div v-else-if="layout === 'portal'" class="portal-layout">
    <header class="portal-header">
      <div class="portal-logo">
        <div class="logo-icon">HJB</div>
        <span class="logo-text">Insurance</span>
      </div>
      <div class="portal-nav-right">
        <span class="portal-username">{{ username }}</span>
        <el-button size="small" @click="logout">Logout</el-button>
      </div>
    </header>
    <main class="portal-main">
      <router-view />
    </main>
  </div>

  <!-- Admin layout: sidebar + content -->
  <div v-else class="app-layout">
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-icon">HJB</div>
        <span class="logo-text">Insurance</span>
      </div>
      <nav class="nav-menu">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          active-class="nav-item--active"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <div class="sidebar-user">
          <span class="nav-icon">👤</span>
          <span class="nav-label">{{ username }}</span>
        </div>
        <button class="logout-btn" @click="logout">Logout</button>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const layout = computed(() => route.meta.layout || 'admin')
const username = ref(localStorage.getItem('username') || '')
watch(route, () => {
  username.value = localStorage.getItem('username') || ''
})

const menuItems = [
  { path: '/customers',      label: 'Customers',       icon: '👥' },
  { path: '/auto-policies',  label: 'Auto Policies',   icon: '🚗' },
  { path: '/home-policies',  label: 'Home Policies',   icon: '🏠' },
  { path: '/vehicles',       label: 'Vehicles',        icon: '🚙' },
  { path: '/drivers',        label: 'Drivers',         icon: '🪪' },
  { path: '/homes',          label: 'Homes',           icon: '🏡' },
  { path: '/invoices',       label: 'Invoices',        icon: '📄' },
  { path: '/payments',       label: 'Payments',        icon: '💳' },
  { path: '/vehicle-drivers',label: 'Vehicle-Drivers', icon: '🔗' },
  { path: '/employees',      label: 'Employees',       icon: '👔' },
]

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  router.push('/')
}
</script>

<style scoped>
/* ── Admin layout ── */
.app-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background: var(--sidebar-bg);
  padding: 24px 16px 16px;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0; left: 0; bottom: 0;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 12px;
  margin-bottom: 32px;
}

.logo-icon {
  width: 40px; height: 40px;
  background: var(--primary);
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: white; font-weight: 700; font-size: 14px;
}

.logo-text { color: white; font-size: 20px; font-weight: 700; }

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  overflow-y: auto;
}

.nav-item {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  color: var(--sidebar-text);
  text-decoration: none;
  font-size: 14px; font-weight: 500;
  transition: all 0.2s;
}

.nav-item:hover { background: rgba(255,255,255,0.08); color: var(--sidebar-active); }
.nav-item--active { background: rgba(37,99,235,0.2) !important; color: var(--primary-light) !important; }
.nav-icon { font-size: 18px; }

.sidebar-footer {
  border-top: 1px solid rgba(255,255,255,0.1);
  padding-top: 12px;
  margin-top: 8px;
}

.sidebar-user {
  display: flex; align-items: center; gap: 12px;
  padding: 8px 12px;
  color: var(--sidebar-text);
  font-size: 13px;
  margin-bottom: 6px;
}

.logout-btn {
  width: 100%;
  padding: 8px;
  background: rgba(239,68,68,0.15);
  color: #f87171;
  border: 1px solid rgba(239,68,68,0.3);
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: background 0.2s;
}
.logout-btn:hover { background: rgba(239,68,68,0.25); }

.main-content { flex: 1; margin-left: 240px; padding: 32px; }

/* ── Portal layout ── */
.portal-layout { min-height: 100vh; background: var(--bg); }

.portal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 40px;
  height: 64px;
  background: var(--sidebar-bg);
  border-bottom: 1px solid rgba(255,255,255,0.08);
  position: sticky; top: 0; z-index: 100;
}

.portal-logo { display: flex; align-items: center; gap: 12px; }
.portal-nav-right { display: flex; align-items: center; gap: 16px; }

.portal-username { color: var(--sidebar-text); font-size: 14px; }

.portal-main { padding: 40px; max-width: 1100px; margin: 0 auto; }
</style>
