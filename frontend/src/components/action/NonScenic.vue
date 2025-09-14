<script setup lang="ts">

import apiClient from "@/api";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import ProgressSpinner from 'primevue/progressspinner';
import {EventVto, Mode, Scene, WinderActions} from "@/model/gpadOs";
import GpadAction from "@/components/action/GpadAction.vue";
import {onMounted, ref, watch} from "vue";
import {getVerbs} from "@/api/dataStore";

// Reactive state for async data
const scene = ref<Scene | null>(null);
const events = ref<EventVto[]>([]);
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
  console.log('verbsRef.value', verbsRef.value);
  console.log('props.mode?.keyEvtTypes', props.mode?.keyEvtTypes);

  loading.value = false;

  events.value = updateSceneWithMissingVerbs(scene.value!!, verbsRef.value);
  console.log('events.value', events.value);
});

function updateSceneWithMissingVerbs(scene: Scene, verbsRef: string[]): EventVto[] {
  // Step 1: Collect existing keyEvt values from events and inheritedGamepadEvents
  const existingKeyEvts = new Set<string>();

  // Check events
  scene.events?.forEach(event => {
    event.actions?.forEach(action => {
      if (action.keyEvt) {
        existingKeyEvts.add(action.keyEvt);
      }
    });
  });


  let existingEvents: EventVto[] = [];
  scene.events?.forEach(event => {
    event.actions?.forEach(action => {
      if (verbsRef.indexOf(action.keyEvt!!) !== -1) {
        existingEvents.push(event);
      }
    });
  });


  const missingVerbs = verbsRef.filter(verb => !existingKeyEvts.has(verb));
  const newEvents: EventVto[] = missingVerbs.map(verb => ({
    id: undefined,
    gestureEvent: undefined,
    buttonEvent: undefined,
    parentFk: scene.id, // Link to scene's ID, or undefined if scene.id is undefined
    nextSceneFk: undefined,
    actions: [{
      id: undefined,
      keyEvt: verb,
      keyStrokes: [],
      eventFk: undefined,
      activator: undefined
    }]
  }));

  return [...existingEvents, ...newEvents];
}

// Ref to store fetched verbs
const verbsRef = ref<string[]>([]);

const saveEvent = async (e: EventVto) => {
  console.log('event to save', e);

  // e.id = (await apiClient.post(`event`))
}

// Fetch verbs when mode changes
watch(() => props.mode?.adapterMode, async (newMode) => {
  if (newMode) {
    verbsRef.value = await getVerbs(newMode);
  } else {
    verbsRef.value = [];
  }

  console.log('verbsRef.value', verbsRef.value);

}, {immediate: true});

</script>

<template>
  <div class="p-4">
    <div v-if="error" class="text-red-500 mb-4">{{ error }}</div>
    <ProgressSpinner v-else-if="loading"/>
    <div v-else-if="!verbsRef.length" class="text-gray-500">
      No events available
    </div>
    <DataTable v-else :value="events" class="p-datatable-sm" responsiveLayout="scroll">
      <Column field="key" header="Operation"
              style="width: 20%; min-width: 2%;"
      >
        <template #body="{ data }: { data: EventVto }">
          <span class="font-semibold">{{
              WinderActions[data.actions[0].keyEvt ?? 'unknown'] || data.actions[0].keyEvt
            }}</span>
        </template>
      </Column>
      <Column field="value.actions" header="Gamepad Action"
              style="width: 80%; min-width: 90%;"
      >
        <template #body="{ data }: { data: EventVto }">
          <GpadAction :event="data"
                      :render-action="false"
                      :mode="props.mode!!"
                      @update-event="saveEvent"
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