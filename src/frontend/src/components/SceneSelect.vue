<script setup lang="ts">
import Select from 'primevue/select';
import FloatLabel from 'primevue/floatlabel';
import apiClient from '@/api';
import {axisValues, EAxisEvent, GPadEvent} from '@/model/gpadOs';
import type {Scene} from '@/model/gpadOs'
import GpadAction from "@/components/GpadAction.vue";
import _ from 'lodash';
import {onMounted, ref, watch} from "vue";

const scenesRef = ref<Scene[]>([]);
let selectedSceneRef = ref<Scene>();
const actionsRef = ref<GPadEvent[]>([]);
const inheritedAvailableRef = ref<Scene[]>();
const inheritedRef = ref<Scene>();

const leftAxisRef = ref<EAxisEvent>();
const rightAxisRef = ref<EAxisEvent>();

// const currScene: Scene = {} as Scene;
const allScenes = [] as Scene[];

const fetchScenes = async () => {
  const scenes = await apiClient.get("allScenes");
  console.log(scenes.data);
  scenesRef.value = scenes.data;
}

let actions: GPadEvent[];
let currScene: Scene | undefined;


const chagedScene = (event: any) => {
  // selectedSceneRef.value = scenesRef.value.filter(s => s.name === event.value?.name)[0];
  selectedSceneRef.value = event.value;

  inheritedAvailableRef.value = _.filter(scenesRef.value, s => s.name !== event.value?.name);
  console.log("inherited name ", event.value.inherits?.name)
  console.log("chagedScene", event.value);
  console.log("chagedScene name", event.value.name);

  const inheritedScene = inheritedAvailableRef.value.find(s => s.name == event.value.inherits?.name);
  console.log("is the same name", event.value.inherits === inheritedScene);
  console.log("inheritedScene", inheritedScene);
  inheritedRef.value = inheritedScene;
  actions = selectedSceneRef.value?.gpadEvents ?? [];
  console.log("actions", actions);
  leftAxisRef.value = selectedSceneRef.value?.leftAxisEvent ?? undefined;
  rightAxisRef.value = selectedSceneRef.value?.rightAxisEvent ?? undefined;
}

const changedLeftAxis = (event: any) => {
  selectedSceneRef!.value!.leftAxisEvent = event.value;
}

const changedRightAxis = (event: any) => {
  selectedSceneRef!.value!.rightAxisEvent = event.value;
}

onMounted(fetchScenes);
</script>

<template>
  <div class="card grid nested-grid grid-nogutter">

    <div class="col-12">
      <div class="col-4">
        <!--      <div class="card flex flex-wrap justify-center gap-4">-->
        <!--        <div class="flex items-center">-->

        <FloatLabel class="w-full md:w-56" variant="on">
          <Select name="scene" @change="chagedScene"
                  v-model="selectedSceneRef"
                  :options="scenesRef"
                  optionLabel="name"
                  class="input-item"
                  placeholder="Select a scene"/>
          <label for="scene">Scene</label>
        </FloatLabel>

        <!--        </div>-->
        <!--      </div>-->
      </div>

      <div class="grid">
        <div class="col-4">
          <FloatLabel class="w-full md:w-56" variant="on">
            <Select name="inherited"
                    v-model="inheritedRef"
                    :options="inheritedAvailableRef"
                    optionLabel="name"
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
      <!--    </div>-->
    </div>

    <div class="grid grid-nogutter">
      <div class="col" v-if="selectedSceneRef">
        <div v-for="action in actions">
          <GpadAction :action="action"/>
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