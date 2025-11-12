<template>
  <section class="admin">
    <header class="admin__header">
      <div>
        <h1>Gestion des rencontres personnages</h1>
        <p>Planifiez les rencontres entre les visiteurs et les personnages du parc.</p>
      </div>
      <div class="admin__actions">
        <button class="btn" type="button" @click="showCreateDialog = true" :disabled="!selectedId">
          Nouvelle rencontre
        </button>
        <button class="btn btn--ghost" type="button" @click="refreshAll" :disabled="loadingAny">
          Rafraîchir
        </button>
      </div>
    </header>

    <!-- ErrorMessage supprimé, erreurs affichées via toasts -->
    <LoaderOverlay :visible="loadingAny" />

    <section class="admin__content">
      <aside class="admin__sidebar">
        <h3>Personnages</h3>
        <ul class="list">
          <li
            v-for="personnage in personnageStore.items"
            :key="personnage.id"
            :class="['list__item', { 'list__item--active': personnage.id === selectedId }]"
            @click="selectPersonnage(personnage.id)"
          >
            <div>
              <strong>{{ personnage.nom }}</strong>
            </div>
            <span class="list__badge">{{ getRencontresForPersonnage(personnage.id).length }}</span>
          </li>
        </ul>
      </aside>

      <!-- Liste des rencontres du personnage sélectionné -->
      <section class="admin__panel">
        <div v-if="!selectedId" class="admin__empty">
          <p>Sélectionnez un personnage pour voir ses rencontres</p>
        </div>

        <div v-else>
          <h2>Rencontres de {{ selectedPersonnage?.nom }}</h2>

          <div v-if="selectedRencontres.length === 0" class="admin__empty">
            <p>Aucune rencontre planifiée pour ce personnage.</p>
            <button class="btn btn--primary" type="button" @click="showCreateDialog = true">
              Créer une rencontre
            </button>
          </div>

          <ul v-else class="rencontres-list">
            <li v-for="rencontre in selectedRencontres" :key="rencontre.id" class="rencontre-card">
              <div class="rencontre-card__header">
                <span class="rencontre-card__day">{{ getDayLabel(rencontre.jourSemaine) }}</span>
                <button
                  class="rencontre-card__delete"
                  type="button"
                  @click="handleDelete(rencontre.id)"
                  :disabled="deleting"
                >
                  🗑️ Supprimer
                </button>
              </div>
              <div class="rencontre-card__body">
                <div class="rencontre-card__info">
                  <span class="rencontre-card__label">📍 Lieu</span>
                  <span class="rencontre-card__value">{{ rencontre.lieu.nom }}</span>
                </div>
                <div class="rencontre-card__info">
                  <span class="rencontre-card__label">🕐 Horaires</span>
                  <span class="rencontre-card__value">
                    {{ formatTime(rencontre.heureDebut) }} → {{ formatTime(rencontre.heureFin) }}
                  </span>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </section>
    </section>

    <!-- Dialog de création -->
    <div v-if="showCreateDialog" class="dialog-overlay" @click.self="showCreateDialog = false">
      <div class="dialog">
        <h3>Nouvelle rencontre pour {{ selectedPersonnage?.nom }}</h3>

        <form @submit.prevent="handleCreate" class="form">
          <div class="form__row">
            <label class="form__label" for="lieu">Lieu</label>
            <select
              id="lieu"
              v-model.number="createForm.lieuId"
              class="form__input"
              required
            >
              <option value="" disabled>Choisir un lieu</option>
              <option v-for="lieu in lieuStore.items" :key="lieu.id" :value="lieu.id">
                {{ lieu.nom }}
              </option>
            </select>
          </div>

          <div class="form__row">
            <label class="form__label" for="jour">Jour de la semaine</label>
            <select
              id="jour"
              v-model.number="createForm.jourSemaine"
              class="form__input"
              required
            >
              <option v-for="day in days" :key="day.value" :value="day.value">
                {{ day.label }}
              </option>
            </select>
          </div>

          <div class="form__grid">
            <div class="form__row">
              <label class="form__label" for="debut">Heure de début</label>
              <input
                id="debut"
                v-model="createForm.heureDebut"
                class="form__input"
                type="time"
                required
              />
            </div>

            <div class="form__row">
              <label class="form__label" for="fin">Heure de fin</label>
              <input
                id="fin"
                v-model="createForm.heureFin"
                class="form__input"
                type="time"
                required
              />
            </div>
          </div>

          <div class="dialog__actions">
            <button class="btn btn--ghost" type="button" @click="showCreateDialog = false">
              Annuler
            </button>
            <button class="btn btn--primary" type="submit" :disabled="creating">
              Créer
            </button>
          </div>
        </form>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { useRencontrePersonnageStore } from '@/application/stores/rencontrePersonnageStore';
import { usePersonnageStore } from '@/application/stores/personnageStore';
import { useLieuStore } from '@/application/stores/lieuStore';
import { useNotificationsStore } from '@/application/stores/notificationsStore';
import { DAY_LABEL_BY_NUMBER, DAY_NUMBERS, type DayNumber } from '@/constants/days';
import LoaderOverlay from '@/ui/components/LoaderOverlay.vue';
import { computed, onMounted, ref, watch } from 'vue';

