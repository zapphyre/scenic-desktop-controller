<script setup lang="ts">
import Select from 'primevue/select';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import MultiSelect from 'primevue/multiselect';
import {actionValues, XdoAction} from "@/model/gpadOs";
import {onMounted, ref, watch} from "vue";
import {getStrokes, useStrokesStore} from "@/api/dataStore";
import apiClient from '@/api';
import _ from "lodash";

const filteredStrokes = ref<string[]>();
const strokes = ref<string[]>([]);
const filtered = ref<string>();

const props = defineProps<{
  xdoAction: XdoAction;
  disabled?: boolean;
}>();

const emit = defineEmits<{
  remove: [xdoAction: XdoAction];
  addKeyStroke: [stroke: string];
}>();

// Access the store's reactive ref for all strokes
const {strokesRef} = useStrokesStore();

const add = async () => {
  console.log("add invoked");
  if (filtered.value && !strokesRef.value.includes(filtered.value)) {
    // Add to all available strokes (store)
    strokesRef.value.push(filtered.value);
  }
  if (filtered.value && !strokes.value.includes(filtered.value)) {
    // Add to selected strokes
    strokes.value.push(filtered.value);
    // emit('addKeyStroke', filtered.value); // Notify parent
  }
  filtered.value = undefined; // Clear filter
  filterChange(); // Update filtered options
  await apiClient.put("updateXdoAction", props.xdoAction);

  // changed();
};

const filterChange = () => {
  // filteredStrokes.value = getStrokes().filter((p: string) =>
  //     filtered.value ? p.includes(filtered.value) : true
  // );
  filteredStrokes.value = getStrokes().filter((p: string) => p.includes(filtered.value || ""));

  if (filtered.value)
    filteredStrokes.value = [...filteredStrokes.value, ...strokes.value]  //filteredStrokes.value.concat(strokes.value);

  props.xdoAction.keyStrokes = strokes.value;
};

const changed = async (e: any) => {
  props.xdoAction.keyStrokes = e.value;
  await apiClient.put("updateXdoAction", props.xdoAction);
}

const evtTypeChanged = async (e: any) => {
  await apiClient.put("updateXdoAction", props.xdoAction);
}

onMounted(() => {
  strokes.value = [...props.xdoAction.keyStrokes]; // Sync initial strokes
  filterChange();
  // console.log("Initial strokes:", strokes.value);

  // Sync strokes with props.xdoAction.keyStrokes
  watch(
      () => props.xdoAction.keyStrokes,
      (newKeyStrokes) => {
        strokes.value = [...newKeyStrokes];
        // console.log("Props keyStrokes updated:", newKeyStrokes);
      },
      { deep: true, immediate: false }
  );

});

</script>

<template>
  <div class="flex justify-content-center align-items-center gap-2">
    <Select
        v-model="props.xdoAction.keyEvt"
        :options="actionValues"
        placeholder="XdoActionType"
        class="w-4 input-item"
        :disabled="disabled"
        @change="evtTypeChanged"
    />
    <MultiSelect
        :disabled="disabled"
        v-model="strokes"
        :options="filteredStrokes"
        placeholder="Select key strokes"
        class="w-4 input-item"
        @change="changed"
    >
      <template #header>
        <div class="header">Available Key Strokes</div>
        <div>
          <InputText
              name="keystrokesSearch"
              v-model="filtered"
              @input="filterChange"
              @keyup.enter="add"
              placeholder="Search or Create"
          />
        </div>
      </template>
    </MultiSelect>
    <Button
        :disabled="disabled"
        @click="() => emit('remove', props.xdoAction)"
        icon="pi pi-trash"
        class="p-button-danger p-button-sm"
    />
  </div>
</template>

<style scoped>
.input-item {
  min-width: 11rem;
}

.header {
  padding: 10px;
}

.footer {
  padding: 10px;
}

.add-button {
  padding: 6px 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>