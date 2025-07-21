<script setup lang="ts">
import Select, {SelectChangeEvent} from 'primevue/select';
import Button from 'primevue/button';
import FloatLabel from 'primevue/floatlabel';
import MultiSelect from 'primevue/multiselect';
import apiClient from '@/api';
import {getSceneNameIdList, getScenes, getTriggers} from "@/api/dataStore";
import {
  axisEaserValues,
  axisValues,
  ButtonEventVto,
  EAxisEvent,
  EventVto,
  GPadEvent,
  NameId,
  Scene
} from '@/model/gpadOs'
import GpadAction from "@/components/action/GpadAction.vue";
import SelectDialog from "@/components/action/SceneDialog.vue";

import {onMounted, ref, watch} from "vue";
import _ from "lodash";

const scenesRef = ref<Scene[]>([]);
const selectedSceneRef = ref<Scene | undefined>()
const inheritedAvailableRef = ref<NameId[]>();
const inheritedRef = ref<NameId[]>();
const allSceneNames = ref<string[]>([]);
const dialogScene = ref<Scene>();
const dialogVisible = ref(false);

const leftAxisRef = ref<EAxisEvent>();
const rightAxisRef = ref<EAxisEvent>();

const fetchScenes = async () => {
  scenesRef.value = await getScenes();
  allSceneNames.value = (await getScenes()).map((q: Scene) => q.name);

  const triggers = getTriggers();
  console.log("triggers", triggers);
}

const changedScene = async (event: SelectChangeEvent) => {
  selectedSceneRef.value = event.value;
  console.log("selectedSceneRef.value", selectedSceneRef.value);

  selectedSceneRef.value?.events.sort((a: EventVto, b: EventVto) => (b.id ?? 0) - (a.id ?? 0));

  inheritedAvailableRef.value = (await getSceneNameIdList()).filter(s => s.id !== event.value?.id)

  inheritedRef.value = (await getSceneNameIdList()).filter(q => selectedSceneRef.value?.inheritsIdFk?.includes(q.id));

  console.log("inheritedAvailableRef.value", inheritedAvailableRef.value);
  console.log("inheritedRef.value", inheritedRef.value);

  leftAxisRef.value = selectedSceneRef.value?.leftAxisEvent ?? undefined;
  rightAxisRef.value = selectedSceneRef.value?.rightAxisEvent ?? undefined;
}

const saveScene = (event: any) => apiClient.put("scene", selectedSceneRef.value);

const addNewGamepadEvent = async () => {
  const eventVto = {parentFk: selectedSceneRef.value?.id} as EventVto;

  eventVto.id = (await apiClient.post("event", eventVto)).data;
  console.log("eventVto.id", eventVto.id);
  if (!selectedSceneRef.value?.events) selectedSceneRef.value!.events = [];

  selectedSceneRef.value?.events.unshift(eventVto);

  // watch(eventVto, async (q) => {
  //   console.log('sending update', eventVto);
  //   // await apiClient.put("updateGamepadEvent", eventVto);
  // });
}
const removeEvent = async  (event: EventVto) => {
  console.log('removing', event);

  _.remove(selectedSceneRef?.value?.events ?? [], q => q === event);
}

const changedInherents = async (q: any) => {
  selectedSceneRef.value!.inheritsIdFk = q.value.map((p: NameId) => p.id);
  // console.log("selectedSceneRef.value!.inheritsIdFk", selectedSceneRef.value!.inheritsIdFk);
  await apiClient.put("scene", selectedSceneRef.value);

  apiClient.get(`events/inherents/${selectedSceneRef.value?.id}`)
      .then((res) => res.data)
      .then(q => {
        console.log('inheritedGamepadEvents', q);
        selectedSceneRef.value!.inheritedGamepadEvents = q
      })
}

let newSceneDialog = false;
const newScene = () => {
  dialogScene.value = {} as Scene;
  dialogVisible.value = true;
  newSceneDialog = true;
}
const editScene = () => {
  dialogScene.value = selectedSceneRef.value;
  dialogVisible.value = true;
  newSceneDialog = false;
}
const dialogDelete = () => apiClient.delete(`scene/${dialogScene.value?.id}`)

const dialogOk = async (q: Scene) => {
  if (newSceneDialog) {
    dialogScene.value!!.id = (await apiClient.post("scene", q)).data;
    console.log("adding new scene to list", dialogScene.value);
    scenesRef.value.push(dialogScene.value!!);
  } else {
    await apiClient.put("scene", q);
  }

  selectedSceneRef.value = q;
  leftAxisRef.value = selectedSceneRef.value.leftAxisEvent;
  rightAxisRef.value = selectedSceneRef.value.rightAxisEvent;
}

onMounted(fetchScenes);
</script>