const rencontreStore = useRencontrePersonnageStore();
const personnageStore = usePersonnageStore();
const lieuStore = useLieuStore();
const notify = useNotificationsStore();

const selectedId = ref<number | null>(null);
const showCreateDialog = ref(false);
const creating = ref(false);
const deleting = ref(false);

const createForm = ref({
  lieuId: null as number | null,
  jourSemaine: 0 as DayNumber,
  heureDebut: '10:00',
  heureFin: '10:30'
});

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

const selectedPersonnage = computed(() => {
  if (!selectedId.value) return null;
  return personnageStore.byId(selectedId.value);
});

const selectedRencontres = computed(() => {
  if (!selectedId.value) return [];
  return rencontreStore.items.filter(r => r.personnage.id === selectedId.value);
});

const days = computed(() =>
  DAY_NUMBERS.map((value) => ({ value, label: DAY_LABEL_BY_NUMBER[value] }))
);

const selectPersonnage = (id: number) => {
  selectedId.value = id;
};

const refreshAll = async () => {
  await Promise.all([
    rencontreStore.fetchAll(true),
    personnageStore.fetchAll(true),
    lieuStore.fetchAll(true)
  ]);
};

const getRencontresForPersonnage = (personnageId: number) => {
  return rencontreStore.items.filter(r => r.personnage.id === personnageId);
};

const handleCreate = async () => {
  if (!selectedId.value || !createForm.value.lieuId) return;

  creating.value = true;
  try {
    const success = await rencontreStore.create({
      personnageId: selectedId.value,
      lieuId: createForm.value.lieuId,
      jourSemaine: createForm.value.jourSemaine,
      heureOuverture: createForm.value.heureDebut,
      heureFermeture: createForm.value.heureFin
    });

    if (success) {
      showCreateDialog.value = false;
      createForm.value = {
        lieuId: null,
        jourSemaine: 0,
        heureDebut: '10:00',
        heureFin: '10:30'
      };
    }
  } finally {
    creating.value = false;
  }
};

const handleDelete = async (id: number) => {
  if (!confirm('Supprimer cette rencontre ?')) return;

  deleting.value = true;
  try {
    await rencontreStore.remove(id);
  } finally {
    deleting.value = false;
  }
};

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

.admin__sidebar h3 {
  margin: 0 0 1rem 0;
  font-size: 1rem;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.05em;
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
  transition: all 0.2s ease;
}

.list__item:hover {
  background: rgba(255, 255, 255, 1);
  border-color: rgba(59, 130, 246, 0.2);
}

.list__item--active {
  border-color: rgba(59, 130, 246, 0.4);
  background: rgba(59, 130, 246, 0.08);
}

.list__item strong {
  display: block;
  color: #1f2937;
}

.list__badge {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.1);
  color: #2563eb;
  font-weight: 600;
}

.admin__panel {
  padding: 1.5rem;
  border-radius: 1.2rem;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(226, 232, 240, 0.9);
}

.admin__panel h2 {
  margin: 0 0 1.5rem 0;
  font-size: 1.5rem;
  color: #1f2937;
}

.admin__empty {
  padding: 3rem 2rem;
  text-align: center;
  color: #64748b;
  border: 1px dashed rgba(148, 163, 184, 0.4);
  border-radius: 1rem;
}

.admin__empty p {
  margin: 0 0 1rem 0;
  font-size: 1.1rem;
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

.rencontre-card:hover {
  border-color: rgba(59, 130, 246, 0.3);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.rencontre-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.rencontre-card__day {
  font-weight: 600;
  color: #1f2937;
  font-size: 1.1rem;
}

.rencontre-card__delete {
  background: none;
  border: none;
  color: #dc2626;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.rencontre-card__delete:hover:not(:disabled) {
  background: rgba(220, 38, 38, 0.1);
}

.rencontre-card__delete:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

/* Dialog */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.dialog {
  background: white;
  border-radius: 1rem;
  padding: 1.5rem;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  animation: slideUp 0.3s;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.dialog h3 {
  margin: 0 0 1.5rem 0;
  font-size: 1.25rem;
  color: #1f2937;
}

.form {
  display: grid;
  gap: 1rem;
}

.form__grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
}

.form__row {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form__label {
  font-size: 0.95rem;
  font-weight: 500;
  color: #1f2937;
}

.form__input {
  border: 1px solid rgba(148, 163, 184, 0.4);
  border-radius: 0.5rem;
  padding: 0.6rem 0.9rem;
  font-size: 1rem;
  background: #fff;
  transition: all 0.2s;
}

.form__input:focus {
  outline: none;
  border-color: rgba(59, 130, 246, 0.7);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.dialog__actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.btn {
  border: none;
  border-radius: 0.5rem;
  padding: 0.65rem 1.4rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn--primary {
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  color: #fff;
}

.btn--primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.4);
}

.btn--ghost {
  background: none;
  border: 1px solid rgba(148, 163, 184, 0.4);
  color: #475569;
}

.btn--ghost:hover {
  background: rgba(148, 163, 184, 0.1);
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
