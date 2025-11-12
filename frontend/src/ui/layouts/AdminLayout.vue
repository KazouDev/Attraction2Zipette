<template>
  <div class="admin">
    <aside class="admin__sidebar">
      <RouterLink class="admin__brand" :to="{ name: 'home' }">
        Attraction 2 Zipette
      </RouterLink>
      <nav class="admin__nav">
        <RouterLink class="admin__link" :class="linkClass('admin-attractions')" :to="{ name: 'admin-attractions' }">
          Attractions
        </RouterLink>
        <RouterLink class="admin__link" :class="linkClass('admin-spectacles')" :to="{ name: 'admin-spectacles' }">
          Spectacles
        </RouterLink>
        <RouterLink class="admin__link" :class="linkClass('admin-rencontres')" :to="{ name: 'admin-rencontres' }">
          Rencontres
        </RouterLink>
        <RouterLink class="admin__link" :class="linkClass('admin-settings')" :to="{ name: 'admin-settings' }">
          Réglages
        </RouterLink>
        <RouterLink class="admin__back" :to="{ name: 'home' }">
          ← Retour au site public
        </RouterLink>
      </nav>
    </aside>

    <div class="admin__content">
      <header class="admin__header">
        <slot name="header">
          <h1>Tableau de bord</h1>
        </slot>
      </header>
      <main class="admin__main">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

const linkClass = (name: string) =>
  computed(() => ({
    'admin__link--active': route.name === name
  })).value;
</script>

<style scoped>
.admin {
  display: grid;
  grid-template-columns: 260px 1fr;
  min-height: 100vh;
  background: #f8fafc;
}

.admin__sidebar {
  background: #0f172a;
  color: #e2e8f0;
  padding: 2rem 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  position: sticky;
  top: 0;
  height: 100vh;
}

.admin__brand {
  font-size: 1.4rem;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.admin__nav {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.admin__link {
  padding: 0.65rem 1rem;
  border-radius: 0.75rem;
  transition: background-color 0.2s ease;
  color: rgba(226, 232, 240, 0.8);
  font-weight: 500;
}

.admin__link:hover {
  background: rgba(148, 163, 184, 0.16);
  color: #ffffff;
}

.admin__link--active {
  background: linear-gradient(135deg, #38bdf8, #6366f1);
  color: #0f172a;
}

.admin__back {
  margin-top: auto;
  padding-top: 1rem;
  color: rgba(226, 232, 240, 0.6);
  font-size: 0.95rem;
}

.admin__content {
  display: grid;
  grid-template-rows: auto 1fr;
  background: #f8fafc;
}

.admin__header {
  padding: 1.75rem 2rem 0;
}

.admin__main {
  padding: 1.5rem 2rem 2.5rem;
}

@media (max-width: 960px) {
  .admin {
    grid-template-columns: 1fr;
  }

  .admin__sidebar {
    position: relative;
    height: auto;
    flex-direction: row;
    align-items: center;
    gap: 1rem;
    padding: 1rem 1.5rem;
  }

  .admin__nav {
    flex-direction: row;
    gap: 0.5rem;
    flex-wrap: wrap;
  }

  .admin__content {
    min-height: calc(100vh - 120px);
  }
}
</style>
