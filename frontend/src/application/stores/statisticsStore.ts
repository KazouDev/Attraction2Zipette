import { defineStore } from 'pinia';
import { ref } from 'vue';
import { fetchSpectaclesRanking, fetchTopPersonnages, type SpectacleWithDuration, type PersonnageWithActivity } from '@/infrastructure/api/statApi';
import { useNotificationsStore } from './notificationsStore';

export const useStatisticsStore = defineStore('statistics', () => {
  const spectacles = ref<SpectacleWithDuration[]>([]);
  const personnages = ref<PersonnageWithActivity[]>([]);
  // états de chargement séparés pour éviter que l'update d'une section impacte l'autre
  const loadingSpectacles = ref(false);
  const loadingPersonnages = ref(false);
  const error = ref<string | null>(null);

  const notify = useNotificationsStore();

  const loadSpectacles = async (day?: number) => {
    loadingSpectacles.value = true;
    error.value = null;
    try {
      const data = await fetchSpectaclesRanking(day);
      spectacles.value = data ?? [];
    } catch (err: any) {
      error.value = err?.message ?? 'Impossible de charger les statistiques des spectacles';
      notify.error(error.value);
    } finally {
      loadingSpectacles.value = false;
    }
  };

  const loadTopPersonnages = async (limit = 5) => {
    loadingPersonnages.value = true;
    error.value = null;
    try {
      const data = await fetchTopPersonnages(limit);
      personnages.value = data ?? [];
    } catch (err: any) {
      error.value = err?.message ?? 'Impossible de charger les personnages';
      notify.error(error.value);
    } finally {
      loadingPersonnages.value = false;
    }
  };

  return {
    spectacles,
    personnages,
    loadingSpectacles,
    loadingPersonnages,
    error,
    loadSpectacles,
    loadTopPersonnages
  };
});
