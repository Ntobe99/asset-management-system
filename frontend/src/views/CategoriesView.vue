<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { categoriesApi } from '@/api/categories'
import type { Category } from '@/types'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import { useUiStore } from '@/stores/ui'
import { useAuthStore } from '@/stores/auth'

const ui = useUiStore()
const auth = useAuthStore()

const categories = ref<Category[]>([])
const loading = ref(true)

const showForm = ref(false)
const editing = ref<Category | null>(null)
const deleteTarget = ref<Category | null>(null)

const form = ref({ name: '', description: '' })

async function load() {
  loading.value = true
  try {
    categories.value = await categoriesApi.getAll()
  } catch {
    ui.error('Could not load categories.')
  } finally {
    loading.value = false
  }
}
onMounted(load)

function openCreate() {
  editing.value = null
  form.value = { name: '', description: '' }
  showForm.value = true
}
function openEdit(c: Category) {
  editing.value = c
  form.value = { name: c.name, description: c.description || '' }
  showForm.value = true
}

async function save() {
  try {
    if (editing.value) {
      await categoriesApi.update(editing.value.id, form.value)
      ui.success('Category updated.')
    } else {
      await categoriesApi.create(form.value)
      ui.success('Category created.')
    }
    showForm.value = false
    load()
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not save category.')
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  try {
    await categoriesApi.delete(deleteTarget.value.id)
    ui.success('Category deleted.')
    deleteTarget.value = null
    load()
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not delete category. It may still have assets assigned.')
    deleteTarget.value = null
  }
}
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1>Categories</h1>
        <p class="subtitle">Group assets by type — furniture, vehicles, equipment, and more.</p>
      </div>
      <button v-if="auth.canEdit" class="btn btn-primary" @click="openCreate">+ Add category</button>
    </div>

    <div class="card">
      <div v-if="loading" class="empty-state">Loading…</div>
      <div v-else-if="categories.length === 0" class="empty-state">No categories yet.</div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Assets</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in categories" :key="c.id">
            <td style="font-weight: 600">{{ c.name }}</td>
            <td>{{ c.description || '—' }}</td>
            <td>{{ c.assetCount ?? 0 }}</td>
            <td class="row-actions">
              <button v-if="auth.canEdit" class="btn btn-ghost btn-sm" @click="openEdit(c)">Edit</button>
              <button v-if="auth.isAdmin" class="btn btn-ghost btn-sm" style="color: var(--color-danger)" @click="deleteTarget = c">
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showForm" class="modal-backdrop" @click.self="showForm = false">
      <div class="modal-panel" style="max-width: 440px">
        <h2>{{ editing ? 'Edit category' : 'Add category' }}</h2>
        <form @submit.prevent="save">
          <div class="field">
            <label>Name</label>
            <input v-model="form.name" required placeholder="e.g. Vehicles" />
          </div>
          <div class="field">
            <label>Description</label>
            <textarea v-model="form.description" placeholder="Optional description" />
          </div>
          <div style="display: flex; justify-content: flex-end; gap: 10px">
            <button type="button" class="btn btn-secondary" @click="showForm = false">Cancel</button>
            <button type="submit" class="btn btn-primary">{{ editing ? 'Save changes' : 'Create category' }}</button>
          </div>
        </form>
      </div>
    </div>

    <ConfirmDialog
      :open="!!deleteTarget"
      title="Delete this category?"
      :message="`'${deleteTarget?.name}' will be permanently removed.`"
      confirm-label="Delete"
      danger
      @confirm="confirmDelete"
      @cancel="deleteTarget = null"
    />
  </div>
</template>

<style scoped>
.row-actions {
  display: flex;
  gap: 6px;
  justify-content: flex-end;
}
</style>
