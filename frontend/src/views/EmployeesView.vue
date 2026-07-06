<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { employeesApi } from '@/api/employees'
import type { Employee } from '@/types'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import { useUiStore } from '@/stores/ui'
import { useAuthStore } from '@/stores/auth'

const ui = useUiStore()
const auth = useAuthStore()

const employees = ref<Employee[]>([])
const loading = ref(true)
const search = ref('')

const showForm = ref(false)
const editing = ref<Employee | null>(null)
const deleteTarget = ref<Employee | null>(null)

const form = ref({
  firstName: '',
  lastName: '',
  email: '',
  department: '',
  position: '',
  active: true,
})

async function load() {
  loading.value = true
  try {
    employees.value = await employeesApi.getAll(search.value || undefined)
  } catch {
    ui.error('Could not load employees.')
  } finally {
    loading.value = false
  }
}

let t: ReturnType<typeof setTimeout>
watch(search, () => {
  clearTimeout(t)
  t = setTimeout(load, 300)
})

onMounted(load)

function openCreate() {
  editing.value = null
  form.value = { firstName: '', lastName: '', email: '', department: '', position: '', active: true }
  showForm.value = true
}
function openEdit(e: Employee) {
  editing.value = e
  form.value = {
    firstName: e.firstName,
    lastName: e.lastName,
    email: e.email || '',
    department: e.department || '',
    position: e.position || '',
    active: e.active,
  }
  showForm.value = true
}

async function save() {
  try {
    if (editing.value) {
      await employeesApi.update(editing.value.id, form.value)
      ui.success('Employee updated.')
    } else {
      await employeesApi.create(form.value)
      ui.success('Employee added.')
    }
    showForm.value = false
    load()
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not save employee.')
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  try {
    await employeesApi.delete(deleteTarget.value.id)
    ui.success('Employee removed.')
    deleteTarget.value = null
    load()
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not remove employee.')
    deleteTarget.value = null
  }
}
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1>Employees</h1>
        <p class="subtitle">People who can have assets assigned to them.</p>
      </div>
      <button v-if="auth.canEdit" class="btn btn-primary" @click="openCreate">+ Add employee</button>
    </div>

    <div class="card filters-card">
      <input v-model="search" type="search" placeholder="Search by name or email…" class="search-input" />
    </div>

    <div class="card">
      <div v-if="loading" class="empty-state">Loading…</div>
      <div v-else-if="employees.length === 0" class="empty-state">No employees found.</div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Department</th>
            <th>Position</th>
            <th>Email</th>
            <th>Assigned assets</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="e in employees" :key="e.id">
            <td style="font-weight: 600">{{ e.firstName }} {{ e.lastName }}</td>
            <td>{{ e.department || '—' }}</td>
            <td>{{ e.position || '—' }}</td>
            <td>{{ e.email || '—' }}</td>
            <td>{{ e.assignedAssetCount ?? 0 }}</td>
            <td>
              <span class="badge" :class="e.active ? 'badge-available' : 'badge-retired'">
                {{ e.active ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="row-actions">
              <button v-if="auth.canEdit" class="btn btn-ghost btn-sm" @click="openEdit(e)">Edit</button>
              <button v-if="auth.isAdmin" class="btn btn-ghost btn-sm" style="color: var(--color-danger)" @click="deleteTarget = e">
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showForm" class="modal-backdrop" @click.self="showForm = false">
      <div class="modal-panel">
        <h2>{{ editing ? 'Edit employee' : 'Add employee' }}</h2>
        <form @submit.prevent="save">
          <div class="grid-2">
            <div class="field">
              <label>First name</label>
              <input v-model="form.firstName" required />
            </div>
            <div class="field">
              <label>Last name</label>
              <input v-model="form.lastName" required />
            </div>
          </div>
          <div class="field">
            <label>Email</label>
            <input v-model="form.email" type="email" />
          </div>
          <div class="grid-2">
            <div class="field">
              <label>Department</label>
              <input v-model="form.department" placeholder="e.g. Operations" />
            </div>
            <div class="field">
              <label>Position</label>
              <input v-model="form.position" placeholder="e.g. Site Manager" />
            </div>
          </div>
          <label style="display: flex; align-items: center; gap: 8px; font-size: 13.5px; margin-bottom: 18px">
            <input v-model="form.active" type="checkbox" style="width: auto" />
            Active employee
          </label>
          <div style="display: flex; justify-content: flex-end; gap: 10px">
            <button type="button" class="btn btn-secondary" @click="showForm = false">Cancel</button>
            <button type="submit" class="btn btn-primary">{{ editing ? 'Save changes' : 'Add employee' }}</button>
          </div>
        </form>
      </div>
    </div>

    <ConfirmDialog
      :open="!!deleteTarget"
      title="Remove this employee?"
      :message="`${deleteTarget?.firstName} ${deleteTarget?.lastName} will be removed. This is blocked if they still have assets assigned.`"
      confirm-label="Remove"
      danger
      @confirm="confirmDelete"
      @cancel="deleteTarget = null"
    />
  </div>
</template>

<style scoped>
.filters-card {
  padding: 14px;
  margin-bottom: 18px;
}
.search-input {
  width: 100%;
  font-size: 14px;
  padding: 9px 11px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border-strong);
  outline: none;
}
.search-input:focus {
  border-color: var(--color-accent);
}
.row-actions {
  display: flex;
  gap: 6px;
  justify-content: flex-end;
}
.grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
@media (max-width: 520px) {
  .grid-2 {
    grid-template-columns: 1fr;
  }
}
</style>
