import axios from "axios";

console.log('Mode:', import.meta.env.MODE);
console.log('Env:', import.meta.env);
console.log('VITE_API_BASE_URL:', import.meta.env.VITE_API_BASE_URL);

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL, // Dynamically set from .env
    timeout: 10000, // Optional: set a timeout
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor
apiClient.interceptors.request.use(
    (config) => {
        console.log('Request URL:', config.url);
        console.log('Request Method:', config.method);
        console.log('Request Headers:', config.headers);
        console.log('Request Data:', config.data); // Payload/body (e.g., POST/PUT)
        console.log('Full Request Config:', config); // Entire config object
        return config;
    },
    (error) => {
        console.error('Request Error:', error);
        return Promise.reject(error);
    }
);

export default apiClient;