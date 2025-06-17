<script setup lang="ts">
import Select from 'primevue/select';
import MultiSelect from 'primevue/multiselect';
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';

import {
  ButtonEventVto,
  buttonValues,
  EKeyEvt,
  EventVto, Gesture,
  GestureEventVto,
  multiplicityValues,
  NameId,
  XdoAction
} from "@/model/gpadOs";
import XdoActionSection from "@/components/action/XdoActionSection.vue";
import _ from "lodash";
import apiClient from "@/api";
import {getGestures, getGesturesNameIdList, getSceneNameIdList, getTriggers} from "@/api/dataStore";
import {onMounted, ref} from "vue";

const gestures = getGesturesNameIdList();

const forcedAvailableRef = ref<NameId[]>();

const props = defineProps<{
  event: EventVto;
  disabled?: boolean | false;
  selectedSceneId: number
}>();

const addNewAction = async () => {
  const toSave: XdoAction = {
    eventFk: props.event.id,
    id: undefined,
    keyEvt: EKeyEvt.STROKE,
    keyStrokes: []
  };
  toSave.id = (await apiClient.post("action", toSave)).data;

  if (!props.event.actions) props.event.actions = [];

  props.event.actions.push(toSave);
}

const removeXdoAction = async (action: XdoAction) => {
  await apiClient.delete("action", {data: action.id});
  _.remove(props.event.actions, q => q === action);
}

const change = async () => {
  console.log("changed");
  await apiClient.put("event", props.event);
}

const addNewGesture = async () => {
  const id = (await apiClient.post(`event/${props.event.id}/gesture`)).data;
  const gestEvt = {id} as GestureEventVto;

  props.event.gestureEvent = gestEvt;
}

const removeGestureFromEvent = async () => {
  await apiClient.delete(`event/${props.event.id}/gesture/${props.event.gestureEvent?.id}`);

  props.event.gestureEvent = undefined;
}

const gestureChange = async () => {
  await apiClient.put(`event/${props.event.id}/gesture`, props.event.gestureEvent);
}

const addButtonEvent = async  () => {
  const id = (await apiClient.post(`event/${props.event.id}/button`)).data;

  props.event.buttonEvent = {id} as ButtonEventVto;
}

const removeButtonEvent = async  () => {
  await apiClient.delete(`event/${props.event.id}/button/${props.event.buttonEvent?.id}`);

  props.event.buttonEvent = undefined;
}

const removeEvent = async  () => {
  await apiClient.delete(`event/${props.event.id}`);

  emit('removeEvent', props.event);
}

const emit = defineEmits<{
  removeEvent: [event: EventVto]
}>();

onMounted(() => {
  forcedAvailableRef.value = getSceneNameIdList().filter(q => q.id !== props.selectedSceneId)
})
</script>

<template>
  <hr />

  <div class="grid w-full gpad-action-container">
    <div class="card p-3 w-full">
      <div class="grid">
        <!-- Left Section -->
        <div class="col-8">
          <div class="flex flex-column gap-3 min-h-full">
            <!-- First Row: Buttons + 3 Selects -->
            <div class="flex align-items-center">
              <Button
                  v-if="props.event.buttonEvent"
                  :disabled="disabled"
                  class="p-button-danger p-button-sm"
                  icon="pi pi-trash"
                  @click="removeButtonEvent"
              />
              <Button
                  v-else
                  :disabled="disabled"
                  class="p-button-sm"
                  icon="pi pi-th-large"
                  @click="addButtonEvent"
              />

              <div
                  v-if="!props.event.buttonEvent"
                  class="flex justify-content-center gap-2 flex-grow-1"
              >
                <Button
                    :disabled="disabled"
                    class="p-button-sm p-button-danger"
                    icon="pi pi-times-circle"
                    @click="removeEvent"
                />
              </div>

              <div
                  class="flex justify-content-center gap-2 flex-grow-1"
                  v-if="props.event.buttonEvent"
              >
                <Select
                    v-model="props.event.buttonEvent.trigger"
                    :options="getTriggers()"
                    placeholder="Trigger"
                    class="w-4 input-item"
                    @change="change"
                    :disabled="disabled"
                />
                <Select
                    v-model="props.event.buttonEvent.multiplicity"
                    :options="multiplicityValues"
                    placeholder="Multiplicity"
                    class="w-2 input-item"
                    @change="change"
                    :disabled="disabled"
                />
                <MultiSelect
                    v-model="props.event.buttonEvent.modifiers"
                    :options="buttonValues"
                    placeholder="Modifiers"
                    class="w-4 input-item"
                    @change="change"
                    :disabled="disabled"
                />
              </div>
            </div>

            <!-- Second Row: Checkbox + Select -->
            <div
                class="flex justify-content-center align-items-center gap-2"
                v-if="props.event.buttonEvent"
            >
              <div class="flex align-items-center gap-2">
                <label for="longPress">Long Press</label>
                <Checkbox
                    :disabled="disabled"
                    name="longPress"
                    v-model="props.event.buttonEvent.longPress"
                    binary
                    @change="change"
                />
              </div>
              <Select
                  v-model="props.event.nextSceneFk"
                  :options="forcedAvailableRef"
                  option-value="id"
                  option-label="name"
                  placeholder="Forced next scene"
                  class="w-6 input-item"
                  @change="change"
                  :disabled="disabled"
                  show-clear
              />
            </div>

            <!-- Third Row: Button + Dynamic Select Rows -->
            <div class="flex flex-column gap-2">
              <div class="flex align-items-center gap-2">
                <Button
                    v-if="!props.event.gestureEvent"
                    class="p-button-sm"
                    icon="pi pi-bullseye"
                    @click="addNewGesture"
                    :disabled="disabled"
                />
                <Button
                    v-else
                    class="p-button-danger p-button-sm"
                    icon="pi pi-trash"
                    @click="removeGestureFromEvent"
                />
                <div
                    v-if="props.event.gestureEvent"
                    class="flex justify-content-center align-items-center gap-2 flex-grow-1"
                >
                  <Select
                      v-model="props.event.gestureEvent.leftStickGestureFk"
                      :options="gestures"
                      option-value="id"
                      option-label="name"
                      placeholder="Left Stick"
                      class="w-3 input-item"
                      @change="gestureChange"
                      show-clear
                  />
                  <Select
                      v-model="props.event.gestureEvent.rightStickGestureFk"
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
        <div class="col-4">
          <div class="flex flex-column gap-2 align-items-center min-h-full">
            <XdoActionSection
                v-for="act in props.event.actions || []"
                :key="act.id"
                :xdo-action="act"
                :disabled="disabled"
                @addKeyStroke="(q) => act?.keyStrokes?.push(q)"
                @remove="removeXdoAction"
            />
            <div class="flex justify-content-center">
              <Button
                  :disabled="disabled"
                  label="Add Action"
                  @click="addNewAction"
              />
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