<script setup lang="ts">
import Select from 'primevue/select';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import FloatLabel from 'primevue/floatlabel';
import type {XdoAction} from "@/model/gpadOs";
import {actionValues} from "@/model/gpadOs";
import {watch} from "vue";
import apiClient from "@/api";

const props = defineProps<{
  xdoAction: XdoAction;
}>();

watch(props.xdoAction, (q) => apiClient.put("updateXdoAction", props.xdoAction));

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
          />
        </div>
        <div class="col">
          <FloatLabel variant="on">
            <InputText name="xDoKeyPress" v-model="xdoAction.keyPress"/>
            <label for="xDoKeyPress">xDo Press</label>
          </FloatLabel>
        </div>
        <div class="col">
          <Button icon="pi pi-trash"/>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.input-item {
  min-width: 11rem;
}
</style>