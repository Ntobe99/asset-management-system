import api from './axios'
import type { Employee } from '@/types'

export const employeesApi = {
  getAll: (search?: string) =>
    api.get<Employee[]>('/employees', { params: { search } }).then((r) => r.data),
  getById: (id: number) => api.get<Employee>(`/employees/${id}`).then((r) => r.data),
  create: (payload: Partial<Employee>) => api.post<Employee>('/employees', payload).then((r) => r.data),
  update: (id: number, payload: Partial<Employee>) =>
    api.put<Employee>(`/employees/${id}`, payload).then((r) => r.data),
  delete: (id: number) => api.delete(`/employees/${id}`),
}
