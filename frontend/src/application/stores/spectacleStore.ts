import type { IdResponse } from '@/domain/entities/IdResponse';
import type { Spectacle } from '@/domain/entities/Spectacle';
import {
    spectacleApi,
    type SpectacleCreatePayload,
    type SpectacleProgrammationPayload,
    type SpectacleUpdatePayload
} from '@/infrastructure/api/spectacleApi';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useNotificationsStore } from './notificationsStore';

export const useSpectacleStore = defineStore('spectacles', () => {
  const items = ref<Spectacle[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const notify = useNotificationsStore();

  const byId = (id: number): Spectacle | null =>
    items.value.find((spectacle: Spectacle) => spectacle.id === id) ?? null;
  const hasData = computed(() => items.value.length > 0);

  const fetchAll = async (force = false): Promise<void> => {
    if (hasData.value && !force) {
      return;
    }
    loading.value = true;
    error.value = null;
    try {
      items.value = await spectacleApi.fetchAll();
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Impossible de charger les spectacles';
    } finally {
      loading.value = false;
    }
  };

  const refresh = async (): Promise<void> => {
    await fetchAll(true);
  };

  const create = async (payload: SpectacleCreatePayload): Promise<number | null> => {
    try {
      const response = await spectacleApi.create(payload);
      await refresh();
      notify.success('Spectacle créé avec succès');
      return response.id;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Création du spectacle impossible';
      notify.error(error.value);
      return null;
    }
  };

  const update = async (id: number, payload: SpectacleUpdatePayload): Promise<IdResponse | null> => {
    try {
      const response = await spectacleApi.update(id, payload);
      const refreshed = await spectacleApi.fetchById(id);
      const index = items.value.findIndex((item: Spectacle) => item.id === id);
      if (index !== -1) {
        items.value.splice(index, 1, refreshed);
      }
      notify.success('Spectacle mis à jour avec succès');
      return response;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Mise à jour impossible';
      notify.error(error.value);
      return null;
    }
  };

  const setProgrammations = async (
    id: number,
    programmations: SpectacleProgrammationPayload[]
  ): Promise<IdResponse | null> => {
    try {
      const response = await spectacleApi.setProgrammations(id, programmations);
      const refreshed = await spectacleApi.fetchById(id);
      const index = items.value.findIndex((item: Spectacle) => item.id === id);
      if (index !== -1) {
        items.value.splice(index, 1, refreshed);
      }
      notify.success('Programmations enregistrées avec succès');
      return response;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Mise à jour des programmations impossible';
      notify.error(error.value);
      return null;
    }
  };

  const addProgrammation = async (
    id: number,
    programmation: SpectacleProgrammationPayload
  ): Promise<IdResponse | null> => {
    try {
      const response = await spectacleApi.addProgrammation(id, programmation);
      const refreshed = await spectacleApi.fetchById(id);
      const index = items.value.findIndex((item: Spectacle) => item.id === id);
      if (index !== -1) {
        items.value.splice(index, 1, refreshed);
      }
      notify.success('Programmation ajoutée avec succès');
      return response;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Ajout de programmation impossible';
      notify.error(error.value);
      return null;
    }
  };

  const addPersonnage = async (spectacleId: number, personnageId: number): Promise<IdResponse | null> => {
    try {
      const response = await spectacleApi.addPersonnage(spectacleId, personnageId);
      const refreshed = await spectacleApi.fetchById(spectacleId);
      const index = items.value.findIndex((item: Spectacle) => item.id === spectacleId);
      if (index !== -1) {
        items.value.splice(index, 1, refreshed);
      }
      notify.success('Personnage ajouté au spectacle');
      return response;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Ajout du personnage impossible';
      notify.error(error.value);
      return null;
    }
  };

  const removePersonnage = async (spectacleId: number, personnageId: number): Promise<IdResponse | null> => {
    try {
      const response = await spectacleApi.removePersonnage(spectacleId, personnageId);
      const refreshed = await spectacleApi.fetchById(spectacleId);
      const index = items.value.findIndex((item: Spectacle) => item.id === spectacleId);
      if (index !== -1) {
        items.value.splice(index, 1, refreshed);
      }
      notify.success('Personnage retiré du spectacle');
      return response;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Suppression du personnage impossible';
      notify.error(error.value);
      return null;
    }
  };

  const remove = async (id: number): Promise<IdResponse | null> => {
    try {
      const response = await spectacleApi.delete(id);
      const index = items.value.findIndex((item: Spectacle) => item.id === id);
      if (index !== -1) {
        items.value.splice(index, 1);
      }
      notify.success('Spectacle supprimé avec succès');
      return response;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Suppression du spectacle impossible';
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
    setProgrammations,
    addProgrammation,
    addPersonnage,
    removePersonnage,
    remove
  };
});
