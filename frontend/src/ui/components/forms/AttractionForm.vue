<template>
  <form class="form" @submit.prevent="onSubmit">
    <div class="form__row">
      <label class="form__label" for="nom">Nom</label>
      <input
        id="nom"
        v-model="localValue.nom"
        class="form__input"
        name="nom"
        type="text"
        required
        minlength="3"
        placeholder="Nom de l'attraction"
      />
    </div>

    <div class="form__row">
      <label class="form__label" for="type">Type</label>
      <select
        id="type"
        v-model.number="localValue.typeId"
        class="form__input"
        name="type"
        required
      >
        <option value="" disabled>Choisir un type</option>
        <option v-for="type in types" :key="type.id" :value="type.id">
          {{ type.nom }}
        </option>
      </select>
    </div>

    <div class="form__grid">
      <div class="form__row">
        <label class="form__label" for="taille-min">Taille minimale (cm)</label>
        <input
          id="taille-min"
          v-model.number="localValue.tailleMin"
          class="form__input"
          name="taille-min"
          type="number"
          min="50"
          step="1"
          required
        />
      </div>

      <div class="form__row">
        <label class="form__label" for="taille-min-adulte">Taille avec adulte (cm)</label>
        <input
          id="taille-min-adulte"
          v-model.number="localValue.tailleMinAdulte"
          class="form__input"
          name="taille-min-adulte"
          type="number"
          min="50"
          step="1"
          required
        />
      </div>
    </div>

    <div class="form__actions">
      <button class="form__submit" type="submit" :disabled="submitting">
        <slot name="submit-label">Enregistrer</slot>
      </button>
      <slot name="secondary-action" />
    </div>
  </form>
</template>

<script setup lang="ts">
import type { AttractionType } from '@/domain/entities/AttractionType';
import { reactive, watch } from 'vue';

interface AttractionFormModel {
  nom: string;
  typeId: number | null;
  tailleMin: number | null;
  tailleMinAdulte: number | null;
}

interface Props {
  modelValue: AttractionFormModel;
  types: AttractionType[];
  submitting?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  submitting: false
});

const emit = defineEmits<{
  (event: 'update:modelValue', value: AttractionFormModel): void;
  (event: 'submit', value: AttractionFormModel): void;
}>();

const localValue = reactive<AttractionFormModel>({ ...props.modelValue });

// Synchroniser uniquement quand le parent change (sans causer de boucle)
watch(
  () => props.modelValue,
  (newValue) => {
    Object.assign(localValue, newValue);
  },
  { deep: true }
);

// Émettre les changements au parent
const isValid = () => {
  if (!localValue.nom || localValue.nom.length < 3) {
    return false;
  }
  if (!localValue.typeId) {
    return false;
  }
  if (localValue.tailleMin == null || localValue.tailleMinAdulte == null) {
    return false;
  }
  return localValue.tailleMin > 0 && localValue.tailleMinAdulte > 0;
};

const onSubmit = () => {
  if (!isValid()) {
    return;
  }
  console.log('Submitting form with value:', localValue);
  emit('submit', { ...localValue });
};
</script>

<style scoped>
.form {
  display: grid;
  gap: 1.25rem;
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
  border-radius: 0.75rem;
  padding: 0.6rem 0.9rem;
  font-size: 1rem;
  background: #fff;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form__input:focus {
  outline: none;
  border-color: rgba(59, 130, 246, 0.7);
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.15);
}

.form__actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.form__submit {
  border: none;
  border-radius: 999px;
  padding: 0.85rem 1.8rem;
  font-weight: 600;
  cursor: pointer;
  color: #fff;
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.form__submit[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
}

.form__submit:not([disabled]):hover {
  transform: translateY(-1px);
}
</style>
