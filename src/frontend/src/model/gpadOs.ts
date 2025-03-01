
export interface Scene {
    name: string;
    windowName: string;
    leftAxisEvent: EAxisEvent;
    rightAxisEvent: EAxisEvent;
    inherits: Scene;
    gpadEvents: GPadEvent[];
}

export interface GPadEvent {
    id: number;
    trigger: EButtonAxisMapping;
    longPress: boolean;
    nextScene: Scene;
    modifiers: EButtonAxisMapping[];
    actions: XdoAction[];
    multiplicity: EMultiplicity;
}

export interface XdoAction {
    id: number;
    keyEvt: EKeyEvt;
    keyPress: string;
}

export enum EMultiplicity {
    CLICK,
    DOUBLE,
    TRIPLE,
    MULTIPLE
}

export enum EKeyEvt {
    PRESS,
    RELEASE,
    STROKE,
    TIMEOUT,
    SCENE_RESET,
    CLICK,
    MOUSE_DOWN,
    MOUSE_UP
}

export enum EButtonAxisMapping {
    A,
    B,
    X,
    Y,
    SELECT,
    START,
    LEFT_STICK_CLICK,
    RIGHT_STICK_CLICK,
    LEFT_STICK_X,
    LEFT_STICK_Y,
    RIGHT_STICK_X,
    RIGHT_STICK_Y,
    UP,
    DOWN,
    LEFT,
    RIGHT,
    TRIGGER_LEFT,
    TRIGGER_RIGHT,
    BUMPER_LEFT,
    BUMPER_RIGHT,
    OTHER
}

export enum EAxisEvent {
    MOUSE, SCROLL, VOL, INHERITED, NOOP
}

export const buttonValues = Object.values(EButtonAxisMapping)
    .filter(key => isNaN(Number(key))) // Filter out numeric keys
    // .map(value => value.charAt(0).toUpperCase() + toLower(value.slice(1)))
    .map(key => (key));
    // .map(key => ({name: key}));

export const actionValues = Object.values(EKeyEvt)
    .filter(key => isNaN(Number(key))) // Filter out numeric keys
    // .map(value => value.charAt(0).toUpperCase() + toLower(value.slice(1)))
    .map(key => (key));
    // .map(key => ({name: key}));



export const multiplicityValues = Object.values(EMultiplicity)
    .filter(key => isNaN(Number(key)))
    .map(key => (key));