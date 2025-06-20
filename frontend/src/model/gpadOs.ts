export interface Scene {
    id: number | undefined;
    name: string;
    windowName: string;
    leftAxisEvent: EAxisEvent;
    rightAxisEvent: EAxisEvent;
    inheritsIdFk: number[] | undefined;
    events: EventVto[];
    inheritedGamepadEvents: EventVto[];
}

export interface NameId {
    name: string;
    id?: number;
}

export interface EventVto {
    id: number | undefined;
    gestureEvent?: GestureEventVto;
    buttonEvent?: ButtonEventVto;
    parentFk: number | null;
    nextSceneFk: number | null;
    actions: XdoAction[];
}

export interface GestureEventVto {
    id: number | undefined;
    leftStickGestureFk?: number;
    rightStickGestureFk?: number;
}

export interface Gesture {
    id: number | undefined;
    name: string;
    paths: GesturePath[]
}

export interface GesturePath {
    id: number | undefined;
    path: string;
    edit: boolean;
}

export interface ButtonEventVto {
    id: number | null;
    trigger: string;
    longPress: boolean;
    multiplicity: EMultiplicity;
    modifiers: string[];
}

export interface GPadEvent {
    id: number | undefined;
    trigger: EButtonAxisMapping | undefined;
    longPress: boolean;
    nextSceneFk: number | undefined;
    parentFk: number | undefined;
    modifiers: EButtonAxisMapping[] | [];
    actions: XdoAction[];
    multiplicity: EMultiplicity;
}

export interface XdoAction {
    id: number | undefined;
    keyEvt: EKeyEvt | undefined;
    keyStrokes: string[];
    eventFk: number | undefined;
    activator: EButtonAxisMapping | undefined;
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
    ipAddress: string | undefined;
    ipSetManually: boolean;
    port: string | undefined;
    hintedIpAddress: string | undefined;
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
    MOUSE_UP = "MOUSE_UP",
    BUTTON = "BUTTON",
    KEYBOARD_ON = "KEYBOARD_ON",
    KEYBOARD_OFF = "KEYBOARD_OFF",
    KEYBOARD_LONG = "KEYBOARD_LONG",
    WINDER_FF = "WINDER_FF",
    WINDER_RW = "WINDER_RW",
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
    MOUSE, SCROLL, VOL, NOOP, DEFAULT
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