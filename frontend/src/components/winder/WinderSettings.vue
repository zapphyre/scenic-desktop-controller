<script setup lang="ts">

import apiClient from "@/api";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import ProgressSpinner from 'primevue/progressspinner';
import {EventVto, Scene, WinderActions, WinderOp, WinderOpEventMap} from "@/model/gpadOs";
import GpadAction from "@/components/action/GpadAction.vue";
import {computed, onMounted, ref} from "vue";

interface DataTableRow {
  key: WinderOp;
  value: EventVto;
}

// Reactive state for async data
const winderScene = ref<Scene | null>(null);
const eventMap = ref<WinderOpEventMap | null>(null);
const error = ref<string | null>(null);
const loading = ref(true);

onMounted(async () => {
  winderScene.value = (await apiClient.get('winder/scene')).data;
  eventMap.value = (await apiClient.get('winder/events')).data;
  loading.value = false;
});
// Transform eventMap to array for DataTable
const mapEntries = computed<DataTableRow[]>(() => {
  if (!eventMap.value) {
    console.log('mapEntries: eventMap is null');
    return [];
  }
  const entries = Object.entries(eventMap.value).map(([key, value]) => ({
    key: key as WinderOp,
    value
  }));
  return entries;
});
</script>

<template>
  <div class="p-4">
    <div v-if="error" class="text-red-500 mb-4">{{ error }}</div>
    <ProgressSpinner v-else-if="loading"/>
    <div v-else-if="!mapEntries.length" class="text-gray-500">
      No events available
    </div>
    <DataTable v-else :value="mapEntries" class="p-datatable-sm" responsiveLayout="scroll">
      <Column field="key" header="Operation"
              style="width: 20%; min-width: 2%;"
      >
        <template #body="{ data }: { data: DataTableRow }">
          <span class="font-semibold">{{ WinderActions[data.key] || 'Unknown Operation' }}</span>
        </template>
      </Column>
      <Column field="value.actions" header="Gamepad Action"
              style="width: 80%; min-width: 90%;"
      >
        <template #body="{ data }: { data: DataTableRow }">
          <GpadAction :event="data.value"
                      :render-action="false"
                      :selected-scene-id="winderScene?.id ?? 0"/>
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