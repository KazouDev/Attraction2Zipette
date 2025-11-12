<template>
  <!-- Mode inline (par défaut) -->
  <p v-if="message && mode === 'inline'" class="inline" role="alert">{{ message }}</p>

  <!-- Mode toast compact (optionnel) -->
  <transition name="toast-fade">
    <div v-if="message && mode === 'toast' && isOpen" class="toast" :class="`toast--${type}`" role="alert" aria-live="assertive">
      <div class="toast__icon" aria-hidden="true" />
      <div class="toast__content">
        <p class="toast__message">{{ message }}</p>
      </div>
      <button class="toast__close" type="button" @click="close" aria-label="Fermer la notification">×</button>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

interface Props {
  message: string | null;
  mode?: 'inline' | 'toast';
  type?: 'error' | 'success' | 'info' | 'warning';
  autoHide?: boolean; // utilisé en mode toast
  duration?: number; // ms, utilisé en mode toast
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'inline',
  type: 'error',
  autoHide: true,
  duration: 5000
});

const isOpen = ref(false);
let timer: number | null = null;

const clearTimer = () => {
  if (timer) {
    window.clearTimeout(timer);
    timer = null;
  }
};

const startTimer = () => {
  clearTimer();
  if (props.mode === 'toast' && props.autoHide && props.message) {
    timer = window.setTimeout(() => {
      isOpen.value = false;
      clearTimer();
    }, props.duration);
  }
};

const close = () => {
  isOpen.value = false;
  clearTimer();
};

watch(
  () => props.message,
  (val) => {
    if (props.mode === 'toast') {
      if (val) {
        isOpen.value = true;
        startTimer();
      } else {
        close();
      }
    }
  },
  { immediate: true }
);
</script>

<style scoped>
/* Style inline (compat ancien) */
.inline {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0;
  padding: 0.4rem 0.6rem;
  border-radius: 0.5rem;
  background: rgba(248, 113, 113, 0.06);
  color: #b91c1c;
  border: 1px solid rgba(239, 68, 68, 0.25);
  font-size: 0.92rem;
  line-height: 1.25;
  width: fit-content;
  max-width: min(90vw, 560px);
}
.inline::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 999px;
  background: currentColor;
  opacity: 0.85;
}

/* Toast compact */
.toast {
  position: fixed;
  right: 1rem;
  bottom: 1rem;
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  max-width: 22rem;
  padding: 0.625rem 0.75rem 0.625rem 0.625rem;
  border-radius: 0.75rem;
  background: #0f172a;
  color: #e2e8f0;
  box-shadow: 0 12px 30px -12px rgba(2, 6, 23, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.08);
  z-index: 1000;
}

.toast__icon {
  width: 0.75rem;
  height: 0.75rem;
  margin-top: 0.3rem;
  border-radius: 999px;
  background: currentColor;
  opacity: 0.8;
}

.toast__content { flex: 1 1 auto; }

.toast__message {
  margin: 0;
  line-height: 1.25rem;
  font-size: 0.925rem;
}

.toast__close {
  appearance: none;
  border: 0;
  background: transparent;
  color: inherit;
  font-size: 1rem;
  line-height: 1;
  padding: 0 0.25rem;
  cursor: pointer;
  opacity: 0.7;
}
.toast__close:hover { opacity: 1; }

/* Variantes */
.toast--error { color: #fecaca; border-color: rgba(239, 68, 68, 0.4); box-shadow: 0 12px 30px -12px rgba(239, 68, 68, 0.35); }
.toast--success { color: #bbf7d0; border-color: rgba(34, 197, 94, 0.4); box-shadow: 0 12px 30px -12px rgba(34, 197, 94, 0.35); }
.toast--info { color: #bae6fd; border-color: rgba(59, 130, 246, 0.35); box-shadow: 0 12px 30px -12px rgba(59, 130, 246, 0.3); }
.toast--warning { color: #fde68a; border-color: rgba(245, 158, 11, 0.4); box-shadow: 0 12px 30px -12px rgba(245, 158, 11, 0.35); }

/* Transition */
.toast-fade-enter-active, .toast-fade-leave-active {
  transition: all 180ms ease;
}
.toast-fade-enter-from, .toast-fade-leave-to {
  opacity: 0;
  transform: translateY(8px) scale(0.98);
}
</style>
