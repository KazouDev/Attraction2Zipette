<template>
  <section class="admin">
    <header class="admin__header">
      <div>
        <h1>Gestion des attractions</h1>
        <p>Créez, mettez à jour et planifiez les horaires des attractions du parc.</p>
      </div>
      <div class="admin__actions">
        <button class="btn" type="button" @click="prepareCreation">Nouvelle attraction</button>
        <button class="btn btn--ghost" type="button" @click="refreshAll" :disabled="loadingAny">
          Rafraîchir
        </button>
      </div>
    </header>

    <LoaderOverlay :visible="loadingAny" />

    <section class="admin__content">
      <aside class="admin__sidebar">
        <ul class="list">
          <li
            v-for="attraction in attractionStore.items"
            :key="attraction.id"
            :class="['list__item', { 'list__item--active': attraction.id === selectedId }]"
            @click="selectAttraction(attraction.id)"
          >
            <div>
              <strong>{{ attraction.nom }}</strong>
              <p>{{ attraction.type.nom }}</p>
            </div>
            <span class="list__badge">{{ attraction.horaireOuvertures.length }} créneau(x)</span>
          </li>
        </ul>
      </aside>

      <section class="admin__panel">
        <AttractionForm
          v-model="formModel"
          :types="typeStore.items"
          :submitting="savingDetails"
          @submit="handleSubmit"
        >
          <template #submit-label>
            {{ selectedId ? 'Mettre à jour' : 'Créer' }}
          </template>
          <template v-if="selectedId" #secondary-action>
            <button class="btn btn--danger" type="button" :disabled="savingDetails" @click="handleDelete">
              Supprimer
            </button>
          </template>
        </AttractionForm>

        <HoraireListEditor
          v-model="horairesModel"
          class="admin__schedule"
          :disabled="!selectedId || savingHoraires"
        />

        <div class="admin__footer">
          <button
            class="btn btn--primary"
            type="button"
            :disabled="!selectedId || savingHoraires"
            @click="handleHorairesSave"
          >
            Enregistrer les horaires
          </button>
        </div>
      </section>
    </section>
  </section>
</template>

<script setup lang="ts">
import { useAttractionStore } from '@/application/stores/attractionStore';
import { useAttractionTypeStore } from '@/application/stores/attractionTypeStore';
import { DAY_ENUM_TO_NUMBER, type DayNumber } from '@/constants/days';
import type { Attraction } from '@/domain/entities/Attraction';
import type { Horaire } from '@/domain/entities/Horaire';
import AttractionForm from '@/ui/components/forms/AttractionForm.vue';
import HoraireListEditor from '@/ui/components/forms/HoraireListEditor.vue';
import LoaderOverlay from '@/ui/components/LoaderOverlay.vue';
import { computed, onMounted, ref } from 'vue';

interface AttractionFormState {
  nom: string;
  typeId: number | null;
  tailleMin: number | null;
  tailleMinAdulte: number | null;
}

interface HoraireFormState {
  jourSemaine: DayNumber;
  heureOuverture: string;
  heureFermeture: string;
}

const attractionStore = useAttractionStore();
const typeStore = useAttractionTypeStore();

const selectedId = ref<number | null>(null);
const savingDetails = ref(false);
const savingHoraires = ref(false);

const formModel = ref<AttractionFormState>(createEmptyForm());
const horairesModel = ref<HoraireFormState[]>([]);

const loadingAny = computed(() => attractionStore.loading || typeStore.loading);

onMounted(async () => {
  await Promise.all([attractionStore.fetchAll(), typeStore.fetchAll()]);
});

const prepareCreation = () => {
  selectedId.value = null;
  formModel.value = createEmptyForm();
  horairesModel.value = [];
};

const refreshAll = async () => {
  await Promise.all([attractionStore.fetchAll(true), typeStore.fetchAll(true)]);
};

const selectAttraction = (id: number) => {
  const attraction = attractionStore.byId(id);
  if (!attraction) {
    return;
  }
  selectedId.value = id;
  formModel.value = toFormState(attraction);
  horairesModel.value = toHoraireState(attraction.horaireOuvertures);
};

const handleSubmit = async (payload: AttractionFormState) => {
  savingDetails.value = true;
  try {
    if (selectedId.value) {
      await attractionStore.update(selectedId.value, normalizeUpdatePayload(payload));
    } else {
      const createdId = await attractionStore.create(normalizeCreatePayload(payload));
      if (createdId) {
        selectedId.value = createdId;
        const freshlyCreated = attractionStore.byId(createdId);
        if (freshlyCreated) {
          selectAttraction(freshlyCreated.id);
        }
      }
    }
  } finally {
    savingDetails.value = false;
  }
};

