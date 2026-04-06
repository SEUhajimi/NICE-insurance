import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // Public
    { path: '/', name: 'Landing', component: () => import('../views/LandingPage.vue'), meta: { layout: 'public', public: true } },
    { path: '/customer-login', name: 'CustomerAuth', component: () => import('../views/CustomerAuthView.vue'), meta: { layout: 'public', public: true } },

    // Employee login
    { path: '/login', name: 'Login', component: () => import('../views/LoginView.vue'), meta: { layout: 'public', public: true } },

    // Customer portal
    { path: '/portal', name: 'Portal', component: () => import('../views/CustomerPortalView.vue'), meta: { layout: 'portal', role: 'CUSTOMER' } },

    // Admin views
    { path: '/customers',       name: 'Customers',      component: () => import('../views/CustomerView.vue') },
    { path: '/auto-policies',   name: 'AutoPolicies',   component: () => import('../views/AutoPolicyView.vue') },
    { path: '/home-policies',   name: 'HomePolicies',   component: () => import('../views/HomePolicyView.vue') },
    { path: '/vehicles',        name: 'Vehicles',       component: () => import('../views/VehicleView.vue') },
    { path: '/drivers',         name: 'Drivers',        component: () => import('../views/DriverView.vue') },
    { path: '/homes',           name: 'Homes',          component: () => import('../views/HomeView.vue') },
    { path: '/invoices',        name: 'Invoices',       component: () => import('../views/InvoiceView.vue') },
    { path: '/payments',        name: 'Payments',       component: () => import('../views/PaymentView.vue') },
    { path: '/vehicle-drivers', name: 'VehicleDrivers', component: () => import('../views/VehicleDriverView.vue') },
    { path: '/employees',       name: 'Employees',      component: () => import('../views/EmployeeView.vue') },
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.meta.public) return true

  if (!token) return '/login'

  // Customer trying to access admin routes → redirect to portal
  if (role === 'CUSTOMER' && to.meta.layout !== 'portal') return '/portal'

  // Employee trying to access portal → redirect to customers
  if (role === 'EMPLOYEE' && to.meta.role === 'CUSTOMER') return '/customers'
})

export default router
