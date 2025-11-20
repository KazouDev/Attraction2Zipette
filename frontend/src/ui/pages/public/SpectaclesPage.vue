<template>
  <section class="page">
    <header class="page__header">
      <div>
        <h1>Spectacles</h1>
        <p>Découvrez les animations et préparez votre visite grâce aux horaires détaillés.</p>
      </div>
      <div class="page__filters">
        <select v-model="selectedLieu" class="page__select">
          <option value="all">Tous les lieux</option>
          <option v-for="lieu in lieuStore.items" :key="lieu.id" :value="lieu.id">
            {{ lieu.nom }}
          </option>
        </select>
      </div>
    </header>

    <LoaderOverlay :visible="spectacleStore.loading" />

    <section v-if="filteredSpectacles.length > 0" class="grid">
      <article v-for="spectacle in filteredSpectacles" :key="spectacle.id" class="card">
        <header class="card__header">
          <div>
            <h2>{{ spectacle.titre }}</h2>
            <p class="card__subtitle"><mdicon name="map-marker" /> {{ spectacle.lieu.nom }}</p>
          </div>
        </header>

        <section class="card__schedule">
          <h3>Programmations</h3>
          <ul>
            <li v-for="programmation in sortedProgrammations(spectacle.programmations)" :key="`${spectacle.id}-${programmation.jourSemaine}-${programmation.heureOuverture}`">
              <strong>{{ readDayLabel(programmation.jourSemaine) }}</strong>
              <span>{{ summarizeTimeRange(programmation.heureOuverture, programmation.heureFermeture) }}</span>
            </li>
          </ul>
        </section>

        <section v-if="spectacle.personnages.length" class="card__people">
          <h3>Personnages présents</h3>
          <ul>
            <li v-for="personnage in spectacle.personnages" :key="personnage.id">
              {{ personnage.nom }}
            </li>
          </ul>
        </section>
      </article>
    </section>

    <p v-else-if="!spectacleStore.loading" class="page__empty">Aucun spectacle à afficher.</p>
  </section>
</template>

<script setup lang="ts">
import { useLieuStore } from '@/application/stores/lieuStore';
import { useSpectacleStore } from '@/application/stores/spectacleStore';
import { useNotificationsStore } from '@/application/stores/notificationsStore';
import { DAY_ENUM_TO_NUMBER, readDayLabel } from '@/constants/days';
import type { Programmation } from '@/domain/entities/Programmation';
import type { Spectacle } from '@/domain/entities/Spectacle';
import LoaderOverlay from '@/ui/components/LoaderOverlay.vue';
import { summarizeTimeRange } from '@/utils/time';
import { computed, onMounted, ref, watch } from 'vue';

const spectacleStore = useSpectacleStore();
const lieuStore = useLieuStore();
const notify = useNotificationsStore();

const selectedLieu = ref<'all' | number>('all');

onMounted(async () => {
  await Promise.all([spectacleStore.fetchAll(), lieuStore.fetchAll()]);
});

// Toast auto sur erreurs serveur
const serverError = computed(() => spectacleStore.error || lieuStore.error);
watch(serverError, (val, prev) => {
  if (val && val !== prev) notify.error(val, 15000);
});

const filteredSpectacles = computed(() => {
  if (selectedLieu.value === 'all') {
    return spectacleStore.items;
  }
  return spectacleStore.items.filter((spectacle: Spectacle) => spectacle.lieu.id === selectedLieu.value);
});

const sortedProgrammations = (programmations: Programmation[]): Programmation[] => {
  return programmations.slice().sort((a, b) => {
    const aOrder = DAY_ENUM_TO_NUMBER[a.jourSemaine] ?? 0;
    const bOrder = DAY_ENUM_TO_NUMBER[b.jourSemaine] ?? 0;
    if (aOrder === bOrder) {
      return a.heureOuverture.localeCompare(b.heureOuverture);
    }
    return aOrder - bOrder;
  });
};
</script>

<style scoped>
.page {
  display: grid;
  gap: 1.75rem;
}

.page__header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 1.5rem;
}

.page__header h1 {
  margin: 0;
  font-size: clamp(1.8rem, 3vw, 2.4rem);
}

.page__filters {
  display: flex;
  gap: 1rem;
}

.page__select {
  border: 1px solid rgba(148, 163, 184, 0.4);
  border-radius: 999px;
  padding: 0.6rem 1.2rem;
}

.grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.card {
  display: grid;
  gap: 1.1rem;
  padding: 1.4rem;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 1.2rem;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 22px 48px -32px rgba(15, 23, 42, 0.35);
}

.card__header h2 {
  margin: 0;
  font-size: 1.3rem;
}

.card__subtitle {
  margin: 0.15rem 0 0;
  color: #64748b;
  font-size: 0.95rem;
}

.card__schedule h3,
.card__people h3 {
  margin: 0 0 0.75rem;
  font-size: 1rem;
}

.card__schedule ul,
.card__people ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 0.5rem;
}

.card__schedule li {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  color: #475569;
  font-size: 0.95rem;
}

.card__schedule strong {
  color: #1f2937;
}

.card__people li {
  color: #1f2937;
}

.page__empty {
  text-align: center;
  color: #64748b;
}
</style>
