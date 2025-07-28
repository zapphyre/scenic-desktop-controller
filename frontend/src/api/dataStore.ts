import apiClient from "@/api";
import type {Gesture, Lang, NameId, Scene} from "@/model/gpadOs";
import { ref } from "vue";

// ========== STATE ==========

const strokes = ref<string[]>([]);
const winderActions = ref<string[]>([]);
const scenes = ref<Scene[]>([]);
const triggers = ref([]);
const gestures = ref<Gesture[]>([]);
const languages = ref<Lang[]>([]);

const gesturesNameId = ref<NameId[]>([]);
const sceneNameIdList = ref<NameId[]>([]);

// ========== FETCH METHODS ==========

const fetchStrokes = async () => {
    try {
        const response = await apiClient.get("action/all/STROKE");
        strokes.value = response.data;
    } catch (error) {
        console.error("Failed to fetch desktop_actions:", error);
        strokes.value = [];
    }
};

const fetchWinderActions = async () => {
    try {
        const response = await apiClient.get("action/all/WINDER");
        winderActions.value = response.data;
    } catch (error) {
        console.error("Failed to fetch winder_actions:", error);
        winderActions.value = [];
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
};

const fetchGestures = async () => {
    try {
        gestures.value = (await apiClient.get("gestures")).data;
    } catch (e) {
        console.error("Failed to fetch gestures:", e);
    }
};

const fetchLanguages = async () => {
    try {
        languages.value = (await apiClient.get("languages/all")).data;
    } catch (e) {
        console.error("Failed to fetch languages:", e);
    }
};

// ========== PUBLIC API ==========

export const useStrokesStore = () => {
    return {
        strokes: strokes.value,
        strokesRef: strokes,
        reloadStrokes: fetchStrokes,
    };
};

export const getStrokes = async () => {
    if (!strokes.value.length) {
        await fetchStrokes();
    }
    return strokes.value;
};

export const getWinderActions = async () => {
    if (!winderActions.value.length) {
        await fetchWinderActions();
    }
    return winderActions.value;
};

export const getScenes = async () => {
    if (!scenes.value.length) {
        await fetchScenes();
    }
    return scenes.value;
};

export const getSceneNameIdList = async () => {
    const sc = await getScenes();
    if (!sceneNameIdList.value.length) {
        sceneNameIdList.value = sc.map((s) => ({ name: s.name, id: s.id }));
    }
    return sceneNameIdList.value;
};

export const getTriggers = async () => {
    if (!triggers.value.length) {
        await fetchTriggers();
    }
    return triggers.value;
};

export const getGestures = async () => {
    if (!gestures.value.length) {
        await fetchGestures();
    }
    return gestures.value;
};

export const getGesturesNameIdList = async () => {
    const list = await getGestures();
    if (!gesturesNameId.value.length) {
        gesturesNameId.value = list.map((g) => ({ name: g.name, id: g.id }));
    }
    return gesturesNameId.value;
};

export const getLanguages = async () => {
    if (!languages.value.length) {
        await fetchLanguages();
    }
    return languages.value;
};

export const addGesture = (g: Gesture) => {
    gestures.value.push(g);
    gesturesNameId.value.push({ name: g.name, id: g.id });
};

export const addLanguage = (l: Lang) => {
    languages.value.push(l);
};
