<script setup lang="ts">
import Select from 'primevue/select';
import FloatLabel from 'primevue/floatlabel';
import apiClient from '@/api';
import type {Scene} from '@/model/gpadOs'
import {axisValues, EAxisEvent, EMultiplicity, GPadEvent} from '@/model/gpadOs';
import GpadAction from "@/components/GpadAction.vue";
import {onMounted, ref} from "vue";
import Button from "primevue/button";
import _ from "lodash";

const scenesRef = ref<Scene[]>([]);
const selectedSceneRef = ref<Scene>();
const inheritedAvailableRef = ref<string[]>();
const inheritedRef = ref<string>();

const leftAxisRef = ref<EAxisEvent>();
const rightAxisRef = ref<EAxisEvent>();

const fetchScenes = async () => {
  const scenes = await apiClient.get("allScenes");
  console.log(scenes.data);
  scenesRef.value = scenes.data;
}

const changedScene = (event: any) => {
  selectedSceneRef.value = event.value;

  selectedSceneRef.value?.gamepadEvents.sort((a: GPadEvent, b: GPadEvent) => (b.id ?? 0) - (a.id ?? 0));

  inheritedAvailableRef.value = scenesRef.value
      .filter(s => s.name !== event.value?.name)
      .map(q => (q.name));
  inheritedRef.value = selectedSceneRef.value?.inheritsNameFk;
Scene
  leftAxisRef.value = selectedSceneRef.value?.leftAxisEvent ?? undefined;
  rightAxisRef.value = selectedSceneRef.value?.rightAxisEvent ?? undefined;
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
  const gPadEvent: GPadEvent = {
    trigger: undefined,
    actions: [],
    id: undefined,
    longPress: false,
    modifiers: [],
    multiplicity: EMultiplicity.CLICK,
    nextSceneNameFk: undefined,
    parentSceneFk: selectedSceneRef.value?.name
  }

  gPadEvent.id = (await apiClient.post("saveGamepadEvent", gPadEvent)).data;
  selectedSceneRef.value?.gamepadEvents.unshift(gPadEvent);
}

const removeGpadEvent = async (gpadEvent: GPadEvent) => {
  await apiClient.delete("removeGamepadEvent", {data: gpadEvent.id});
  _.remove(selectedSceneRef?.value?.gamepadEvents ?? [], q => q === gpadEvent);
}

onMounted(fetchScenes);
</script>

<template>
  <div class="card grid nested-grid grid-nogutter">
    <div class="col-12">
      <div class="grid">
        <div class="col-12"></div>
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
        <div class="col-12 card"></div>
        <div class="col-12 card"></div>

      </div>
    </div>
    <div class="col-12">
      <div class="grid">
        <div class="col-4">
          <FloatLabel class="w-full md:w-56" variant="on">
            <Select name="inherited"
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
      <div class="col" v-if="selectedSceneRef">
        <div v-for="ihr in selectedSceneRef.inheritedGamepadEvents">
          <GpadAction :disabled="true" :gpadEvent="ihr"/>
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