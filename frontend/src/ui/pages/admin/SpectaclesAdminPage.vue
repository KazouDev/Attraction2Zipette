<template>
  <section class="admin">
    <header class="admin__header">
      <div>
        <h1>Gestion des spectacles</h1>
        <p>Organisez les animations, lieux et programmations hebdomadaires.</p>
      </div>
      <div class="admin__actions">
        <button class="btn" type="button" @click="prepareCreation">Nouveau spectacle</button>
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
            v-for="spectacle in spectacleStore.items"
            :key="spectacle.id"
            :class="['list__item', { 'list__item--active': spectacle.id === selectedId }]"
            @click="selectSpectacle(spectacle.id)"
          >
            <div>
              <strong>{{ spectacle.titre }}</strong>
              <p>{{ spectacle.lieu.nom }}</p>
            </div>
            <span class="list__badge">{{ spectacle.programmations.length }} session(s)</span>
          </li>
        </ul>
      </aside>

      <section class="admin__panel">
        <SpectacleForm
          v-model="formModel"
          :lieux="lieuStore.items"
          :submitting="savingDetails"
          @submit="handleSubmit"
        >
          <template #submit-label>
            {{ selectedId ? 'Mettre à jour' : 'Créer' }}
          </template>
        </SpectacleForm>

        <HoraireListEditor
          v-model="programmationsModel"
          class="admin__schedule"
          :disabled="!selectedId || savingProgrammations"
          :include-seconds="true"
          title="Programmations"
        />

        <PersonnageListEditor
          :selected-personnages="selectedSpectaclePersonnages"
          :all-personnages="personnageStore.items"
          :disabled="!selectedId || savingPersonnages"
          @add="handleAddPersonnage"
          @remove="handleRemovePersonnage"
          class="admin__characters"
        />

        <div class="admin__footer">
          <button
            class="btn btn--primary"
            type="button"
            :disabled="!selectedId || savingProgrammations"
            @click="handleProgrammationsSave"
          >
            Enregistrer les programmations
          </button>
        </div>
      </section>
    </section>
  </section>
</template>

<script setup lang="ts">
import { useLieuStore } from '@/application/stores/lieuStore';
import { useSpectacleStore } from '@/application/stores/spectacleStore';
import { usePersonnageStore } from '@/application/stores/personnageStore';
import { useNotificationsStore } from '@/application/stores/notificationsStore';
import { DAY_ENUM_TO_NUMBER, type DayNumber } from '@/constants/days';
import type { Programmation } from '@/domain/entities/Programmation';
import type { Spectacle } from '@/domain/entities/Spectacle';
import HoraireListEditor from '@/ui/components/forms/HoraireListEditor.vue';
import SpectacleForm from '@/ui/components/forms/SpectacleForm.vue';
import PersonnageListEditor from '@/ui/components/forms/PersonnageListEditor.vue';
import LoaderOverlay from '@/ui/components/LoaderOverlay.vue';
import { computed, onMounted, ref, watch } from 'vue';

interface SpectacleFormState {
  titre: string;
  lieuId: number | null;
}

interface ProgrammationFormState {
  jourSemaine: DayNumber;
  heureOuverture: string;
  heureFermeture: string;
}

const spectacleStore = useSpectacleStore();
const lieuStore = useLieuStore();
const personnageStore = usePersonnageStore();
const notify = useNotificationsStore();

const selectedId = ref<number | null>(null);
const savingDetails = ref(false);
const savingProgrammations = ref(false);
const savingPersonnages = ref(false);

const formModel = ref<SpectacleFormState>(createEmptyForm());
const programmationsModel = ref<ProgrammationFormState[]>([]);
const personnagesModel = ref<number[]>([]);

const loadingAny = computed(() => spectacleStore.loading || lieuStore.loading || personnageStore.loading);

const selectedSpectaclePersonnages = computed(() => {
  if (!selectedId.value) return [];
  const spectacle = spectacleStore.byId(selectedId.value);
  return spectacle?.personnages ?? [];
});

onMounted(async () => {
  await Promise.all([
    spectacleStore.fetchAll(),
    lieuStore.fetchAll(),
    personnageStore.fetchAll()
  ]);
});

const prepareCreation = () => {
  selectedId.value = null;
  formModel.value = createEmptyForm();
  programmationsModel.value = [];
  personnagesModel.value = [];
};

const refreshAll = async () => {
  await Promise.all([
    spectacleStore.fetchAll(true),
    lieuStore.fetchAll(true),
    personnageStore.fetchAll(true)
  ]);
};

const selectSpectacle = (id: number) => {
  const spectacle = spectacleStore.byId(id);
  if (!spectacle) {
    return;
  }
  selectedId.value = id;
  formModel.value = toFormState(spectacle);
  programmationsModel.value = toProgrammationState(spectacle.programmations);
  personnagesModel.value = spectacle.personnages.map(personnage => personnage.id);
};

