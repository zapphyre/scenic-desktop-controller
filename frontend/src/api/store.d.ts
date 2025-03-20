declare module '@/api/store' {
    export function useStrokesStore(): {
        strokes: string[];
        strokesRef: import('vue').Ref<string[]>;
        reloadStrokes: () => Promise<void>;
    };
    export function getStrokes(): string[];
}