<script setup lang="ts">
import Select from 'primevue/select';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import FloatLabel from 'primevue/floatlabel';
import {actionValues} from "@/model/gpadOs";
import type {XdoAction} from "@/model/gpadOs";

// console.log("action vals");
// console.log(actionValues);

const props = defineProps<{
  xdoAction: XdoAction;
  change: (xdoAction: XdoAction) => void;
  remove: (xdoAction: XdoAction) => void;
}>();


</script>

<template>

  <div class="grid grid-nogutter">
    <div class=" flex flex-wrap justify-center">
      <div class="flex items-center">
        <div class="col">
          <Select
              v-model="xdoAction.keyEvt"
              :options="actionValues"
              placeholder="XdoActionType"
              class="input-item"
              @change="q => change(xdoAction)"
          />
        </div>
        <div class="col">
          <FloatLabel variant="on">
            <InputText @change="q => change(xdoAction)" name="xDoKeyPress" v-model="xdoAction.keyPress"/>
            <label for="xDoKeyPress">xDo Press</label>
          </FloatLabel>
        </div>
        <div class="col">
          <Button @click="q => remove(xdoAction)" icon="pi pi-trash"/>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.input-item {
  min-width: 11rem; /* Consistent width for Select and MultiSelect */
}
</style>