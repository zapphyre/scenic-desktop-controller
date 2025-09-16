<script setup lang="ts">
import Select from 'primevue/select';
import MultiSelect from 'primevue/multiselect';
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';
import {ref, watch} from 'vue';
import {
  ButtonEventVto,
  buttonValues,
  EKeyEvt,
  EventVto,
  GestureEventVto,
  Mode,
  multiplicityValues,
  NameId,
  XdoAction
} from "@/model/gpadOs";
import XdoActionUi from "@/components/action/XdoActionUi.vue";
import _ from "lodash";
import apiClient from "@/api";
import {getGesturesNameIdList, getSceneNameIdList, getTriggers} from "@/api/dataStore";

// Reactive state
const gestures = ref<NameId[]>([]);
const forcedAvailableRef = ref<NameId[]>();
const triggers = ref<string[]>([]);

// Local event ref, defaults to { id: -1, parentFk: selectedSceneId } if props.event is undefined
const localEvent = ref<EventVto>({
  id: -1,
  parentFk: undefined, // Will be set in watch
  actions: [],
  buttonEvent: undefined,
  gestureEvent: undefined,
  nextSceneFk: undefined
});

const props = defineProps<{
  event?: EventVto;
  disabled?: boolean | false;
  selectedSceneId: number;
  renderAction?: boolean | true;
  mode: Mode;
}>();

const emit = defineEmits<{
  removeEvent: [event: EventVto];
  updateEvent: [event: EventVto];
}>();

// Watch props.event to sync localEvent
watch(() => props.event, (newEvent) => {
  if (newEvent) {
    localEvent.value = {...newEvent, parentFk: newEvent.parentFk ?? props.selectedSceneId};
  } else {
    localEvent.value = {
      id: -1,
      parentFk: props.selectedSceneId,
      actions: [],
      buttonEvent: undefined,
      gestureEvent: undefined,
      nextSceneFk: undefined
    };
  }
}, {immediate: true});

// Watch selectedSceneId to update parentFk
watch(() => props.selectedSceneId, (newSceneId) => {
  localEvent.value.parentFk = newSceneId;
});

// Fetch initial data
watch(() => props.mode.adapterMode, async (adapterMode) => {
  gestures.value = await getGesturesNameIdList();
  triggers.value = await getTriggers();
  if (adapterMode) {
    forcedAvailableRef.value = (await getSceneNameIdList(adapterMode)).filter(q => q.id !== props.selectedSceneId);
  } else {
    forcedAvailableRef.value = [];
  }
}, {immediate: true});

const createEventIfDefault = async () => {
  console.log('localEvent.value', localEvent.value);

  if (localEvent.value.id && localEvent.value.id !== -1) return localEvent.value;

  const newEvent: EventVto = {
    id: (await apiClient.post("event", {parentFk: props.selectedSceneId})).data,
    // id: undefined,
    parentFk: props.selectedSceneId,
    actions: localEvent.value.actions,
    buttonEvent: localEvent.value.buttonEvent,
    gestureEvent: localEvent.value.gestureEvent,
    nextSceneFk: localEvent.value.nextSceneFk,
  };
  localEvent.value = newEvent;
  emit('updateEvent', newEvent); // Notify parent of new event

  return newEvent.id;
};

const addNewAction = async () => {
  if (localEvent.value.id === -1) await createEventIfDefault();
  const toSave: XdoAction = {
    eventFk: localEvent.value.id,
    id: undefined,
    keyEvt: EKeyEvt.STROKE,
    keyStrokes: [],
    activator: undefined,
  };
  toSave.id = (await apiClient.post("action", toSave)).data;

  if (!localEvent.value.actions) localEvent.value.actions = [];
  localEvent.value.actions.push(toSave);
};

const removeXdoAction = async (action: XdoAction) => {
  if (!localEvent.value.actions || localEvent.value.id === -1) return;
  await apiClient.delete("action", {data: action.id});
  _.remove(localEvent.value.actions, q => q === action);
};

const change = async () => {
  if (localEvent.value.id === -1) return;
  console.log("changed");
  localEvent.value = (await apiClient.put("event", localEvent.value)).data;
};

const addNewGesture = async () => {
  await createEventIfDefault();
  const id = (await apiClient.post(`event/${localEvent.value.id}/gesture`)).data;
  localEvent.value.gestureEvent = {id} as GestureEventVto;
};

const removeGestureFromEvent = async () => {
  if (localEvent.value.id === -1 || !localEvent.value.gestureEvent?.id) return;
  await apiClient.delete(`event/${localEvent.value.id}/gesture/${localEvent.value.gestureEvent.id}`);
  localEvent.value.gestureEvent = undefined;
};

const gestureChange = async () => {
  if (localEvent.value.id === -1 || !localEvent.value.gestureEvent) return;
  await apiClient.put(`event/${localEvent.value.id}/gesture`, localEvent.value.gestureEvent);
};

const addButtonEvent = async () => {
  await createEventIfDefault();
  const id = (await apiClient.post(`event/${localEvent.value.id}/button`)).data;
  localEvent.value.buttonEvent = {id} as ButtonEventVto;
};

const removeButtonEvent = async () => {
  if (localEvent.value.id === -1 || !localEvent.value.buttonEvent?.id) return;
  await apiClient.delete(`event/${localEvent.value.id}/button/${localEvent.value.buttonEvent.id}`);
  localEvent.value.buttonEvent = undefined;
};

const removeEvent = async () => {
  if (localEvent.value.id === -1) return;
  await apiClient.delete(`event/${localEvent.value.id}`);
  emit('removeEvent', localEvent.value);
};
</script>

