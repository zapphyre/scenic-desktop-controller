<script setup lang="ts">
import Select from 'primevue/select';
import MultiSelect from 'primevue/multiselect';
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';

import {EKeyEvt, GPadEvent, multiplicityValues, buttonValues, NameId, XdoAction} from "@/model/gpadOs";
import XdoActionSection from "@/components/action/XdoActionSection.vue";
import _ from "lodash";
import apiClient from "@/api";
import {getSceneNameIdList, getScenes, getTriggers} from "@/api/store";
import {onMounted, ref} from "vue";

const forcedAvailableRef = ref<NameId[]>();

const props = defineProps<{
  gpadEvent: GPadEvent;
  disabled?: boolean | false;
  selectedSceneId: number
}>();

const addNewAction = async () => {
  const toSave: XdoAction = {
    gamepadEventFk: props.gpadEvent.id,
    id: undefined,
    keyEvt: EKeyEvt.STROKE,
    keyStrokes: []
  };
  toSave.id = (await apiClient.post("saveXdoAction", toSave)).data;

  if (!props.gpadEvent.actions) props.gpadEvent.actions = [];

  props.gpadEvent.actions.push(toSave);
}

const removeXdoAction = async (action: XdoAction) => {
  await apiClient.delete("removeXdoAction", {data: action.id});
  _.remove(props.gpadEvent.actions, q => q === action);
}

const change = async () => {
  console.log("changed");
  await apiClient.put("updateGamepadEvent", props.gpadEvent);
}

const emit = defineEmits<{
  remove: [gPadEvent: GPadEvent];
}>();

onMounted(() => {
  forcedAvailableRef.value = getSceneNameIdList().filter(q => q.id !== props.selectedSceneId)
  forcedAvailableRef.value.unshift({id: undefined, name: "[unset]"});
})
</script>

<template>
  <hr/>

  <div class="grid nested-grid">
    <div class="card flex flex-wrap justify-center gap-4">
      <div class="flex items-center gap-2">

        <div class="col-6">
          <div class="col-12" style="width: 100%">
            <div class="grid">
              <div class="col-1">
                <Button :disabled="disabled" @click="() => emit('remove', props.gpadEvent)" icon="pi pi-trash"/>
              </div>
              <div class="col-4">
                <Select
                    v-model="props.gpadEvent.trigger"
                    :options="getTriggers()"
                    placeholder="Trigger"
                    class="input-item"
                    @change="change"
                    :disabled="disabled"
                />
              </div>
              <div class="col-4">
                <Select
                    v-model="props.gpadEvent.multiplicity"
                    :options="multiplicityValues"
                    placeholder="Multiplicity"
                    class="input-item"
                    @change="change"
                    :disabled="disabled"
                />
              </div>
              <div class="col-3">
                <MultiSelect
                    v-model="props.gpadEvent.modifiers"
                    :options="buttonValues"
                    placeholder="Modifiers"
                    class="input-item"
                    @change="change"
                    :disabled="disabled"
                />
              </div>

              <div class="col-5">
                <div class="flex flex-wrap justify-content-end gap-5">
                  <div class="flex align-items-end gap-2">
                    <label for="longPress">Long Press</label>
                    <Checkbox :disabled="disabled" name="longPress" v-model="props.gpadEvent.longPress" binary
                              @change="change"
                              label="Long Press"/>
                  </div>
                </div>
              </div>

              <div class="col-7">
                <div class="flex justify-content-center align-items-center justify-center gap-4">
                  <Select
                      v-model="props.gpadEvent.nextSceneFk"
                      :options="forcedAvailableRef"
                      option-value="id"
                      option-label="name"
                      placeholder="Forced next scene"
                      class="input-item"
                      @change="change"
                      :disabled="disabled"
                  />
                </div>
              </div>

            </div>
          </div>
        </div>

        <div class="col-6" style="min-width: 552px">
          <XdoActionSection v-for="act in props.gpadEvent.actions"
                            :xdo-action="act"
                            :disabled="disabled"
                            @addKeyStroke="q => act?.keyStrokes?.push(q)"
                            @remove="removeXdoAction"/>
          <div class="flex justify-content-center align-items-center justify-center">
            <Button :disabled="disabled" @click="addNewAction">Add Action</Button>
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