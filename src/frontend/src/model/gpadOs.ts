
export interface Scene {
    id: number | undefined;
    name: string;
    windowName: string;
    leftAxisEvent: EAxisEvent;
    rightAxisEvent: EAxisEvent;
    inheritsNameFk: string | undefined;
    gamepadEvents: GPadEvent[];
    inheritedGamepadEvents: GPadEvent[];
}

export interface GPadEvent {
    id: number | undefined;
    trigger: EButtonAxisMapping | undefined;
    longPress: boolean;
    nextSceneNameFk: string | undefined;
    parentSceneFk: string | undefined;
    modifiers: EButtonAxisMapping[] | [];
    actions: XdoAction[] | [];
    multiplicity: EMultiplicity;
}

export interface XdoAction {
    id: number | undefined;
    keyEvt: EKeyEvt | undefined;
    keyPress: string | undefined;
    gamepadEventFk: number | undefined;
}

export interface KeyPart {
    keyEvt: EKeyEvt;
    keyPress: string | undefined;
}

export interface WebSourceDef {
    baseUrl: string;
    port: number;
    name: string;
}

export interface Settings {
    instanceName: string;
    allowNetworkDiscovery: boolean;
    baseSceneName: string;
    disconnectLocalOnRemoteConnection: boolean;
}

export interface SourceEvent {
    evt: ESourceEvent;
    def: WebSourceDef;
}

export enum ESourceEvent {
    APPEARED = "APPEARED",
    LOST = "LOST",
    DISCONNECTED = "DISCONNECTED",
    CONNECTED = "CONNECTED"
}

export enum EMultiplicity {
    CLICK,
    DOUBLE,
    TRIPLE,
    MULTIPLE
}

export enum EKeyEvt {
    PRESS = "PRESS",
    RELEASE = "RELEASE",
    STROKE = "STROKE",
    TIMEOUT = "TIMEOUT",
    SCENE_RESET = "SCENE_RESET",
    CLICK = "CLICK",
    MOUSE_DOWN = "MOUSE_DOWN",
    MOUSE_UP = "MOUSE_UP"
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
    .filter(key => isNaN(Number(key)))
    // .map(value => value.charAt(0).toUpperCase() + toLower(value.slice(1)))
    .map(key => (key));

export const actionValues = Object.values(EKeyEvt)
    .filter(key => isNaN(Number(key)))
    .map(key => (key));

export const multiplicityValues = Object.values(EMultiplicity)
    .filter(key => isNaN(Number(key)))
    .map(key => (key));

export const axisValues = Object.values(EAxisEvent)
    .filter(key => isNaN(Number(key)))
    .map(key => (key));