<script setup lang="ts">
import { useUiStore } from '@/stores/ui'

const ui = useUiStore()
</script>

<template>
  <div class="toast-stack">
    <div v-for="t in ui.toasts" :key="t.id" class="toast" :class="t.type">
      {{ t.message }}
    </div>
  </div>
</template>
