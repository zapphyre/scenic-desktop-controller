<script setup lang="ts">

import apiClient from "@/api";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import ProgressSpinner from 'primevue/progressspinner';
import {EventVto, Mode, Scene, WinderActions, WinderOp, WinderOpEventMap} from "@/model/gpadOs";
import GpadAction from "@/components/action/GpadAction.vue";
import {computed, onMounted, ref, watch} from "vue";
import {getVerbs} from "@/api/dataStore";

interface DataTableRow {
  key: WinderOp;
  value: EventVto;
}

// Reactive state for async data
const scene = ref<Scene | null>(null);
const error = ref<string | null>(null);
const loading = ref(true);

const props = defineProps<{
  mode: Mode | undefined;
}>();

onMounted(async () => {

  console.log('props.mode', props.mode);

  scene.value = (await apiClient.get(`scene/${props.mode?.adapterMode}`)).data;
  console.log('scene.value1', scene.value);
  verbsRef.value = await getVerbs(props.mode?.adapterMode!!);
  console.log('verbsRef.value',verbsRef.value);
  loading.value = false;
});

// Ref to store fetched verbs
const verbsRef = ref<string[]>([]);

// Fetch verbs when mode changes
watch(() => props.mode?.adapterMode, async (newMode) => {
  if (newMode) {
    verbsRef.value = await getVerbs(newMode);
  } else {
    verbsRef.value = [];
  }

  console.log('verbsRef.value',verbsRef.value);

}, { immediate: true });

// Computed property to transform verbs into DataTableRow[]
const mapEntries = computed<string[]>(() => {
  if (!verbsRef.value) {
    console.log('mapEntries: verbsRef is empty');
    return [];
  }

  console.log('verbsRef.value',verbsRef.value);


  // const entries = verbsRef.value.map(q => ({
  //   key: q,
  //   value: q
  // }));
  //
  // console.log('entries', entries);

  return null;
});
</script>

<template>
  <div class="p-4">
    <div v-if="error" class="text-red-500 mb-4">{{ error }}</div>
    <ProgressSpinner v-else-if="loading"/>
    <div v-else-if="!verbsRef.length" class="text-gray-500">
      No events available
    </div>
    <DataTable v-else :value="verbsRef" class="p-datatable-sm" responsiveLayout="scroll">
      <Column field="key" header="Operation"
              style="width: 20%; min-width: 2%;"
      >
        <template #body="{ data }: { data: string }">
          <span class="font-semibold">{{ WinderActions[data] || 'Unknown Operation' }}</span>
        </template>
      </Column>
      <Column field="value.actions" header="Gamepad Action"
              style="width: 80%; min-width: 90%;"
      >
        <template #body="{ data }: { data: string }">
          <GpadAction :event=""
                      :render-action="false"
                      :mode="props.mode!!"
                      @update-event=""
                      :selected-scene-id="scene?.id ?? 0"/>
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<style scoped>
:deep(.p-datatable) {
  @apply border border-gray-300 rounded-lg shadow-sm;
}

:deep(.p-datatable-header) {
  @apply bg-gray-100 font-bold;
}

:deep(.p-datatable-tbody > tr) {
  @apply hover:bg-gray-50;
}

:deep(.p-datatable-tbody > tr > td) {
  @apply py-2 px-4;
}
</style>