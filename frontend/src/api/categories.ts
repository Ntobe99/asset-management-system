import api from './axios'
import type { Category } from '@/types'

export const categoriesApi = {
  getAll: () => api.get<Category[]>('/categories').then((r) => r.data),
  getById: (id: number) => api.get<Category>(`/categories/${id}`).then((r) => r.data),
  create: (payload: Partial<Category>) => api.post<Category>('/categories', payload).then((r) => r.data),
  update: (id: number, payload: Partial<Category>) =>
    api.put<Category>(`/categories/${id}`, payload).then((r) => r.data),
  delete: (id: number) => api.delete(`/categories/${id}`),
}
