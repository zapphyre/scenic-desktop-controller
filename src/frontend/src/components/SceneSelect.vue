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
const inheritedAvailableRef = ref();
const inheritedRef = ref();


const fetchScenes = async () => {
  const scenes = await apiClient.get("allScenes");
  console.log(scenes.data);
  scenesRef.value = scenes.data;
}

let actions = [] as GPadEvent[];

watch(selectedSceneRef, (q: Scene | undefined) => {
  console.log("inherits", q?.inherits);
  console.log(selectedSceneRef.value);
  actions = selectedSceneRef.value!.gpadEvents;
  console.log("actions", actions);
  const otherScenes = _.filter(scenesRef.value, s => s.name !== q?.name);
  inheritedAvailableRef.value = [...otherScenes, q?.inherits];
  inheritedRef.value = q?.inherits;
})

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
          <Select name="scene" v-model="selectedSceneRef" :options="scenesRef" optionLabel="name"
                  class="input-item"
                  placeholder="Select a scene"/>
          <label for="scene">Scene</label>
        </FloatLabel>

        <!--        </div>-->
        <!--      </div>-->
      </div>

      <!--    <div class="grid grid-nogutter">-->
      <div class="col-4">
        <!--      <div class="card flex flex-wrap justify-center gap-4">-->
        <!--        <div class="flex items-center">-->
        <FloatLabel class="w-full md:w-56" variant="on">
          <Select name="inherited" checkmark v-model="inheritedRef" :options="inheritedAvailableRef"
                  optionLabel="name"
                  placeholder="Select a scene"
                  class="w-full"/>
          <label for="inherited">Inherited</label>
        </FloatLabel>
        <!--        </div>-->
        <!--      </div>-->
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