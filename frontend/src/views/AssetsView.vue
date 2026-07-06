<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { assetsApi } from '@/api/assets'
import { categoriesApi } from '@/api/categories'
import type { Asset, AssetStatus, Category } from '@/types'
import StatusBadge from '@/components/common/StatusBadge.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import AssetFormModal from '@/components/assets/AssetFormModal.vue'
import { useUiStore } from '@/stores/ui'
import { useAuthStore } from '@/stores/auth'

const ui = useUiStore()
const auth = useAuthStore()

const assets = ref<Asset[]>([])
const categories = ref<Category[]>([])
const loading = ref(true)
const totalElements = ref(0)
const page = ref(0)
const pageSize = 12

const search = ref('')
const categoryFilter = ref<number | undefined>(undefined)
const statusFilter = ref<AssetStatus | ''>('')

const showForm = ref(false)
const editingAsset = ref<Asset | null>(null)
const deleteTarget = ref<Asset | null>(null)

const statuses: AssetStatus[] = ['AVAILABLE', 'ASSIGNED', 'IN_MAINTENANCE', 'RETIRED', 'DISPOSED']

async function loadAssets() {
  loading.value = true
  try {
    const result = await assetsApi.search({
      search: search.value || undefined,
      categoryId: categoryFilter.value,
      status: (statusFilter.value || undefined) as AssetStatus | undefined,
      page: page.value,
      size: pageSize,
    })
    assets.value = result.content
    totalElements.value = result.totalElements
  } catch (e: any) {
    ui.error('Could not load assets.')
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  categories.value = await categoriesApi.getAll()
}

let searchTimeout: ReturnType<typeof setTimeout>
watch(search, () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    page.value = 0
    loadAssets()
  }, 300)
})
watch([categoryFilter, statusFilter], () => {
  page.value = 0
  loadAssets()
})
watch(page, loadAssets)

onMounted(() => {
  loadCategories()
  loadAssets()
})

function openCreate() {
  editingAsset.value = null
  showForm.value = true
}
function openEdit(asset: Asset) {
  editingAsset.value = asset
  showForm.value = true
}

async function saveAsset(payload: Partial<Asset>) {
  try {
    if (editingAsset.value) {
      await assetsApi.update(editingAsset.value.id, payload)
      ui.success('Asset updated.')
    } else {
      await assetsApi.create(payload)
      ui.success('Asset created.')
    }
    showForm.value = false
    loadAssets()
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not save asset.')
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  try {
    await assetsApi.delete(deleteTarget.value.id)
    ui.success('Asset deleted.')
    deleteTarget.value = null
    loadAssets()
  } catch (e: any) {
    ui.error(e?.response?.data?.message || 'Could not delete asset.')
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
        <h1>Assets</h1>
        <p class="subtitle">{{ totalElements }} assets tracked across your organization.</p>
      </div>
      <button v-if="auth.canEdit" class="btn btn-primary" @click="openCreate">+ Add asset</button>
    </div>

    <div class="filters card">
      <input v-model="search" type="search" placeholder="Search by name, tag, serial…" class="search-input" />
      <select v-model="categoryFilter">
        <option :value="undefined">All categories</option>
        <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <select v-model="statusFilter">
        <option value="">All statuses</option>
        <option v-for="s in statuses" :key="s" :value="s">{{ s.replace('_', ' ') }}</option>
      </select>
    </div>

    <div class="card">
      <div v-if="loading" class="empty-state">Loading assets…</div>
      <div v-else-if="assets.length === 0" class="empty-state">
        No assets match your filters yet.
        <div v-if="auth.canEdit" style="margin-top: 12px">
          <button class="btn btn-secondary btn-sm" @click="openCreate">+ Add your first asset</button>
        </div>
      </div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>Tag</th>
            <th>Name</th>
            <th>Category</th>
            <th>Status</th>
            <th>Assigned to</th>
            <th>Current value</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="a in assets" :key="a.id">
            <td><span class="asset-tag">{{ a.assetTag }}</span></td>
            <td>
              <RouterLink :to="`/assets/${a.id}`" style="font-weight: 600">{{ a.name }}</RouterLink>
            </td>
            <td>{{ a.categoryName || '—' }}</td>
            <td><StatusBadge :status="a.status" /></td>
            <td>{{ a.assignedEmployeeName || '—' }}</td>
            <td>{{ currency(a.currentValue) }}</td>
            <td class="row-actions">
              <button class="btn btn-ghost btn-sm" @click="openEdit(a)" v-if="auth.canEdit">Edit</button>
              <button class="btn btn-ghost btn-sm" style="color: var(--color-danger)" v-if="auth.isAdmin" @click="deleteTarget = a">
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="!loading && assets.length > 0" class="pagination">
        <button class="btn btn-secondary btn-sm" :disabled="page === 0" @click="page--">Previous</button>
        <span>Page {{ page + 1 }} of {{ totalPages() }}</span>
        <button class="btn btn-secondary btn-sm" :disabled="page + 1 >= totalPages()" @click="page++">Next</button>
      </div>
    </div>

    <AssetFormModal
      :open="showForm"
      :categories="categories"
      :asset="editingAsset"
      @close="showForm = false"
      @save="saveAsset"
    />

    <ConfirmDialog
      :open="!!deleteTarget"
      title="Delete this asset?"
      :message="`'${deleteTarget?.name}' (${deleteTarget?.assetTag}) will be permanently removed.`"
      confirm-label="Delete"
      danger
      @confirm="confirmDelete"
      @cancel="deleteTarget = null"
    />
  </div>
</template>

<style scoped>
.filters {
  display: flex;
  gap: 10px;
  padding: 14px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}
.search-input {
  flex: 1;
  min-width: 220px;
  font-size: 14px;
  padding: 9px 11px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border-strong);
  outline: none;
}
.search-input:focus {
  border-color: var(--color-accent);
}
.filters select {
  font-size: 13.5px;
  padding: 9px 11px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border-strong);
  background: var(--color-surface);
}
.row-actions {
  display: flex;
  gap: 6px;
  justify-content: flex-end;
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
