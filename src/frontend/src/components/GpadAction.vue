<script setup lang="ts">
import Select from 'primevue/select';
import MultiSelect from 'primevue/multiselect';
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';

import type {GPadEvent, XdoAction} from "@/model/gpadOs";
import {buttonValues, multiplicityValues} from '@/model/gpadOs';
import XdoActionSection from "@/components/XdoActionSection.vue";
import _ from "lodash";
import apiClient from "@/api";
import {watch} from "vue";

const props = defineProps<{
  gpadEvent: GPadEvent;
}>();

watch(props.gpadEvent, (q) => apiClient.put("updateGamepadEvent", props.gpadEvent));

const addNewAction = async () => {
  const id = (await apiClient.post("saveXdoAction", {})).data;
  props.gpadEvent.actions.push({id} as XdoAction);
}
const updateAction = (action: XdoAction): void => {
  console.log("updated", action);
}
const removeAction = (action: XdoAction): void => {
  _.remove(props.gpadEvent.actions, q => q === action);
}
</script>

<template>
  <hr/>

  <div class="grid nested-grid">
    <div class="card flex flex-wrap justify-center gap-4">
      <div class="flex items-center gap-2">

        <div class="col-6">
          <div class="col-12">
            <div class="grid">
              <div class="col-4">
                <Select
                    v-model="gpadEvent.trigger"
                    :options="buttonValues"
                    placeholder="Trigger"
                    class="input-item"
                />
              </div>
              <div class="col-4">
                <Select
                    v-model="gpadEvent.multiplicity"
                    :options="multiplicityValues"
                    placeholder="Multiplicity"
                    class="input-item"
                />
              </div>
              <div class="col">
                <MultiSelect
                    v-model="gpadEvent.modifiers"
                    :options="buttonValues"
                    placeholder="Modifiers"
                    class="input-item"
                />
              </div>

              <div class="col-4">
                <div class="flex flex-wrap justify-content-end gap-5">
                  <div class="flex align-items-end gap-2">
                    <label for="longPress">Long Press</label>
                    <Checkbox name="longPress" v-model="gpadEvent.longPress" binary label="Long Press"/>
                  </div>
                </div>
              </div>

              <div class="col-8">
                <div class="flex justify-content-center align-items-center justify-center gap-4">
                  <Select
                      v-model="gpadEvent.nextScene"
                      placeholder="Forced next scene"
                      class="input-item"
                  />
                </div>
              </div>

            </div>
          </div>
        </div>

        <div class="col-6">
          <XdoActionSection v-for="act in gpadEvent.actions" :xdo-action="act" :change="updateAction"
                            :remove="removeAction"/>
          <div class="flex justify-content-center align-items-center justify-center">
            <Button @click="addNewAction">Add Action</Button>
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