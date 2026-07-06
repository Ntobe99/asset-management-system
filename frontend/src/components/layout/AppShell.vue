<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const navItems = [
  { to: '/dashboard', label: 'Dashboard', icon: 'grid' },
  { to: '/assets', label: 'Assets', icon: 'box' },
  { to: '/maintenance', label: 'Maintenance', icon: 'wrench' },
  { to: '/employees', label: 'Employees', icon: 'user' },
  { to: '/categories', label: 'Categories', icon: 'tag' },
]

function handleLogout() {
  auth.logout()
  router.push('/login')
}

function initials(name?: string) {
  if (!name) return '?'
  return name
    .split(' ')
    .map((p) => p[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()
}
</script>

<template>
  <div class="shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">AL</span>
        <span class="brand-name">AssetLedger</span>
      </div>

      <nav class="nav">
        <router-link
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="nav-item"
          active-class="nav-item-active"
        >
          <svg
            v-if="item.icon === 'grid'"
            width="17"
            height="17"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <rect x="3" y="3" width="7" height="7" rx="1.5" />
            <rect x="14" y="3" width="7" height="7" rx="1.5" />
            <rect x="3" y="14" width="7" height="7" rx="1.5" />
            <rect x="14" y="14" width="7" height="7" rx="1.5" />
          </svg>
          <svg
            v-else-if="item.icon === 'box'"
            width="17"
            height="17"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M21 8l-9-5-9 5 9 5 9-5z" />
            <path d="M3 8v8l9 5 9-5V8" />
            <path d="M12 13v8" />
          </svg>
          <svg
            v-else-if="item.icon === 'wrench'"
            width="17"
            height="17"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              d="M14.7 6.3a4 4 0 10-5.4 5.4L3 18l3 3 6.3-6.3a4 4 0 005.4-5.4l-2.83 2.83-2.12-2.12L14.7 6.3z"
            />
          </svg>
          <svg
            v-else-if="item.icon === 'user'"
            width="17"
            height="17"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <circle cx="12" cy="8" r="4" />
            <path d="M4 21c0-4.4 3.6-7 8-7s8 2.6 8 7" />
          </svg>
          <svg v-else width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20.6 12.4L12.4 20.6a2 2 0 01-2.8 0l-6.2-6.2a2 2 0 010-2.8L11.6 3.4a2 2 0 011.4-.6H19a2 2 0 012 2v6.6a2 2 0 01-.4 1.4z" />
            <circle cx="16" cy="8" r="1.2" fill="currentColor" stroke="none" />
          </svg>
          {{ item.label }}
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="user-chip">
          <span class="avatar">{{ initials(auth.user?.fullName || auth.user?.username) }}</span>
          <div class="user-meta">
            <span class="user-name">{{ auth.user?.fullName || auth.user?.username }}</span>
            <span class="user-role">{{ auth.user?.role }}</span>
          </div>
        </div>
        <button class="logout-btn" title="Log out" @click="handleLogout">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4" />
            <path d="M16 17l5-5-5-5" />
            <path d="M21 12H9" />
          </svg>
        </button>
      </div>
    </aside>

    <main class="content">
      <slot />
    </main>
  </div>
</template>

<style scoped>
.shell {
  display: grid;
  grid-template-columns: 236px 1fr;
  min-height: 100vh;
}

.sidebar {
  background: var(--color-sidebar);
  color: var(--color-sidebar-text);
  display: flex;
  flex-direction: column;
  padding: 20px 14px;
  position: sticky;
  top: 0;
  height: 100vh;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 8px 22px;
}
.brand-mark {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: var(--color-accent);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-mono);
  font-weight: 700;
  font-size: 12.5px;
}
.brand-name {
  font-weight: 700;
  font-size: 15px;
  color: #fff;
  letter-spacing: -0.01em;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 9px 12px;
  border-radius: var(--radius-sm);
  font-size: 13.5px;
  font-weight: 500;
  color: var(--color-sidebar-text);
  transition: background 0.12s ease, color 0.12s ease;
}
.nav-item:hover {
  background: var(--color-sidebar-alt);
  color: #fff;
}
.nav-item-active {
  background: var(--color-accent);
  color: #fff;
}

.sidebar-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding-top: 14px;
  margin-top: 10px;
}
.user-chip {
  display: flex;
  align-items: center;
  gap: 9px;
  flex: 1;
  min-width: 0;
}
.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: var(--color-sidebar-alt);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11.5px;
  font-weight: 700;
  flex-shrink: 0;
}
.user-meta {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.user-name {
  font-size: 12.5px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.user-role {
  font-size: 10.5px;
  color: var(--color-sidebar-text);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.logout-btn {
  background: transparent;
  border: none;
  color: var(--color-sidebar-text);
  cursor: pointer;
  padding: 7px;
  border-radius: var(--radius-sm);
  display: flex;
}
.logout-btn:hover {
  background: var(--color-sidebar-alt);
  color: #fff;
}

.content {
  padding: 28px 32px 60px;
  min-width: 0;
}

@media (max-width: 860px) {
  .shell {
    grid-template-columns: 1fr;
  }
  .sidebar {
    display: none;
  }
  .content {
    padding: 20px;
  }
}
</style>
