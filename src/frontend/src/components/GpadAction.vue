<script setup lang="ts">
import Select from 'primevue/select';
import MultiSelect from 'primevue/multiselect';
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';

import type {GPadEvent, XdoAction} from "@/model/gpadOs";
import {buttonValues, multiplicityValues} from '@/model/gpadOs';
import XdoActionSection from "@/components/XdoActionSection.vue";
import _ from "lodash";

const props = defineProps<{
  action: GPadEvent;
}>();

const addNewAction = () => props.action.actions.push({} as XdoAction);
const updateAction = (action: XdoAction): void => {
  console.log("updated", action);
}
const removeAction = (action: XdoAction): void => {
  _.remove(props.action.actions, q => q === action);
}
</script>

<template>
  <hr/>

  <div class="grid nested-grid">
    <div class="col-6">

      <div class="grid">
        <div class="col-4">
          <div class="card flex flex-wrap justify-center gap-4">
            <div class="flex items-center gap-2">
              <Select
                  v-model="action.trigger"
                  :options="buttonValues"
                  placeholder="Trigger"
                  class="input-item"
              />
            </div>
          </div>
        </div>
        <div class="col-4">
          <div class="card flex flex-wrap justify-center gap-4">
            <div class="flex items-center gap-2">
              <Select
                  :options="multiplicityValues"
                  placeholder="Multiplicity"
                  class="input-item"
              />
            </div>
          </div>
        </div>
        <div class="col-4">
          <div class="card flex flex-wrap justify-center gap-4">
            <div class="flex items-center gap-2">
              <MultiSelect
                  v-model="action.modifiers"
                  :options="buttonValues"
                  placeholder="Modifiers"
                  class="input-item"
              />
            </div>
          </div>
        </div>

        <div class="col-4">
          <div class="flex flex-wrap justify-content-end gap-5">
            <div class="flex align-items-end gap-2">
              <label for="longPress">Long Press</label>
              <Checkbox name="longPress" v-model="action.longPress" binary label="Long Press"/>
            </div>
          </div>
        </div>

        <div class="col-8">
          <div class="flex justify-content-center align-items-center justify-center gap-4">
            <Select
                v-model="action.nextScene"
                placeholder="Forced next scene"
                class="input-item"
            />
          </div>
        </div>

      </div>

    </div>
    <div class="col-6">
      <div class="card">
        <XdoActionSection v-for="act in action.actions" :xdo-action="act" :change="updateAction"
                          :remove="removeAction"/>
      </div>
      <div class="flex justify-content-center align-items-center justify-center gap-4">
        <Button @click="addNewAction">Add Action</Button>
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


.grid {
  width: 100%; /* Ensure grid takes full width */
  justify-content: start; /* Align grid items to the left */
}

.input-item {
  min-width: 11rem; /* Consistent width for Select and MultiSelect */
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