import { defineStore } from 'pinia'

export interface Toast {
  id: number
  message: string
  type: 'info' | 'success' | 'error'
}

let nextId = 1

export const useUiStore = defineStore('ui', {
  state: () => ({
    toasts: [] as Toast[],
  }),
  actions: {
    notify(message: string, type: Toast['type'] = 'info') {
      const id = nextId++
      this.toasts.push({ id, message, type })
      setTimeout(() => this.dismiss(id), 4000)
    },
    success(message: string) {
      this.notify(message, 'success')
    },
    error(message: string) {
      this.notify(message, 'error')
    },
    dismiss(id: number) {
      this.toasts = this.toasts.filter((t) => t.id !== id)
    },
  },
})
