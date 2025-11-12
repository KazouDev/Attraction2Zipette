<template>
  <div class="editor">
    <header class="editor__header">
      <h3>Personnages du spectacle</h3>
      <button
        class="editor__add"
        type="button"
        :disabled="disabled || availablePersonnages.length === 0"
        @click="showAddDialog = true"
      >
        + Ajouter un personnage ({{ availablePersonnages.length }} disponibles)
      </button>
    </header>

    <!-- Debug info -->
    <div v-if="!disabled" class="editor__debug">
      <small>
        Total: {{ allPersonnages.length }} |
        Assignés: {{ selectedPersonnages.length }} |
        Disponibles: {{ availablePersonnages.length }}
      </small>
    </div>

    <div v-if="selectedPersonnages.length === 0" class="editor__empty">
      <p>Aucun personnage assigné à ce spectacle.</p>
    </div>

    <ul v-else class="editor__list">
      <li v-for="personnage in selectedPersonnages" :key="personnage.id" class="editor__item">
        <span class="editor__name">{{ personnage.nom }}</span>
        <button
          class="editor__remove"
          type="button"
          :disabled="disabled"
          @click="removePersonnage(personnage.id)"
        >
          Supprimer
        </button>
      </li>
    </ul>

    <!-- Dialog pour ajouter un personnage -->
    <div v-if="showAddDialog" class="dialog-overlay" @click.self="showAddDialog = false">
      <div class="dialog">
        <h3>Ajouter un personnage</h3>
        <p class="dialog__description">Sélectionnez un personnage à ajouter au spectacle</p>

        <div v-if="availablePersonnages.length === 0" class="dialog__empty">
          <p>Tous les personnages sont déjà assignés à ce spectacle.</p>
        </div>

        <ul v-else class="dialog__list">
          <li
            v-for="personnage in availablePersonnages"
            :key="personnage.id"
            class="dialog__item"
            @click="addPersonnage(personnage.id)"
          >
            {{ personnage.nom }}
          </li>
        </ul>

        <div class="dialog__actions">
          <button class="btn btn--ghost" type="button" @click="showAddDialog = false">
            Annuler
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Personnage } from '@/domain/entities/Personnage';
import { computed, ref, watch } from 'vue';

interface Props {
  selectedPersonnages: Personnage[];
  allPersonnages: Personnage[];
  disabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false
});

const emit = defineEmits<{
  (event: 'add', personnageId: number): void;
  (event: 'remove', personnageId: number): void;
}>();

const showAddDialog = ref(false);

const availablePersonnages = computed(() => {
  const selectedIds = new Set(props.selectedPersonnages.map(p => p.id));
  return props.allPersonnages.filter(p => !selectedIds.has(p.id));
});

// Debug: afficher les props dans la console
watch([() => props.selectedPersonnages, () => props.allPersonnages], ([selected, all]) => {
  console.log('PersonnageListEditor - Selected:', selected);
  console.log('PersonnageListEditor - All:', all);
  console.log('PersonnageListEditor - Available:', availablePersonnages.value);
}, { immediate: true });

const addPersonnage = (personnageId: number) => {
  console.log('Adding personnage:', personnageId);
  emit('add', personnageId);
  showAddDialog.value = false;
};

const removePersonnage = (personnageId: number) => {
  console.log('Removing personnage:', personnageId);
  emit('remove', personnageId);
};
</script>

<style scoped>
.editor {
  display: grid;
  gap: 1rem;
}

.editor__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.editor__header h3 {
  margin: 0;
  font-size: 1.1rem;
}

.editor__add {
  background: none;
  border: 1px solid rgba(59, 130, 246, 0.4);
  color: #2563eb;
  padding: 0.4rem 0.85rem;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.2s;
}

.editor__add:hover:not(:disabled) {
  background: rgba(59, 130, 246, 0.1);
  border-color: #2563eb;
}

.editor__add:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.editor__debug {
  padding: 0.5rem;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 0.5rem;
  color: #1e40af;
  font-size: 0.85rem;
}

.editor__empty {
  padding: 1rem;
  border-radius: 0.75rem;
  border: 1px dashed rgba(148, 163, 184, 0.6);
  color: #64748b;
  text-align: center;
}

.editor__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 0.5rem;
}

.editor__item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.8);
  transition: all 0.2s;
}

.editor__item:hover {
  border-color: rgba(59, 130, 246, 0.3);
  background: rgba(255, 255, 255, 1);
}

.editor__name {
  font-weight: 500;
  color: #1f2937;
}

.editor__remove {
  background: none;
  border: none;
  color: #b91c1c;
  font-weight: 500;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
  transition: all 0.2s;
}

.editor__remove:hover:not(:disabled) {
  background: rgba(185, 28, 28, 0.1);
}

.editor__remove:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.dialog {
  background: white;
  border-radius: 1rem;
  padding: 1.5rem;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
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
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
  color: #1f2937;
}

.dialog__description {
  margin: 0 0 1rem 0;
  color: #6b7280;
  font-size: 0.9rem;
}

.dialog__empty {
  padding: 1rem;
  text-align: center;
  color: #6b7280;
  font-style: italic;
}

.dialog__list {
  list-style: none;
  margin: 0 0 1.5rem 0;
  padding: 0;
  display: grid;
  gap: 0.5rem;
}

.dialog__item {
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  border: 1px solid rgba(226, 232, 240, 0.8);
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
  color: #374151;
}

.dialog__item:hover {
  background: rgba(59, 130, 246, 0.05);
  border-color: #2563eb;
  color: #2563eb;
}

.dialog__actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn--ghost {
  background: none;
  border: 1px solid rgba(148, 163, 184, 0.4);
  color: #475569;
}

.btn--ghost:hover {
  background: rgba(148, 163, 184, 0.1);
  border-color: #64748b;
}
</style>
