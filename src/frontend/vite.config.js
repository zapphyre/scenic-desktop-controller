import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
    plugins: [vue()],
    envDir: './',
    resolve: {
        alias: {
            '@': '/src', // Already set by default
        },
    },
    build: {
        outDir: '../src/main/resources/static', // Output to Spring's static folder
        emptyOutDir: true, // Clean the directory before building
    },
    server: {
        proxy: {
            '/api': 'http://localhost:8080', // Proxy API calls during development
        },
    },
});