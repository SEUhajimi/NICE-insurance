import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      redirect: '/customers'
    },
    {
      path: '/customers',
      name: 'Customers',
      component: () => import('../views/CustomerView.vue')
    },
    {
      path: '/auto-policies',
      name: 'AutoPolicies',
      component: () => import('../views/AutoPolicyView.vue')
    },
    {
      path: '/home-policies',
      name: 'HomePolicies',
      component: () => import('../views/HomePolicyView.vue')
    },
    {
      path: '/vehicles',
      name: 'Vehicles',
      component: () => import('../views/VehicleView.vue')
    },
    {
      path: '/drivers',
      name: 'Drivers',
      component: () => import('../views/DriverView.vue')
    },
    {
      path: '/homes',
      name: 'Homes',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/invoices',
      name: 'Invoices',
      component: () => import('../views/InvoiceView.vue')
    },
    {
      path: '/payments',
      name: 'Payments',
      component: () => import('../views/PaymentView.vue')
    },
    {
      path: '/vehicle-drivers',
      name: 'VehicleDrivers',
      component: () => import('../views/VehicleDriverView.vue')
    },
    {
      path: '/employees',
      name: 'Employees',
      component: () => import('../views/EmployeeView.vue')
    }
  ]
})

// Navigation guard: redirect to login if no token
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (!to.meta.public && !token) {
    return '/login'
  }
})

export default router
