<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import { useUiStore } from '@/stores/ui'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const ui = useUiStore()

const mode = ref<'login' | 'register'>('login')
const loading = ref(false)
const error = ref('')

const form = ref({
  username: '',
  email: '',
  password: '',
  fullName: '',
})

async function submit() {
  error.value = ''
  loading.value = true
  try {
    const user =
      mode.value === 'login'
        ? await authApi.login({ username: form.value.username, password: form.value.password })
        : await authApi.register(form.value)

    auth.setUser(user)
    ui.success(`Welcome, ${user.fullName || user.username}`)
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Something went wrong. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-panel card">
      <div class="login-brand">
        <span class="brand-mark">AL</span>
        <h1>AssetLedger</h1>
      </div>
      <p class="tagline">Track equipment, assignments, and maintenance in one ledger.</p>

      <form @submit.prevent="submit">
        <div v-if="mode === 'register'" class="field">
          <label>Full name</label>
          <input v-model="form.fullName" type="text" placeholder="Jordan Reyes" />
        </div>
        <div class="field">
          <label>Username</label>
          <input v-model="form.username" type="text" required placeholder="jordan" autocomplete="username" />
        </div>
        <div v-if="mode === 'register'" class="field">
          <label>Email</label>
          <input v-model="form.email" type="email" required placeholder="jordan@company.com" />
        </div>
        <div class="field">
          <label>Password</label>
          <input
            v-model="form.password"
            type="password"
            required
            placeholder="••••••••"
            autocomplete="current-password"
          />
        </div>

        <p v-if="error" class="field-error" style="margin-bottom: 14px">{{ error }}</p>

        <button class="btn btn-primary" style="width: 100%" type="submit" :disabled="loading">
          {{ loading ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account' }}
        </button>
      </form>

      <p class="switch-mode">
        <template v-if="mode === 'login'">
          New here?
          <a href="#" @click.prevent="mode = 'register'">Create an account</a>
        </template>
        <template v-else>
          Already have an account?
          <a href="#" @click.prevent="mode = 'login'">Sign in</a>
        </template>
      </p>

      <p class="demo-hint">Demo login: <strong>admin</strong> / <strong>admin123</strong></p>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-sidebar);
  background-image: radial-gradient(circle at 20% 20%, #1a2133 0%, var(--color-sidebar) 60%);
  padding: 20px;
}
.login-panel {
  width: 100%;
  max-width: 380px;
  padding: 32px;
}
.login-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.brand-mark {
  width: 34px;
  height: 34px;
  border-radius: 9px;
  background: var(--color-accent);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-mono);
  font-weight: 700;
  font-size: 13px;
}
.login-brand h1 {
  font-size: 19px;
}
.tagline {
  color: var(--color-text-muted);
  font-size: 13.5px;
  margin-bottom: 26px;
}
.switch-mode {
  text-align: center;
  font-size: 13px;
  color: var(--color-text-muted);
  margin-top: 18px;
}
.switch-mode a {
  color: var(--color-accent);
  font-weight: 600;
}
.demo-hint {
  text-align: center;
  font-size: 11.5px;
  color: var(--color-text-faint);
  margin-top: 14px;
  font-family: var(--font-mono);
}
</style>
