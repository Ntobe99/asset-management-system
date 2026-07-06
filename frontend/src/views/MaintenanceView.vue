<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { maintenanceApi } from '@/api/maintenance'
import type { MaintenanceRecord, MaintenanceStatus } from '@/types'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { useUiStore } from '@/stores/ui'
import { useAuthStore } from '@/stores/auth'

const ui = useUiStore()
const auth = useAuthStore()

const records = ref<MaintenanceRecord[]>([])
const loading = ref(true)
const page = ref(0)
const pageSize = 15
const totalElements = ref(0)

const editingStatus = ref<Record<number, MaintenanceStatus>>({})

async function load() {
  loading.value = true
  try {
    const result = await maintenanceApi.getAll(undefined, page.value, pageSize)
    records.value = result.content
    totalElements.value = result.totalElements
  } catch {
    ui.error('Could not load maintenance records.')
  } finally {
    loading.value = false
  }
}

watch(page, load)
onMounted(load)

const statusOptions: MaintenanceStatus[] = ['SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED']

async function updateStatus(record: MaintenanceRecord, status: MaintenanceStatus) {
  try {
    const updated = await maintenanceApi.update(record.id, { ...record, status })
    const idx = records.value.findIndex((r) => r.id === record.id)
    if (idx !== -1) records.value[idx] = updated
    ui.success('Maintenance status updated.')
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not update status.')
  }
}

function currency(v?: number | null) {
  if (v === null || v === undefined) return '—'
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(v)
}

const totalPages = () => Math.max(1, Math.ceil(totalElements.value / pageSize))
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1>Maintenance</h1>
        <p class="subtitle">Every service record across all assets. Log new work from an asset's detail page.</p>
      </div>
    </div>

    <div class="card">
      <div v-if="loading" class="empty-state">Loading…</div>
      <div v-else-if="records.length === 0" class="empty-state">No maintenance records yet.</div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>Asset</th>
            <th>Type</th>
            <th>Scheduled</th>
            <th>Completed</th>
            <th>Cost</th>
            <th>Vendor / performed by</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in records" :key="r.id">
            <td>
              <RouterLink :to="`/assets/${r.assetId}`" style="font-weight: 600">{{ r.assetName }}</RouterLink>
              <div><span class="asset-tag">{{ r.assetTag }}</span></div>
            </td>
            <td>{{ r.type.replace('_', ' ') }}</td>
            <td>{{ r.scheduledDate || '—' }}</td>
            <td>{{ r.completedDate || '—' }}</td>
            <td>{{ currency(r.cost) }}</td>
            <td>{{ r.vendor || r.performedBy || '—' }}</td>
            <td>
              <select
                v-if="auth.canEdit"
                :value="r.status"
                class="status-select"
                @change="updateStatus(r, ($event.target as HTMLSelectElement).value as MaintenanceStatus)"
              >
                <option v-for="s in statusOptions" :key="s" :value="s">{{ s.replace('_', ' ') }}</option>
              </select>
              <StatusBadge v-else :status="r.status" />
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="!loading && records.length > 0" class="pagination">
        <button class="btn btn-secondary btn-sm" :disabled="page === 0" @click="page--">Previous</button>
        <span>Page {{ page + 1 }} of {{ totalPages() }}</span>
        <button class="btn btn-secondary btn-sm" :disabled="page + 1 >= totalPages()" @click="page++">Next</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.status-select {
  font-size: 12.5px;
  padding: 6px 8px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border-strong);
  background: var(--color-surface);
}
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  padding: 16px;
  font-size: 13px;
  color: var(--color-text-muted);
}
</style>
