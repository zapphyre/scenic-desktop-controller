<script setup lang="ts">

import {onMounted, onUnmounted, ref} from "vue";
import Button from "primevue/button";
import apiClient from "@/api";

const currentSceneNameRef = ref<string>("[none]");

let sceneStateSource: EventSource;

const nullifyForced = async (e: any) => await apiClient.put("state/nullify-forced", {});

onMounted(() => {
  sceneStateSource = new EventSource(`${import.meta.env.VITE_API_BASE_URL}/state/scene`);

  sceneStateSource.onmessage = (e: MessageEvent) => {
    console.log("sceneStateSource", e.data);
    currentSceneNameRef.value = e.data;
  }
});

onUnmounted(() => {
  sceneStateSource.close();
});

</script>

<template>
  <div class="card">
    <div class="grid">
      <div class="col-5 gap-7">
        <span>currently recognized scene: {{ currentSceneNameRef }}</span>
      </div>
    </div>
    <div class="grid">
      <div class="col-4 gap-8">
        <Button @click="nullifyForced">Release Pressed Keys&Reset Forced Scene</Button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>