<template>
  <div class="card grid nested-grid grid-nogutter">
    <div class="col-12">
      <div class="grid">
        <div class="col-12"></div>
        <div class="col-12">
          <SelectDialog
              v-if="dialogScene"
              :is-edit="!newSceneDialog"
              v-model:visible="dialogVisible"
              @ok="dialogOk"
              @delete="dialogDelete"
              :inherits-avail="allSceneNames"
              :scene="dialogScene"
          />
        </div>
        <div class="col-4">
          <FloatLabel class="w-full md:w-56" variant="on">
            <Select
                name="scene"
                @change="changedScene"
                v-model="selectedSceneRef"
                :options="scenesRef"
                optionLabel="name"
                class="input-item"
                placeholder="Select a scene"
            />
            <label for="scene">Scene</label>
          </FloatLabel>
        </div>
        <div class="col-2">
          <Button class="mr-2" @click="editScene">Edit</Button>
          <Button @click="newScene">Add New</Button>
        </div>
        <div class="col-7 card"></div>

        <div class="col-12 card"></div>
        <div class="col-12 card"></div>
      </div>
    </div>

    <div v-if="selectedSceneRef" class="col-12">
      <div class="grid">
        <div class="col-4">
          <FloatLabel class="w-full md:w-56" variant="on">
            <MultiSelect
                v-model="inheritedRef"
                :options="inheritedAvailableRef"
                placeholder="Inherits From"
                optionLabel="name"
                class="w-full"
                @change="changedInherents"
            />
          </FloatLabel>
        </div>

        <!-- Left Axis + Smoothing -->
        <div class="col-4">
          <div class="grid grid-nogutter">
            <div class="col-6">
              <FloatLabel class="w-full" variant="on">
                <Select
                    name="leftAxis"
                    @change="saveScene"
                    v-model="selectedSceneRef.leftAxisEvent"
                    :options="axisValues"
                    class="w-full"
                />
                <label for="leftAxis">Left Axis</label>
              </FloatLabel>
            </div>
            <div class="col-6">
              <FloatLabel class="w-full" variant="on">
                <Select
                    name="leftSmoothing"
                    @change="saveScene"
                    v-model="selectedSceneRef.leftAxisEaser"
                    :options="axisEaserValues"
                    class="w-full"
                />
                <label for="leftSmoothing">Smoothing</label>
              </FloatLabel>
            </div>
          </div>
        </div>

        <!-- Right Axis + Smoothing -->
        <div class="col-4">
          <div class="grid grid-nogutter">
            <div class="col-6">
              <FloatLabel class="w-full" variant="on">
                <Select
                    name="rightAxis"
                    @change="saveScene"
                    v-model="selectedSceneRef.rightAxisEvent"
                    :options="axisValues"
                    class="w-full"
                />
                <label for="rightAxis">Right Axis</label>
              </FloatLabel>
            </div>
            <div class="col-6">
              <FloatLabel class="w-full" variant="on">
                <Select
                    name="rightSmoothing"
                    @change="saveScene"
                    v-model="selectedSceneRef.rightAxisEaser"
                    :options="axisEaserValues"
                    class="w-full"
                />
                <label for="rightSmoothing">Smoothing</label>
              </FloatLabel>
            </div>
          </div>
        </div>
      </div>

      <!-- Add Gamepad Event + Centered New Selects -->
      <div class="col-12">
        <div class="grid align-items-center">
          <!-- Button on left -->
          <div class="col-4">
            <Button v-if="selectedSceneRef" @click="addNewGamepadEvent">Add Gamepad Event</Button>
          </div>

          <!-- Two centered select boxes -->
          <!-- Centered selects, nudged right with margin -->
          <div class="col-7 col-offset-1">
            <div class="grid grid-nogutter">
              <div class="col-4 pr-1">
                <FloatLabel class="w-full" variant="on">
                  <Select
                      name="leftTriggerEase"
                      @change="saveScene"
                      class="w-full"
                      :options="axisEaserValues"
                      v-model="selectedSceneRef.leftTriggerEaser"
                  />
                  <label for="leftTriggerEase">Left Trigger Ease</label>
                </FloatLabel>
              </div>
              <div class="col-4 col-offset-2">
                <FloatLabel class="w-full" variant="on">
                  <Select
                      name="rightTriggerEase"
                      @change="saveScene"
                      class="w-full"
                      :options="axisEaserValues"
                      v-model="selectedSceneRef.rightTriggerEaser"
                  />
                  <label for="rightTriggerEase">Right Trigger Ease</label>
                </FloatLabel>
              </div>
            </div>
          </div>

        </div>
      </div>

      <!-- Gamepad Actions -->
      <div class="grid grid-nogutter full-width-container">
        <div class="col w-full" v-if="selectedSceneRef">
          <div v-for="action in selectedSceneRef.events" class="gpad-action-wrapper">
            <GpadAction
                :selected-scene-id="selectedSceneRef.id!"
                :event="action"
                @removeEvent="removeEvent"
                :render-action="true"
            />
          </div>
        </div>
        <div v-if="selectedSceneRef">
          <div class="col">
            <div v-for="ihr in selectedSceneRef.inheritedGamepadEvents" class="gpad-action-wrapper">
              <GpadAction
                  :selected-scene-id="selectedSceneRef.id!"
                  :disabled="true"
                  :event="ihr"
                  :render-action="true"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
.full-width-container {
  width: 100%;
  min-width: 100%;
}
.gpad-action-wrapper {
  width: 100%;
  min-width: 100%;
}

.input-item {
  width: 100%;
}
</style>