const handleSubmit = async (payload: SpectacleFormState) => {
  savingDetails.value = true;
  try {
    if (selectedId.value) {
      const res = await spectacleStore.update(selectedId.value, normalizeUpdatePayload(payload));
      if (res) {
        notify.success('Spectacle mis à jour avec succès', 8000);
      }
    } else {
      const createdId = await spectacleStore.create(normalizeCreatePayload(payload));
      if (createdId) {
        selectedId.value = createdId;
        const freshlyCreated = spectacleStore.byId(createdId);
        if (freshlyCreated) {
          selectSpectacle(freshlyCreated.id);
        }
        notify.success('Spectacle créé avec succès', 10000);
      }
    }
  } finally {
    savingDetails.value = false;
  }
};

const handleProgrammationsSave = async () => {
  if (!selectedId.value) {
    return;
  }
  savingProgrammations.value = true;
  try {
    const res = await spectacleStore.setProgrammations(
      selectedId.value,
      programmationsModel.value.map((programmation: ProgrammationFormState) => ({
        jourSemaine: programmation.jourSemaine,
        heureOuverture: ensureSeconds(programmation.heureOuverture),
        heureFermeture: ensureSeconds(programmation.heureFermeture)
      }))
    );
    if (res) {
      const refreshed = spectacleStore.byId(selectedId.value);
      if (refreshed) {
        programmationsModel.value = toProgrammationState(refreshed.programmations);
      }
      notify.success('Programmations enregistrées', 8000);
    }
    // Sinon, erreur gérée par serverError watcher
  } finally {
    savingProgrammations.value = false;
  }
};

const handleAddPersonnage = async (personnageId: number) => {
  if (!selectedId.value) return;

  savingPersonnages.value = true;
  try {
    const res = await spectacleStore.addPersonnage(selectedId.value, personnageId);
    if (res) {
      const refreshed = spectacleStore.byId(selectedId.value);
      if (refreshed) {
        personnagesModel.value = refreshed.personnages.map(p => p.id);
      }
      notify.success('Personnage ajouté au spectacle', 8000);
    }
  } finally {
    savingPersonnages.value = false;
  }
};

const handleRemovePersonnage = async (personnageId: number) => {
  if (!selectedId.value) return;

  savingPersonnages.value = true;
  try {
    const res = await spectacleStore.removePersonnage(selectedId.value, personnageId);
    if (res) {
      const refreshed = spectacleStore.byId(selectedId.value);
      if (refreshed) {
        personnagesModel.value = refreshed.personnages.map(p => p.id);
      }
      notify.info('Personnage retiré du spectacle', 7000);
    }
  } finally {
    savingPersonnages.value = false;
  }
};

function createEmptyForm(): SpectacleFormState {
  return {
    titre: '',
    lieuId: null
  };
}

function normalizeCreatePayload(payload: SpectacleFormState) {
  return {
    titre: payload.titre,
    lieuId: payload.lieuId ?? 0
  };
}

function normalizeUpdatePayload(payload: SpectacleFormState) {
  return {
    titre: payload.titre,
    lieuId: payload.lieuId ?? undefined
  };
}

function toFormState(spectacle: Spectacle): SpectacleFormState {
  return {
    titre: spectacle.titre,
    lieuId: spectacle.lieu?.id ?? null
  };
}

function toProgrammationState(programmations: Programmation[]): ProgrammationFormState[] {
  return programmations.map((programmation) => ({
    jourSemaine: toDayNumber(programmation.jourSemaine),
    heureOuverture: ensureSeconds(programmation.heureOuverture),
    heureFermeture: ensureSeconds(programmation.heureFermeture)
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

function ensureSeconds(value: string): string {
  if (!value) {
    return '00:00:00';
  }
  if (value.length === 5) {
    return `${value}:00`;
  }
  if (value.length >= 8) {
    return value.slice(0, 8);
  }
  return value;
}
</script>

<style scoped>
.admin {
  display: grid;
  gap: 1.5rem;
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
  grid-template-columns: minmax(220px, 320px) 1fr;
  gap: 1.75rem;
}

.admin__sidebar {
  padding: 1rem;
  border-radius: 1rem;
  background: rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.3);
  max-height: 70vh;
  overflow-y: auto;
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
  padding: 1.5rem;
  border-radius: 1.2rem;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(226, 232, 240, 0.9);
}

.admin__schedule {
  border-top: 1px solid rgba(226, 232, 240, 0.8);
  padding-top: 1rem;
}

.admin__characters {
  border-top: 1px solid rgba(226, 232, 240, 0.8);
  padding-top: 1rem;
}

.admin__footer {
  display: flex;
  justify-content: flex-end;
}

.btn {
  border: none;
  border-radius: 999px;
  padding: 0.65rem 1.4rem;
  font-weight: 600;
  cursor: pointer;
  background: rgba(148, 163, 184, 0.2);
}

.btn--primary {
  background: linear-gradient(135deg, #22c55e, #10b981);
  color: #fff;
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
