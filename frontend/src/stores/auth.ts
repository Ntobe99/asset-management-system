import { defineStore } from 'pinia'
import type { AuthUser } from '@/types'

const STORAGE_KEY = 'asset-mgmt-auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as AuthUser | null,
  }),
  getters: {
    token: (state) => state.user?.token ?? null,
    isAuthenticated: (state) => !!state.user?.token,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    canEdit: (state) => state.user?.role === 'ADMIN' || state.user?.role === 'MANAGER',
  },
  actions: {
    setUser(user: AuthUser) {
      this.user = user
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(user))
    },
    restore() {
      const raw = sessionStorage.getItem(STORAGE_KEY)
      if (raw) {
        try {
          this.user = JSON.parse(raw)
        } catch {
          sessionStorage.removeItem(STORAGE_KEY)
        }
      }
    },
    logout() {
      this.user = null
      sessionStorage.removeItem(STORAGE_KEY)
    },
  },
})
