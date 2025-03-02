<script setup lang="ts">
import Select from 'primevue/select';
import FloatLabel from 'primevue/floatlabel';
import apiClient from '@/api';
import type {GPadEvent, Scene, EButtonAxisMapping} from '@/model/gpadOs';
import GpadAction from "@/components/GpadAction.vue";
import _ from 'lodash';
import {onMounted, ref, watch} from "vue";

const scenesRef = ref<Scene[]>([]);
const selectedSceneRef = ref<Scene>();
const actionsRef = ref<GPadEvent[]>([]);
const inheritedAvailableRef = ref<Scene[]>();
const inheritedRef = ref<Scene>();


const fetchScenes = async () => {
  const scenes = await apiClient.get("allScenes");
  console.log(scenes.data);
  scenesRef.value = scenes.data;
}

let actions = [] as GPadEvent[];


const chagedScene = (event: any) => {
  inheritedAvailableRef.value = _.filter(scenesRef.value, s => s.name !== event.value?.name);
  console.log("inherited name ", event.value.inherits?.name)
  console.log("chagedScene", event.value);
  console.log("chagedScene name", event.value.name);

  const inheritedScene = inheritedAvailableRef.value.find(s => s.name == event.value.inherits?.name);
  console.log("is the same name", event.value.inherits === inheritedScene);
  console.log("inheritedScene", inheritedScene);
  inheritedRef.value = inheritedScene;
}

watch(inheritedRef, q => {

})

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

      <!--    <div class="grid grid-nogutter">-->
      <div class="col-4">
<!--        <SceneWideSection :all-scenes="scenesRef" :inherited="inheritedRef" />-->
        <FloatLabel class="w-full md:w-56" variant="on">
          <Select name="inherited__"
                  v-model="inheritedRef"
                  :options="inheritedAvailableRef"
                  optionLabel="name"
                  class="w-full"/>
          <label for="inherited">Inherited</label>
        </FloatLabel>
      </div>
      <!--    </div>-->
    </div>

    <div class="grid grid-nogutter">
      <div class="col" v-if="inheritedRef">
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