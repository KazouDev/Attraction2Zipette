<template>
  <div class="wrapper">
    <section class="hero">
      <div class="hero__content">
        <div class="title">
          <h1>Vivez des émotions inoubliables</h1>
          <h1>au parc Attraction2Zipette</h1>
        </div>
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
    </section>
    <section class="features">
      <article class="feature">
        <img alt="attraction variées illustration" src="/images/attractions.svg" />
        <div class="footer">
          <h3>Attractions variées</h3>
          <p>
            Des sensations fortes aux aventures familiales, retrouvez toutes nos attractions avec leurs caractéristiques
            détaillées.
          </p>
        </div>
      </article>
      <article class="feature">
        <img alt="spectacle" src="/images/spectacle.svg" />
        <div class="footer">
          <h3>Spectacles immersifs</h3>
          <p>
            Une programmation hebdomadaire riche, des personnages iconiques et des lieux magiques pour vous émerveiller.
          </p>
        </div>
      </article>
      <article class="feature">
        <img alt="spectacle" src="/images/waiting.svg" />
        <div class="footer">
          <h3>Temps d'attente en direct</h3>
          <p>Consultez en temps réel les temps d'attente pour vous organiser facilement.</p>
        </div>
      </article>
    </section>
  </div>
</template>

<style scoped>
.wrapper {
  height: 100%;
  display: grid;
}
.hero {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  align-items: center;
}

.hero__content {
  text-align: center;
}

.hero__content h1 {
  font-size: clamp(2rem, 3vw, 3.2rem);
  color: #0f172a;
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
  justify-content: center;
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

.features {
  display: flex;
  justify-content: center;
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
  flex-basis: 25em;
  text-align: center;

  display: grid;
}

.feature h3 {
  margin-bottom: 0.75rem;
  color: #1e293b;
}

.feature img {
  width: 15em;
  height: 15em;

  object-fit: cover;

  display: block;
  margin: auto;
}


.feature p {
  color: #475569;
  margin: 0;
}
</style>
<script setup>
</script>