const handleDelete = async () => {
  if (!selectedId.value) {
    return;
  }
  const confirmed = window.confirm('Supprimer cette attraction ?');
  if (!confirmed) {
    return;
  }
  const success = await attractionStore.remove(selectedId.value);
  if (success) {
    prepareCreation();
  }
};

const handleHorairesSave = async () => {
  if (!selectedId.value) {
    return;
  }
  savingHoraires.value = true;
  try {
    await attractionStore.updateHoraires(
      selectedId.value,
      horairesModel.value.map((horaire: HoraireFormState) => ({
        jourSemaine: horaire.jourSemaine,
        heureOuverture: horaire.heureOuverture,
        heureFermeture: horaire.heureFermeture
      }))
    );
    const refreshed = attractionStore.byId(selectedId.value);
    if (refreshed) {
      horairesModel.value = toHoraireState(refreshed.horaireOuvertures);
    }
  } finally {
    savingHoraires.value = false;
  }
};

function createEmptyForm(): AttractionFormState {
  return {
    nom: '',
    typeId: null,
    tailleMin: 120,
    tailleMinAdulte: 90
  };
}

function normalizeCreatePayload(payload: AttractionFormState) {
  return {
    nom: payload.nom,
    typeId: payload.typeId ?? 0,
    tailleMin: payload.tailleMin ?? 0,
    tailleMinAdulte: payload.tailleMinAdulte ?? 0
  };
}

function normalizeUpdatePayload(payload: AttractionFormState) {
  return {
    nom: payload.nom,
    typeId: payload.typeId ?? undefined,
    tailleMin: payload.tailleMin ?? undefined,
    tailleMinAdulte: payload.tailleMinAdulte ?? undefined
  };
}

function toFormState(attraction: Attraction): AttractionFormState {
  return {
    nom: attraction.nom,
    typeId: attraction.type?.id ?? null,
    tailleMin: attraction.tailleMin ?? null,
    tailleMinAdulte: attraction.tailleMinAdulte ?? null
  };
}

function toHoraireState(horaires: Horaire[]): HoraireFormState[] {
  return horaires.map((horaire) => ({
    jourSemaine: toDayNumber(horaire.jourSemaine),
    heureOuverture: trimTime(horaire.heureOuverture),
    heureFermeture: trimTime(horaire.heureFermeture)
  }));
}

function toDayNumber(value: string | number): DayNumber {
  if (typeof value === 'number') {
    return (value % 7) as DayNumber;
  }
  const upper = value.toUpperCase();
  const mapped = DAY_ENUM_TO_NUMBER[upper];
  return (mapped ?? 0) as DayNumber;
}

function trimTime(value: string): string {
  if (!value) {
    return '00:00';
  }
  if (value.length >= 5) {
    return value.slice(0, 5);
  }
  return value;
}
</script>

<style scoped>
.admin {
  display: grid;
  gap: 1.5rem;
  max-width: 100%;
  overflow-x: hidden;
}

.admin__header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 1.5rem;
}

.admin__actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.admin__content {
  display: grid;
  grid-template-columns: minmax(280px, 340px) 1fr;
  gap: 1.75rem;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.admin__sidebar {
  padding: 1rem;
  border-radius: 1rem;
  background: rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.3);
  max-height: 70vh;
  overflow-y: auto;
  min-width: 0;
}

.list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 0.75rem;
}

.list__item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  border-radius: 0.85rem;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid transparent;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.list__item--active {
  border-color: rgba(59, 130, 246, 0.4);
  background: rgba(59, 130, 246, 0.08);
}

.list__item strong {
  display: block;
  margin-bottom: 0.25rem;
}

.list__item p {
  margin: 0;
  color: #64748b;
  font-size: 0.85rem;
}

.list__badge {
  font-size: 0.8rem;
  color: #475569;
}

.admin__panel {
  display: grid;
  gap: 1.75rem;
  padding: 2rem;
  border-radius: 1.2rem;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  width: 100%;
  min-width: 0;
  max-width: 900px;
}

.admin__schedule {
  border-top: 1px solid rgba(226, 232, 240, 0.8);
  padding-top: 1rem;
}

.admin__footer {
  display: flex;
  justify-content: flex-end;
}

.btn {
  border: none;
  border-radius: 0.5rem;
  padding: 0.65rem 1.4rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  height: 40px;
}

.btn--primary {
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  color: #fff;
}

.btn--primary:hover:not(:disabled) {
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
}

.btn--danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: #fff;
}

.btn--danger:hover:not(:disabled) {
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

.btn--ghost {
  background: none;
  border: 1px solid rgba(148, 163, 184, 0.4);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 900px) {
  .admin__content {
    grid-template-columns: 1fr;
  }
}
</style>
