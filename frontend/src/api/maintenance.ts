import api from './axios'
import type { MaintenanceRecord, Page } from '@/types'

export const maintenanceApi = {
  getAll: (assetId?: number, page = 0, size = 20) =>
    api
      .get<Page<MaintenanceRecord>>('/maintenance', { params: { assetId, page, size } })
      .then((r) => r.data),
  getById: (id: number) => api.get<MaintenanceRecord>(`/maintenance/${id}`).then((r) => r.data),
  create: (payload: Partial<MaintenanceRecord>) =>
    api.post<MaintenanceRecord>('/maintenance', payload).then((r) => r.data),
  update: (id: number, payload: Partial<MaintenanceRecord>) =>
    api.put<MaintenanceRecord>(`/maintenance/${id}`, payload).then((r) => r.data),
  delete: (id: number) => api.delete(`/maintenance/${id}`),
}
