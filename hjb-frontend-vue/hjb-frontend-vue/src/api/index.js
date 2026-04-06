import request from './request'

// ========== Customer ==========
export const customerApi = {
  findAll: () => request.get('/customers'),
  findById: (id) => request.get(`/customers/${id}`),
  add: (data) => request.post('/customers', data),
  update: (data) => request.put('/customers', data),
  delete: (id) => request.delete(`/customers/${id}`)
}

// ========== Auto Policy ==========
export const autoPolicyApi = {
  findAll: () => request.get('/auto-policies'),
  findById: (id) => request.get(`/auto-policies/${id}`),
  findByCustomerId: (custId) => request.get(`/auto-policies/customer/${custId}`),
  add: (data) => request.post('/auto-policies', data),
  update: (data) => request.put('/auto-policies', data),
  delete: (id) => request.delete(`/auto-policies/${id}`)
}

// ========== Home Policy ==========
export const homePolicyApi = {
  findAll: () => request.get('/home-policies'),
  findById: (id) => request.get(`/home-policies/${id}`),
  findByCustomerId: (custId) => request.get(`/home-policies/customer/${custId}`),
  add: (data) => request.post('/home-policies', data),
  update: (data) => request.put('/home-policies', data),
  delete: (id) => request.delete(`/home-policies/${id}`)
}

// ========== Vehicle ==========
export const vehicleApi = {
  findAll: () => request.get('/vehicles'),
  findById: (vin) => request.get(`/vehicles/${vin}`),
  findByAutoPolicyId: (apId) => request.get(`/vehicles/policy/${apId}`),
  add: (data) => request.post('/vehicles', data),
  update: (data) => request.put('/vehicles', data),
  delete: (vin) => request.delete(`/vehicles/${vin}`)
}

// ========== Driver ==========
export const driverApi = {
  findAll: () => request.get('/drivers'),
  findById: (license) => request.get(`/drivers/${license}`),
  add: (data) => request.post('/drivers', data),
  update: (data) => request.put('/drivers', data),
  delete: (license) => request.delete(`/drivers/${license}`)
}

// ========== Home ==========
export const homeApi = {
  findAll: () => request.get('/homes'),
  findById: (id) => request.get(`/homes/${id}`),
  findByHomePolicyId: (hpId) => request.get(`/homes/policy/${hpId}`),
  add: (data) => request.post('/homes', data),
  update: (data) => request.put('/homes', data),
  delete: (id) => request.delete(`/homes/${id}`)
}

// ========== Invoice ==========
export const invoiceApi = {
  findAll: () => request.get('/invoices'),
  findById: (id) => request.get(`/invoices/${id}`),
  add: (data) => request.post('/invoices', data),
  update: (data) => request.put('/invoices', data),
  delete: (id) => request.delete(`/invoices/${id}`)
}

// ========== Payment ==========
export const paymentApi = {
  findAll: () => request.get('/payments'),
  findById: (id) => request.get(`/payments/${id}`),
  add: (data) => request.post('/payments', data),
  update: (data) => request.put('/payments', data),
  delete: (id) => request.delete(`/payments/${id}`)
}

// ========== Employee ==========
export const employeeApi = {
  findAll: () => request.get('/admin/employees'),
  add: (data) => request.post('/admin/employees', data),
  delete: (id) => request.delete(`/admin/employees/${id}`)
}

// ========== Vehicle-Driver ==========
export const vehicleDriverApi = {
  findAll: () => request.get('/vehicle-drivers'),
  findByVin: (vin) => request.get(`/vehicle-drivers/vehicle/${vin}`),
  findByDriverLicense: (license) => request.get(`/vehicle-drivers/driver/${license}`),
  add: (data) => request.post('/vehicle-drivers', data),
  delete: (vin, license) => request.delete(`/vehicle-drivers/${vin}/${license}`)
}
