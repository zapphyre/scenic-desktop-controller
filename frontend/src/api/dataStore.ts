import apiClient from "@/api";
import type {Gesture, Lang, NameId, Scene} from "@/model/gpadOs";
import { ref } from "vue";

const unset = {id: undefined, name: "[unset]"};

// Singleton state
const strokes = ref([]); // Initialize as empty array
const scenes = ref<Scene[]>([]);
const triggers = ref([]);
const gestures = ref<Gesture[]>([]);
const languages = ref<Lang[]>([]);

const gesturesNameId = ref<NameId[]>([]);
const sceneNameIdList = ref<NameId[]>([]);

let isInitialized = false; // Track if data has been fetched

// Fetch strokes from API (called only once)
const fetchStrokes = async () => {
    if (isInitialized) return; // Skip if already fetched

    try {
        const response = await apiClient.get("action/all");
        strokes.value = response.data; // Assuming response.data is an array of strings
        isInitialized = true;
    } catch (error) {
        console.error("Failed to fetch strokes:", error);
        strokes.value = []; // Fallback to empty array on error
    }
};


const fetchScenes = async () => {
    try {
        scenes.value = (await apiClient.get("scene/all")).data;
    } catch (error) {
        console.error("Failed to fetch scenes:", error);
    }
};

const fetchTriggers = async () => {
    try {
        triggers.value = (await apiClient.get("scene/triggers")).data;
    } catch (error) {
        console.error("Failed to fetch triggers:", error);
    }
}

const fetchGestures = async () => {
    try {
        gestures.value = (await apiClient.get("gestures")).data;
    } catch (e) {
        console.error("Failed to fetch gestures:", e);
    }
}

const fetchLanguages = async () => {
    try {
        languages.value = (await apiClient.get("languages/all")).data;
    } catch (e) {
        console.error("Failed to fetch gestures:", e);
    }
}

// Initialize the data immediately when the module is imported
await fetchStrokes();
await fetchScenes();
await fetchTriggers();
await fetchGestures();
await fetchLanguages();

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
export const getSceneNameIdList = () => !sceneNameIdList.value.length ?
    scenes.value.map((s) => ({name: s.name, id: s.id})) :
    sceneNameIdList.value;

export const getTriggers = () => triggers.value;
export const getGestures = () => gestures.value;

export const getGesturesNameIdList = () => !gesturesNameId.value.length ?
    gesturesNameId.value = gestures.value.map((s) => ({name: s.name, id: s.id})) :
    gesturesNameId.value;

export const addGesture = (g: Gesture) => {
    gestures.value.push(g);
    gesturesNameId.value.push({name: g.name, id: g.id});
}

export const getLanguages = () => languages.value
export const addLanguage = languages.value.push;