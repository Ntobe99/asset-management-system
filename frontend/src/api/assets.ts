import api from './axios'
import type { Asset, AssetStatus, Page } from '@/types'

export interface AssetSearchParams {
  search?: string
  categoryId?: number
  status?: AssetStatus
  employeeId?: number
  page?: number
  size?: number
  sort?: string
}

export const assetsApi = {
  search: (params: AssetSearchParams) => api.get<Page<Asset>>('/assets', { params }).then((r) => r.data),
  getById: (id: number) => api.get<Asset>(`/assets/${id}`).then((r) => r.data),
  create: (payload: Partial<Asset>) => api.post<Asset>('/assets', payload).then((r) => r.data),
  update: (id: number, payload: Partial<Asset>) => api.put<Asset>(`/assets/${id}`, payload).then((r) => r.data),
  delete: (id: number) => api.delete(`/assets/${id}`),
  assign: (id: number, employeeId: number, notes?: string) =>
    api.post<Asset>(`/assets/${id}/assign`, { employeeId, notes }).then((r) => r.data),
  unassign: (id: number) => api.post<Asset>(`/assets/${id}/unassign`).then((r) => r.data),
  updateStatus: (id: number, status: AssetStatus) =>
    api.patch<Asset>(`/assets/${id}/status`, null, { params: { status } }).then((r) => r.data),
}
