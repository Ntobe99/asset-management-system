<script setup lang="ts">
withDefaults(
  defineProps<{
    open: boolean
    title?: string
    message?: string
    confirmLabel?: string
    danger?: boolean
  }>(),
  {
    title: 'Are you sure?',
    message: 'This action cannot be undone.',
    confirmLabel: 'Confirm',
    danger: false,
  }
)

const emit = defineEmits<{ confirm: []; cancel: [] }>()
</script>

<template>
  <div v-if="open" class="modal-backdrop" @click.self="emit('cancel')">
    <div class="modal-panel" style="max-width: 420px">
      <h2>{{ title }}</h2>
      <p style="color: var(--color-text-muted); font-size: 14px; margin-bottom: 24px">
        {{ message }}
      </p>
      <div style="display: flex; justify-content: flex-end; gap: 10px">
        <button class="btn btn-secondary" @click="emit('cancel')">Cancel</button>
        <button :class="danger ? 'btn btn-danger' : 'btn btn-primary'" @click="emit('confirm')">
          {{ confirmLabel }}
        </button>
      </div>
    </div>
  </div>
</template>
