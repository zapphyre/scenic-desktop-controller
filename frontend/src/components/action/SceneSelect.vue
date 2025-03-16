<script setup lang="ts">
import Select from 'primevue/select';
import Button from 'primevue/button';
import FloatLabel from 'primevue/floatlabel';
import apiClient from '@/api';
import {axisValues, EAxisEvent, GPadEvent, Scene} from '../../model/gpadOs'
import GpadAction from "@/components/action/GpadAction.vue";
import SelectDialog from "@/components/action/SceneDialog.vue";

import {onMounted, ref} from "vue";
import _ from "lodash";

const scenesRef = ref<Scene[]>([]);
const selectedSceneRef = ref<Scene>();
const inheritedAvailableRef = ref<string[]>();
const inheritedRef = ref<string>();
const allSceneNames = ref<string[]>([]);
const dialogScene = ref<Scene>();
const dialogVisible = ref(false);

const leftAxisRef = ref<EAxisEvent>();
const rightAxisRef = ref<EAxisEvent>();

const fetchScenes = async () => {
  const scenes = await apiClient.get("allScenes");
  console.log(scenes.data);
  scenesRef.value = scenes.data;
  allSceneNames.value = scenes.data.map((q: Scene) => q.name);
}

const changedScene = (event: any) => {
  selectedSceneRef.value = event.value;

  selectedSceneRef.value?.gamepadEvents.sort((a: GPadEvent, b: GPadEvent) => (b.id ?? 0) - (a.id ?? 0));

  inheritedAvailableRef.value = scenesRef.value
      .filter(s => s.name !== event.value?.name)
      .map(q => (q.name));
  inheritedRef.value = selectedSceneRef.value?.inheritsNameFk;
  leftAxisRef.value = selectedSceneRef.value?.leftAxisEvent ?? undefined;
  rightAxisRef.value = selectedSceneRef.value?.rightAxisEvent ?? undefined;
}

const inheritedChanged = async (event: any) => {
  selectedSceneRef.value!.inheritsNameFk = inheritedRef.value;

  await apiClient.put("updateScene", selectedSceneRef.value)
}

const changedLeftAxis = (event: any) => {
  selectedSceneRef!.value!.leftAxisEvent = event.value;
  apiClient.put("updateScene", selectedSceneRef.value);
}

const changedRightAxis = (event: any) => {
  selectedSceneRef!.value!.rightAxisEvent = event.value;
  apiClient.put("updateScene", selectedSceneRef.value);
}

const addNewGamepadEvent = async () => {
  const gPadEvent = {parentSceneFk: selectedSceneRef.value?.name, actions: []} as GPadEvent;

  gPadEvent.id = (await apiClient.post("saveGamepadEvent", gPadEvent)).data;
  selectedSceneRef.value?.gamepadEvents.unshift(gPadEvent);
}

const removeGpadEvent = async (gpadEvent: GPadEvent) => {
  await apiClient.delete("removeGamepadEvent", {data: gpadEvent.id});
  _.remove(selectedSceneRef?.value?.gamepadEvents ?? [], q => q === gpadEvent);
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
const dialogDelete = () => apiClient.delete("removeScene", {data: dialogScene.value?.id})
const dialogOk = async (q: Scene) => {

  if (newSceneDialog) {
    dialogScene.value!!.id = (await apiClient.post("saveScene", q)).data;
    console.log("adding new scene to list", dialogScene.value);
    scenesRef.value.push(dialogScene.value!!);
  }
  else {
    await apiClient.put("updateScene", q);
  }

  selectedSceneRef.value = q;
}

onMounted(fetchScenes);
</script>

<template>

  <div class="card grid nested-grid grid-nogutter">
    <div class="col-12">
      <div class="grid">
        <div class="col-12"></div>
        <div class="col-12">
          <SelectDialog v-if="dialogScene"
                        :is-edit="!newSceneDialog"
                        v-model:visible="dialogVisible"
                        @ok="dialogOk"
                        @delete="dialogDelete"
                        :inherits-avail="allSceneNames"
                        :scene="dialogScene"/>
        </div>
        <div class="col-4">
          <FloatLabel class="w-full md:w-56" variant="on">
            <Select name="scene" @change="changedScene"
                    v-model="selectedSceneRef"
                    :options="scenesRef"
                    optionLabel="name"
                    class="input-item"
                    placeholder="Select a scene"/>
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
    <div class="col-12">
      <div class="grid">
        <div class="col-4">
          <FloatLabel class="w-full md:w-56" variant="on">
            <Select name="inherited"
                    @change="inheritedChanged"
                    v-model="inheritedRef"
                    :options="inheritedAvailableRef"
                    class="w-full"/>
            <label for="inherited">Inherited</label>
          </FloatLabel>
        </div>

        <div class="col-4">
          <FloatLabel class="w-full md:w-56" variant="on">
            <Select name="leftAxis"
                    @change="changedLeftAxis"
                    v-model="leftAxisRef"
                    :options="axisValues"
                    class="w-full"/>
            <label for="leftAxis">Left Axis</label>
          </FloatLabel>
        </div>

        <div class="col-4">
          <FloatLabel class="w-full md:w-56" variant="on">
            <Select name="rightAxis"
                    @change="changedRightAxis"
                    v-model="rightAxisRef"
                    :options="axisValues"
                    class="w-full"/>
            <label for="rightAxis">Right Axis</label>
          </FloatLabel>
        </div>
      </div>
      <div class="col-4">
        <Button v-if="selectedSceneRef" @click="addNewGamepadEvent">Add Gamepad Event</Button>
      </div>
    </div>

    <div class="grid grid-nogutter">
      <div class="col" v-if="selectedSceneRef">
        <div v-for="action in selectedSceneRef.gamepadEvents">
          <GpadAction :gpadEvent="action" @remove="removeGpadEvent"/>
        </div>
      </div>
      <div v-if="selectedSceneRef">
        <div class="col">
          <div v-for="ihr in selectedSceneRef.inheritedGamepadEvents">
            <GpadAction :disabled="true" :gpadEvent="ihr"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.input-item {
  width: 100%;
}
</style>