<script setup lang="ts">

import {onMounted, onUnmounted, ref} from "vue";
import {EKeyEvt, KeyPart} from "@/model/gpadOs";
import Button from "primevue/button";
import apiClient from "@/api";

const currentSceneNameRef = ref<string>("[none]");
const keysdownsRef = ref<KeyPart[]>([]);

let sceneStateSource: EventSource;
let keyStateSource: EventSource;

const releaseKey = async (key: KeyPart, e: any) => await apiClient.put("state/issue", key);
const nullifyForced = async (e: any) => await apiClient.put("state/nullify-forced", {});

onMounted(() => {
  sceneStateSource = new EventSource(`${import.meta.env.VITE_API_BASE_URL}/state/scene`);
  keyStateSource = new EventSource(`${import.meta.env.VITE_API_BASE_URL}/state/keydown`);

  sceneStateSource.onmessage = (e: MessageEvent) => {
    console.log("sceneStateSource", e.data);
    currentSceneNameRef.value = e.data;
  }

  keyStateSource.onmessage = (e: MessageEvent) => {
    console.log("keyStateSource", e.data);

    const keyPart: KeyPart = JSON.parse(e.data);

    if (keyPart.keyEvt === EKeyEvt.PRESS)
      keysdownsRef.value.push(keyPart);

    if (keyPart.keyEvt === EKeyEvt.RELEASE)
      keysdownsRef.value.splice(keysdownsRef.value.indexOf({
        keyEvt: EKeyEvt.PRESS,
        keyPress: keyPart.keyPress
      } as KeyPart), 1);
  };
});

onUnmounted(() => {
  sceneStateSource.close();
  keyStateSource.close();
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
        <span>Buttons currently pressed: </span>
        <span v-for="key in keysdownsRef">
          <Button @click="releaseKey(key, $event?.target)">{{ key.keyPress }}</Button>
        </span>
      </div>
    </div>
    <div class="grid">
      <div class="col-4 gap-8">
        <Button @click="nullifyForced">Nullify Forced Scene</Button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>