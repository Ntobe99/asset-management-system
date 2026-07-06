<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { dashboardApi } from '@/api/dashboard'
import type { DashboardStats } from '@/types'
import StatusBadge from '@/components/common/StatusBadge.vue'

const stats = ref<DashboardStats | null>(null)
const loading = ref(true)

function currency(v: number) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(v)
}

onMounted(async () => {
  try {
    stats.value = await dashboardApi.getStats()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1>Dashboard</h1>
        <p class="subtitle">A snapshot of everything your organization owns and where it stands.</p>
      </div>
    </div>

    <div v-if="loading" class="empty-state">Loading…</div>

    <template v-else-if="stats">
      <div class="stat-grid">
        <div class="stat-card card">
          <span class="stat-label">Total assets</span>
          <span class="stat-value">{{ stats.totalAssets }}</span>
        </div>
        <div class="stat-card card">
          <span class="stat-label">Available</span>
          <span class="stat-value accent">{{ stats.availableAssets }}</span>
        </div>
        <div class="stat-card card">
          <span class="stat-label">Assigned</span>
          <span class="stat-value info">{{ stats.assignedAssets }}</span>
        </div>
        <div class="stat-card card">
          <span class="stat-label">In maintenance</span>
          <span class="stat-value warn">{{ stats.inMaintenanceAssets }}</span>
        </div>
        <div class="stat-card card">
          <span class="stat-label">Total value</span>
          <span class="stat-value">{{ currency(stats.totalAssetValue) }}</span>
        </div>
      </div>

      <div class="dashboard-grid">
        <div class="card panel">
          <div class="panel-header">
            <h2>Assets by category</h2>
          </div>
          <div v-if="Object.keys(stats.assetsByCategory).length === 0" class="empty-state">
            No categories yet.
          </div>
          <div v-else class="category-bars">
            <div v-for="(count, name) in stats.assetsByCategory" :key="name" class="category-row">
              <span class="category-name">{{ name }}</span>
              <div class="bar-track">
                <div
                  class="bar-fill"
                  :style="{ width: `${(count / stats.totalAssets) * 100}%` }"
                ></div>
              </div>
              <span class="category-count">{{ count }}</span>
            </div>
          </div>
        </div>

        <div class="card panel">
          <div class="panel-header">
            <h2>Upcoming maintenance</h2>
            <RouterLink to="/maintenance" class="panel-link">View all</RouterLink>
          </div>
          <div v-if="stats.upcomingMaintenance.length === 0" class="empty-state">
            Nothing scheduled in the next 30 days.
          </div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>Asset</th>
                <th>Type</th>
                <th>Scheduled</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="m in stats.upcomingMaintenance" :key="m.id">
                <td>
                  <RouterLink :to="`/assets/${m.assetId}`" style="font-weight: 600">{{ m.assetName }}</RouterLink>
                  <div><span class="asset-tag">{{ m.assetTag }}</span></div>
                </td>
                <td>{{ m.type.replace('_', ' ') }}</td>
                <td>{{ m.scheduledDate }}</td>
                <td><StatusBadge :status="m.status" /></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
  margin-bottom: 24px;
}
.stat-card {
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.stat-label {
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}
.stat-value {
  font-size: 26px;
  font-weight: 800;
  font-family: var(--font-mono);
}
.stat-value.accent {
  color: var(--color-accent);
}
.stat-value.info {
  color: var(--color-info);
}
.stat-value.warn {
  color: var(--color-warn);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1.3fr;
  gap: 18px;
  align-items: start;
}
.panel {
  padding: 20px;
}
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.panel-header h2 {
  font-size: 15px;
}
.panel-link {
  font-size: 12.5px;
  font-weight: 600;
  color: var(--color-accent);
}

.category-bars {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.category-row {
  display: grid;
  grid-template-columns: 110px 1fr 30px;
  align-items: center;
  gap: 10px;
}
.category-name {
  font-size: 13px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.bar-track {
  height: 8px;
  background: var(--color-surface-sunken);
  border-radius: 4px;
  overflow: hidden;
}
.bar-fill {
  height: 100%;
  background: var(--color-accent);
  border-radius: 4px;
}
.category-count {
  font-size: 12.5px;
  font-family: var(--font-mono);
  text-align: right;
  color: var(--color-text-muted);
}

@media (max-width: 1180px) {
  .stat-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 640px) {
  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
