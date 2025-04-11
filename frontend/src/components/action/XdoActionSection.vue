<script setup lang="ts">
import Select from 'primevue/select';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import MultiSelect from 'primevue/multiselect';
import {actionValues, XdoAction} from "@/model/gpadOs";
import {onMounted, ref, watch} from "vue";
import {getStrokes, useStrokesStore} from "@/api/store";
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

// Update parent and API when strokes change
//   watch(
//       strokes,
//       async (newStrokes, oldValue) => {
//         if (_.isEqual(newStrokes, strokes.value)) return;
//
//         console.log("strokes.value", strokes.value);
//         console.log("newStrokes", newStrokes);
//         console.log("oldValue", oldValue);
//
//         console.log('not equa$$$$$$$$$$$$$$$$l');
//         props.xdoAction.keyStrokes = newStrokes;
//         await apiClient.put("updateXdoAction", { ...props.xdoAction, keyStrokes: newStrokes });
//       },
//       { deep: true, immediate: false }
//   );
});;

// const handleFilter = (event: any) => {
//   filtered.value = event.value;
//   filterChange();
// };
</script>

<template>
  <div class="grid grid-nogutter">
    <div class="flex flex-wrap justify-center">
      <div class="flex items-center">
        <div class="col">
          <Select
              v-model="props.xdoAction.keyEvt"
              :options="actionValues"
              placeholder="XdoActionType"
              class="input-item"
              :disabled="disabled"
              @change="evtTypeChanged"
          />
        </div>
        <div class="col">
          <MultiSelect
              :disabled="disabled"
              v-model="strokes"
              :options="filteredStrokes"
              placeholder="Select key strokes"
              class="w-full input-item"
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
        </div>
        <div class="col">
          <Button
              :disabled="disabled"
              @click="() => emit('remove', props.xdoAction)"
              icon="pi pi-trash"
          />
        </div>
      </div>
    </div>
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