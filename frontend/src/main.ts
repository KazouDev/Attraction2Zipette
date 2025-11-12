import '@/styles/main.css';
import App from '@/ui/App.vue';
import router from '@/ui/router';
import { createPinia } from 'pinia';
import { createApp } from 'vue';

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount('#app');
