import request from './request'

// Auth
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)

// Users
export const getUsers = () => request.get('/users')
export const createUser = (data) => request.post('/users', data)
export const updateUser = (id, data) => request.put(`/users/${id}`, data)
export const deleteUser = (id) => request.delete(`/users/${id}`)

// Buildings & Rooms
export const getBuildings = () => request.get('/buildings')
export const createBuilding = (data) => request.post('/buildings', data)
export const updateBuilding = (id, data) => request.put(`/buildings/${id}`, data)
export const deleteBuilding = (id) => request.delete(`/buildings/${id}`)

export const getRooms = () => request.get('/rooms')
export const createRoom = (data) => request.post('/rooms', data)
export const updateRoom = (id, data) => request.put(`/rooms/${id}`, data)
export const deleteRoom = (id) => request.delete(`/rooms/${id}`)

// Applications & Allocations
export const getApplications = () => request.get('/applications')
export const getMyApplications = (studentId) => request.get(`/applications/student/${studentId}`)
export const createApplication = (data) => request.post('/applications', data)
export const updateApplicationStatus = (id, status) => request.put(`/applications/${id}/status`, null, { params: { status } })
export const deleteApplication = (id) => request.delete(`/applications/${id}`)

export const getAllocations = () => request.get('/allocations')
export const getUnallocatedStudents = () => request.get('/allocations/unallocated-students')
export const getAvailableRooms = (buildingId) => request.get(`/allocations/available-rooms/${buildingId}`)
export const createAllocation = (data) => request.post('/allocations', data)
export const updateAllocation = (id, data) => request.put(`/allocations/${id}`, data)
export const deleteAllocation = (id) => request.delete(`/allocations/${id}`)

// Fees
export const getFees = () => request.get('/fees')
export const createFee = (data) => request.post('/fees', data)
export const updateFee = (id, data) => request.put(`/fees/${id}`, data)
export const deleteFee = (id) => request.delete(`/fees/${id}`)

export const getFeeStandards = () => request.get('/fee-standards')
export const createFeeStandard = (data) => request.post('/fee-standards', data)
export const updateFeeStandard = (id, data) => request.put(`/fee-standards/${id}`, data)
export const deleteFeeStandard = (id) => request.delete(`/fee-standards/${id}`)

// Maintenance
export const getMaintenances = () => request.get('/maintenances')
export const getMyMaintenances = (studentId) => request.get(`/maintenances/student/${studentId}`)
export const createMaintenance = (data) => request.post('/maintenances', data)
export const updateMaintenance = (id, data) => request.put(`/maintenances/${id}`, data)
export const deleteMaintenance = (id) => request.delete(`/maintenances/${id}`)

// Safety
export const getSafetyRecords = () => request.get('/safety')
export const createSafetyRecord = (data) => request.post('/safety', data)
export const updateSafetyRecord = (id, data) => request.put(`/safety/${id}`, data)
export const deleteSafetyRecord = (id) => request.delete(`/safety/${id}`)

export const getAccessRecords = () => request.get('/access')
export const createAccessRecord = (data) => request.post('/access', data)
export const updateAccessRecord = (id, data) => request.put(`/access/${id}`, data)
export const deleteAccessRecord = (id) => request.delete(`/access/${id}`)

// Attendance
export const getAttendances = () => request.get('/attendance')
export const createAttendance = (data) => request.post('/attendance', data)
export const updateAttendance = (id, data) => request.put(`/attendance/${id}`, data)
export const deleteAttendance = (id) => request.delete(`/attendance/${id}`)

// Hygiene
export const getHygieneRecords = () => request.get('/hygiene')
export const createHygieneRecord = (data) => request.post('/hygiene', data)
export const updateHygieneRecord = (id, data) => request.put(`/hygiene/${id}`, data)
export const deleteHygieneRecord = (id) => request.delete(`/hygiene/${id}`)

// Notices
export const getNotices = () => request.get('/notices')
export const createNotice = (data) => request.post('/notices', data)
export const updateNotice = (id, data) => request.put(`/notices/${id}`, data)
export const deleteNotice = (id) => request.delete(`/notices/${id}`)

// System
export const getSystemParams = () => request.get('/system/params')
export const updateSystemParam = (id, data) => request.put(`/system/params/${id}`, data)
export const getOperationLogs = () => request.get('/system/logs')
