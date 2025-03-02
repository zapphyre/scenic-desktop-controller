<script setup lang="ts">

import Select from "primevue/select";
import FloatLabel from "primevue/floatlabel";
import type {Scene} from "@/model/gpadOs";
import {ref} from "vue";
import _ from "lodash";

const inheritedAvailableRef = ref<Scene[]>();
const inheritedSceneRef = ref<Scene>();

const props = defineProps<{
  selScene: Scene | undefined;
  allScenes: Scene[];
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void; // Custom event for v-model
}>();

inheritedAvailableRef.value = _.filter(props.allScenes, s => s?.name !== props.selScene?.name);
// const inheritedScene = inheritedAvailableRef.value.find(s => s.name === props.selScene?.inherits?.name);

console.log("avail", inheritedAvailableRef.value);

</script>

<template>
  <!--      <div class="card flex flex-wrap justify-center gap-4">-->
  <!--        <div class="flex items-center">-->
  <FloatLabel class="w-full md:w-56" variant="on">
    <Select name="inherited" checkmark
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