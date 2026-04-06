<template>
  <div v-if="isLoginPage" class="login-layout">
    <router-view />
  </div>
  <div v-else class="app-layout">
    <!-- Sidebar -->
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
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isLoginPage = computed(() => route.name === 'Login')

const menuItems = [
  { path: '/customers', label: 'Customers', icon: '👥' },
  { path: '/auto-policies', label: 'Auto Policies', icon: '🚗' },
  { path: '/home-policies', label: 'Home Policies', icon: '🏠' },
  { path: '/vehicles', label: 'Vehicles', icon: '🚙' },
  { path: '/drivers', label: 'Drivers', icon: '🪪' },
  { path: '/homes', label: 'Homes', icon: '🏡' },
  { path: '/invoices', label: 'Invoices', icon: '📄' },
  { path: '/payments', label: 'Payments', icon: '💳' },
  { path: '/vehicle-drivers', label: 'Vehicle-Drivers', icon: '🔗' },
  { path: '/employees', label: 'Employees', icon: '👔' }
]
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background: var(--sidebar-bg);
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
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
  width: 40px;
  height: 40px;
  background: var(--primary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 14px;
}

.logo-text {
  color: white;
  font-size: 20px;
  font-weight: 700;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  color: var(--sidebar-text);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: var(--sidebar-active);
}

.nav-item--active {
  background: rgba(37, 99, 235, 0.2) !important;
  color: var(--primary-light) !important;
}

.nav-icon {
  font-size: 18px;
}

.main-content {
  flex: 1;
  margin-left: 240px;
  padding: 32px;
}
</style>
