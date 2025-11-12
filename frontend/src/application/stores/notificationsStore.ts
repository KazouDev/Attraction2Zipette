import { defineStore } from 'pinia';

export type NotificationType = 'success' | 'info' | 'warning' | 'error';

export interface NotificationOptions {
  id?: number;
  type?: NotificationType;
  message: string;
  duration?: number; // ms
  sticky?: boolean; // si true, pas d'auto-hide
}

export interface NotificationItem {
  id: number;
  type: NotificationType;
  message: string;
  duration: number; // ms
  sticky: boolean;
}

let seed = 1;

export const useNotificationsStore = defineStore('notifications', {
  state: () => ({
    items: [] as NotificationItem[]
  }),
  actions: {
    push(opts: NotificationOptions): number {
      const id = opts.id ?? seed++;
      const item: NotificationItem = {
        id,
        type: opts.type ?? 'info',
        message: opts.message,
        duration: Math.max(1000, opts.duration ?? 8000),
        sticky: Boolean(opts.sticky)
      };
      this.items.push(item);
      return id;
    },
    remove(id: number) {
      this.items = this.items.filter((n) => n.id !== id);
    },
    clear() {
      this.items = [];
    },

    success(message: string, duration?: number) {
      return this.push({ type: 'success', message, duration });
    },
    info(message: string, duration?: number) {
      return this.push({ type: 'info', message, duration });
    },
    warning(message: string, duration?: number) {
      return this.push({ type: 'warning', message, duration });
    },
    error(message: string, duration?: number) {
      return this.push({ type: 'error', message, duration });
    }
  }
});

