<template>
  <section class="settings">
    <header class="settings__header">
      <div>
        <h1>Paramètres du parc</h1>
          <p>Administrez les types d'attractions, les lieux et les personnages disponibles.</p>
      </div>
      <button class="btn btn--ghost" type="button" @click="refreshAll" :disabled="loadingAny">
        Rafraîchir
      </button>
    </header>

    <LoaderOverlay :visible="loadingAny" />

    <section class="settings__grid">
      <article class="panel">
        <header class="panel__header">
          <h2>Types d'attractions</h2>
          <form class="panel__form" @submit.prevent="handleCreateType">
            <input v-model="newTypeName" class="panel__input" type="text" placeholder="Nouveau type" required />
            <button class="btn btn--primary" type="submit" :disabled="!newTypeName.trim() || creatingType">
              Ajouter
            </button>
          </form>
        </header>

        <ul class="panel__list">
          <li v-for="type in typeStore.items" :key="type.id" class="panel__item">
            <span>{{ type.nom }}</span>
            <div class="panel__item-actions">
              <button class="btn btn--ghost" type="button" @click="handleRenameType(type.id, type.nom)">
                Renommer
              </button>
              <button class="btn btn--danger" type="button" @click="handleDeleteType(type.id)">
                Supprimer
              </button>
            </div>
          </li>
        </ul>
      </article>

      <article class="panel">
        <header class="panel__header">
          <h2>Lieux</h2>
          <form class="panel__form" @submit.prevent="handleCreateLieu">
            <input v-model="newLieuName" class="panel__input" type="text" placeholder="Nouveau lieu" required />
            <button class="btn btn--primary" type="submit" :disabled="!newLieuName.trim() || creatingLieu">
              Ajouter
            </button>
          </form>
        </header>

        <ul class="panel__list">
          <li v-for="lieu in lieuStore.items" :key="lieu.id" class="panel__item">
            <span>{{ lieu.nom }}</span>
            <div class="panel__item-actions">
              <button class="btn btn--ghost" type="button" @click="handleRenameLieu(lieu.id, lieu.nom)">
                Renommer
              </button>
              <button class="btn btn--danger" type="button" @click="handleDeleteLieu(lieu.id)">
                Supprimer
              </button>
            </div>
          </li>
        </ul>
      </article>

      <article class="panel">
        <header class="panel__header">
          <h2>Personnages</h2>
          <form class="panel__form" @submit.prevent="handleCreatePersonnage">
            <input v-model="newPersonnageName" class="panel__input" type="text" placeholder="Nouveau personnage" required />
            <button class="btn btn--primary" type="submit" :disabled="!newPersonnageName.trim() || creatingPersonnage">
              Ajouter
            </button>
          </form>
        </header>

        <ul class="panel__list">
          <li v-for="personnage in personnageStore.items" :key="personnage.id" class="panel__item">
            <span>{{ personnage.nom }}</span>
            <div class="panel__item-actions">
              <button class="btn btn--danger" type="button" @click="handleDeletePersonnage(personnage.id)">
                Supprimer
              </button>
            </div>
          </li>
        </ul>
      </article>
    </section>
  </section>
</template>

<script setup lang="ts">
import { useAttractionTypeStore } from '@/application/stores/attractionTypeStore';
import { useLieuStore } from '@/application/stores/lieuStore';
import { usePersonnageStore } from '@/application/stores/personnageStore';
import LoaderOverlay from '@/ui/components/LoaderOverlay.vue';
import { computed, onMounted, ref } from 'vue';

const typeStore = useAttractionTypeStore();
const lieuStore = useLieuStore();
const personnageStore = usePersonnageStore();

const newTypeName = ref('');
const newLieuName = ref('');
const newPersonnageName = ref('');
const creatingType = ref(false);
const creatingLieu = ref(false);
const creatingPersonnage = ref(false);

const loadingAny = computed(() => typeStore.loading || lieuStore.loading || personnageStore.loading);

onMounted(async () => {
  await Promise.all([typeStore.fetchAll(), lieuStore.fetchAll(), personnageStore.fetchAll()]);
});

const refreshAll = async () => {
  await Promise.all([typeStore.fetchAll(true), lieuStore.fetchAll(true), personnageStore.fetchAll(true)]);
};

const handleCreateType = async () => {
  if (!newTypeName.value.trim()) {
    return;
  }
  creatingType.value = true;
  try {
    await typeStore.create(newTypeName.value.trim());
    newTypeName.value = '';
  } finally {
    creatingType.value = false;
  }
};

const handleRenameType = async (id: number, current: string) => {
  const nom = window.prompt("Nouveau nom pour le type", current);
  if (!nom || nom.trim() === current.trim()) {
    return;
  }
  await typeStore.update(id, nom.trim());
};

const handleDeleteType = async (id: number) => {
  const confirmed = window.confirm('Supprimer ce type ?');
  if (!confirmed) {
    return;
  }
  await typeStore.remove(id);
};

const handleCreateLieu = async () => {
  if (!newLieuName.value.trim()) {
    return;
  }
  creatingLieu.value = true;
  try {
    await lieuStore.create(newLieuName.value.trim());
    newLieuName.value = '';
  } finally {
    creatingLieu.value = false;
  }
};

const handleRenameLieu = async (id: number, current: string) => {
  const nom = window.prompt('Nouveau nom pour le lieu', current);
  if (!nom || nom.trim() === current.trim()) {
    return;
  }
  await lieuStore.update(id, nom.trim());
};

const handleDeleteLieu = async (id: number) => {
  const confirmed = window.confirm('Supprimer ce lieu ?');
  if (!confirmed) {
    return;
  }
  await lieuStore.remove(id);
};

const handleCreatePersonnage = async () => {
  if (!newPersonnageName.value.trim()) {
    return;
  }
  creatingPersonnage.value = true;
  try {
    await personnageStore.create(newPersonnageName.value.trim());
    newPersonnageName.value = '';
  } finally {
    creatingPersonnage.value = false;
  }
};

const handleDeletePersonnage = async (id: number) => {
  const confirmed = window.confirm('Supprimer ce personnage ?');
  if (!confirmed) {
    return;
  }
  await personnageStore.remove(id);
};
</script>

<style scoped>
.settings {
  display: grid;
  gap: 1.75rem;
}

.settings__header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.settings__grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
}

.panel {
  display: grid;
  gap: 1.25rem;
  padding: 1.5rem;
  border-radius: 1.2rem;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(226, 232, 240, 0.9);
}

.panel__header {
  display: grid;
  gap: 1rem;
}

.panel__form {
  display: flex;
  gap: 0.75rem;
}

.panel__input {
  flex: 1;
  border: 1px solid rgba(148, 163, 184, 0.4);
  border-radius: 0.75rem;
  padding: 0.6rem 0.9rem;
  font-size: 1rem;
}

.panel__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 0.75rem;
}

.panel__item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  border-radius: 0.85rem;
  background: rgba(15, 23, 42, 0.04);
}

.panel__item-actions {
  display: flex;
  gap: 0.5rem;
}

.btn {
  border: none;
  border-radius: 999px;
  padding: 0.6rem 1.2rem;
  font-weight: 600;
  cursor: pointer;
  background: rgba(148, 163, 184, 0.2);
}

.btn--primary {
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  color: #fff;
}

.btn--danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
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
</style>
