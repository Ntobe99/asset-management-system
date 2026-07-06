import api from './axios'
import type { AuthUser } from '@/types'

export interface LoginPayload {
  username: string
  password: string
}

export interface RegisterPayload {
  username: string
  email: string
  password: string
  fullName?: string
}

export const authApi = {
  login: (payload: LoginPayload) => api.post<AuthUser>('/auth/login', payload).then((r) => r.data),
  register: (payload: RegisterPayload) => api.post<AuthUser>('/auth/register', payload).then((r) => r.data),
}
