import { createApp } from 'vue'
import './style.css'
import App from './GpadOs.vue';
import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';
import 'primeflex/primeflex.css'; // Import PrimeFlex CSS
import 'primeicons/primeicons.css'; // Add this for icons

let app = createApp(App);
app.use(PrimeVue, {
    theme: {
        preset: Aura
    }
});

app.mount('#app');
app.config.errorHandler = (err) => {
    console.log(err);
}