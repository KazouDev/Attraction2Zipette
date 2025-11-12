<template>
  <div
    class="toast"
    :class="`toast--${item.type}`"
    role="status"
    aria-live="polite"
    @mouseenter="pause"
    @mouseleave="resume"
  >
    <div class="toast__icon" aria-hidden="true" />
    <div class="toast__content">
      <p class="toast__message">{{ item.message }}</p>
    </div>
    <button class="toast__close" type="button" @click="close" aria-label="Fermer">×</button>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { useNotificationsStore, type NotificationItem as Item } from '@/application/stores/notificationsStore';

interface Props { item: Item }
const props = defineProps<Props>();

const store = useNotificationsStore();
let timer: number | null = null;
const remaining = ref(props.item.duration);
let lastStart = Date.now();

const clear = () => { if (timer) { window.clearTimeout(timer); timer = null; } };
const start = () => {
  clear();
  if (!props.item.sticky) {
    lastStart = Date.now();
    timer = window.setTimeout(() => close(), remaining.value);
  }
};
const pause = () => {
  if (!props.item.sticky && timer) {
    remaining.value -= Date.now() - lastStart;
    clear();
  }
};
const resume = () => { if (!props.item.sticky) start(); };

const close = () => {
  clear();
  store.remove(props.item.id);
};

onMounted(start);
onBeforeUnmount(clear);
</script>

<style scoped>
.toast {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 0.75rem 0.875rem 0.75rem 0.75rem;
  border-radius: 0.75rem;
  background: #0f172a;
  color: #e2e8f0;
  box-shadow: 0 16px 40px -20px rgba(2, 6, 23, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.08);
  width: 320px;
  max-width: calc(100vw - 2rem);
}
.toast__icon {
  width: 0.8rem;
  height: 0.8rem;
  margin-top: 0.3rem;
  border-radius: 999px;
  background: currentColor;
  opacity: 0.9;
}
.toast__content { flex: 1; }
.toast__message { margin: 0; font-size: 0.95rem; line-height: 1.35; }
.toast__close {
  appearance: none; border: 0; background: transparent; color: inherit;
  font-size: 1rem; line-height: 1; padding: 0 0.25rem; cursor: pointer; opacity: 0.7;
}
.toast__close:hover { opacity: 1; }

.toast--success { color: #bbf7d0; border-color: rgba(34, 197, 94, 0.45); box-shadow: 0 16px 40px -20px rgba(34, 197, 94, 0.35); }
.toast--info { color: #bae6fd; border-color: rgba(59, 130, 246, 0.45); box-shadow: 0 16px 40px -20px rgba(59, 130, 246, 0.35); }
.toast--warning { color: #fde68a; border-color: rgba(245, 158, 11, 0.45); box-shadow: 0 16px 40px -20px rgba(245, 158, 11, 0.35); }
.toast--error { color: #fecaca; border-color: rgba(239, 68, 68, 0.45); box-shadow: 0 16px 40px -20px rgba(239, 68, 68, 0.35); }
</style>

