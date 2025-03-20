import apiClient from "@/api";
import { ref } from "vue";

// Singleton state
const strokes = ref([]); // Initialize as empty array
let isInitialized = false; // Track if data has been fetched

// Fetch strokes from API (called only once)
const fetchStrokes = async () => {
    if (isInitialized) return; // Skip if already fetched

    try {
        const response = await apiClient.get("xdoStrokes");
        strokes.value = response.data; // Assuming response.data is an array of strings
        isInitialized = true;
    } catch (error) {
        console.error("Failed to fetch strokes:", error);
        strokes.value = []; // Fallback to empty array on error
    }
};

// Initialize the data immediately when the module is imported
fetchStrokes();

// Export a function to access the strokes
export const useStrokesStore = () => {
    return {
        strokes: strokes.value, // Synchronous access to the array
        strokesRef: strokes, // Optional: expose the ref for reactivity if needed
        reloadStrokes: fetchStrokes, // Optional: allow manual reload
    };
};

// Optional: Export strokes directly if you just want the array
export const getStrokes = () => strokes.value;