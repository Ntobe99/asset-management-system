<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { assetsApi } from '@/api/assets'
import { employeesApi } from '@/api/employees'
import { maintenanceApi } from '@/api/maintenance'
import type { Asset, Employee, MaintenanceRecord, MaintenanceType } from '@/types'
import StatusBadge from '@/components/common/StatusBadge.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import { useUiStore } from '@/stores/ui'
import { useAuthStore } from '@/stores/auth'

const props = defineProps<{ id: string }>()
const route = useRoute()
const router = useRouter()
const ui = useUiStore()
const auth = useAuthStore()

const asset = ref<Asset | null>(null)
const employees = ref<Employee[]>([])
const maintenanceRecords = ref<MaintenanceRecord[]>([])
const loading = ref(true)

const showAssignForm = ref(false)
const selectedEmployeeId = ref<number | undefined>(undefined)
const assignNotes = ref('')
const showUnassignConfirm = ref(false)

const showMaintenanceForm = ref(false)
const maintenanceForm = ref({
  type: 'PREVENTIVE' as MaintenanceType,
  description: '',
  scheduledDate: '',
  cost: undefined as number | undefined,
  performedBy: '',
  vendor: '',
})

const maintenanceTypes: MaintenanceType[] = ['PREVENTIVE', 'CORRECTIVE', 'INSPECTION', 'CALIBRATION', 'OTHER']

async function loadAll() {
  loading.value = true
  const assetId = Number(props.id)
  try {
    const [a, emps, maint] = await Promise.all([
      assetsApi.getById(assetId),
      employeesApi.getAll(),
      maintenanceApi.getAll(assetId, 0, 50),
    ])
    asset.value = a
    employees.value = emps
    maintenanceRecords.value = maint.content
  } catch (e) {
    ui.error('Could not load this asset.')
  } finally {
    loading.value = false
  }
}

watch(() => props.id, loadAll)
onMounted(loadAll)

async function submitAssign() {
  if (!asset.value || !selectedEmployeeId.value) return
  try {
    asset.value = await assetsApi.assign(asset.value.id, selectedEmployeeId.value, assignNotes.value)
    ui.success('Asset assigned.')
    showAssignForm.value = false
    selectedEmployeeId.value = undefined
    assignNotes.value = ''
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not assign asset.')
  }
}

async function confirmUnassign() {
  if (!asset.value) return
  try {
    asset.value = await assetsApi.unassign(asset.value.id)
    ui.success('Asset unassigned.')
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not unassign asset.')
  } finally {
    showUnassignConfirm.value = false
  }
}

async function submitMaintenance() {
  if (!asset.value) return
  try {
    const record = await maintenanceApi.create({
      assetId: asset.value.id,
      ...maintenanceForm.value,
    })
    maintenanceRecords.value = [record, ...maintenanceRecords.value]
    ui.success('Maintenance record added.')
    showMaintenanceForm.value = false
    maintenanceForm.value = {
      type: 'PREVENTIVE',
      description: '',
      scheduledDate: '',
      cost: undefined,
      performedBy: '',
      vendor: '',
    }
    // status may have changed if maintenance started immediately
    asset.value = await assetsApi.getById(asset.value.id)
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not create maintenance record.')
  }
}

function currency(v?: number | null) {
  if (v === null || v === undefined) return '—'
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(v)
}
</script>

