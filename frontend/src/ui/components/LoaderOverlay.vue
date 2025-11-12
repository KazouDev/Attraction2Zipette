<template>
  <transition name="loader-fade">
    <div v-if="visible" class="loader" role="status" aria-live="polite">
      <span class="loader__spinner" />
      <span class="loader__label">
        <slot>Chargement…</slot>
      </span>
    </div>
  </transition>
</template>

<script setup lang="ts">
interface Props {
  visible: boolean;
}

defineProps<Props>();
</script>

<style scoped>
.loader {
  position: fixed;
  right: 1rem;
  bottom: 1rem;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 0.6rem;
  background: rgba(15, 23, 42, 0.9);
  color: #f8fafc;
  border-radius: 999px;
  width: max-content;
  box-shadow: 0 12px 30px -12px rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(148, 163, 184, 0.2);
  font-size: 0.85rem;
  z-index: 1000;
  pointer-events: none; /* n'intercepte pas les clics */
}

.loader__spinner {
  width: 0.9rem;
  height: 0.9rem;
  border-radius: 999px;
  border: 2px solid rgba(148, 163, 184, 0.35);
  border-top-color: #38bdf8;
  animation: spin 1s linear infinite;
}

.loader__label {
  opacity: 0.9;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Transition discrète */
.loader-fade-enter-active,
.loader-fade-leave-active {
  transition: opacity 150ms ease, transform 150ms ease;
}
.loader-fade-enter-from,
.loader-fade-leave-to {
  opacity: 0;
  transform: translateY(6px) scale(0.98);
}
</style>