<template>
  <div class="grid w-full gpad-action-container">
    <hr v-if="props.renderAction"/>

    <div class="card p-3 w-full">
      <div class="grid">
        <!-- Left Section -->
        <div :class="props.renderAction ? 'col-7' : 'col-12'">
          <div class="flex flex-column gap-3 min-h-full">
            <!-- First Row: Buttons + 3 Selects -->
            <div class="flex align-items-center">
              <Button
                  v-if="localEvent.buttonEvent"
                  :disabled="props.disabled"
                  class="p-button-danger p-button-sm"
                  icon="pi pi-trash"
                  @click="removeButtonEvent"
              />
              <Button
                  v-else
                  :disabled="props.disabled"
                  class="p-button-sm"
                  icon="pi pi-th-large"
                  @click="addButtonEvent"
              />

              <div
                  v-if="!localEvent.buttonEvent"
                  class="flex justify-content-center gap-2 flex-grow-1"
              >
                <Button
                    :disabled="props.disabled"
                    class="p-button-sm p-button-danger"
                    icon="pi pi-times-circle"
                    @click="removeEvent"
                />
              </div>

              <div
                  class="flex justify-content-center gap-2 flex-grow-1"
                  v-if="localEvent.buttonEvent"
              >
                <Select
                    v-model="localEvent.buttonEvent.trigger"
                    :options="triggers"
                    placeholder="Trigger"
                    class="w-4 input-item"
                    @change="change"
                    :disabled="props.disabled"
                />
                <Select
                    v-model="localEvent.buttonEvent.multiplicity"
                    :options="multiplicityValues"
                    placeholder="Multiplicity"
                    class="w-2 input-item"
                    @change="change"
                    :disabled="props.disabled"
                />
                <MultiSelect
                    v-model="localEvent.buttonEvent.modifiers"
                    :options="buttonValues"
                    placeholder="Modifiers"
                    class="w-4 input-item"
                    @change="change"
                    :disabled="props.disabled"
                />
              </div>
            </div>

            <!-- Second Row: Checkbox + Select -->
            <div
                class="flex justify-content-center align-items-center gap-2"
                v-if="localEvent.buttonEvent"
            >
              <div class="flex align-items-center gap-2">
                <label for="longPress">Long Press</label>
                <Checkbox
                    :disabled="props.disabled"
                    name="longPress"
                    v-model="localEvent.buttonEvent.longPress"
                    binary
                    @change="change"
                />
              </div>
              <Select
                  v-model="localEvent.nextSceneFk"
                  :options="forcedAvailableRef"
                  option-value="id"
                  option-label="name"
                  placeholder="Forced next scene"
                  class="w-6 input-item"
                  @change="change"
                  :disabled="props.disabled"
                  v-if="props.renderAction"
                  show-clear
              />
            </div>

            <!-- Third Row: Button + Dynamic Select Rows -->
            <div class="flex flex-column gap-2">
              <div class="flex align-items-center gap-2">
                <Button
                    v-if="!localEvent.gestureEvent"
                    class="p-button-sm"
                    icon="pi pi-bullseye"
                    @click="addNewGesture"
                    :disabled="props.disabled"
                />
                <Button
                    v-else
                    class="p-button-danger p-button-sm"
                    icon="pi pi-trash"
                    @click="removeGestureFromEvent"
                />
                <div
                    v-if="localEvent.gestureEvent"
                    class="flex justify-content-center align-items-center gap-2 flex-grow-1"
                >
                  <Select
                      v-model="localEvent.gestureEvent.leftStickGestureFk"
                      :options="gestures"
                      option-value="id"
                      option-label="name"
                      placeholder="Left Stick"
                      class="w-3 input-item"
                      @change="gestureChange"
                      show-clear
                  />
                  <Select
                      v-model="localEvent.gestureEvent.rightStickGestureFk"
                      :options="gestures"
                      option-value="id"
                      option-label="name"
                      placeholder="Right Stick"
                      class="w-3 input-item"
                      @change="gestureChange"
                      show-clear
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Section -->
        <div v-if="props.renderAction" class="col-5">
          <div class="flex flex-column gap-2 align-items-center min-h-full">
            <div v-for="(act, i) in localEvent.actions || []">
              <XdoActionUi
                  :key="act.id ? `desktop-${act.id}` : `desktop-no-${i}`"
                  :xdo-action="act"
                  :disabled="props.disabled"
                  @addKeyStroke="(q) => act?.keyStrokes?.push(q)"
                  @remove="removeXdoAction"
              />
            </div>

            <div class="flex justify-content-center">
              <Button
                  :disabled="props.disabled"
                  label="Add Desktop Action"
                  @click="addNewAction"
              />
<!--              <Button-->
<!--                  :disabled="props.disabled"-->
<!--                  label="Add Winder Action"-->
<!--                  @click="q => addNewAction(EAdapterMode.WINDER)()"-->
<!--              />-->
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.gpad-action-container {
  min-width: 100%;
  width: 100%;
}

.input-container {
  display: flex;
  justify-content: center; /* Center horizontally */
  align-items: center; /* Center vertically within the row */
  gap: 1rem; /* Space between elements */
  padding: 1rem; /* Optional padding */
}

.input-item {
  min-width: 9rem; /* Consistent width for Select and MultiSelect */
}

/* Adjust Checkbox alignment */
.p-checkbox {
  display: flex;
  align-items: center;
}

.ml-2 {
  margin-left: 0.5rem; /* Space between Checkbox and label */
}
</style>