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

console.log('gestures', gestures);

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
  toSave.id = (await apiClient.post("saveXdoAction", toSave)).data;

  if (!props.event.actions) props.event.actions = [];

  props.event.actions.push(toSave);
}

const removeXdoAction = async (action: XdoAction) => {
  await apiClient.delete("removeXdoAction", {data: action.id});
  _.remove(props.event.actions, q => q === action);
}

const change = async () => {
  console.log("changed");
  await apiClient.put("updateGamepadEvent", props.event);
}

const addNewGesture = async (event: EventVto) => {
  const id = (await apiClient.post(`event/${event.id}/gesture`)).data;
  const gestEvt = {id} as GestureEventVto;

  event.gestureEvent = gestEvt;
}

const removeGestureFromEvent = async (event: EventVto) => {
  await apiClient.delete(`event/gesture/${event.gestureEvent?.id}`);

  event.gestureEvent = undefined;
}

const gestureChange = async () => {
  await apiClient.put(`event/gesture`, props.event.gestureEvent);


}

const emit = defineEmits<{
  removeButtonEvt: [buttonEvent: ButtonEventVto],
  removeGestureEvt: [gestureEvent: GestureEventVto]
}>();

onMounted(() => {
  forcedAvailableRef.value = getSceneNameIdList().filter(q => q.id !== props.selectedSceneId)
})
</script>

<template>
  <hr />

  <div class="grid w-full">
    <div class="card p-3 w-full">
      <div class="grid">
        <!-- Left Section -->
        <div class="col-6" v-if="props.event.buttonEvent">
          <div class="flex flex-column gap-3">
            <!-- First Row: Button + 3 Selects -->
            <div class="flex align-items-center gap-2">
              <Button
                  :disabled="disabled"
                  class="p-button-danger p-button-sm"
                  icon="pi pi-trash"
                  @click="() => emit('removeButtonEvt', props.event.buttonEvent!!)"
              />
              <div class="flex justify-content-center gap-2 flex-grow-1">
                <Select
                    v-model="props.event.buttonEvent.trigger"
                    :options="getTriggers()"
                    placeholder="Trigger"
                    class="w-3 input-item"
                    @change="change"
                    :disabled="disabled"
                />
                <Select
                    v-model="props.event.buttonEvent.multiplicity"
                    :options="multiplicityValues"
                    placeholder="Multiplicity"
                    class="w-3 input-item"
                    @change="change"
                    :disabled="disabled"
                />
                <MultiSelect
                    v-model="props.event.buttonEvent.modifiers"
                    :options="buttonValues"
                    placeholder="Modifiers"
                    class="w-3 input-item"
                    @change="change"
                    :disabled="disabled"
                />
              </div>
            </div>

            <!-- Second Row: Checkbox + Select -->
            <div class="flex justify-content-center align-items-center gap-2">
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
              />
            </div>

            <!-- Third Row: Button + Dynamic Select Rows -->
            <div class="flex flex-column gap-2">
              <div class="flex align-items-center gap-2">
                <Button
                    v-if="!props.event.gestureEvent"
                    class="p-button-success p-button-sm"
                    icon="pi pi-plus"
                    @click="addNewGesture(props.event)"
                />
                <Button
                    v-if="props.event.gestureEvent"
                    class="p-button-danger p-button-sm"
                    icon="pi pi-trash"
                    @click="removeGestureFromEvent(props.event)"
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
        <div class="col-6">
          <div class="flex flex-column gap-2 align-items-center">
            <XdoActionSection
                v-for="act in props.event.actions"
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