<template>
  <div>
    <RouterLink to="/assets" class="back-link">← Back to assets</RouterLink>

    <div v-if="loading" class="empty-state">Loading…</div>

    <template v-else-if="asset">
      <div class="page-header">
        <div>
          <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 6px">
            <h1>{{ asset.name }}</h1>
            <StatusBadge :status="asset.status" />
          </div>
          <p class="subtitle">
            <span class="asset-tag">{{ asset.assetTag }}</span>
            <span v-if="asset.categoryName"> · {{ asset.categoryName }}</span>
            <span v-if="asset.location"> · {{ asset.location }}</span>
          </p>
        </div>
        <div style="display: flex; gap: 10px" v-if="auth.canEdit">
          <button
            v-if="!asset.assignedEmployeeId && asset.status !== 'RETIRED' && asset.status !== 'DISPOSED'"
            class="btn btn-primary"
            @click="showAssignForm = true"
          >
            Assign to employee
          </button>
          <button v-else-if="asset.assignedEmployeeId" class="btn btn-secondary" @click="showUnassignConfirm = true">
            Unassign
          </button>
        </div>
      </div>

      <div class="detail-grid">
        <div class="card panel">
          <h2>Details</h2>
          <dl class="detail-list">
            <div><dt>Manufacturer</dt><dd>{{ asset.manufacturer || '—' }}</dd></div>
            <div><dt>Model number</dt><dd>{{ asset.modelNumber || '—' }}</dd></div>
            <div><dt>Serial number</dt><dd>{{ asset.serialNumber || '—' }}</dd></div>
            <div><dt>Purchase date</dt><dd>{{ asset.purchaseDate || '—' }}</dd></div>
            <div><dt>Purchase cost</dt><dd>{{ currency(asset.purchaseCost) }}</dd></div>
            <div><dt>Current value</dt><dd>{{ currency(asset.currentValue) }}</dd></div>
            <div><dt>Warranty expiry</dt><dd>{{ asset.warrantyExpiry || '—' }}</dd></div>
            <div><dt>Assigned to</dt><dd>{{ asset.assignedEmployeeName || 'Unassigned' }}</dd></div>
          </dl>
          <p v-if="asset.description" class="description">{{ asset.description }}</p>
        </div>

        <div class="card panel">
          <div class="panel-header">
            <h2>Maintenance history</h2>
            <button v-if="auth.canEdit" class="btn btn-secondary btn-sm" @click="showMaintenanceForm = true">
              + Log maintenance
            </button>
          </div>

          <div v-if="maintenanceRecords.length === 0" class="empty-state">No maintenance recorded yet.</div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>Type</th>
                <th>Status</th>
                <th>Scheduled</th>
                <th>Completed</th>
                <th>Cost</th>
                <th>Notes</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="m in maintenanceRecords" :key="m.id">
                <td>{{ m.type.replace('_', ' ') }}</td>
                <td><StatusBadge :status="m.status" /></td>
                <td>{{ m.scheduledDate || '—' }}</td>
                <td>{{ m.completedDate || '—' }}</td>
                <td>{{ currency(m.cost) }}</td>
                <td class="notes-cell">{{ m.description || m.notes || '—' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Assign modal -->
      <div v-if="showAssignForm" class="modal-backdrop" @click.self="showAssignForm = false">
        <div class="modal-panel">
          <h2>Assign asset</h2>
          <form @submit.prevent="submitAssign">
            <div class="field">
              <label>Employee</label>
              <select v-model.number="selectedEmployeeId" required>
                <option :value="undefined" disabled>Select an employee</option>
                <option v-for="e in employees" :key="e.id" :value="e.id">
                  {{ e.firstName }} {{ e.lastName }} — {{ e.department || 'No department' }}
                </option>
              </select>
            </div>
            <div class="field">
              <label>Notes (optional)</label>
              <textarea v-model="assignNotes" placeholder="Condition at handoff, accessories included, etc." />
            </div>
            <div style="display: flex; justify-content: flex-end; gap: 10px">
              <button type="button" class="btn btn-secondary" @click="showAssignForm = false">Cancel</button>
              <button type="submit" class="btn btn-primary">Assign</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Maintenance modal -->
      <div v-if="showMaintenanceForm" class="modal-backdrop" @click.self="showMaintenanceForm = false">
        <div class="modal-panel">
          <h2>Log maintenance</h2>
          <form @submit.prevent="submitMaintenance">
            <div class="field">
              <label>Type</label>
              <select v-model="maintenanceForm.type" required>
                <option v-for="t in maintenanceTypes" :key="t" :value="t">{{ t.replace('_', ' ') }}</option>
              </select>
            </div>
            <div class="field">
              <label>Description</label>
              <textarea v-model="maintenanceForm.description" placeholder="What needs to happen?" />
            </div>
            <div class="field">
              <label>Scheduled date</label>
              <input v-model="maintenanceForm.scheduledDate" type="date" />
            </div>
            <div class="field">
              <label>Estimated cost (USD)</label>
              <input v-model.number="maintenanceForm.cost" type="number" min="0" step="0.01" />
            </div>
            <div class="field">
              <label>Performed by</label>
              <input v-model="maintenanceForm.performedBy" placeholder="Technician or team" />
            </div>
            <div class="field">
              <label>Vendor</label>
              <input v-model="maintenanceForm.vendor" placeholder="External vendor, if any" />
            </div>
            <div style="display: flex; justify-content: flex-end; gap: 10px">
              <button type="button" class="btn btn-secondary" @click="showMaintenanceForm = false">Cancel</button>
              <button type="submit" class="btn btn-primary">Log maintenance</button>
            </div>
          </form>
        </div>
      </div>

      <ConfirmDialog
        :open="showUnassignConfirm"
        title="Unassign this asset?"
        :message="`${asset.name} will be returned to the available pool.`"
        confirm-label="Unassign"
        @confirm="confirmUnassign"
        @cancel="showUnassignConfirm = false"
      />
    </template>
  </div>
</template>

<style scoped>
.back-link {
  display: inline-block;
  font-size: 13px;
  color: var(--color-text-muted);
  margin-bottom: 14px;
}
.back-link:hover {
  color: var(--color-accent);
}
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1.4fr;
  gap: 18px;
  align-items: start;
}
.panel {
  padding: 20px;
}
.panel h2 {
  font-size: 15px;
  margin-bottom: 16px;
}
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.panel-header h2 {
  margin-bottom: 0;
}
.detail-list {
  display: grid;
  gap: 12px;
  margin: 0 0 14px;
}
.detail-list > div {
  display: flex;
  justify-content: space-between;
  font-size: 13.5px;
  border-bottom: 1px dashed var(--color-border);
  padding-bottom: 8px;
}
.detail-list dt {
  color: var(--color-text-muted);
}
.detail-list dd {
  margin: 0;
  font-weight: 600;
  text-align: right;
}
.description {
  font-size: 13.5px;
  color: var(--color-text-muted);
  line-height: 1.6;
  padding-top: 6px;
}
.notes-cell {
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 980px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
