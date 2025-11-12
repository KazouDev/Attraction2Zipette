import type { IdResponse } from '@/domain/entities/IdResponse';
import type { Lieu } from '@/domain/entities/Lieu';
import { lieuApi } from '@/infrastructure/api/lieuApi';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useNotificationsStore } from './notificationsStore';

export const useLieuStore = defineStore('lieux', () => {
  const items = ref<Lieu[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const notify = useNotificationsStore();

  const hasData = computed(() => items.value.length > 0);

  const fetchAll = async (force = false): Promise<void> => {
    if (hasData.value && !force) {
      return;
    }
    loading.value = true;
    error.value = null;
    try {
      items.value = await lieuApi.fetchAll();
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Impossible de charger les lieux';
    } finally {
      loading.value = false;
    }
  };

  const create = async (nom: string): Promise<number | null> => {
    try {
      const response = await lieuApi.create({ nom });
      await fetchAll(true);
      notify.success('Lieu créé avec succès');
      return response.id;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Création impossible';
      notify.error(error.value);
      return null;
    }
  };

  const update = async (id: number, nom: string): Promise<IdResponse | null> => {
    try {
      const response = await lieuApi.update(id, nom);
      const index = items.value.findIndex((lieu: Lieu) => lieu.id === id);
      if (index !== -1) {
        items.value.splice(index, 1, { ...items.value[index], nom });
      }
      notify.success('Lieu mis à jour');
      return response;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Mise à jour impossible';
      notify.error(error.value);
      return null;
    }
  };

  const remove = async (id: number): Promise<boolean> => {
    try {
      await lieuApi.remove(id);
      items.value = items.value.filter((lieu: Lieu) => lieu.id !== id);
      notify.success('Lieu supprimé');
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
    hasData,
    fetchAll,
    create,
    update,
    remove
  };
});
