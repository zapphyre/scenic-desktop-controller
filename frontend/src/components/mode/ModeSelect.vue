<script setup lang="ts">
import Select, {SelectChangeEvent} from 'primevue/select';
import FloatLabel from 'primevue/floatlabel';
import {getModes} from "@/api/dataStore";
import {Mode} from '@/model/gpadOs';
import SceneSelector from "@/components/action/SceneSelect.vue";
import {onMounted, ref} from "vue";
import NonScenic from "@/components/action/NonScenic.vue";

const modesRef = ref<Mode[]>([]);
const modeRef = ref<Mode | undefined>();

const changedMode = async (event: SelectChangeEvent) => {
  modeRef.value = event.value;
  console.log("selected mode:", modeRef.value);
};

onMounted(async () => {
  modesRef.value = await getModes();
  if (modesRef.value.length > 0) {
    modeRef.value = modesRef.value[0]; // Set default mode to the first available
  }
});
</script>

<template>
  <div class="card grid nested-grid grid-nogutter">
    <div class="col-12">
      <div class="grid">
        <div class="col-4">
          <FloatLabel class="w-full md:w-56" variant="on">
            <Select
                name="mode"
                @change="changedMode"
                v-model="modeRef"
                :options="modesRef"
                option-label="adapterMode"
                class="input-item"
                placeholder="Select a mode"
            />
            <label for="mode">Mode</label>
          </FloatLabel>
        </div>
        <div class="col-12">
          <SceneSelector v-if="modeRef?.scenic" :mode="modeRef" />
          <NonScenic v-else :mode="modeRef"/>
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