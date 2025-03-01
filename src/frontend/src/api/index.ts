import axios from "axios";

console.log('Env:', import.meta.env);
console.log('VITE_API_BASE_URL:', import.meta.env.VITE_API_BASE_URL);

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL, // Dynamically set from .env
    timeout: 10000, // Optional: set a timeout
    headers: {
        'Content-Type': 'application/json',
    },
});

export default apiClient;