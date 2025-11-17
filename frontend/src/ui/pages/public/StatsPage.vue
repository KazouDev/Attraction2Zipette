<template>
  <section class="stats-page">
    <header class="stats__header">
      <h1>Statistiques</h1>
      <div class="controls">
        <label for="day-select">Jour :</label>
        <select id="day-select" class="day-select" v-model.number="selectedDay" @change="onDayChange">
          <option :value="-1">Tous</option>
          <option v-for="d in DAY_NUMBERS" :key="d" :value="d">{{ d + 1 }} - {{ readDayLabel(d) }}</option>
        </select>
      </div>
    </header>

    <main>
      <section class="stats__block">
        <h2>Classement par durée</h2>

        <div v-if="store.loadingSpectacles" class="muted">Chargement...</div>
        <div v-else>
          <template v-if="selectedDay === -1">
            <div v-if="groupedByDay.length === 0" class="muted small">Aucun Spectacle</div>
            <div v-else class="days-grid">
              <div v-for="block in groupedByDay" :key="block.day" class="day-card">
                <div class="day-card__header">
                  <div class="day-badge">{{ block.day + 1 }}</div>
                  <div class="day-title">{{ readDayLabel(block.day) }}</div>
                </div>
                <div v-if="block.items.length === 0" class="muted small">Aucun Spectacle</div>
                <table v-else class="table small-table">
                  <thead>
                    <tr>
                      <th class="col-rank">#</th>
                      <th>Spectacle</th>
                      <th>Durée</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(row, idx) in block.items" :key="`${row.spectacleId}-${row.jourSemaine}-${row.durationMinutes}`">
                      <td class="col-rank">{{ idx + 1 }}</td>
                      <td>{{ row.spectacleTitre }}</td>
                      <td>{{ row.durationMinutes }} min</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </template>

          <template v-else>
            <div v-if="sortedSpectacles.length === 0" class="muted small">Aucun Spectacle</div>
            <table v-else class="table">
              <thead>
                <tr>
                  <th class="col-rank">#</th>
                  <th>Spectacle</th>
                  <th>Durée (minutes)</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, idx) in sortedSpectacles" :key="`${row.spectacleId}-${row.durationMinutes}`">
                  <td class="col-rank">{{ idx + 1 }}</td>
                  <td>{{ row.spectacleTitre }}</td>
                  <td>{{ row.durationMinutes }}</td>
                </tr>
              </tbody>
            </table>
          </template>
        </div>
      </section>

      <section class="stats__block">
        <h2>Top personnages (activité)</h2>
        <div v-if="store.loadingPersonnages" class="muted">Chargement...</div>
        <div v-else>
          <table class="table">
            <thead>
              <tr>
                <th>#</th>
                <th>Personnage</th>
                <th>Total (minutes)</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(p, idx) in store.personnages" :key="p.personnageId">
                <td>{{ idx + 1 }}</td>
                <td>{{ p.personnageNom }}</td>
                <td>{{ p.totalActivity }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </section>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue';
import { useStatisticsStore } from '@/application/stores/statisticsStore';
import { DAY_NUMBERS, DAY_LABEL_BY_NUMBER, readDayLabel } from '@/constants/days';
import type { SpectacleWithDuration } from '@/infrastructure/api/statApi';

const store = useStatisticsStore();
const selectedDay = ref<number>(-1);

const load = async () => {
  await store.loadTopPersonnages(5);
  await store.loadSpectacles(selectedDay.value === -1 ? undefined : selectedDay.value);
};

onMounted(() => {
  load();
});

const onDayChange = async () => {
  await store.loadSpectacles(selectedDay.value === -1 ? undefined : selectedDay.value);
};

// computed: spectacles triées globalement par durée décroissante
const sortedSpectacles = computed(() => {
  // clone pour ne pas muter la source
  return [...store.spectacles].sort((a, b) => (b.durationMinutes ?? 0) - (a.durationMinutes ?? 0));
});

// groupedByDay retourne un tableau de { day, items } pour simplifier l'itération dans le template
const groupedByDay = computed(() => {
  const map: Record<number, SpectacleWithDuration[]> = {};
  for (const row of sortedSpectacles.value) {
    const d = Number(row.jourSemaine);
    if (!map[d]) map[d] = [];
    map[d].push(row);
  }
  // transformer en tableau et trier par jour
  const blocks = Object.keys(map).map(k => ({ day: Number(k), items: map[Number(k)] }));
  blocks.sort((a, b) => a.day - b.day);
  return blocks as { day: number; items: SpectacleWithDuration[] }[];
});

</script>

<style scoped>
.stats-page { padding: 1.5rem; }
.stats__header { display:flex; justify-content:space-between; align-items:center; gap:1rem; }
.controls { display:flex; align-items:center; gap:0.5rem; }
.stats__block { margin-top:1.25rem; }
.table { width:100%; border-collapse:collapse; }
.table th, .table td { text-align:left; padding:0.5rem; border-bottom:1px solid #e5e7eb; }
.muted { color: #6b7280; }

/* Simple styling pour le select jour afin d'afficher clairement le numéro */
.day-select {
  padding: 0.35rem 0.6rem;
  border-radius: 6px;
  border: 1px solid #cbd5e1;
  background: #fff;
  color: #0f172a;
  font-weight: 500;
  min-width: 160px;
}
.day-select option { padding: 0.25rem 0.5rem; }

/* Day cards grid */
.days-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1rem;
}
.day-card {
  background: #ffffff;
  border: 1px solid #e6e9ef;
  padding: 0.75rem;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}
.day-card__header {
  display:flex;
  align-items:center;
  gap:0.5rem;
  margin-bottom:0.5rem;
}
.day-badge {
  background: linear-gradient(135deg,#f97316,#ef4444);
  color: white;
  font-weight:700;
  width:32px;
  height:32px;
  display:inline-flex;
  align-items:center;
  justify-content:center;
  border-radius:50%;
}
.day-title { font-weight:700; font-size:0.95rem; }
.small { font-size:0.9rem; }
.small-table td, .small-table th { padding:0.4rem; }
.col-rank { width:48px; text-align:center; font-weight:600; }

</style>
