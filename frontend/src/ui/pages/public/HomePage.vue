<template>
  <section class="hero">
    <div class="hero__content">
      <h1>Vivez des émotions inoubliables au parc Attraction 2 Zipette</h1>
      <p>
        Découvrez nos attractions pour toute la famille, vivez des spectacles enchanteurs et profitez de
        rencontres inoubliables avec vos personnages favoris.
      </p>
      <div class="hero__actions">
        <RouterLink class="hero__cta" :to="{ name: 'public-attractions' }">
          Explorer les attractions
        </RouterLink>
        <RouterLink class="hero__ghost" :to="{ name: 'public-spectacles' }">
          Voir les spectacles
        </RouterLink>
      </div>
    </div>
    <div class="hero__illustration">
      <div class="hero__card">
        <h2>Temps d'attente en direct</h2>
        <p>Consultez en temps réel les temps d'attente pour vous organiser facilement.</p>
      </div>
      <div class="hero__card hero__card--accent">
        <h2>Planifiez votre journée</h2>
        <p>Synchronisez attractions et spectacles pour ne rien manquer du spectacle.</p>
      </div>
    </div>
  </section>

  <section class="features">
    <article class="feature">
      <h3>Attractions variées</h3>
      <p>
        Des sensations fortes aux aventures familiales, retrouvez toutes nos attractions avec leurs caractéristiques
        détaillées.
      </p>
    </article>
    <article class="feature">
      <h3>Spectacles immersifs</h3>
      <p>
        Une programmation hebdomadaire riche, des personnages iconiques et des lieux magiques pour vous émerveiller.
      </p>
    </article>
    <article class="feature">
      <h3>Gestion fluide</h3>
      <p>
        Un espace administration pensé pour les équipes du parc afin de mettre à jour l'offre en toute simplicité.
      </p>
    </article>
  </section>
</template>

<style scoped>
.hero {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  align-items: center;
}

.hero__content h1 {
  font-size: clamp(2rem, 3vw, 3.2rem);
  color: #0f172a;
  margin-bottom: 1rem;
}

.hero__content p {
  font-size: 1.05rem;
  color: #475569;
  margin-bottom: 1.5rem;
}

.hero__actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.hero__cta,
.hero__ghost {
  padding: 0.85rem 1.6rem;
  border-radius: 999px;
  font-weight: 600;
}

.hero__cta {
  background: linear-gradient(135deg, #6366f1, #22d3ee);
  color: #fff;
}

.hero__ghost {
  border: 1px solid rgba(100, 116, 139, 0.3);
  color: #334155;
}

.hero__ghost:hover {
  border-color: rgba(79, 70, 229, 0.5);
  color: #1d4ed8;
}

.hero__illustration {
  display: grid;
  gap: 1rem;
}

.hero__card {
  padding: 1.5rem;
  background: #fff;
  border-radius: 1rem;
  box-shadow: 0 14px 40px -24px rgba(15, 23, 42, 0.5);
  border: 1px solid rgba(148, 163, 184, 0.2);
}

.hero__card--accent {
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.2), rgba(79, 70, 229, 0.2));
}

.features {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  margin-top: 3rem;
}

.feature {
  padding: 1.6rem;
  background: #fff;
  border-radius: 1rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 16px 44px -36px rgba(30, 64, 175, 0.5);
}

.feature h3 {
  margin-bottom: 0.75rem;
  color: #1e293b;
}

.feature p {
  color: #475569;
  margin: 0;
}
</style>
