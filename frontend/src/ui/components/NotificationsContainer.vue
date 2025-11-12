<template>
  <div class="toasts" aria-live="polite" aria-atomic="false">
    <transition-group name="toasts-move" tag="div">
      <NotificationItem v-for="n in items" :key="n.id" :item="n" />
    </transition-group>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia';
import { useNotificationsStore } from '@/application/stores/notificationsStore';
import NotificationItem from './NotificationItem.vue';

const store = useNotificationsStore();
const { items } = storeToRefs(store);
</script>

<style scoped>
.toasts {
  position: fixed;
  top: 1rem;
  right: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  z-index: 2000;
  width: 340px;
  max-width: min(90vw, 340px);
}

/* Animations d’apparition/déplacement */
.toasts-move-enter-active,
.toasts-move-leave-active {
  transition: all 180ms ease;
}
.toasts-move-enter-from,
.toasts-move-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.98);
}
.toasts-move-move {
  transition: transform 180ms ease;
}
</style>

