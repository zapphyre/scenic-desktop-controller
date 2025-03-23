import apiClient from "@/api";
// import Scene from "@/GpadOs.vue";
import { ref } from "vue";

// Singleton state
const strokes = ref([]); // Initialize as empty array
const scenes = ref([]);
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


const fetchScenes = async () => {
    // if (!scenes.value) return; // Skip if already fetched

    try {
        // console.log("_______________getting scenes");
        scenes.value = (await apiClient.get("allScenes")).data;
        // console.log("fater fetch scenes", scenes.value);
    } catch (error) {
        console.error("Failed to fetch scenes:", error);
    }
};

// Initialize the data immediately when the module is imported
await fetchStrokes();
await fetchScenes();

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
export const getScenes = () => scenes.value;
export const getSceneNameIdList = () => scenes.value.map((s) => ({name: s.name, id: s.id}));