import '@/styles/main.css';
import App from '@/ui/App.vue';
import router from '@/ui/router';
import { createPinia } from 'pinia';
import { createApp } from 'vue';

import mdiVue from 'mdi-vue/v3'
import * as mdijs from '@mdi/js'

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.use(mdiVue, { icons: mdijs });

app.mount('#app');
