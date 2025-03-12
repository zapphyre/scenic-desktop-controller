<script setup lang="ts">
  import Select from 'primevue/select';
import MultiSelect from 'primevue/multiselect';
import Checkbox from 'primevue/checkbox';
import Button from 'primevue/button';

import {buttonValues, EKeyEvt, GPadEvent, multiplicityValues, XdoAction} from "@/model/gpadOs";
import XdoActionSection from "@/components/action/XdoActionSection.vue";
import _ from "lodash";
import apiClient from "@/api";
import {watch} from "vue";

const props = defineProps<{
  gpadEvent: GPadEvent;
  disabled?: boolean | false;
}>();

watch(props.gpadEvent, async (q) => {
  console.log('sending update', props.gpadEvent);
  await apiClient.put("updateGamepadEvent", props.gpadEvent)
});

const addNewAction = async () => {
  const toSave: XdoAction = {
    gamepadEventFk: props.gpadEvent.id,
    id: undefined,
    keyEvt: EKeyEvt.STROKE,
    keyPress: undefined
  };
  toSave.id = (await apiClient.post("saveXdoAction", toSave)).data;
  props.gpadEvent.actions.push(toSave);
}

const removeXdoAction = async (action: XdoAction) => {
  await apiClient.delete("removeXdoAction", {data: action.id});
  _.remove(props.gpadEvent.actions, q => q === action);
}

const emit = defineEmits<{
  remove: [gPadEvent: GPadEvent];
}>();

</script>

<template>
  <hr/>

  <div class="grid nested-grid">
    <div class="card flex flex-wrap justify-center gap-4">
      <div class="flex items-center gap-2">

        <div class="col-6">
          <div class="col-12">
            <div class="grid">
              <div class="col-1">
                <Button :disabled="disabled" @click="() => emit('remove', props.gpadEvent)" icon="pi pi-trash"/>
              </div>
              <div class="col">
                <Select
                    v-model="gpadEvent.trigger"
                    :options="buttonValues"
                    placeholder="Trigger"
                    class="input-item"
                    :disabled="disabled"
                />
              </div>
              <div class="col">
                <Select
                    v-model="gpadEvent.multiplicity"
                    :options="multiplicityValues"
                    placeholder="Multiplicity"
                    class="input-item"
                    :disabled="disabled"
                />
              </div>
              <div class="col">
                <MultiSelect
                    v-model="gpadEvent.modifiers"
                    :options="buttonValues"
                    placeholder="Modifiers"
                    class="input-item"
                    :disabled="disabled"
                />
              </div>

              <div class="col-4">
                <div class="flex flex-wrap justify-content-end gap-5">
                  <div class="flex align-items-end gap-2">
                    <label for="longPress">Long Press</label>
                    <Checkbox :disabled="disabled" name="longPress" v-model="gpadEvent.longPress" binary label="Long Press"/>
                  </div>
                </div>
              </div>

              <div class="col-8">
                <div class="flex justify-content-center align-items-center justify-center gap-4">
                  <Select
                      v-model="gpadEvent.nextSceneNameFk"
                      placeholder="Forced next scene"
                      class="input-item"
                      :disabled="disabled"
                  />
                </div>
              </div>

            </div>
          </div>
        </div>

        <div class="col-6">
          <XdoActionSection v-for="act in gpadEvent.actions" :xdo-action="act"
                            :disabled="disabled"
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