<template>
  <section class="page">
    <header class="page__header">
      <div>
        <h1>Attractions</h1>
        <p>Consultez les caractéristiques et temps d'attente avant de rejoindre la file.</p>
      </div>
      <div class="page__filters">
        <select v-model="selectedType" class="page__select">
          <option value="all">Tous les types</option>
          <option v-for="type in typeStore.items" :key="type.id" :value="type.id">
            {{ type.nom }}
          </option>
        </select>
      </div>
    </header>

    <LoaderOverlay :visible="attractionStore.loading" />

    <section v-if="filteredAttractions.length > 0" class="grid">
      <article v-for="attraction in filteredAttractions" :key="attraction.id" class="card">
        <div class="crowds">
          <dd>
              <span class="card__wait" :class="waitClass(attraction.tempsAttente ?? 0)" v-if="isOpen(attraction)">
                {{ attraction.tempsAttente ?? 0 }} min d'attente
              </span>
            <span class="card__wait card__wait--high" v-else>
                Fermé
              </span>

          </dd>
        </div>

        <header class="card__header">
          <h2>{{ attraction.nom }}</h2>
          <span class="card__badge">{{ attraction.type.nom }}</span>
        </header>

        <dl class="card__stats">
          <div>
            <dt>Taille minimale</dt>
            <dd>{{ attraction.tailleMin }} cm</dd>
          </div>
          <div>
            <dt>À partir de</dt>
            <dd>{{ attraction.tailleMinAdulte }} cm</dd>
          </div>
        </dl>

        <section class="card__schedule">
          <h3>Horaires</h3>
          <ul>
            <li v-for="horaire in attraction.horaireOuvertures" :key="`${attraction.id}-${horaire.jourSemaine}`">
              <strong>{{ readDayLabel(horaire.jourSemaine) }}</strong>
              <span>{{ summarizeTimeRange(horaire.heureOuverture, horaire.heureFermeture) }}</span>
            </li>
          </ul>
        </section>
      </article>
    </section>

    <p v-else-if="!attractionStore.loading" class="page__empty">
      Aucune attraction à afficher.
    </p>
  </section>
</template>

<script setup lang="ts">
import { useAttractionStore } from '@/application/stores/attractionStore';
import { useAttractionTypeStore } from '@/application/stores/attractionTypeStore';
import { useNotificationsStore } from '@/application/stores/notificationsStore';
import { readDayLabel } from '@/constants/days';
import type { Attraction } from '@/domain/entities/Attraction';
import LoaderOverlay from '@/ui/components/LoaderOverlay.vue';
import { summarizeTimeRange } from '@/utils/time';
import { computed, onMounted, ref, watch } from 'vue';
import { JOUR_NUMBER_TO_NAME} from "@/domain/entities/Horaire";

const attractionStore = useAttractionStore();
const typeStore = useAttractionTypeStore();
const notify = useNotificationsStore();

const selectedType = ref<'all' | number>('all');

onMounted(async () => {
  await Promise.all([attractionStore.fetchAll(), typeStore.fetchAll()]);
});

// Toast auto sur erreurs serveur
const serverError = computed(() => attractionStore.error || typeStore.error);
watch(serverError, (val, prev) => {
  if (val && val !== prev) notify.error(val, 15000);
});

const filteredAttractions = computed(() => {
  if (selectedType.value === 'all') {
    return attractionStore.items;
  }
  return attractionStore.items.filter((attraction: Attraction) => attraction.type.id === selectedType.value);
});

const waitClass = (minutes: number) => {
  if (minutes < 15) return 'card__wait--low';
  if (minutes < 30) return 'card__wait--medium';
  return 'card__wait--high';
};

const isOpen = (attraction: Attraction) => {
  const now = new Date();
  const dayOfWeek = now.getDay();
  const currentTime = now.getHours() * 60 + now.getMinutes();

  return attraction.horaireOuvertures.filter(horaire => {
    if (horaire.jourSemaine.toUpperCase() !== JOUR_NUMBER_TO_NAME[dayOfWeek].toUpperCase()) return false;
    const ouvertureParts = horaire.heureOuverture.split(':').map(Number);
    const fermetureParts = horaire.heureFermeture.split(':').map(Number);
    const ouvertureTime = ouvertureParts[0] * 60 + ouvertureParts[1];
    const fermetureTime = fermetureParts[0] * 60 + fermetureParts[1];

    console.log(horaire, currentTime >= ouvertureTime && currentTime <= fermetureTime);

    return currentTime >= ouvertureTime && currentTime <= fermetureTime;
  }).length > 0;
}
</script>

<style scoped>
.page {
  display: grid;
  gap: 1.75rem;
}
.card {
  position: relative;
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
  display: flex;
  flex-direction: column;
  gap: 1.1rem;

  padding: 1.4rem;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 1.2rem;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 22px 48px -32px rgba(15, 23, 42, 0.35);
}

.card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.card__header h2 {
  margin: 0;
  font-size: 1.3rem;
}

.card__badge {
  padding: 0.35rem 0.8rem;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.15);
  color: #2563eb;
  font-weight: 600;
}

.card__stats {
  display: grid;
  gap: 0.8rem;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  margin: 0;
  justify-items: center;

  div {
    width: fit-content;
  }
}

.card__stats dt {
  font-size: 0.85rem;
  color: #64748b;
}

.card__stats dd {
  margin: 0.15rem 0 0;
  font-size: 1.05rem;
  font-weight: 600;
  color: #1e293b;
}

.card__wait {
  padding: 0.25rem 0.65rem;
  border-radius: 100px;
  font-size: 0.95rem;
  font-weight: 600;

  display: block;
  text-align: center;

  position: absolute;
  top: -.5em;
  left: 50%;
  transform: translateX(-50%);
}

.card__wait--high {
  background: rgba(248, 113, 113, 0.3);
  color: #b91c1c;
}
.card__wait--low {
  background: rgba(34, 197, 94, 0.15);
  color: #15803d;
}
.card__wait--medium {
  background: rgba(250, 204, 21, 0.2);
  color: #b45309;
}

.card__schedule h3 {
  margin: 0 0 0.75rem;
  font-size: 1rem;
}

.card__schedule ul {
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

.page__empty {
  text-align: center;
  color: #64748b;
}
</style>
