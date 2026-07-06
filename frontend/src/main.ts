import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './style.css'
import { useAuthStore } from '@/stores/auth'

const app = createApp(App)

app.use(createPinia())

// Restore any previously saved session before the router guard runs
useAuthStore().restore()

app.use(router)

app.mount('#app')
