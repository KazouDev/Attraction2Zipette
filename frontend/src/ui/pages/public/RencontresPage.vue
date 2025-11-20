<template>
  <section class="rencontres">
    <header class="rencontres__header">
      <h1>Rencontres avec les personnages</h1>
      <p>Découvrez les horaires et lieux des rencontres avec vos personnages préférés !</p>
    </header>

    <!-- ErrorMessage supprimé, erreurs affichées via toasts -->
    <LoaderOverlay :visible="loadingAny" />

    <section class="rencontres__content">
      <ul class="rencontres-list">
        <li v-for="rencontre in rencontreStore.items" :key="rencontre.id" class="rencontre-card">
          <div class="rencontre-card__header">
            <h2 class="rencontre-card__title">{{ rencontre.personnage.nom }}</h2>
            <span class="rencontre-card__day">{{ getDayLabel(rencontre.jourSemaine) }}</span>
          </div>
          <div class="rencontre-card__body">
            <div class="rencontre-card__info">
              <span class="rencontre-card__label"><mdicon name="map-marker" /> Lieu</span>
              <span class="rencontre-card__value">{{ rencontre.lieu.nom }}</span>
            </div>
            <div class="rencontre-card__info">
              <span class="rencontre-card__label"><mdicon name="clock-outline" /> Horaires</span>
              <span class="rencontre-card__value">
                {{ formatTime(rencontre.heureDebut) }} → {{ formatTime(rencontre.heureFin) }}
              </span>
            </div>
          </div>
        </li>
      </ul>
    </section>
  </section>
</template>

<script setup lang="ts">
import { useRencontrePersonnageStore } from '@/application/stores/rencontrePersonnageStore';
import { usePersonnageStore } from '@/application/stores/personnageStore';
import { useLieuStore } from '@/application/stores/lieuStore';
import { useNotificationsStore } from '@/application/stores/notificationsStore';
import { DAY_LABEL_BY_NUMBER, type DayNumber } from '@/constants/days';
import LoaderOverlay from '@/ui/components/LoaderOverlay.vue';
import { computed, onMounted, watch } from 'vue';

const rencontreStore = useRencontrePersonnageStore();
const personnageStore = usePersonnageStore();
const lieuStore = useLieuStore();
const notify = useNotificationsStore();

const loadingAny = computed(() =>
  rencontreStore.loading || personnageStore.loading || lieuStore.loading
);

onMounted(async () => {
  await Promise.all([
    rencontreStore.fetchAll(),
    personnageStore.fetchAll(),
    lieuStore.fetchAll()
  ]);
});

// Toast auto sur erreurs serveur
const serverError = computed(() => rencontreStore.error || personnageStore.error || lieuStore.error);
watch(serverError, (val, prev) => {
  if (val && val !== prev) notify.error(val, 15000);
});

const getDayLabel = (jour: string | number): string => {
  const dayNum = typeof jour === 'string' ? parseInt(jour) : jour;
  return DAY_LABEL_BY_NUMBER[dayNum as DayNumber] || jour.toString();
};

const formatTime = (time: string): string => {
  if (!time) return '';
  return time.substring(0, 5);
};
</script>

<style scoped>
.rencontres {
  display: grid;
  gap: 1.5rem;
}

.rencontres__header {
  text-align: center;
  margin-bottom: 2rem;
}

.rencontres__header h1 {
  font-size: 2rem;
  color: #1f2937;
}

.rencontres__header p {
  font-size: 1.1rem;
  color: #475569;
}

.rencontres__content {
  display: grid;
  gap: 1.5rem;
}

.rencontres-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 1rem;
}

.rencontre-card {
  background: #fff;
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 0.75rem;
  padding: 1rem;
  transition: all 0.2s;
}

.rencontre-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.rencontre-card__title {
  font-size: 1.25rem;
  color: #1f2937;
  font-weight: 600;
}

.rencontre-card__day {
  font-weight: 600;
  color: #1f2937;
  font-size: 1.1rem;
}

.rencontre-card__body {
  display: grid;
  gap: 0.5rem;
}

.rencontre-card__info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rencontre-card__label {
  color: #64748b;
  font-size: 0.9rem;
}

.rencontre-card__value {
  color: #1f2937;
  font-weight: 500;
}
</style>
