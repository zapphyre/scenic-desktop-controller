declare module '@/api/store' {
    import {Gesture, NameId, Scene} from "@/model/gpadOs";

    export function useStrokesStore(): {
        strokes: string[];
        strokesRef: import('vue').Ref<string[]>;
        reloadStrokes: () => Promise<void>;
    };
    export function getStrokes(): string[];
    export function getScenes(): Scene[];
    export function getSceneNameIdList(): NameId[];
    export function getTriggers(): string[];
    export function getGestures(): Gesture[];
}