import type { Personnage } from '@/domain/entities/Personnage';
import { personnageApi } from '@/infrastructure/api/personnageApi';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useNotificationsStore } from './notificationsStore';

export const usePersonnageStore = defineStore('personnages', () => {
  const items = ref<Personnage[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const notify = useNotificationsStore();

  const byId = (id: number): Personnage | null =>
    items.value.find((personnage: Personnage) => personnage.id === id) ?? null;
  const hasData = computed(() => items.value.length > 0);

  const fetchAll = async (force = false): Promise<void> => {
    if (hasData.value && !force) {
      return;
    }
    loading.value = true;
    error.value = null;
    try {
      items.value = await personnageApi.fetchAll();
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Impossible de charger les personnages';
    } finally {
      loading.value = false;
    }
  };

  const refresh = async (): Promise<void> => {
    await fetchAll(true);
  };

  const create = async (nom: string): Promise<number | null> => {
    loading.value = true;
    error.value = null;
    try {
      const result = await personnageApi.create(nom);
      await fetchAll(true);
      notify.success('Personnage créé avec succès');
      return result.id;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Impossible de créer le personnage';
      notify.error(error.value);
      return null;
    } finally {
      loading.value = false;
    }
  };

  const remove = async (id: number): Promise<boolean> => {
    loading.value = true;
    error.value = null;
    try {
      await personnageApi.delete(id);
      await fetchAll(true);
      notify.success('Personnage supprimé');
      return true;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Impossible de supprimer le personnage';
      notify.error(error.value);
      return false;
    } finally {
      loading.value = false;
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
