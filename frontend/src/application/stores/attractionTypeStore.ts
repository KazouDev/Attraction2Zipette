import type { AttractionType } from '@/domain/entities/AttractionType';
import { attractionTypeApi } from '@/infrastructure/api/typeApi';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useNotificationsStore } from './notificationsStore';

export const useAttractionTypeStore = defineStore('attraction-types', () => {
  const items = ref<AttractionType[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const notify = useNotificationsStore();

  const byId = (id: number): AttractionType | null =>
    items.value.find((type: AttractionType) => type.id === id) ?? null;
  const hasData = computed(() => items.value.length > 0);

  const fetchAll = async (force = false): Promise<void> => {
    if (hasData.value && !force) {
      return;
    }
    loading.value = true;
    error.value = null;
    try {
      items.value = await attractionTypeApi.fetchAll();
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Impossible de charger les types';
    } finally {
      loading.value = false;
    }
  };

  const create = async (nom: string): Promise<number | null> => {
    try {
      const response = await attractionTypeApi.create({ nom });
      await fetchAll(true);
      notify.success('Type d\'attraction créé avec succès');
      return response.id;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Création impossible';
      notify.error(error.value);
      return null;
    }
  };

  const update = async (id: number, nom: string): Promise<boolean> => {
    try {
      await attractionTypeApi.update(id, nom);
      const index = items.value.findIndex((type: AttractionType) => type.id === id);
      if (index !== -1) {
        items.value.splice(index, 1, { ...items.value[index], nom });
      }
      notify.success('Type d\'attraction mis à jour');
      return true;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Mise à jour impossible';
      notify.error(error.value);
      return false;
    }
  };

  const remove = async (id: number): Promise<boolean> => {
    try {
      await attractionTypeApi.remove(id);
      items.value = items.value.filter((type: AttractionType) => type.id !== id);
      notify.success('Type d\'attraction supprimé');
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
    create,
    update,
    remove
  };
});
