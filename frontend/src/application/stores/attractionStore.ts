import type { Attraction } from '@/domain/entities/Attraction';
import type { IdResponse } from '@/domain/entities/IdResponse';
import {
  attractionApi,
  type AttractionCreatePayload,
  type AttractionHorairePayload,
  type AttractionUpdatePayload
} from '@/infrastructure/api/attractionApi';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useNotificationsStore } from './notificationsStore';

export const useAttractionStore = defineStore('attractions', () => {
  const items = ref<Attraction[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const notify = useNotificationsStore();

  const byId = (id: number): Attraction | null =>
    items.value.find((item: Attraction) => item.id === id) ?? null;
  const hasData = computed(() => items.value.length > 0);

  const fetchAll = async (force = false): Promise<void> => {
    if (hasData.value && !force) {
      return;
    }
    loading.value = true;
    error.value = null;
    try {
      items.value = await attractionApi.fetchAll();
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Impossible de charger les attractions';
    } finally {
      loading.value = false;
    }
  };

  const refresh = async (): Promise<void> => {
    await fetchAll(true);
  };

  const create = async (payload: AttractionCreatePayload): Promise<number | null> => {
    try {
      const response = await attractionApi.create(payload);
      await refresh();
      notify.success('Attraction créée avec succès');
      return response.id;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Création impossible';
      notify.error(error.value);
      return null;
    }
  };

  const update = async (id: number, payload: AttractionUpdatePayload): Promise<IdResponse | null> => {
    try {
      const response = await attractionApi.update(id, payload);
      const refreshed = await attractionApi.fetchById(id);
      const index = items.value.findIndex((item: Attraction) => item.id === id);
      if (index !== -1) {
        items.value.splice(index, 1, refreshed);
      }
      notify.success('Attraction mise à jour avec succès');
      return response;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Mise à jour impossible';
      notify.error(error.value);
      return null;
    }
  };

  const remove = async (id: number): Promise<boolean> => {
    try {
      await attractionApi.remove(id);
      items.value = items.value.filter((item: Attraction) => item.id !== id);
      notify.success('Attraction supprimée avec succès');
      return true;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Suppression impossible';
      notify.error(error.value);
      return false;
    }
  };

  const updateHoraires = async (
    id: number,
    horaires: AttractionHorairePayload[]
  ): Promise<IdResponse | null> => {
    try {
      const response = await attractionApi.updateHoraires(id, horaires);
      const refreshed = await attractionApi.fetchById(id);
      const index = items.value.findIndex((item: Attraction) => item.id === id);
      if (index !== -1) {
        items.value.splice(index, 1, refreshed);
      }
      notify.success('Horaires enregistrés avec succès');
      return response;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Mise à jour des horaires impossible';
      notify.error(error.value);
      return null;
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
    update,
    remove,
    updateHoraires
  };
});
