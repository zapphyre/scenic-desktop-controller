<script setup lang="ts">
import Select from 'primevue/select';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import MultiSelect from 'primevue/multiselect';
import {actionValues, XdoAction} from "@/model/gpadOs";
import {onMounted, ref, watch} from "vue";
import {getStrokes} from "@/api/store";
import apiClient from '@/api';

const filteredStrokes = ref();
const strokes = ref();
const filtered = ref();

const props = defineProps<{
  xdoAction: XdoAction;
  disabled?: boolean | false;
}>();

const emit = defineEmits<{
  remove: [xdoAction: XdoAction];
  addKeyStroke: [stroke: string];
}>();

const add = (q: any) => {
  getStrokes().push(filtered.value);
  filteredStrokes.value = getStrokes();
  strokes.value.push(filtered.value);

  filtered.value = undefined;
}

const filterChange = (q: any) => {
  filteredStrokes.value = getStrokes().filter((p: string) => p.includes(filtered.value));
}

onMounted(() => {
  filteredStrokes.value = getStrokes();
  strokes.value = props.xdoAction.keyStrokes || [];

  watch(() => strokes.value, async (q) => {
    props.xdoAction.keyStrokes = strokes.value;
    console.log("filtered.value", strokes.value);
    await apiClient.put("updateXdoAction", props.xdoAction)
  }, {deep: true});
})

const handleFilter = (event: any) => {
  filtered.value = event.value;
}

</script>target

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
              :disabled="disabled"
          />
        </div>
        <div class="col">
          <MultiSelect
              ref="multipleSelect"
              v-model="strokes"
              :options="filteredStrokes"
              placeholder="Select key strokes"
              @filter="handleFilter"
              class="w-full"
          >
            <!-- Header Template -->
            <template #header>
              <div class="header">Available Key Strokes</div>
              <div>
                <InputText
                    name="keystrokesSearch"
                    v-model="filtered"
                    @value-change="filterChange"
                    @keyup.enter="add"
                    placeholder="Search or Create"
                />
              </div>
            </template>

            <!-- Footer Template with Add New Button -->
            <template #footer>
              <div class="footer">
                <button
                    @click="add"
                    class="add-button"
                >
                  Add New
                </button>
              </div>
            </template>
          </MultiSelect>
        </div>
        <div class="col">
          <Button :disabled="disabled" @click="() => emit('remove', props.xdoAction)" icon="pi pi-trash"/>
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