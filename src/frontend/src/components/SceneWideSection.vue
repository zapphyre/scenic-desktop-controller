<script setup lang="ts">

import Select from "primevue/select";
import FloatLabel from "primevue/floatlabel";
import type {Scene} from "@/model/gpadOs";
import {ref} from "vue";
import _ from "lodash";

const inheritedAvailableRef = ref<Scene[]>();
const inheritedSceneRef = ref<Scene>();

const props = defineProps<{
  inherited: Scene | undefined;
  allScenes: Scene[];
}>();

inheritedAvailableRef.value = _.filter(props.allScenes, s => s?.name !== props.inherited?.name);
console.log("avail", inheritedAvailableRef.value);

</script>

<template>
  <!--      <div class="card flex flex-wrap justify-center gap-4">-->
  <!--        <div class="flex items-center">-->
  <FloatLabel class="w-full md:w-56" variant="on">
    <Select name="inherited" checkmark
            v-model="inherited"
            :options="inheritedAvailableRef"
            optionLabel="name"
            class="w-full"/>
    <label for="inherited">Inherited</label>
  </FloatLabel>
  <!--        </div>-->
  <!--      </div>-->
</template>

<style scoped>

</style>