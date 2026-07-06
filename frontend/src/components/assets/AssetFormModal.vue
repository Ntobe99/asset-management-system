<script setup lang="ts">
import { reactive, watch } from 'vue'
import type { Asset, Category } from '@/types'

const props = defineProps<{
  open: boolean
  categories: Category[]
  asset?: Asset | null
}>()

const emit = defineEmits<{ close: []; save: [payload: Partial<Asset>] }>()

const defaultForm = (): Partial<Asset> => ({
  assetTag: '',
  name: '',
  description: '',
  categoryId: undefined,
  location: '',
  manufacturer: '',
  modelNumber: '',
  serialNumber: '',
  purchaseDate: '',
  purchaseCost: undefined,
  currentValue: undefined,
  warrantyExpiry: '',
})

const form = reactive<Partial<Asset>>(defaultForm())

watch(
  () => props.open,
  (isOpen) => {
    if (isOpen) {
      Object.assign(form, props.asset ? { ...props.asset } : defaultForm())
    }
  }
)

function submit() {
  emit('save', { ...form })
}
</script>

<template>
  <div v-if="open" class="modal-backdrop" @click.self="emit('close')">
    <div class="modal-panel" style="max-width: 640px">
      <h2>{{ asset ? 'Edit asset' : 'Add asset' }}</h2>
      <form @submit.prevent="submit">
        <div class="grid-2">
          <div class="field">
            <label>Asset tag</label>
            <input v-model="form.assetTag" required placeholder="e.g. FUR-0142" />
          </div>
          <div class="field">
            <label>Name</label>
            <input v-model="form.name" required placeholder="e.g. Standing desk" />
          </div>
        </div>

        <div class="field">
          <label>Description</label>
          <textarea v-model="form.description" placeholder="Optional notes about this asset" />
        </div>

        <div class="grid-2">
          <div class="field">
            <label>Category</label>
            <select v-model.number="form.categoryId" required>
              <option :value="undefined" disabled>Select a category</option>
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="field">
            <label>Location</label>
            <input v-model="form.location" placeholder="e.g. HQ - Floor 3" />
          </div>
        </div>

        <div class="grid-2">
          <div class="field">
            <label>Manufacturer</label>
            <input v-model="form.manufacturer" placeholder="e.g. Steelcase" />
          </div>
          <div class="field">
            <label>Model number</label>
            <input v-model="form.modelNumber" />
          </div>
        </div>

        <div class="grid-2">
          <div class="field">
            <label>Serial number</label>
            <input v-model="form.serialNumber" />
          </div>
          <div class="field">
            <label>Purchase date</label>
            <input v-model="form.purchaseDate" type="date" />
          </div>
        </div>

        <div class="grid-2">
          <div class="field">
            <label>Purchase cost (USD)</label>
            <input v-model.number="form.purchaseCost" type="number" min="0" step="0.01" />
          </div>
          <div class="field">
            <label>Current value (USD)</label>
            <input v-model.number="form.currentValue" type="number" min="0" step="0.01" />
          </div>
        </div>

        <div class="field">
          <label>Warranty expiry</label>
          <input v-model="form.warrantyExpiry" type="date" />
        </div>

        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 8px">
          <button type="button" class="btn btn-secondary" @click="emit('close')">Cancel</button>
          <button type="submit" class="btn btn-primary">{{ asset ? 'Save changes' : 'Create asset' }}</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
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
