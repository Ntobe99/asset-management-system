<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ status: string }>()

const label = computed(() => props.status.replace(/_/g, ' '))
</script>

<template>
  <span class="badge" :class="`badge-${status.toLowerCase()}`">{{ label }}</span>
</template>
