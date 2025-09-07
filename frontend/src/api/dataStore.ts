import { ref } from "vue";
import apiClient from "@/api";
import type { Gesture, Lang, Mode, NameId, Scene, Settings } from "@/model/gpadOs";

// ========== STATE ==========

const strokes = ref<string[]>([]);
const winderActions = ref<string[]>([]);
const scenesMap = ref<Record<string, Scene[]>>({});
const triggers = ref([]);
const gestures = ref<Gesture[]>([]);
const languages = ref<Lang[]>([]);
const gesturesNameId = ref<NameId[]>([]);
const sceneNameIdList = ref<Record<string, NameId[]>>({});
const settings = ref<Settings | null>(null);
const modes = ref<Mode[]>([]);
const verbs = ref<Record<string, string[]>>({});
const nouns = ref<Record<string, string[]>>({});

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

const fetchScenes = async (mode: string) => {
    try {
        scenesMap.value[mode] = (await apiClient.get(`scene/all/${mode}`)).data;
    } catch (error) {
        console.error(`Failed to fetch scenes for mode ${mode}:`, error);
        scenesMap.value[mode] = [];
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

const fetchSettings = async () => {
    try {
        settings.value = (await apiClient.get("settings")).data;
    } catch (e) {
        console.error("Failed to fetch settings:", e);
        settings.value = null;
    }
};

const fetchModes = async () => {
    try {
        modes.value = (await apiClient.get("mode/all")).data;
    } catch (error) {
        console.error("Failed to fetch modes:", error);
        modes.value = [];
    }
};

const fetchVerbs = async (mode: string) => {
    try {
        verbs.value[mode] = (await apiClient.get(`mode/verbs/${mode}`)).data;
    } catch (error) {
        console.error(`Failed to fetch verbs for mode ${mode}:`, error);
        verbs.value[mode] = [];
    }
};

const fetchNouns = async (mode: string) => {
    try {
        nouns.value[mode] = (await apiClient.get(`mode/nouns/${mode}`)).data;
    } catch (error) {
        console.error(`Failed to fetch nouns for mode ${mode}:`, error);
        nouns.value[mode] = [];
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

export const getScenes = async (mode: string) => {
    if (!scenesMap.value[mode] || !scenesMap.value[mode].length) {
        await fetchScenes(mode);
    }
    return scenesMap.value[mode] || [];
};

export const getSceneNameIdList = async (mode: string) => {
    const sc = await getScenes(mode);
    if (!sceneNameIdList.value[mode] || !sceneNameIdList.value[mode].length) {
        sceneNameIdList.value[mode] = sc.map((s) => ({ name: s.name, id: s.id }));
    }
    return sceneNameIdList.value[mode] || [];
};

export const getVerbs = async (mode: string) => {
    if (!verbs.value[mode] || !verbs.value[mode].length) {
        await fetchVerbs(mode);
    }
    return verbs.value[mode] || [];
};

export const getNouns = async (mode: string) => {
    if (!nouns.value[mode] || !nouns.value[mode].length) {
        await fetchNouns(mode);
    }
    return nouns.value[mode] || [];
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

export const getModes = async () => {
    if (!modes.value.length) {
        await fetchModes();
    }
    return modes.value;
};

export const addGesture = (g: Gesture) => {
    gestures.value.push(g);
    gesturesNameId.value.push({ name: g.name, id: g.id });
};

export const addLanguage = (l: Lang) => {
    languages.value.push(l);
};

export const getSettings = async () => {
    if (!settings.value) {
        await fetchSettings();
    }
    return settings.value;
};

export const updateSettings = async (newSettings: Settings) => {
    try {
        return settings.value = (await apiClient.put("settings", newSettings)).data;
    } catch (e) {
        console.error("Failed to update settings:", e);
    }
};