<template>
  <div class="layout">
    <header class="layout__header">
      <RouterLink class="layout__brand" :to="{ name: 'home' }">
        Attraction 2 Zipette
      </RouterLink>
      <nav class="layout__nav">
        <RouterLink class="layout__link" :class="linkClass('home')" :to="{ name: 'home' }">
          Accueil
        </RouterLink>
        <RouterLink class="layout__link" :class="linkClass('public-attractions')" :to="{ name: 'public-attractions' }">
          Attractions
        </RouterLink>
        <RouterLink class="layout__link" :class="linkClass('public-spectacles')" :to="{ name: 'public-spectacles' }">
          Spectacles
        </RouterLink>
        <RouterLink class="layout__link" :class="linkClass('public-stats')" :to="{ name: 'public-stats' }">
          Statistiques
        </RouterLink>
        <RouterLink class="layout__link" :class="linkClass('public-rencontres')" :to="{ name: 'public-rencontres' }">
          Rencontres
        </RouterLink>
        <RouterLink class="layout__cta" :to="{ name: 'admin-attractions' }">
          Espace administration
        </RouterLink>
      </nav>
    </header>

    <main class="layout__content">
      <RouterView />
    </main>

    <footer class="layout__footer">
      <p>© {{ currentYear }} Parc Attraction 2 Zipette. Tous droits réservés.</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

const currentYear = new Date().getFullYear();

const linkClass = (name: string) =>
  computed(() => ({
    'layout__link--active': route.name === name
  })).value;
</script>

<style scoped>
.layout {
  display: grid;
  grid-template-rows: auto 1fr auto;
  min-height: 100vh;
}

.layout__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: rgba(15, 23, 42, 0.9);
  color: #f8fafc;
  backdrop-filter: blur(8px);
  position: sticky;
  top: 0;
  z-index: 10;
}

.layout__brand {
  font-size: 1.25rem;
  font-weight: 600;
  letter-spacing: 0.04em;
}

.layout__nav {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.layout__link,
.layout__cta {
  padding: 0.5rem 0.9rem;
  border-radius: 999px;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.layout__link {
  color: rgba(248, 250, 252, 0.8);
}

.layout__link:hover {
  background-color: rgba(255, 255, 255, 0.12);
  color: #f8fafc;
}

.layout__link--active {
  background-color: rgba(255, 255, 255, 0.2);
  color: #f8fafc;
}

.layout__cta {
  background: linear-gradient(135deg, #f97316, #ef4444);
  color: #fff;
  font-weight: 600;
}

.layout__cta:hover {
  opacity: 0.9;
}

.layout__content {
  padding: 2rem clamp(1rem, 3vw, 3rem);
}

.layout__footer {
  padding: 1.5rem;
  text-align: center;
  background: #0f172a;
  color: rgba(248, 250, 252, 0.7);
}
</style>
