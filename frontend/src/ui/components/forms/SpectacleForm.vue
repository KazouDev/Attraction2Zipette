<template>
  <form class="form" @submit.prevent="onSubmit">
    <div class="form__row">
      <label class="form__label" for="titre">Titre</label>
      <input
        id="titre"
        v-model="localValue.titre"
        class="form__input"
        type="text"
        required
        minlength="3"
        placeholder="Titre du spectacle"
      />
    </div>

    <div class="form__row">
      <label class="form__label" for="lieu">Lieu</label>
      <select
        id="lieu"
        v-model.number="localValue.lieuId"
        class="form__input"
        required
      >
        <option value="" disabled>Choisir un lieu</option>
        <option v-for="lieu in lieux" :key="lieu.id" :value="lieu.id">
          {{ lieu.nom }}
        </option>
      </select>
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
import type { Lieu } from '@/domain/entities/Lieu';
import { reactive, watch } from 'vue';

interface SpectacleFormModel {
  titre: string;
  lieuId: number | null;
}

interface Props {
  modelValue: SpectacleFormModel;
  lieux: Lieu[];
  submitting?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  submitting: false
});

const emit = defineEmits<{
  (event: 'update:modelValue', value: SpectacleFormModel): void;
  (event: 'submit', value: SpectacleFormModel): void;
}>();

const localValue = reactive<SpectacleFormModel>({ ...props.modelValue });

watch(
  () => props.modelValue,
  (value: SpectacleFormModel) => {
    Object.assign(localValue, value);
  },
  { deep: true }
);

watch(
  localValue,
  (value: SpectacleFormModel) => emit('update:modelValue', { ...value }),
  { deep: true }
);

const onSubmit = () => {
  if (!localValue.titre || localValue.titre.length < 3) {
    return;
  }
  if (!localValue.lieuId) {
    return;
  }
  emit('submit', { ...localValue });
};
</script>

<style scoped>
.form {
  display: grid;
  gap: 1.25rem;
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
}

.form__input:focus {
  outline: none;
  border-color: rgba(59, 130, 246, 0.7);
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.12);
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
  background: linear-gradient(135deg, #22c55e, #10b981);
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
