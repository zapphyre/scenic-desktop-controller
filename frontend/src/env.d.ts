interface ImportMetaEnv {
    readonly VITE_API_BASE_URL: string;
    readonly MODE: string;
    // Add other .env variables here
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}