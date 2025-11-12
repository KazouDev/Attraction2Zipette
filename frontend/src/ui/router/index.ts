import AdminLayout from '@/ui/layouts/AdminLayout.vue';
import PublicLayout from '@/ui/layouts/PublicLayout.vue';
import AttractionsAdminPage from '@/ui/pages/admin/AttractionsAdminPage.vue';
import SettingsAdminPage from '@/ui/pages/admin/SettingsAdminPage.vue';
import SpectaclesAdminPage from '@/ui/pages/admin/SpectaclesAdminPage.vue';
import RencontresAdminPage from '@/ui/pages/admin/RencontresAdminPage.vue';
import AttractionsPage from '@/ui/pages/public/AttractionsPage.vue';
import HomePage from '@/ui/pages/public/HomePage.vue';
import SpectaclesPage from '@/ui/pages/public/SpectaclesPage.vue';
import RencontresPage from '@/ui/pages/public/RencontresPage.vue';
import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    {
      path: '/',
      component: PublicLayout,
      children: [
        { path: '', name: 'home', component: HomePage },
        { path: 'attractions', name: 'public-attractions', component: AttractionsPage },
        { path: 'spectacles', name: 'public-spectacles', component: SpectaclesPage },
        { path: 'rencontres', name: 'public-rencontres', component: RencontresPage }
      ]
    },
    {
      path: '/admin',
      component: AdminLayout,
      children: [
        { path: '', redirect: { name: 'admin-attractions' } },
        { path: 'attractions', name: 'admin-attractions', component: AttractionsAdminPage },
        { path: 'spectacles', name: 'admin-spectacles', component: SpectaclesAdminPage },
        { path: 'parametres', name: 'admin-settings', component: SettingsAdminPage },
        { path: 'rencontres', name: 'admin-rencontres', component: RencontresAdminPage }
      ]
    }
  ]
});

export default router;
