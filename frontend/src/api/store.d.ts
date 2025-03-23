declare module '@/api/store' {
    import {NameId, Scene} from "@/model/gpadOs";

    export function useStrokesStore(): {
        strokes: string[];
        strokesRef: import('vue').Ref<string[]>;
        reloadStrokes: () => Promise<void>;
    };
    export function getStrokes(): string[];
    export function getScenes(): Scene[];
    export function getSceneNameIdList(): NameId[];
}