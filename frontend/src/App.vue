<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppShell from '@/components/layout/AppShell.vue'
import ToastStack from '@/components/common/ToastStack.vue'

const route = useRoute()
const isPublic = computed(() => !!route.meta.public)
</script>

<template>
  <ToastStack />
  <AppShell v-if="!isPublic">
    <router-view />
  </AppShell>
  <router-view v-else />
</template>
