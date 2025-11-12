import type { RencontrePersonnage } from '@/domain/entities/RencontrePersonnage';
import {
  rencontrePersonnageApi,
  type RencontrePersonnageCreatePayload
} from '@/infrastructure/api/rencontrePersonnageApi';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useNotificationsStore } from './notificationsStore';

export const useRencontrePersonnageStore = defineStore('rencontres', () => {
  const items = ref<RencontrePersonnage[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const notify = useNotificationsStore();

  const byId = (id: number): RencontrePersonnage | null =>
    items.value.find((rencontre: RencontrePersonnage) => rencontre.id === id) ?? null;
  const hasData = computed(() => items.value.length > 0);

  const fetchAll = async (force = false): Promise<void> => {
    if (hasData.value && !force) {
      return;
    }
    loading.value = true;
    error.value = null;
    try {
      items.value = await rencontrePersonnageApi.fetchAll();
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Impossible de charger les rencontres';
    } finally {
      loading.value = false;
    }
  };

  const refresh = async (): Promise<void> => {
    await fetchAll(true);
  };

  const create = async (payload: RencontrePersonnageCreatePayload): Promise<number | null> => {
    try {
      const response = await rencontrePersonnageApi.create(payload);
      await refresh();
      notify.success('Rencontre créée avec succès');
      return response.id;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Création de la rencontre impossible';
      notify.error(error.value);
      return null;
    }
  };

  const remove = async (id: number): Promise<boolean> => {
    try {
      await rencontrePersonnageApi.delete(id);
      await refresh();
      notify.success('Rencontre supprimée avec succès');
      return true;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Suppression impossible';
      notify.error(error.value);
      return false;
    }
  };

  return {
    items,
    loading,
    error,
    byId,
    hasData,
    fetchAll,
    refresh,
    create,
    remove
  };
});
