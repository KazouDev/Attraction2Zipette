<template>
  <div class="editor">
    <header class="editor__header">
      <h3>{{ title }}</h3>
      <button class="editor__add" type="button" :disabled="disabled" @click="addEntry">
        + Ajouter un créneau
      </button>
    </header>

    <div v-if="localValue.length === 0" class="editor__empty">
      <p>Aucun créneau défini.</p>
    </div>

    <ul class="editor__list">
      <li v-for="(entry, index) in localValue" :key="index" class="editor__item">
        <select
          v-model.number="entry.jourSemaine"
          class="editor__select"
          :disabled="disabled"
        >
          <option v-for="day in days" :key="day.value" :value="day.value">
            {{ day.label }}
          </option>
        </select>

        <input
          v-model="entry.heureOuverture"
          class="editor__time"
          type="time"
          :step="timeStep"
          :disabled="disabled"
          required
        />
        <span class="editor__separator">→</span>
        <input
          v-model="entry.heureFermeture"
          class="editor__time"
          type="time"
          :step="timeStep"
          :disabled="disabled"
          required
        />

        <button class="editor__remove" type="button" :disabled="disabled" @click="removeEntry(index)">
          Supprimer
        </button>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { DAY_LABEL_BY_NUMBER, DAY_NUMBERS, type DayNumber } from '@/constants/days';
import { computed, reactive, watch } from 'vue';

interface HoraireItem {
  jourSemaine: DayNumber;
  heureOuverture: string;
  heureFermeture: string;
}

interface Props {
  modelValue: HoraireItem[];
  disabled?: boolean;
  title?: string;
  includeSeconds?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  title: 'Horaires',
  includeSeconds: false
});

const emit = defineEmits<{
  (event: 'update:modelValue', value: HoraireItem[]): void;
}>();

const localValue = reactive<HoraireItem[]>(props.modelValue.map((item: HoraireItem) => ({ ...item })));

let isUpdatingFromProps = false;

watch(
  () => props.modelValue,
  (value: HoraireItem[]) => {
    isUpdatingFromProps = true;
    localValue.splice(0, localValue.length, ...value.map((item) => ({ ...item })));
    isUpdatingFromProps = false;
  },
  { deep: true }
);

// Émettre les changements au parent (sauf si on vient de recevoir une mise à jour du parent)
watch(
  localValue,
  () => {
    if (!isUpdatingFromProps) {
      emit(
        'update:modelValue',
        localValue.map((item: HoraireItem) => ({ ...item }))
      );
    }
  },
  { deep: true }
);

const days = computed(() => DAY_NUMBERS.map((value) => ({ value, label: DAY_LABEL_BY_NUMBER[value] })));

const timeStep = computed(() => (props.includeSeconds ? 1 : 60));

const addEntry = () => {
  if (props.disabled) {
    return;
  }
  const defaultDay = localValue.length > 0 ? ((localValue.at(-1)?.jourSemaine ?? 0) as DayNumber) : 0;
  localValue.push({
    jourSemaine: defaultDay,
    heureOuverture: '10:00',
    heureFermeture: props.includeSeconds ? '11:00:00' : '11:00'
  });
};

const removeEntry = (index: number) => {
  if (props.disabled) {
    return;
  }
  localValue.splice(index, 1);
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
}

.editor__add:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.editor__empty {
  padding: 1rem;
  border-radius: 0.75rem;
  border: 1px dashed rgba(148, 163, 184, 0.6);
  color: #64748b;
}

.editor__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 0.75rem;
}

.editor__item {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 0.6rem;
  align-items: center;
  padding: 0.75rem;
  border-radius: 0.75rem;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.editor__select,
.editor__time {
  width: 100%;
  border: 1px solid rgba(148, 163, 184, 0.4);
  border-radius: 0.6rem;
  padding: 0.45rem 0.75rem;
}

.editor__separator {
  text-align: center;
  font-weight: 600;
  color: #334155;
}

.editor__remove {
  justify-self: end;
  background: none;
  border: none;
  color: #b91c1c;
  font-weight: 500;
  cursor: pointer;
}

.editor__remove:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
